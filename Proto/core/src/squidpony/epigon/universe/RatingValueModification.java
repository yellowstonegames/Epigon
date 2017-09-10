package squidpony.epigon.universe;

/**
 * A modification for a Rating. Contains nullable values, which have no effect if they are null.
 *
 * @author Eben Howard
 */
public class RatingValueModification {

    /**
     * Change the value to this without checking old value
     */
    public Rating overwrite;

    /**
     * Change to this value only if it is lower than the current value
     */
    public Rating overwriteDecrease;

    /**
     * Change to this value only if it is higher than the current value
     */
    public Rating overwriteIncrease;

    /**
     * Change the level by this many steps, no bounding (except for normal end of Rating scale)
     */
    public Integer deltaLevel;

    /**
     * The highest to go if the delta is positive.
     */
    public Rating deltaMax;

    /**
     * The highest to go if the delta is negative
     */
    public Rating deltaMin;
}
