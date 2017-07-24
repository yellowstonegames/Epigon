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
                return SColor.SLATE_GRAY; // Dark grey
            case SLIGHT:
                return SColor.CW_GRAY; // Medium grey
            case TYPICAL:
                return SColor.LIGHT_GRAY; // White
            case GOOD:
                return SColor.EMERALD; // Green
            case HIGH:
                return SColor.SKY_BLUE; // Blue
            case SUPERB:
                return SColor.LAVENDER_FLORAL; // Purple
            case AMAZING:
                return SColor.PUMPKIN; // Orange
            case ULTIMATE:
                return SColor.FUCSHIA_PINK;// Pink
            default:
                return SColor.WHITE;
        }
    }

    /**
     * Returns the next lowest Rating or itself if it is the lowest already.
     */
    public Rating decrease(){
        switch (this) {
            case NONE:
                return NONE;
            case SLIGHT:
                return NONE;
            case TYPICAL:
                return SLIGHT;
            case GOOD:
                return TYPICAL;
            case HIGH:
                return GOOD;
            case SUPERB:
                return HIGH;
            case AMAZING:
                return SUPERB;
            case ULTIMATE:
                return AMAZING;
            default:
                return NONE;
        }
    }

    /**
     * Returns the next highest Rating or itself if it is the highest already.
     */
    public Rating increase(){
        switch (this) {
            case NONE:
                return SLIGHT;
            case SLIGHT:
                return TYPICAL;
            case TYPICAL:
                return GOOD;
            case GOOD:
                return HIGH;
            case HIGH:
                return SUPERB;
            case SUPERB:
                return AMAZING;
            case AMAZING:
                return ULTIMATE;
            case ULTIMATE:
                return ULTIMATE;
            default:
                return NONE;
        }
    }

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
