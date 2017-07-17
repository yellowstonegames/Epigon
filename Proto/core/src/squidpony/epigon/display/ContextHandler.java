package squidpony.epigon.display;

import squidpony.ArrayTools;
import squidpony.epigon.data.specific.Physical;
import squidpony.epigon.mapping.EpiTile;
import squidpony.epigon.universe.LiveValue;
import squidpony.epigon.universe.Stat;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidgrid.gui.gdx.SquidLayers;
import squidpony.squidgrid.gui.gdx.SquidPanel;
import squidpony.squidmath.Coord;

import java.util.Arrays;
import java.util.EnumMap;
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
                .map(s -> s.substring(0, 1) + s.substring(1))
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

    private SquidPanel back;
    private SquidPanel front;
    private SquidPanel miniMap;
    private int width;
    private int height;
    private ContextMode contextMode = ContextMode.TILE_CONTENTS;
    private EnumMap<ContextMode, char[][]> cachedTexts = new EnumMap<>(ContextMode.class);

    public Coord arrowLeft;
    public Coord arrowRight;

    public ContextHandler(SquidLayers layers, SquidLayers mainMap) {
        width = layers.getGridWidth();
        height = layers.getGridHeight();
        back = layers.getBackgroundLayer();
        front = layers.getForegroundLayer();
        miniMap = new SquidPanel(mainMap.getTotalWidth(), mainMap.getTotalHeight(),
                layers.getTextFactory().copy().width(3f).height(3f), front.getColorCenter(), 0, mainMap.getTotalHeight() * 1.5f,
                new char[mainMap.getTotalWidth()][mainMap.getTotalHeight()]);
        miniMap.colors = mainMap.getForegroundLayer().colors;
        layers.setExtraPanel(miniMap, 3);
        miniMap.setVisible(false);
        arrowLeft = Coord.get(1, 0);
        arrowRight = Coord.get(layers.getGridWidth() - 2, 0);
        for (ContextMode mode : ContextMode.values()) {
            cachedTexts.put(mode, ArrayTools.fill(' ', width, height));
        }

        ArrayTools.fill(back.colors, back.getDefaultForegroundColor().toFloatBits());
        ArrayTools.fill(back.contents, '\0');
        ArrayTools.fill(front.colors, front.getDefaultForegroundColor().toFloatBits());
        ArrayTools.fill(front.contents, ' ');
    }

    private void clear() {
        ArrayTools.fill(front.contents, ' ');
        String title = contextMode.toString();
        int x = width / 2 - title.length() / 2;
        put(x, 0, title, true);

        put(arrowLeft.x, arrowLeft.y, '◀', true);
        put(arrowRight.x, arrowRight.y, '▶', true);
    }

    private void put(String[] text, boolean cache) {
        clear();
        for (int y = 0; y < text.length && y < height - 2; y++) {
            put(1, y + 1, text[y], cache);
        }
    }

    private void put(char[][] chars, boolean cache) {
        clear();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                put(x, y, chars[x][y], cache);
            }
        }
    }

    private void put(int x, int y, String s, boolean cache) {
        for (int sx = 0; sx < s.length() && sx + x < width; sx++) {
            put(sx + x, y, s.charAt(sx), cache);
        }
    }

    private void put(int x, int y, char c, boolean cache) {
        front.put(x, y, c);
        if (cache && x >= 0 && x < width && y >= 0 && y < height) {
            cachedTexts.get(contextMode)[x][y] = c;
        }
    }

    public void next() {
        front.summon(arrowRight.x, arrowRight.y, arrowRight.x+1, arrowRight.y-2, '✔', SColor.CW_HONEYDEW,
                SColor.CW_RICH_HONEYDEW.cpy().sub(0f, 0f, 0f, 0.8f),0f, 0.6f);
        if(contextMode == ContextMode.MINI_MAP)
            miniMap.setVisible(false);
        contextMode = contextMode.next();
        put(cachedTexts.get(contextMode), false);
        if(contextMode == ContextMode.MINI_MAP)
            contextMiniMap();
    }

    public void prior() {
        front.summon(arrowLeft.x, arrowLeft.y, arrowLeft.x+1, arrowLeft.y-2, '✔', SColor.CW_HONEYDEW,
                SColor.CW_RICH_HONEYDEW.cpy().sub(0f, 0f, 0f, 0.8f),0f, 0.6f);
        if(contextMode == ContextMode.MINI_MAP)
            miniMap.setVisible(false);
        contextMode = contextMode.prior();
        put(cachedTexts.get(contextMode), false);
        if(contextMode == ContextMode.MINI_MAP)
            contextMiniMap();
    }

    public void contextStatDetails(Stat stat, LiveValue lv) {
        contextMode = ContextMode.STAT_DETAILS;
        clear();
        put(new String[]{
            stat.toString() + " (" + stat.nick() + ")",
            "Base:  " + lv.base(),
            "Max:   " + lv.max(),
            "Delta: " + lv.delta()
        }, true);
    }

    public void contextMiniMap() {
        contextMode = ContextMode.MINI_MAP;
        clear();
        miniMap.setVisible(true);
    }

    public void contextInventory(List<Physical> inventory) {
        contextMode = ContextMode.INVENTORY;
        clear();
        put(inventory.stream()
            .map(i -> i.name)
            .collect(Collectors.toList())
            .toArray(new String[]{}),
            true);
    }

    public void tileContents(Coord location, EpiTile tile) {
        contextMode = ContextMode.TILE_CONTENTS;
        String tileDescription = "[" + location.x + ", " + location.y + "] ";
        if (tile.floor != null) {
            tileDescription += tile.floor.name + " floor";
        } else {
            tileDescription += "empty space";
        }
        if (!tile.contents.isEmpty()) {
            tileDescription = tile.contents.stream()
                .map(p -> p.name)
                .collect(Collectors.joining("\n", tileDescription + "\n", ""));
        }
        put(tileDescription.split("\n"), true);
    }

    public void message(String[] text) {
        contextMode = ContextMode.MESSAGE;
        put(text, true);
    }

}
