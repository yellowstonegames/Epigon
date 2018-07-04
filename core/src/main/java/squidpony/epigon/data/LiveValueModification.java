package squidpony.epigon.data;

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

    public double baseAdd = 0;
    public double actualAdd = 0;
    public double maxAdd = 0;
    public double minAdd = 0;
    public double deltaAdd = 0;
    public double inertiaAdd = 0;

    public double baseMultiply    = 1;
    public double actualMultiply  = 1;
    public double maxMultiply     = 1;
    public double minMultiply     = 1;
    public double deltaMultiply   = 1;
    public double inertiaMultiply = 1;

    public LiveValueModification(){}

    /**
     * Creates a live value modification that overwrites a LiveValue's base, actual, and max values to the provided
     * value, overwrites its min value to 0, and overwrites its delta and inertia to 0 as well
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
    public static LiveValueModification add(double change)
    {
        LiveValueModification lvm = new LiveValueModification();
        lvm.actualAdd = change;
        return lvm;
    }

    public static LiveValueModification multiply(double change)
    {
        LiveValueModification lvm = new LiveValueModification();
        lvm.actualMultiply = change;
        return lvm;
    }

}
