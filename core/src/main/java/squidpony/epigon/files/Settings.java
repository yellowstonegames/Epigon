package squidpony.epigon.files;

import squidpony.epigon.GameMode;
import squidpony.epigon.mapping.MapConstants;

/**
 * User accessible settings, should be displayed in-game.
 */
public class Settings { // TODO - split Crawl and Dive into separate configs

    public final String[] advice = new String[]{
        "If you ever want to reset your settings, you can simply delete this file.",
        "If the seed can be read as a long, it will be. Otherwise it will be read as a string."
    };

    public GameMode mode = GameMode.CRAWL;

    public String seed = "bananas";

    public int worldWidth = 160;
    public int worldHeight = 160;
    public int worldDepth = 10;

    public int diveWorldDepth = 40 + MapConstants.DIVE_HEADER.length; // only applies during Dive mode

    // In-flight values that shouldn't be saved to settings file
    transient public long seedValue;
    // End contstants

    public void calcSeed() {
        try {
            seedValue = Long.parseLong(seed);
        } catch (NumberFormatException ex) {
            seedValue = seed.hashCode();
        }
    }

}
