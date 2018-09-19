package squidpony.epigon.display;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import squidpony.ArrayTools;
import squidpony.epigon.Epigon;
import squidpony.epigon.data.LiveValue;
import squidpony.epigon.data.Physical;
import squidpony.epigon.data.Stat;
import squidpony.epigon.mapping.EpiMap;
import squidpony.epigon.mapping.EpiTile;
import squidpony.epigon.mapping.RememberedTile;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidgrid.gui.gdx.SparseLayers;
import squidpony.squidgrid.gui.gdx.SubcellLayers;
import squidpony.squidgrid.gui.gdx.TextCellFactory;
import squidpony.squidmath.Coord;
import squidpony.squidmath.EnumOrderedMap;
import squidpony.squidmath.EnumOrderedSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles the contents relevant to the current context mode, switching modes as needed.
 *
 * @author Eben Howard
 */
public class ContextHandler {

    public enum ContextMode {
        TILE_CONTENTS, INVENTORY, STAT_DETAILS, MINI_MAP, MESSAGE;

        private final String name;

        private ContextMode() {
            name = Arrays.stream(name().split("_"))
                .map(s -> s.substring(0, 1) + s.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
        }

        private int position() {
            for (int i = 0; i < values().length; i++) {
                if (values()[i] == this) {
                    return i;
                }
            }
            return -1;
        }

        public ContextMode next() {
            return values()[(position() + 1) % values().length];
        }

        public ContextMode prior() {
            return values()[(position() + (values().length - 1)) % values().length];
        }

        @Override
        public String toString() {
            return name;
        }
    }
    public Group group;
    private SparseLayers layers;
    private SubcellLayers mainMap;
    private Actor miniMap;
    private int width;
    private int height;
    private EpiMap epiMap;
    private TextCellFactory miniMapFont;
    private ContextMode contextMode = ContextMode.TILE_CONTENTS;
    private EnumOrderedMap<ContextMode, char[][]> cachedTexts = new EnumOrderedMap<>(ContextMode.class);
    private EnumOrderedMap<ContextMode, float[][]> cachedColors = new EnumOrderedMap<>(ContextMode.class);
    private EnumOrderedSet<ContextMode> cacheIsValid = new EnumOrderedSet<>(ContextMode.class);
    private float defaultFrontColor;
    private Epigon game;

    public Coord arrowLeft;
    public Coord arrowRight;
    
    public ContextHandler(SparseLayers layers, SubcellLayers mainMap, Epigon game) {
        group = new Group();
        this.layers = layers;
        group.addActor(this.layers);
        this.mainMap = mainMap;
        width = layers.gridWidth;
        height = layers.gridHeight;
        epiMap = null;
        arrowLeft = Coord.get(1, 0);
        arrowRight = Coord.get(width - 2, 0);
        this.game = game;
        defaultFrontColor = layers.defaultPackedForeground;
        for (ContextMode mode : ContextMode.values()) {
            cachedTexts.put(mode, ArrayTools.fill(' ', width, height));
            cachedColors.put(mode, ArrayTools.fill(defaultFrontColor, width, height));
            cacheIsValid.remove(mode);
        }
        
        layers.fillBackground(layers.defaultPackedBackground);
    }

    public void setMap(EpiMap map) {
        if (miniMap != null) {
            group.removeActor(miniMap);
            miniMap.setVisible(false);
            miniMap.clear();
            miniMap = null;
        }

        epiMap = map;
        if (epiMap != null) {
            miniMap = new Actor() {
                @Override
                public void draw(Batch batch, float parentAlpha) {
                    super.draw(batch, parentAlpha);
                    float xo = getX() + Epigon.contextSize.cellWidth, yo = getY(),
                            yOff = yo + Epigon.contextSize.cellHeight + mainMap.gridHeight * miniMapFont.actualCellHeight;
                    //mainMap.font.configureShader(batch);
                    float widthInc = miniMapFont.actualCellWidth, heightInc = -miniMapFont.actualCellHeight;
                    RememberedTile memory;
                    for (int i = 0; i < epiMap.width; i++) {
                        for (int j = 0; j < epiMap.height; j++) {
                            if ((memory = epiMap.remembered[i][j]) != null) {
                                miniMapFont.draw(batch, '\u0000',
                                    (epiMap.fovResult[i][j] > 0)
                                        ? SColor.lerpFloatColors(memory.miniMapColor, SColor.FLOAT_WHITE, 0.25f)
                                        : SColor.lerpFloatColors(memory.miniMapColor, SColor.FLOAT_BLACK, 0.2f),
                                    xo + widthInc * i, yOff + heightInc * j);

                            }
                        }
                    }
                    //mainMap.getLayer(0).draw(batch, miniMapFont, xo, yOff, '\u0000');
                    int x, y;
                    ArrayList<TextCellFactory.Glyph> glyphs = mainMap.glyphs;
                    for (int i = 0; i < glyphs.size(); i++) {
                        TextCellFactory.Glyph glyph = glyphs.get(i);
                        if (glyph == null) {
                            continue;
                        }
                        //glyph.act(Gdx.graphics.getDeltaTime());
                        if ((x = mainMap.gridX(glyph.getX())) < 0 || x >= mainMap.gridWidth
                            || (y = mainMap.gridY(glyph.getY())) < 0 || y >= mainMap.gridHeight
                            || mainMap.backgrounds[x * 3 + 1][y * 3 + 1] == 0f) {
                            continue;
                        }
                        miniMapFont.draw(batch, '\u0000', game.player != null && 
                                        glyph.equals(game.player.appearance)
                            ? -0x1.fffep126F // SColor.CYAN
                            : -0x1.0049fep125F, // SColor.SCARLET
                            xo + widthInc * x, yOff + heightInc * y);
                    }
                }
            };
            miniMapFont = mainMap.font.copy().width(1f).height(1f).initBySize();
            group.addActor(miniMap);
            miniMap.setVisible(false);
        }
    }

