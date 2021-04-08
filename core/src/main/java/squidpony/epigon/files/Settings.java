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

    public int worldWidth = 160;
    public int worldHeight = 160;
    public int worldDepth = 10;
    public int totalDepth = 40 + MapConstants.DIVE_HEADER.length;
    public int primaryAreaWidth = 102; //World.DIVE_HEADER[0].length() + 2;
    public int primaryAreaHeight = 26;
    public int smallAreaWidth = 50;
    public int smallAreaHeight = 22;
    public int cellWidth = 14;
    public int cellHeight = 28;
    public int bottomHeight = 8;

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

    public void setSeed(long value) {
        seedValue = value;
        seed = String.valueOf(value);
    }

    public PanelSize mapSize() {
        if (mapSize == null) {
            mapSize = new PanelSize(primaryAreaWidth / 2, primaryAreaHeight, cellHeight, cellHeight);
        }
        return mapSize;
    }

    public PanelSize messageSize() {
        if (messageSize == null) {
            messageSize = new PanelSize(primaryAreaWidth, bottomHeight, cellWidth, cellHeight);
        }
        return messageSize;
    }

    public PanelSize infoSize() {
        if (infoSize == null) {
            infoSize = new PanelSize(smallAreaWidth, smallAreaHeight * 7 / 5, 9, 20); // some mmagic numbers to get things to fit well
        }
        return infoSize;
    }

    public PanelSize contextSize() {
        if (contextSize == null) {
            contextSize = new PanelSize(smallAreaWidth, (primaryAreaHeight + bottomHeight - smallAreaHeight) * 7 / 5, 9, 20); // some magic numbers to get things to fit well
        }
        return contextSize;
    }

    public int messageCount() {
        if (messageCount <= 0) {
            messageCount = bottomHeight - 2; // have to leave room for the border
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
