package squidpony.epigon.display;

import com.badlogic.gdx.graphics.Color;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import squidpony.ArrayTools;
import squidpony.epigon.data.generic.Skill;
import squidpony.epigon.data.specific.Physical;
import squidpony.epigon.universe.CalcStat;
import squidpony.epigon.universe.Rating;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidgrid.gui.gdx.SquidColorCenter;
import squidpony.squidgrid.gui.gdx.SquidLayers;
import squidpony.squidgrid.gui.gdx.SquidPanel;
import squidpony.squidmath.*;


/**
 * Controls what happens on the full map overlay panel.
 *
 * @author Eben Howard
 */
public class PrimaryHandler {

    public enum PrimaryMode {
        EQUIPMENT, CRAFTING, HELP;

        private final String name;

        private PrimaryMode() {
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

        public PrimaryMode next() {
            return values()[(position() + 1) % values().length];
        }

        public PrimaryMode prior() {
            return values()[(position() + (values().length - 1)) % values().length];
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private SquidPanel back;
    private SquidPanel front;
    private int width;
    private int height;
    private PrimaryMode mode = PrimaryMode.EQUIPMENT;
    private SquidColorCenter colorCenter;
    private Physical player;

    public Coord arrowLeft;
    public Coord arrowRight;

    public PrimaryHandler(SquidLayers layers, SquidColorCenter colorCenter) {
        this.colorCenter = colorCenter;
        width = layers.getGridWidth();
        height = layers.getGridHeight();
        back = layers.getBackgroundLayer();
        front = layers.getForegroundLayer();

        arrowLeft = Coord.get(1, 0);
        arrowRight = Coord.get(layers.getGridWidth() - 2, 0);

        ArrayTools.fill(back.colors, back.getDefaultForegroundColor().toFloatBits());
        ArrayTools.fill(front.colors, front.getDefaultForegroundColor().toFloatBits());
        hide();
    }

    private void clear() {
        ArrayTools.fill(back.contents, '\0');
        ArrayTools.fill(front.contents, ' ');

        int w = width;
        int h = height;
        // all box drawing chars we know we can use:
        // ┼├┤┴┬┌┐└┘│─
        // ┌───┐
        // │┌┐ │
        // ├┴┼┬┤
        // │ └┘│
        // └───┘
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

        String title = mode.toString();
        int x = width / 2 - title.length() / 2;
        put(x, 0, title);

        put(arrowLeft.x, arrowLeft.y, '◀');
        put(arrowRight.x, arrowRight.y, '▶');
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

    private void put(int x, int y, char c) {
        front.put(x, y, c);
    }

    private void put(int x, int y, char c, Color color) {
        front.put(x, y, c, color);
    }

    private void put(int x, int y, char c, float color) {
        front.put(x, y, c, color);
    }

    public void next() {
        front.summon(arrowRight.x, arrowRight.y, arrowRight.x + 1, arrowRight.y - 2, '✔', SColor.CW_HONEYDEW,
            SColor.CW_RICH_HONEYDEW.cpy().sub(0f, 0f, 0f, 0.8f), 0f, 0.6f);
        mode = mode.next();
        updateDisplay();
    }

    public void prior() {
        front.summon(arrowLeft.x, arrowLeft.y, arrowLeft.x + 1, arrowLeft.y - 2, '✔', SColor.CW_HONEYDEW,
            SColor.CW_RICH_HONEYDEW.cpy().sub(0f, 0f, 0f, 0.8f), 0f, 0.6f);
        mode = mode.prior();
        updateDisplay();
    }

    private void showHelp(){

    }

    private void showEquipment(){
        int y = 2;
        for (Physical p : player.inventory){
            int x = 1;
            put(x, y, "-");
            x+=2;
            put (x, y, p.symbol, p.color);
            x+=2;
            String descr = p.name;
            if (p.weaponData != null){
                descr += " - Hands: " + p.weaponData.hands;
                descr += " Damage: " + p.weaponData.calcStats[Physical.DAMAGE];
                descr = descr.substring(0, 1).toUpperCase() + descr.substring(1);
            }
            put (x, y, descr);
            y++;
        }
    }

    private void showCrafting(){

    }

    public void hide() {
        back.setVisible(false);
        front.setVisible(false);
    }

    public PrimaryMode getMode(){
        return mode;
    }

    public void setMode(PrimaryMode mode) {
        this.mode = mode;
        updateDisplay();
    }

    public void setPlayer(Physical player){
        this.player = player;
    }

    public void updateDisplay() {
        back.setVisible(true);
        front.setVisible(true);
        clear();
        switch (mode) {
            case CRAFTING:
                showCrafting();
                break;
            case EQUIPMENT:
                showEquipment();
                break;
            case HELP:
                showHelp();
                break;
        }
    }

}
