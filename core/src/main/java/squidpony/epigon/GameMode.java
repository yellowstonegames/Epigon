package squidpony.epigon;

import squidpony.epigon.util.Utilities;

/**
 *
 * @author howar
 */
public enum GameMode {
    DIVE, CRAWL;
    private final String name;

    GameMode() {
        name = Utilities.caps(name(), "_", " ");
    }

    @Override
    public String toString() {
        return name;
    }

}
