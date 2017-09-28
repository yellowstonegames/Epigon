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
     * Creates a live value modification with all values set to the provided value and no delta or
     * inertia.
     *
     * @param value desired new base, max, and min value
     */
    public LiveValueModification(double value) {
        baseOverwrite = value;
        actualOverwrite = value;
        maxOverwrite = value;
        minOverwrite = value;
        deltaOverwrite = 0.0;
        inertiaOverwrite = 0.0;
        stableOverwrite = true;
    }
}
