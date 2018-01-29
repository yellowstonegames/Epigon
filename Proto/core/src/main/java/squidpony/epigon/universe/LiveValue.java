package squidpony.epigon.universe;

/**
 * The bundle that represents a stat at a point in time. For all stats a higher number is better.
 *
 * Base value is the "all things being equal" value actual should be at. When all needs are fully
 * met and no external effects are in place actual should be at base.
 *
 * Max and min are the limits a value can go to no matter what. They limit the effects that external
 * changes can make on the value so the closer to base the min is the better and the higher max is
 * the better.
 *
 * Note that a stable value just cancels out a delta, it does not move actual towards base.
 *
 * @author Eben Howard
 */
public class LiveValue {

    /**
     * All values set to 0; can technically be changed but never should be.
     */
    public static final LiveValue ZERO = new LiveValue(0.0);

    /**
     * All values set to 1; can technically be changed but never should be.
     */
    public static final LiveValue ONE = new LiveValue(1.0);

    private double base;
    private double max;
    private double min;
    private double actual;
    private double delta; // change per turn
    private double inertia; // change in delta per turn
    private boolean stable; // when true inertia will not reverse the delta's sign when it crosses zero

    public LiveValue() {
        this(1.0);
    }

    public LiveValue(double base) {
        set(base);
    }

    public LiveValue(double base, double max) {
        base = Math.min(max, base);
        this.base = base;
        this.max = max;
        min = 0;
        actual = base;
        delta = 0;
        inertia = 0;
        stable = true;
    }

    public LiveValue(LiveValue other) {
        base = other.base;
        max = other.max;
        min = other.min;
        actual = other.actual;
        delta = other.delta;
        inertia = other.inertia;
        stable = other.stable;
    }

    public void set(double base)
    {
        this.base = base;
        max = base;
        min = 0;
        actual = base;
        delta = 0;
        inertia = 0;
        stable = true;
    }

    /**
     * Changes the stat by one turn's delta and inertia and returns the amount changed.
     */
    public double tick() {
        // NOTE - should inertia go before or after delta application?
        if (inertia != 0) {
            if (stable && (delta < 0) != (delta + inertia < 0)) {
                delta = 0;
            } else {
                delta += inertia;
            }
        }

        if (delta != 0) {
            actual += delta;
            //actual = Double.max(actual, min); // TODO - reconcile with rollover for stat damage
            actual = Double.min(actual, max);
        }

        return delta;
    }

    /**
     * Modifies this value in place by the values in the provided object.
     */
    public void modify(LiveValueModification mod) {
        // these default to null, so we need to check before overwriting
        base = mod.baseOverwrite == null ? base : mod.baseOverwrite;
        actual = mod.actualOverwrite == null ? actual : mod.actualOverwrite;
        max = mod.maxOverwrite == null ? max : mod.maxOverwrite;
        min = mod.minOverwrite == null ? min : mod.minOverwrite;
        delta = mod.deltaOverwrite == null ? delta : mod.deltaOverwrite;
        inertia = mod.inertiaOverwrite == null ? inertia : mod.inertiaOverwrite;
        stable = mod.stableOverwrite == null ? stable : mod.stableOverwrite;

        // these default to 1 if unassigned, so nothing changes if they are the default
        base *=    mod.baseMultiply;
        multiplyActual(mod.actualMultiply);
        max *=     mod.maxMultiply;
        min *=     mod.minMultiply;
        delta *=   mod.deltaMultiply;
        inertia *= mod.inertiaMultiply;

        // these default to 0 if unassigned, so this is similar to the above case
        base +=    mod.baseAdd;
        addActual(mod.actualAdd);
        max +=     mod.maxAdd;
        min +=     mod.minAdd;
        delta +=   mod.deltaAdd;
        inertia += mod.inertiaAdd;
    }

    public void addActual(double change)
    {
        actual = Math.min(Math.max(actual + change, min), max);
    }

    public void multiplyActual(double change)
    {
        actual = Math.min(Math.max(actual * change, min), max);
    }

    public double base() {
        return base;
    }

    public void base(double base) {
        this.base = base;
    }

    public double max() {
        return max;
    }

    public void max(double max) {
        this.max = max;
    }

    public double min() {
        return min;
    }

    public void min(double min) {
        this.min = min;
    }

    public double actual() {
        return actual;
    }

    /**
     * Sets the actual value, within the existing min and max bounds; set min and max before calling this
     * @param actual the double to set as the "actual" value; will be clamped to be within min and max
     */
    public void actual(double actual) {
        this.actual = Math.min(Math.max(actual, min), max);
    }

    public double delta() {
        return delta;
    }

    public void delta(double delta) {
        this.delta = delta;
    }

    public double inertia() {
        return inertia;
    }

    public void inertia(double inertia) {
        this.inertia = inertia;
    }

    public boolean stable() {
        return stable;
    }

    public void stable(boolean stable) {
        this.stable = stable;
    }
}
