package squidpony.epigon.display;

import com.badlogic.gdx.graphics.Color;
import java.util.Arrays;
import java.util.stream.Collectors;
import squidpony.ArrayTools;
import squidpony.epigon.data.specific.Physical;
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

    private SColor headingColor = SColor.CW_BLUE;
    private SColor keyColor = SColor.FLORAL_LEAF;

    private SquidPanel back;
    private SquidPanel front;
    private int width;
    private int halfWidth;
    private int height;
    private PrimaryMode mode = PrimaryMode.EQUIPMENT;
    private SquidColorCenter colorCenter;
    private Physical player;

    public Coord arrowLeft;
    public Coord arrowRight;

    public PrimaryHandler(SquidLayers layers, SquidColorCenter colorCenter) {
        this.colorCenter = colorCenter;
        width = layers.getGridWidth();
        halfWidth = width / 2;
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

    private void showHelp() {
        int y = 1;

        put(1, y, "Game Overview", headingColor);
        y++;
        put(1, y, "Epigon is a roguelike. Probably gonna die and such.");
        y += 2;

        put(1, y, "Ratings & Rarities, worst to best", headingColor);
        y++;
        int x = 1;
        for (Rating r : Rating.values()) {
            String display = r.toString();
            put(x, y, display, r.color());
            x += display.length() + 1;
        }
        y += 2;

        put(1, y, "Movement Keys (Numpad and Arrows also work)", headingColor);
        y++;
        int tempY = y;
        x = 2;
        put(x, y, " y k u", keyColor);
        y++;
        put(x, y, "  ↖↑↗ ", keyColor);
        y++;
        put(x, y, "h ←.→ l", keyColor);
        y++;
        put(x, y, "  ↙↓↘ ", keyColor);
        y++;
        put(x, y, " b j n", keyColor);
        y = tempY;
        x = 11;
        put(x, y, "Bumping into enemies attacks with");
        put(x, y + 1, "your default attack skill.");
        put(x, y + 3, "Waiting '.' skips your turn.");
        y += 6;

        put(1, y, "Default Key Commands", headingColor);
        y++;
        x = 1;
        int descX = x + 11;
        put(x, y, "F1, ?", keyColor);
        put(descX, y, "Opens this help screen");
        y++;
        put(x, y, "e", keyColor);
        put(descX, y, "Opens the equipment inventory screen");
        y++;
        put(x, y, "f", keyColor);
        put(descX, y, "Fires an equipped ranged weapon");
        y++;
        put(x, y, "c", keyColor);
        put(descX, y, "Consume - use up an item, such as drinking or slathering");
        y++;
        put(x, y, "ctrl-c", keyColor);
        put(descX, y, "Consume Weirdly - uses up an item in a non-standard way");
        y += 2;
    }

    private void showEquipment() {
        // Create divider
        for (int line = 1; line < height - 1; line++) {
            put(halfWidth, line, '│');
        }
        put(halfWidth, height - 1, '┴');

        // Left half
        int y = 1;
        put(1, y, "Inventory", headingColor);
        y++;
        for (Physical p : player.inventory) {
            int x = 1;
            put(x, y, p.symbol, p.color);
            x += 2;
            put(x, y, p.name);
            y++;
        }

        // Right half
        y = 1;
        int xOffset = halfWidth + 1;
        put(xOffset, y, "Wielded Equipment", headingColor);
        y++;
        for (Physical p : player.creatureData.equipment.values().stream().distinct().collect(Collectors.toList())) {
            int x = xOffset;
            put(x, y, p.symbol, p.color);
            x += 2;
            put(x, y, p.name);
            y++;
        }

        y++;
        put(xOffset, y, "Worn Armor", headingColor);
        y++;
        for (Physical p : player.creatureData.armor.values().stream().distinct().collect(Collectors.toList())) {
            int x = xOffset;
            put(x, y, p.symbol, p.color);
            x += 2;
            put(x, y, p.name);
            y++;
        }

        y++;
        put(xOffset, y, "Worn Over Armor", headingColor);
        y++;
        for (Physical p : player.creatureData.overArmor.values().stream().distinct().collect(Collectors.toList())) {
            int x = xOffset;
            put(x, y, p.symbol, p.color);
            x += 2;
            put(x, y, p.name);
            y++;
        }

        y++;
        put(xOffset, y, "Worn Clothing", headingColor);
        y++;
        for (Physical p : player.creatureData.clothing.values().stream().distinct().collect(Collectors.toList())) {
            int x = xOffset;
            put(x, y, p.symbol, p.color);
            x += 2;
            put(x, y, p.name);
            y++;
        }

        y++;
        put(xOffset, y, "Worn Jewelry", headingColor);
        y++;
        for (Physical p : player.creatureData.jewelry.values().stream().distinct().collect(Collectors.toList())) {
            int x = xOffset;
            put(x, y, p.symbol, p.color);
            x += 2;
            put(x, y, p.name);
            y++;
        }
    }

    private void showCrafting() {

    }

    public void hide() {
        back.setVisible(false);
        front.setVisible(false);
    }

    public PrimaryMode getMode() {
        return mode;
    }

    public void setMode(PrimaryMode mode) {
        this.mode = mode;
        updateDisplay();
    }

    public void setPlayer(Physical player) {
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
