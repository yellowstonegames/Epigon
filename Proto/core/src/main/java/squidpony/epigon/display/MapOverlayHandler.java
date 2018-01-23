package squidpony.epigon.display;

import com.badlogic.gdx.graphics.Color;
import java.util.ArrayList;
import java.util.List;
import squidpony.ArrayTools;
import squidpony.epigon.Utilities;
import squidpony.epigon.data.specific.Physical;
import squidpony.epigon.universe.Rating;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidgrid.gui.gdx.SquidColorCenter;
import squidpony.squidgrid.gui.gdx.SquidLayers;
import squidpony.squidgrid.gui.gdx.SquidPanel;
import squidpony.squidmath.Coord;

import java.util.stream.Collectors;
import squidpony.squidgrid.Direction;
import squidpony.squidmath.OrderedMap;

/**
 * Controls what happens on the full map overlay panel.
 *
 * @author Eben Howard
 */
public class MapOverlayHandler {

    public enum PrimaryMode {
        EQUIPMENT, CRAFTING, HELP;

        private final String name;

        PrimaryMode() {
            name = Utilities.caps(name(), "_", " ");
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

    private int scrollOffsetY;
    private int helpHeight;

    private Coord selection;
    private List<Coord> leftSelectables = new ArrayList<>(); // track left and right for future using of right and left arrow keys
    private List<Coord> rightSelectables = new ArrayList<>();
    private OrderedMap<Coord, Physical> selectables = new OrderedMap<>();

    public Coord arrowLeft;
    public Coord arrowRight;

    public MapOverlayHandler(SquidLayers layers, SquidColorCenter colorCenter) {
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
        //ArrayTools.fill(front.colors, -0x1.0p125F); // transparent

        doBorder();
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

        String title = mode.toString();
        int x = width / 2 - title.length() / 2;
        put(x, 0, title);

        put(arrowLeft.x, arrowLeft.y, '◀');
        put(arrowRight.x, arrowRight.y, '▶');
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

    public void moveUp() {
        switch (mode) {
            case CRAFTING:
                showCrafting();
                break;
            case EQUIPMENT:
                clear();
                showEquipment(Direction.UP);
                break;
            case HELP:
                if (scrollOffsetY < 0) {
                    scrollOffsetY++;
                    clear();
                    showHelp(scrollOffsetY);
                } else {
                    // TODO - visual cue that end of screen is reached
                }
                break;
        }
    }

    public void moveDown() {
        switch (mode) {
            case CRAFTING:
                showCrafting();
                break;
            case EQUIPMENT:
                clear();
                showEquipment(Direction.DOWN);
                break;
            case HELP:
                if (scrollOffsetY >= 1 - (helpHeight - height)) {
                    scrollOffsetY--;
                    clear();
                    showHelp(scrollOffsetY);
                } else {
                    // TODO - visual cue that end of screen is reached
                }
                break;
        }
    }

    public Physical getSelected(){
        return selectables.get(selection);
    }

    private void showHelp() {
        scrollOffsetY = 0;
        showHelp(scrollOffsetY);
    }

    private void showHelp(int startY) {
        int y = startY + 1;

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
        put(descX, y, "Help - Opens this help screen");
        y++;
        put(x, y, "e", keyColor);
        put(descX, y, "Equipment - Opens the equipment inventory screen");
        y++;
        put(x, y, "ctrl-s, S", keyColor);
        put(descX, y, "Save - saves the game [N/A]");
        y++;
        put(x, y, "ctrl-q, Q, ESC", keyColor);
        put(descX, y, "Quit - exits the game");
        y++;
        put(x, y, "f", keyColor);
        put(descX, y, "Fire - shoots an equipped ranged weapon");
        y++;
        put(x, y, "G", keyColor);
        put(descX, y, "Get - picks up the items at yoru feet");
        y++;
        put(x, y, "g", keyColor);
        put(descX, y, "Gather - pick up all items from surrounding tiles");
        y++;
        put(x, y, "c", keyColor);
        put(descX, y, "Consume - use up an item, such as drinking or slathering");
        y++;
        put(x, y, "ctrl-c", keyColor);
        put(descX, y, "Consume Weirdly - uses up an item in a non-standard way");
        y++;
        put(x, y, "i", keyColor);
        put(descX, y, "Interact - manipulates an object in some way");
        y++;
        put(x, y, "d", keyColor);
        put(descX, y, "Draw - draws a weapon from inventory randomly");
        y++;
        put(x, y, "r", keyColor);
        put(descX, y, "Rest - skips turns until healed");
        y++;
        put(x, y, "p", keyColor);
        put(descX, y, "Power - uses a power [N/A]");
        y++;
        put(x, y, "v", keyColor);
        put(descX, y, "View - looks around using sight only");
        y++;
        put(x, y, "x", keyColor);
        put(descX, y, "Examine - closely inspect with all senses");
        y++;
        put(x, y, "o", keyColor);
        put(descX, y, "Open - opens all nearby doors");
        y++;
        put(x, y, "s", keyColor);
        put(descX, y, "Shut - shuts all nearby doors");
        y += 2;

        helpHeight = y - startY;
        doBorder(startY, helpHeight);
    }

    private void showEquipment() {
        showEquipment(Direction.NONE);
    }

    private void showEquipment(Direction moveSelection){
        // Clear out selection tracking
        if (moveSelection == Direction.NONE) {
            selection = null;
        }
        leftSelectables.clear();
        rightSelectables.clear();
        selectables.clear();

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
            int x = 2;
            put(x, y, p.symbol, p.color);
            Coord select = Coord.get(x - 1, y);
            leftSelectables.add(select);
            selectables.put(select, p);
            x += 2;
            put(x, y, getDisplay(p));
            y++;
        }

        // Right half
        y = 1;
        int xOffset = halfWidth + 1;
        put(xOffset, y, "Wielded Equipment", headingColor);
        y++;
        for (Physical p : player.creatureData.equipment.values().stream().distinct().collect(Collectors.toList())) {
            int x = xOffset + 1;
            put(x, y, p.symbol, p.color);
            Coord select = Coord.get(x - 1, y);
            rightSelectables.add(select);
            selectables.put(select, p);
            x += 2;
            put(x, y, getDisplay(p));
            y++;
        }

        y++;
        put(xOffset, y, "Worn Armor", headingColor);
        y++;
        for (Physical p : player.creatureData.armor.values().stream().distinct().collect(Collectors.toList())) {
            int x = xOffset + 1;
            put(x, y, p.symbol, p.color);
            Coord select = Coord.get(x - 1, y);
            rightSelectables.add(select);
            selectables.put(select, p);
            x += 2;
            put(x, y, getDisplay(p));
            y++;
        }

        y++;
        put(xOffset, y, "Worn Over Armor", headingColor);
        y++;
        for (Physical p : player.creatureData.overArmor.values().stream().distinct().collect(Collectors.toList())) {
            int x = xOffset + 1;
            put(x, y, p.symbol, p.color);
            Coord select = Coord.get(x - 1, y);
            rightSelectables.add(select);
            selectables.put(select, p);
            x += 2;
            put(x, y, getDisplay(p));
            y++;
        }

        y++;
        put(xOffset, y, "Worn Clothing", headingColor);
        y++;
        for (Physical p : player.creatureData.clothing.values().stream().distinct().collect(Collectors.toList())) {
            int x = xOffset + 1;
            put(x, y, p.symbol, p.color);
            Coord select = Coord.get(x - 1, y);
            rightSelectables.add(select);
            selectables.put(select, p);
            x += 2;
            put(x, y, getDisplay(p));
            y++;
        }

        y++;
        put(xOffset, y, "Worn Jewelry", headingColor);
        y++;
        for (Physical p : player.creatureData.jewelry.values().stream().distinct().collect(Collectors.toList())) {
            int x = xOffset + 1;
            put(x, y, p.symbol, p.color);
            Coord select = Coord.get(x - 1, y);
            rightSelectables.add(select);
            selectables.put(select, p);
            x += 2;
            put(x, y, getDisplay(p));
            y++;
        }

        if (selectables.isEmpty()) {
            selection = null;
        } else {
            int i;
            switch (moveSelection) {
                case DOWN:
                    i = selectables.indexOf(selection);
                    if (i >= 0 && i < selectables.size() - 1){
                        selection = selectables.keyAt(i + 1);
                    }
                    break;
                case UP:
                    i = selectables.indexOf(selection);
                    if (i > 0){
                        selection = selectables.keyAt(i - 1);
                    }
                    break;
                case NONE:
                default:
                    selection = selectables.firstKey();
            }
        }
        
        if (selection != null){
            put(selection.x, selection.y, '↣');
        }
    }
    
    private String getDisplay(Physical p){
        if (p.groupingData != null && p.groupingData.quantity > 1){
            return p.name + " x" + p.groupingData.quantity;
        } else {
            return p.name;
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

    public void updateDisplay() { // TODO - add version that doesn't disrupt selection
        back.setVisible(true);
        front.setVisible(true);
        scrollOffsetY = 0;
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