    private void clear() {
        layers.clear();

        int w = width;
        int h = height;
        for (int x = 0; x < w; x++) {
            put(x, 0, '─');
            put(x, h - 1, '─');
        }
        for (int y = 0; y < h; y++) {
            put(0, y, '│');
            put(w - 1, y, '│');
        }
        put(0, 0, '┌');
        put(w - 1, 0, '┐');
        put(0, h - 1, '└');
        put(w - 1, h - 1, '┘');

        String title = contextMode.toString();
        int x = width / 2 - title.length() / 2;
        put(x, 0, title);

        put(arrowLeft.x, arrowLeft.y, '◀');
        put(arrowRight.x, arrowRight.y, '▶');
    }

    private void put(String[] text) {
        for (int y = 0; y < text.length && y < height - 2; y++) {
            put(1, y + 1, text[y]);
        }
    }

    private void put(List<Physical> ps) {
        for (int y = 0; y < ps.size() && y < height - 2; y++) {
            put(1, y + 1, ps.get(y).name);
        }
    }

    private void put(int x, int y, String s) {
        for (int i = 0; i < s.length(); i++) {
            put(x+i, y, s.charAt(i), layers.defaultPackedForeground);
        }
    }

    private void put(int x, int y, char c) {
        put(x, y, c, defaultFrontColor);
    }

    private void put(int x, int y, char c, float color) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            layers.put(x, y, c);
            cachedTexts.get(contextMode)[x][y] = c;
            cachedColors.get(contextMode)[x][y] = color;
        }
    }

    private void putFromCache() {
        final char[][] texts = cachedTexts.get(contextMode);
        final float[][] colors = cachedColors.get(contextMode);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                layers.put(x, y, texts[x][y], colors[x][y]);
            }
        }
    }

    public void next() {
        layers.summon(arrowRight.x, arrowRight.y, arrowRight.x + 1, arrowRight.y - 2, '✔', -0x1.abed4ap125F,//SColor.CW_HONEYDEW,
            SColor.translucentColor(-0x1.abed4ap125F, 0.2f), 0.6f);
        switchTo(contextMode.next());
    }

    public void prior() {
        layers.summon(arrowLeft.x, arrowLeft.y, arrowLeft.x + 1, arrowLeft.y - 2, '✔', -0x1.abed4ap125F,//SColor.CW_HONEYDEW,
                SColor.translucentColor(-0x1.abed4ap125F, 0.2f), 0.6f);
        switchTo(contextMode.prior());
    }

    public void invalidateCache(ContextMode mode) {
        cacheIsValid.remove(mode);
    }

    private void switchTo(ContextMode mode) {
        if (contextMode == ContextMode.MINI_MAP && miniMap != null) {
            miniMap.setVisible(false);
        }
        contextMode = mode;
        if (cacheIsValid.contains(mode)) { // map cache is never valid
            putFromCache();
        } else {
            switch (mode) {
                case INVENTORY:
                    contextInventory(Collections.emptyList());
                    break;
                case MESSAGE:
                    message("");
                    break;
                case MINI_MAP:
                    contextMiniMap();
                    break;
                case STAT_DETAILS:
                    contextStatDetails(null, null);
                    break;
                case TILE_CONTENTS:
                    tileContents(0, 0, null);
            }
        }
    }

    public void contextStatDetails(Stat stat, LiveValue lv) {
        contextMode = ContextMode.STAT_DETAILS;
        if (miniMap != null) {
            miniMap.setVisible(false);
        }
        clear();
        if (stat != null && lv != null) {
            put(1, 1, stat.toString() + " (" + stat.nick() + ")");
            put(1, 2, "Base:  " + lv.base());
            put(1, 3, "Max:   " + lv.max());
            put(1, 4, "Delta: " + lv.delta());
        }
        cacheIsValid.add(contextMode);
    }

    public void contextMiniMap() {
        contextMode = ContextMode.MINI_MAP;
        clear();
        if (miniMap != null) {
            miniMap.setVisible(true);
        }
    }

    public void contextInventory(List<Physical> inventory) {
        contextMode = ContextMode.INVENTORY;
        miniMap.setVisible(false);
        clear();
        put(inventory);
        cacheIsValid.add(contextMode);
    }

    public void tileContents(int x, int y, EpiTile tile) {
        if (contextMode != ContextMode.TILE_CONTENTS) {
            return;
        }
        clear();
        if (tile != null) {
            String tileDescription = "[" + x + ", " + y + "] ";
            if (tile.floor != null) {
                tileDescription += tile.floor.name;
            } else {
                tileDescription += "empty space";
            }
            put(1, 1, tileDescription);
            for (int i = 0; i < tile.contents.size(); i++) {
                put(1, 2 + i, tile.contents.get(i).name); 
            }
            Physical c = tile.getCreature();
            if(c != null)
            {
                put(1, 2 + tile.contents.size(), c.name);
            }
        }
        cacheIsValid.add(contextMode);
    }

    public void message(String... text) {
        contextMode = ContextMode.MESSAGE;
        if (miniMap != null) {
            miniMap.setVisible(false);
        }
        clear();
        put(text);
        cacheIsValid.add(contextMode);
    }

}
