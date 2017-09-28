package squidpony.epigon.data;

/**
 * Used to populate a ProbabilityTable from data with optional elements.
 */
public class ProbabilityTableEntry<T extends EpiData> {

    public T item;
    public int minQuantity;
    public int maxQuantity;
}
