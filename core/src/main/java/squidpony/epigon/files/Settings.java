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

    public int worldGridWidth = 160;
    public int worldGridHeight = 160;
    public int worldGridDepth = 10;
    public int totalGridDepth = 40 + MapConstants.DIVE_HEADER.length;
    public int primaryGridWidth = 102; //World.DIVE_HEADER[0].length() + 2;
    public int primaryGridHeight = 26;
    public int secondaryGridWidth = 50;
    public int secondaryGridHeight = 22;
    public int primaryCellWidth = 14;
    public int primaryCellHeight = 28;
    public int secondaryCellWidth = 9;
    public int secondaryCellHeight = 20;
    public double secondarySize = 7.0 / 5.0; // TODO - add calculation verification of this value on window construction
    public int messageGridHeight = 8;

    transient public long seedValue;

    // Sets a view up to have a map area in the upper left, a info pane to the right, and a message output at the bottom
    transient private PanelSize mapSize;
    transient private PanelSize messageSize;
    transient private PanelSize infoSize;
    transient private PanelSize contextSize;
    transient private int messageCount = -1;

    public void calcSeed() {
        try {
            seedValue = Long.parseLong(seed);
        } catch (NumberFormatException ex) {
            seedValue = seed.hashCode();
        }
    }

    public PanelSize mapSize() {
        if (mapSize == null) {
            mapSize = new PanelSize(primaryGridWidth, primaryGridHeight, primaryCellWidth, primaryCellHeight);
        }
        return mapSize;
    }

    public PanelSize messageSize() {
        if (messageSize == null) {
            messageSize = new PanelSize(primaryGridWidth, messageGridHeight, primaryCellWidth, primaryCellHeight);
        }
        return messageSize;
    }

    public PanelSize infoSize() {
        if (infoSize == null) {
            infoSize = new PanelSize(secondaryGridWidth, (int) (secondaryGridHeight * secondarySize), secondaryCellWidth, secondaryCellHeight);
        }
        return infoSize;
    }

    public PanelSize contextSize() {
        if (contextSize == null) {
            contextSize = new PanelSize(secondaryGridWidth, (int) ((primaryGridHeight + messageGridHeight - secondaryGridHeight) * secondarySize), secondaryCellWidth, secondaryCellHeight);
        }
        return contextSize;
    }

    public int messageCount() {
        if (messageCount <= 0) {
            messageCount = messageGridHeight - 2; // have to leave room for the border
        }
        return messageCount;
    }

    public int defaultPixelWidth() {
        return mapSize().pixelWidth() + infoSize().pixelWidth();
    }

    public int defaultPixelHeight() {
        return mapSize().pixelHeight() + messageSize().pixelHeight();
    }

}
