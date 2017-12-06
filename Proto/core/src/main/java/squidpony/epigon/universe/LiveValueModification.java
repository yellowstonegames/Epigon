package squidpony.epigon.universe;

/**
 * A modification for a live value. Contains nullable values, which have no effect if they are null.
 *
 * @author Eben Howard
 */
public class LiveValueModification {

    public Double baseOverwrite;
    public Double actualOverwrite;
    public Double maxOverwrite;
    public Double minOverwrite;
    public Double deltaOverwrite;
    public Double inertiaOverwrite;
    public Boolean stableOverwrite;

    public Double baseAdditive;
    public Double actualAdditive;
    public Double maxAdditive;
    public Double minAdditive;
    public Double deltaAdditive;
    public Double inertiaAdditive;

    public Double baseMultiply;
    public Double actualMultiply;
    public Double maxMultiply;
    public Double minMultiply;
    public Double deltaMultiply;
    public Double inertiaMultiply;

    public LiveValueModification(){}

    /**
     * Creates a live value modification with base, actual, and max values set to the provided value, min value set to
     * 0, and no delta or inertia (also 0).
     *
     * @param value desired new actual, base, and max value
     */
    public LiveValueModification(double value) {
        baseOverwrite = value;
        actualOverwrite = value;
        maxOverwrite = value;
        minOverwrite = 0.0;
        deltaOverwrite = 0.0;
        inertiaOverwrite = 0.0;
        stableOverwrite = true;
    }
}
