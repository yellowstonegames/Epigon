package squidpony.data;

import squidpony.exception.InvalidInputFormatException;

/**
 * Enum for the possible sizes of Biomes.
 *
 * @author Eben
 */
public enum BiomeSize {

    MICRO, SMALL, MEDIUM, LARGE, GIGANTIC;
    
        /**
     * Returns the BiomeSize that matches the given input, ignoring capitalization.
     * @param name
     * @return
     * @throws InvalidInputFormatException if the input does not match any sizes
     */
    public static BiomeSize getSizeForName(String name) throws InvalidInputFormatException {
        switch (name.toLowerCase()) {
            case "micro":
                return BiomeSize.MICRO;
            case "small":
                return BiomeSize.SMALL;
            case "medium":
                return BiomeSize.MEDIUM;
            case "large":
                return BiomeSize.LARGE;
            case "gigantic":
                return BiomeSize.GIGANTIC;
            default:
                throw new InvalidInputFormatException("Terrain Size " + name + " unrecognized.");
        }
    }
}
