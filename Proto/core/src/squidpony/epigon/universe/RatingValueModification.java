package squidpony.epigon.universe;

/**
 * A modification for a Rating. Contains nullable values, which have no effect if they are null.
 *
 * @author Eben Howard
 */
public class RatingValueModification {
    public Rating overwrite;
    public Rating overwriteDecrease;
    public Rating overwriteIncrease;
    public Integer deltaLevel;
}
