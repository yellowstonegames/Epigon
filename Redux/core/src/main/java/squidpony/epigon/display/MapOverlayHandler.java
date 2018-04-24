package squidpony.epigon.display;

import com.badlogic.gdx.graphics.Color;
import squidpony.ArrayTools;
import squidpony.epigon.Prefs;
import squidpony.epigon.Utilities;
import squidpony.epigon.data.Physical;
import squidpony.epigon.data.Rating;
import squidpony.squidgrid.Direction;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidgrid.gui.gdx.SparseLayers;
import squidpony.squidmath.Coord;
import squidpony.squidmath.OrderedMap;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    private SparseLayers layers;
    private int width;
    private int halfWidth;
    private int height;
    private PrimaryMode mode = PrimaryMode.EQUIPMENT;
    private Physical player;

    private int scrollOffsetY;
    private int helpHeight;

    private Coord selection;
    private List<Coord> leftSelectables = new ArrayList<>(); // track left and right for future using of right and left arrow keys
    private List<Coord> rightSelectables = new ArrayList<>();
    private OrderedMap<Coord, Physical> selectables = new OrderedMap<>();

    public Coord arrowLeft;
    public Coord arrowRight;

    public MapOverlayHandler(SparseLayers layers) {
        width = layers.gridWidth;
        halfWidth = width / 2;
        height = layers.gridHeight;
        this.layers = layers;

        arrowLeft = Coord.get(1, 0);
        arrowRight = Coord.get(layers.gridWidth - 2, 0);

        ArrayTools.fill(this.layers.backgrounds, layers.defaultPackedBackground);
        hide();
    }

    private void clear() {
        layers.clear(0);
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
//        for (int sx = 0; sx < s.length() && sx + x < width; sx++) {
//            put(sx + x, y, s.charAt(sx));
//        }
        layers.put(x, y, s, layers.defaultPackedForeground, 0f);
    }

    private void put(int x, int y, String s, Color color) {
//        for (int sx = 0; sx < s.length() && sx + x < width; sx++) {
//            put(sx + x, y, s.charAt(sx), color);
//        }
        layers.put(x, y, s, color);
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

    private void putWithRarityColor(int x, int y, Physical p) {
        String display = getDisplayString(p);
        put(x, y, display, p.rarity.color());
        int quantity = getDisplayQuantity(p);
        if (quantity > 1) {
            put(x + display.length() + 1, y, "x" + quantity);
        }
    }
//    private Runnable postFlash = new Runnable() {
//        @Override
//        public void run() {
//            layers.fillBackground(layers.defaultPackedBackground);
//        }
//    };

    private void flashScreen() {
        if(layers.hasActiveAnimations())
            return;
        final float time = 0.625f;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                layers.tint(0f, x, y, -0x1.fefefep125F, time, () ->{});
                layers.tint(0f, x, y, -0x1.d5bf64p126F, time, () ->{}); //SColor.CW_PALE_AZURE
            }
        }
    }

    public void next() {
        layers.summon(arrowRight.x, arrowRight.y, arrowRight.x + 1, arrowRight.y - 2, '✔', -0x1.abed4ap125F, //SColor.CW_HONEYDEW
                SColor.translucentColor(-0x1.abed4ap125F, 0.2f), .6f); //SColor.CW_HONEYDEW
        mode = mode.next();
        updateDisplay();
    }

    public void prior() {
        layers.summon(arrowLeft.x, arrowLeft.y, arrowLeft.x + 1, arrowLeft.y - 2, '✔', -0x1.abed4ap125F, //SColor.CW_HONEYDEW
                SColor.translucentColor(-0x1.abed4ap125F, 0.2f), .6f); //SColor.CW_HONEYDEW
        mode = mode.prior();
        updateDisplay();
    }

    public void move(Direction dir) {
        switch (dir) {
            case UP:
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
                            flashScreen();
                        }
                        break;
                }
                break;
            case DOWN:
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
                            flashScreen();
                        }
                        break;
                }
                break;
            case LEFT:
                switch (mode) {
                    case CRAFTING:
                        showCrafting();
                        break;
                    case EQUIPMENT:
                        clear();
                        showEquipment(Direction.LEFT);
                        break;
                    case HELP:
                        // noop
                        break;
                }
                break;
            case RIGHT:
                switch (mode) {
                    case CRAFTING:
                        showCrafting();
                        break;
                    case EQUIPMENT:
                        clear();
                        showEquipment(Direction.RIGHT);
                        break;
                    case HELP:
                        // noop
                        break;
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
        put(1, y, Prefs.getGameTitle() + " is a roguelike. Probably gonna die and such.");
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
        put(3, y, "Falling mode only supports cardinal movement", headingColor);
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
        put(x, y + 5, "Space ' ' pauses during falling.");
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
        put(descX, y, "Get - picks up the items at your feet");
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
            putWithRarityColor(x, y, p);
            y++;
        }

        // Right half
        y = 1;
        int xOffset = halfWidth + 1;
        put(xOffset, y, "Wielded Equipment", headingColor);
        y++;
        for (Physical p : player.creatureData.wielded.values().stream().distinct().collect(Collectors.toList())) {
            int x = xOffset + 1;
            put(x, y, p.symbol, p.color);
            Coord select = Coord.get(x - 1, y);
            rightSelectables.add(select);
            selectables.put(select, p);
            x += 2;
            putWithRarityColor(x, y, p);
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
            putWithRarityColor(x, y, p);
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
            putWithRarityColor(x, y, p);
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
            putWithRarityColor(x, y, p);
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
            putWithRarityColor(x, y, p);
            y++;
        }

        if (selectables.isEmpty()) {
            selection = null;
        } else {
            int  i = selectables.indexOf(selection);
            switch (moveSelection) {
                case DOWN:
                    if (i >= 0 && i < selectables.size() - 1){
                        selection = selectables.keyAt(i + 1);
                    }
                    break;
                case UP:
                    if (i > 0){
                        selection = selectables.keyAt(i - 1);
                    }
                    break;
                case LEFT:
                    if (rightSelectables.contains(selection) && !leftSelectables.isEmpty()){
                        leftSelectables.sort((c1, c2) -> Integer.compare(c2.y, c1.y)); // sort in reverse order
                        boolean found = false;
                        for (Coord c : leftSelectables) {
                            if (c.y <= selection.y) {
                                selection = c;
                                found = true;
                                break;
                            }
                        }
                        if (!found){
                            selection = leftSelectables.get(leftSelectables.size() - 1);
                        }
                    }
                    break;
                case RIGHT:
                    if (leftSelectables.contains(selection) && !rightSelectables.isEmpty()){
                        rightSelectables.sort((c1, c2) -> Integer.compare(c2.y, c1.y)); // sort in reverse order
                        boolean found = false;
                        for (Coord c : rightSelectables) {
                            if (c.y <= selection.y) {
                                selection = c;
                                found = true;
                                break;
                            }
                        }
                        if (!found){
                            selection = rightSelectables.get(rightSelectables.size() - 1);
                        }
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
    
    private String getDisplayString(Physical p){
        int allowedWidth = halfWidth - 5; // allow for left and right edges, selection arrow, symbol, and space
        int quantity = getDisplayQuantity(p);
        int quantWidth = 0;
        if (quantity > 1) {
            quantWidth = (int)(Math.log10(quantity)) +3; // digits in quantity plus 'x' plus space
            allowedWidth -= quantWidth; // log10 gives us the number of digits - 1 in base 10
        }
        //.creatureData.skillWithWeapon(currentWeapon)
        String name = p.mainMaterial == null ? p.name : p.weaponData == null ? p.name
                : p.name + " ₩" + player.creatureData.skillWithWeapon(p.weaponData);  
        int length = name.length();
        if (length > allowedWidth && quantWidth < 1) {
            return name.substring(0, allowedWidth - 1) + "…";
        } else if (length > allowedWidth) {
            return name.substring(0, allowedWidth - quantWidth - 1) + "…"; // leave room for the eventual quantity presentation
        }

        return name;
    }

    private int getDisplayQuantity(Physical p) {
        if (p.groupingData != null && p.groupingData.quantity > 1) {
            return p.groupingData.quantity;
        } else {
            return 0;
        }
    }

    private void showCrafting() {

    }

    public void hide() {
        layers.setVisible(false);
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
        layers.setVisible(true);
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
