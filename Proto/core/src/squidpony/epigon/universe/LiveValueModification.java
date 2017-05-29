package squidpony.epigon.universe;

/**
 * A modification for a live value. Contains nullable values, which have no effect if they are null.
 *
 * @author Eben Howard
 */
public class LiveValueModification {
    public Double baseOverwrite;
    public Double maxOverwrite;
    public Double minOverwrite;
    public Double deltaOverwrite;
    public Double inertiaOverwrite;
    public Boolean stableOverwrite;

    public Double baseAdditive;
    public Double maxAdditive;
    public Double minAdditive;
    public Double deltaAdditive;
    public Double inertiaAdditive;

    public Double baseMultiply;
    public Double maxMultiply;
    public Double minMultiply;
    public Double deltaMultiply;
    public Double inertiaMultiply;
}
