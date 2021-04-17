package squidpony.epigon.files;

import squidpony.epigon.display.PanelSize;

/**
 * Hardware specific settings for presenting the display.
 */
public class ScreenDisplayConfig {

    public final String[] advice = new String[]{
        "If you ever want to reset your settings, you can simply delete this file.",
        "While grid and pixel sizes will attempt to be respected, there are some contraints internally that will override settings sizes in some cases.",
        "The widest pixel size of map or message will be used to choose grid width of both.",
        "Info has strict height and minimum width requirments. Where needed, context will be shrunk or grown to accomidate that.",
        "The widest pixel size of info or context will be used to choose the grid width of both."
    };

    // Sets a view up to have a map area in the upper left, a info pane to the right, and a message output at the bottom
    public PanelSize mapSize;
    public PanelSize messageSize;
    public PanelSize infoSize;
    public PanelSize contextSize;

    public int windowWidth;
    public int windowHeight;
    public int windowXPosition = -1;
    public int windowYPosition = -1;
    public int monitorIndex; // looks like for libgdx tracking by name might be better
    public boolean maximized;
    public boolean fullscreen;
    public String monitorName;

    // In-flight values that shouldn't be saved to settings file
    transient public int messageCount = -1;
    // End contstants

    // Constants for when there is no config
    transient private static final int mapGridWidth = 102; //World.DIVE_HEADER[0].length() + 2;
    transient private static final int mapGridHeight = 26;
    transient private static final int mapCellWidth = 14;
    transient private static final int mapCellHeight = 28;

    transient private static final int messageGridHeight = 8;

    transient private static final int secondaryGridWidth = 50;
    transient private static final int infoGridHeight = 30;
    transient private static final int contextGridHeight = 17;
    transient private static final int secondaryCellWidth = 9;
    transient private static final int secondaryCellHeight = 20;

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
            mapSize = new PanelSize(mapGridWidth, mapGridHeight, mapCellWidth, mapCellHeight);
        }
        if (messageSize == null) {
            messageSize = new PanelSize(mapGridWidth, messageGridHeight, mapCellWidth, mapCellHeight);
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
