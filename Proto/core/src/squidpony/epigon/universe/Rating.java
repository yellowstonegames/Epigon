package squidpony.epigon.universe;

import squidpony.squidgrid.gui.gdx.SColor;

/**
 * This enum represents a rating of none to high, in order, for use in skills
 * and any other relative values such as monster level and item worth.
 *
 * @author Eben Howard - http://squidpony.com
 */
public enum Rating {

    NONE, SLIGHT, TYPICAL, GOOD, HIGH, SUPERB, AMAZING, ULTIMATE;

    public String asAdverb() {
        switch (this) {
            case NONE:
                return "not"; // Dark grey
            case SLIGHT:
                return "slightly"; // Medium grey
            case TYPICAL:
                return "typically"; // White
            case GOOD:
                return "well";  // Green
            case HIGH:
                return "highly"; // Blue
            case SUPERB:
                return "superbly"; // Purple
            case AMAZING:
                return "amazingly"; // Orange
            case ULTIMATE:
                return "ultimately"; // Pink
            default:
                return "awkwardly";
        }
    }

    public SColor color(){
        switch (this) {
            case NONE:
                return SColor.DARK_GRAY; // Dark grey
            case SLIGHT:
                return SColor.GRAY; // Medium grey
            case TYPICAL:
                return SColor.LIGHT_GRAY; // White
            case GOOD:
                return SColor.EMERALD; // Green
            case HIGH:
                return SColor.SKY_BLUE; // Blue
            case SUPERB:
                return SColor.LAVENDER_FLORAL; // Purple
            case AMAZING:
                return SColor.SAFETY_ORANGE; // Orange
            case ULTIMATE:
                return SColor.FUCSHIA_PINK;// Pink
            default:
                return SColor.WHITE;
        }
    }

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
