package squidpony.epigon.universe;

import com.badlogic.gdx.graphics.Color;
import squidpony.squidgrid.gui.gdx.SColor;

/**
 * This enum represents a rating of none to high, in order, for use in skills and any other relative
 * values such as monster level and item worth.
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

    public Color color() {
        switch (this) {
            case NONE:
                return SColor.CHESTNUT_LEATHER_BROWN; // Dark grey
            case SLIGHT:
                return SColor.CW_GRAY; // Medium grey
            case TYPICAL:
                return SColor.CW_PALE_JADE; // White
            case GOOD:
                return SColor.EMERALD; // Green
            case HIGH:
                return SColor.CW_AZURE; // Blue
            case SUPERB:
                return SColor.CW_PURPLE; // Purple
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
    public Rating decrease() {
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
    public Rating increase() {
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

    public Rating applyRatingValueModification(RatingValueModification rvm) {
        if (rvm.overwrite != null) {
            return rvm.overwrite;
        }

        if (rvm.overwriteDecrease != null){
            return rvm.overwriteDecrease.lessThan(this) ? rvm.overwriteDecrease : this;
        }

        if (rvm.overwriteIncrease != null){
            return rvm.overwriteIncrease.greaterThan(this) ? rvm.overwriteIncrease : this;
        }

        Rating rating = this;
        if (rvm.deltaLevel != null) {
            int changes = 0;
            if (rvm.deltaLevel > 0) {
                while (rating.lessThan(rvm.deltaMax) && changes < rvm.deltaLevel) {
                    rating = rating.increase();
                    changes++;
                }
            } else if (rvm.deltaLevel < 0) {

                while (rating.greaterThan(rvm.deltaMin) && changes < rvm.deltaLevel * -1) {
                    rating = rating.decrease();
                    changes++;
                }
            }
        }
        return rating;
    }

    public boolean greaterThan(Rating other) {
        return this.ordinal() > other.ordinal();
    }

    public boolean lessThan(Rating other) {
        return this.ordinal() < other.ordinal();
    }

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
