package squidpony.epigon.display;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import squidpony.ArrayTools;
import squidpony.epigon.data.Physical;
import squidpony.epigon.data.control.RecipeMixer;
import squidpony.epigon.mapping.EpiMap;
import squidpony.epigon.mapping.EpiTile;
import squidpony.epigon.data.quality.Element;
import squidpony.epigon.data.LiveValueModification;
import squidpony.epigon.data.Stat;
import squidpony.squidgrid.Direction;
import squidpony.squidgrid.Radius;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidgrid.gui.gdx.SparseLayers;
import squidpony.squidgrid.gui.gdx.SquidColorCenter;
import squidpony.squidgrid.gui.gdx.SubcellLayers;
import squidpony.squidgrid.gui.gdx.TextCellFactory.Glyph;
import squidpony.squidmath.Coord;

import java.util.ListIterator;

/**
 * Oh no, you're falling!
 */
public class FallingHandler {
    private SquidColorCenter colorCenter;

    private SparseLayers layers;
    private int width;
    private int halfWidth;
    private int quarterWidth;
    private int midLeft;
    private int midRight;
    private int height;
    private Physical player;

    private EpiMap map;
    private Physical trail;
    private FxHandler fx;

    private int scrollOffsetY;
    private boolean pressedUp; // attempting to hover
    public boolean reachedGoal = false;
    private int currentDepth = 0;

    public FallingHandler(SubcellLayers layers) {
        width = layers.gridWidth;
        halfWidth = width / 2;
        quarterWidth = halfWidth / 2;
        midLeft = halfWidth - quarterWidth;
        midRight = halfWidth + quarterWidth;
        height = layers.gridHeight;
        this.layers = layers;

        trail = Physical.makeBasic("trail", (char)0x2801, SColor.GREEN_TEA_DYE);
        colorCenter = new SquidColorCenter();

        layers.addLayer();//first added panel adds at level 1, used for cases when we need "extra background"
        //layers.addLayer();//NOT USED: next adds at level 2, used for the cursor line
        layers.addLayer();//next adds at level 2, used for effects
        double[][] fov = new double[width][height];
        ArrayTools.fill(fov, 1);
        fx = new FxHandler(layers, 2, colorCenter, fov);

        ArrayTools.fill(this.layers.backgrounds, layers.defaultPackedBackground);
        hide();
    }

    public void setPlayer(Physical player){
        this.player = player;
    }

    public void hide() {
        layers.setVisible(false);
    }

