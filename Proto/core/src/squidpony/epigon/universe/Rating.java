package squidpony.epigon.universe;

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
                return "not";
            case SLIGHT:
                return "slightly";
            case TYPICAL:
                return "typically";
            case GOOD:
                return "well";
            case HIGH:
                return "highly";
            case SUPERB:
                return "superbly";
            case AMAZING:
                return "amazingly";
            case ULTIMATE:
                return "ultimately";
            default:
                return "awkwardly";
        }
    }

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
