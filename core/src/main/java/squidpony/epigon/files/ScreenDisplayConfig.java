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
        "Info has strict height and minimum width requirments. Where needed, context will be shrunk or grown to accommodate that.",
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
    // End constants

    private void adjustPrimaryToMaxWidth() {
        if (mapSize.pixelWidth() == messageSize.pixelWidth()) {
            return; // no adjustments needed
        }
        if (mapSize.pixelWidth() > messageSize.pixelWidth()) {
            int adjustedMessageGridWidth = mapSize.pixelWidth() / messageSize.cellWidth;
            if (adjustedMessageGridWidth > messageSize.gridWidth) {
                System.out.println("Message width expanded to " + adjustedMessageGridWidth);
                messageSize.gridWidth = adjustedMessageGridWidth;
            }
        } else {
            int adjustedMapGridWidth = messageSize.pixelWidth() / mapSize.cellWidth;
            if (adjustedMapGridWidth > mapSize.gridWidth) {
                System.out.println("Map width expanded to " + adjustedMapGridWidth);
                mapSize.gridWidth = adjustedMapGridWidth;
            }
        }
    }

    private void adjustSecondaryToMaxHeight() {
        // NOTE - doing this here instead of in PanelSize because added remainders might give a bigger by 1 value
        int height = mapSize.pixelHeight() + messageSize.pixelHeight();
        int totalGridHeight = height / infoSize.cellHeight;
        int adjustedContextGridHeight = totalGridHeight - infoSize.gridHeight;
        if (adjustedContextGridHeight < 0) {
            System.err.println("Tried to adjust context to negative size: " + adjustedContextGridHeight);
            return; // don't change
        } else if (adjustedContextGridHeight == contextSize.gridHeight) {
            System.out.println("Context size already at optimal size: " + adjustedContextGridHeight);
            return;
        }

        System.out.println("Adjusted context size from: " + contextSize.gridHeight + " to: " + adjustedContextGridHeight);
        contextSize.gridHeight = adjustedContextGridHeight;
    }

    private void adjustSecondaryToMaxWidth() {
        if (infoSize.pixelWidth() == contextSize.pixelWidth()) {
            return; // no adjustments needed
        }

        if (infoSize.pixelWidth() > contextSize.pixelWidth()) {
            int adjustedContextGridWidth = infoSize.pixelWidth() / contextSize.cellWidth;
            if (adjustedContextGridWidth > contextSize.gridWidth) {
                System.out.println("Context width expanded to " + adjustedContextGridWidth);
                contextSize.gridWidth = adjustedContextGridWidth;
            }
        } else {
            int adjustedInfoGridWidth = contextSize.pixelWidth() / infoSize.cellWidth;
            if (adjustedInfoGridWidth > infoSize.gridWidth) {
                System.out.println("Info width expanded to " + adjustedInfoGridWidth);
                infoSize.gridWidth = adjustedInfoGridWidth;
            }
        }
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

        // make sure there's enough message size to print at least one line
        messageSize.gridHeight = Math.max(3, messageSize.gridHeight);
        if (messageCount <= 0) {
            messageCount = messageGridHeight - 2; // have to leave room for the border
        }

        if (infoSize == null) {
            infoSize = new PanelSize(secondaryGridWidth, infoGridHeight, secondaryCellWidth, secondaryCellHeight);
        }

        if (contextSize == null) {
            contextSize = new PanelSize(secondaryGridWidth, contextGridHeight, secondaryCellWidth, secondaryCellHeight);
        }

        // TODO - validate against minimum workable sizes where possible

        // Adjust panels to fill out space. Anchor size is primary area height, so no adjustments there
        adjustPrimaryToMaxWidth();
        adjustSecondaryToMaxHeight();
        adjustSecondaryToMaxWidth();
    }

    public int defaultPixelWidth() {
        return mapSize.pixelWidth() + infoSize.pixelWidth();
    }

    public int defaultPixelHeight() {
        return mapSize.pixelHeight() + messageSize.pixelHeight();
    }
}