    public void show(EpiMap map) {
        int ow = map.width, oh = map.height;
        this.map = new EpiMap(width, height);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                final int cx = x % ow, cy = y % oh;
                this.map.contents[x][y] = map.contents[cx][cy];
                this.map.remembered[x][y] = map.remembered[cx][cy];
            }
        }
        update(0);
        layers.setVisible(true);
    }

    public void update(){
        update(scrollOffsetY);
    }

    public void update(int yOffset) {
        clear();

        if (map.contents[player.location.x][player.location.y + currentDepth].blockage != null) {
            smash();
            damagePlayer();
        }

        scrollOffsetY = yOffset;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                put(x, y, map.contents[x][y]);
            }
        }

        put(player.location.x, player.location.y + currentDepth, player.symbol, player.color);
    }

    private void clear() {
        layers.clear(0);

        //doBorder(-scrollOffsetY, map.height);
    }

    private void doBorder() {
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

        String title = "Falling (Side View)";
        int x = width / 2 - title.length() / 2;
        put(x, 0, title);
    }

    /**
     * Draws some characters to indicate that the current view has more content above or below.
     *
     * @param y
     * @param contentHeight
     */
    private void doBorder(int y, int contentHeight) {
        doBorder();
        if (y < 0) {
            put(width - 1, 1, '▲');
        }
        if (contentHeight + y > height) {
            put(width - 1, height - 2, '▼');
        }
        put(width - 1, Math.round(((height - 5f) * y) / (height - contentHeight)) + 2, '█');
    }

    private void put(int x, int y, String s) {
        for (int sx = 0; sx < s.length() && sx + x < width; sx++) {
            put(sx + x, y, s.charAt(sx));
        }
    }

    private void put(int x, int y, String s, Color color) {
        for (int sx = 0; sx < s.length() && sx + x < width; sx++) {
            put(sx + x, y, s.charAt(sx), color);
        }
    }

    private void put(int x, int y, EpiTile tile){
        put(x, y, tile.getSymbol(), tile.getForegroundColor());
    }

    private void put(int x, int y, char c) {
        layers.put(x, y, c);
    }

    private void put(int x, int y, char c, Color color) {
        layers.put(x, y, c, color);
    }

    private void put(int x, int y, char c, float color) {
        layers.put(x, y, c, color);
    }

    private void damagePlayer() {
        player.stats.get(Stat.VIGOR).modify(LiveValueModification.add(-1));

        wigglePlayer();
        layers.burst(player.location.x + 1, player.location.y + currentDepth, 2, Radius.SPHERE, "*^!!*", SColor.CW_PALE_YELLOW.toFloatBits(), SColor.CW_FLUSH_RED.toFloatBits(), 0.3f);
    }

    private void wigglePlayer() {
        Glyph g = layers.glyphFromGrid(player.location.x + 1, player.location.y + currentDepth);
        layers.wiggle(g, 0.2f);//, () -> layers.removeGlyph(g));
    }

    private void smash(){
        EpiTile tile = map.contents[player.location.x][player.location.y + currentDepth];
        if (tile.blockage != null){
            player.addToInventory(RecipeMixer.buildPhysical(tile.blockage));
            tile.blockage = null;
        }
    }

    public void move(Direction dir) {
        doMovement(dir.deltaX, dir.deltaY);
    }

    private void doMovement(int x, int y) {
//        if (y < 0) {
//            pressedUp = true;
//            y = 0;
//        }

        Coord target = player.location.translate(x, y);
//        if (target.equals(player.location)){
//            return;
//        }

        // moving while falling makes you tired!
        //player.stats.get(Stat.SLEEP).addActual(-1);

        if (target.isWithinRectangle(0, scrollOffsetY - currentDepth, map.width, map.height)) { //scrollOffsetY + 
            
            EpiTile tile = map.contents[player.location.x][player.location.y + currentDepth];
            tile.blockage = null;
            Physical floor = tile.floor;
            Physical t = RecipeMixer.buildPhysical(trail);
            t.color = floor.color == SColor.TRANSPARENT.toFloatBits() ? SColor.RAINBOW[(target.x + target.y + 10) % 7].toFloatBits() : floor.color;
            tile.floor = t;
            
            tile = map.contents[target.x][target.y + currentDepth];
            player.location = target;
            if (tile.blockage != null) {
                smash();
                damagePlayer();
            }
            ListIterator<Physical> li = tile.contents.listIterator();
            while (li.hasNext()) {
                Physical p = li.next();
                if(p.unique)
                {
                    // reached goal at the bottom
                    reachedGoal = true;
                    //fx.layeredSparkle(target, 20, Radius.CIRCLE);
                    update();
                    return;
                }
                if(p.groupingData != null && player.inventory.contains(p))
                    p.groupingData.quantity++;
                else 
                    player.addToInventory(p);
                fx.twinkle(Coord.get(target.x, currentDepth + target.y), Element.FIRE);// have to have it lower due to border offset
                li.remove();
            }
            update();
        } else {
            wigglePlayer();
        }
    }

    public void processInput() {
        if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.Q))
            Gdx.app.exit();
        if(reachedGoal)
            return;
        int offX = 0, offY = 0;
        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.NUMPAD_8)) {
            --offY;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.NUM_2)) {
            ++offY;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.NUMPAD_4)) {
            --offX;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.NUMPAD_6)) {
            ++offX;
        }
        doMovement(offX, offY);
    }

    public void fall() {
        if(reachedGoal)
            return;
        if (player.location.y + currentDepth <= scrollOffsetY){
            move(Direction.DOWN);
        }

        player.stats.get(Stat.NUTRITION).tick();

//        pressedUp = false;
        update(scrollOffsetY + 1);
    }

    public void setCurrentDepth(int i) {
        currentDepth = i;
    }
}
