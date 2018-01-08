package squidpony.epigon.data;

/**
 * Used to populate a ProbabilityTable from data with optional elements.
 */
public class ProbabilityTableEntry<T extends EpiData> {

    public T item;
    public int minQuantity;
    public int maxQuantity;

    public ProbabilityTableEntry(T item) {
        this(item, 1, 1);
    }

    public ProbabilityTableEntry(T item, int minQuantity, int maxQuantity) {
        this.item = item;
        this.minQuantity = minQuantity;
        this.maxQuantity = maxQuantity;
    }

}
