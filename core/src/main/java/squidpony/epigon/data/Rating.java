package squidpony.epigon.data;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import squidpony.squidgrid.gui.gdx.SColor;

/**
 * This enum represents a rating of none to high, in order, for use in skills and any other relative
 * values such as monster level and item worth.
 *
 * Colors based on classic MMORPG color range, modified somewhat for clarity between elements.
 */
public enum Rating {
    //0     1       2        3     4     5       6        7
    NONE, SLIGHT, TYPICAL, GOOD, HIGH, SUPERB, AMAZING, ULTIMATE;

    public static final Rating[] allRatings = values();

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
            case SLIGHT:
                return SColor.CW_PALE_ORANGE; // Tan/beige
            case TYPICAL:
                return SColor.CW_PALE_JADE; // Whitish-green
            case GOOD:
                return SColor.FOREST_GREEN; // Deeper green
            case HIGH:
                return SColor.CW_AZURE; // Blue
            case SUPERB:
                return SColor.CW_LIGHT_INDIGO; // Purple
            case AMAZING:
                return SColor.PUMPKIN; // Orange
            case ULTIMATE:
                return SColor.FUCHSIA_PINK;// Pink
            case NONE:
            default:
                return SColor.ALOEWOOD_BROWN; // Dark grey; not actually brown!
        }
    }

    /**
     * Returns the next lowest Rating or itself if it is the lowest already.
     */
    public Rating decrease() {
        switch (this) {
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

        if (rvm.overwriteDecrease != null) {
            return rvm.overwriteDecrease.lessThan(this) ? rvm.overwriteDecrease : this;
        }

        if (rvm.overwriteIncrease != null) {
            return rvm.overwriteIncrease.greaterThan(this) ? rvm.overwriteIncrease : this;
        }

        if (rvm.deltaLevel != null) {
            return allRatings[MathUtils.clamp(
                ordinal() + rvm.deltaLevel,
                Math.max(0, rvm.deltaMin.ordinal()),
                Math.min(7, rvm.deltaMax.ordinal()))];
        }
        return this;
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
