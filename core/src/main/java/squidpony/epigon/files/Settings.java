package squidpony.epigon.files;

/**
 * User accessible settings, should be displayed in-game.
 */
public class Settings {

    public String seed = "bananas";

    transient public long seedValue;

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
}
