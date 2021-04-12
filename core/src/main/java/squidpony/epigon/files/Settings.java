package squidpony.epigon.files;

import squidpony.epigon.GameMode;
import squidpony.epigon.display.PanelSize;
import squidpony.epigon.mapping.MapConstants;

/**
 * User accessible settings, should be displayed in-game.
 */
public class Settings {

    public GameMode mode = GameMode.CRAWL;

    public String seed = "bananas";

    // Sets a view up to have a map area in the upper left, a info pane to the right, and a message output at the bottom
    public PanelSize mapSize;
    public PanelSize messageSize;
    public PanelSize infoSize;
    public PanelSize contextSize;

    public int worldGridWidth = 160;
    public int worldGridHeight = 160;
    public int worldGridDepth = 10;
    public int totalGridDepth = 40 + MapConstants.DIVE_HEADER.length;

    // In-flight values that shouldn't be saved to settings file
    transient public int messageCount = -1;
    transient public long seedValue;

    // Constants for when there is no config
    transient private static final int primaryGridWidth = 102; //World.DIVE_HEADER[0].length() + 2;
    transient private static final int primaryGridHeight = 26;
    transient private static final int primaryCellWidth = 14;
    transient private static final int primaryCellHeight = 28;
    transient private static final int messageGridHeight = 8;

    transient private static final int secondaryGridWidth = 50; // TODO - mark this as something code expects to be a specific size for display purposes
    transient private static final int infoGridHeight = 30; // TODO - mark this as something the code expects to be a specific size for display purposes
    transient private static final int contextGridHeight = 17; // TODO - mark this as flexible sizing
    transient private static final int secondaryCellWidth = 9;
    transient private static final int secondaryCellHeight = 20;
    // End contstants

    public void calcSeed() {
        try {
            seedValue = Long.parseLong(seed);
        } catch (NumberFormatException ex) {
            seedValue = seed.hashCode();
        }
    }

    /**
     * Using the current primary cell and grid heights, calculates the maximum whole-integer grid height for secondary cells.
     *
     * @return
     */
    public int calcSecondaryGridMaxHeight() { // NOTE - doing this here instead of in PanelSize because added remainders might give a bigger by 1 value
        int height = mapSize.pixelHeight() + messageSize.pixelHeight();
        return height / infoSize.cellHeight;
    }

    /**
     * Makes the secondary panels as large as possible, adding height to the context since info is expected to be a specific size and not scrollable
     */
    public void adjustSecondaryToMaxHeight() {
        int totalGridHeight = calcSecondaryGridMaxHeight();
        int size = totalGridHeight - infoSize.gridHeight;
        if (size < 0) {
            System.err.println("Tried to adjust context to negative size: " + size);
            return; // don't change
        } else if (size == contextSize.gridHeight) {
            System.out.println("Context size already at optimal size: " + size);
            return;
        }

        System.out.println("Adjusted context size from: " + contextSize.gridHeight + " to: " + size);
        contextSize.gridHeight = size;
    }

    /**
     * Preforms needed initialization for cases where data needs to be pulled from constants instead of from the file.
     */
    public void init() {
        if (mapSize == null) {
            mapSize = new PanelSize(primaryGridWidth, primaryGridHeight, primaryCellWidth, primaryCellHeight);
        }
        if (messageSize == null) {
            messageSize = new PanelSize(primaryGridWidth, messageGridHeight, primaryCellWidth, primaryCellHeight);
        }
        if (infoSize == null) {
            infoSize = new PanelSize(secondaryGridWidth, infoGridHeight, secondaryCellWidth, secondaryCellHeight);
        }
        if (contextSize == null) {
            contextSize = new PanelSize(secondaryGridWidth, contextGridHeight, secondaryCellWidth, secondaryCellHeight);
        }
        if (messageCount <= 0) {
            messageCount = messageGridHeight - 2; // have to leave room for the border
        }

        if (mapSize.pixelWidth() != messageSize.pixelWidth()) {
            System.out.println("Map and message panels have different widths, may lead to weird behavior in some cases.");
        }
    }

    public int defaultPixelWidth() {
        return mapSize.pixelWidth() + infoSize.pixelWidth();
    }

    public int defaultPixelHeight() {
        return mapSize.pixelHeight() + messageSize.pixelHeight();
    }
}
