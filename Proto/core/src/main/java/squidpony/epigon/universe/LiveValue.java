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
     * All values set to 0 and cannot be changed.
     */
    public static final LiveValue ZERO = new UnModifiableLiveValue(0.0);

    /**
     * All values set to 1 and cannot be changed.
     */
    public static final LiveValue ONE = new UnModifiableLiveValue(1.0);

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
        this.base = base;
        max = base;
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

    /**
     * Changes the stat by one turn's delta and inertia
     */
    public void tick() {
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
    }

    /**
     * Modifies this value in place by the values in the provided object.
     */
    public void modify(LiveValueModification mod) {
        base = mod.baseOverwrite == null ? base : mod.baseOverwrite;
        actual = mod.actualOverwrite == null ? actual : mod.actualOverwrite;
        max = mod.maxOverwrite == null ? max : mod.maxOverwrite;
        min = mod.minOverwrite == null ? min : mod.minOverwrite;
        delta = mod.deltaOverwrite == null ? delta : mod.deltaOverwrite;
        inertia = mod.inertiaOverwrite == null ? inertia : mod.inertiaOverwrite;
        stable = mod.stableOverwrite == null ? stable : mod.stableOverwrite;

        base = mod.baseAdditive == null ? base : mod.baseAdditive;
        actual = mod.actualAdditive == null ? actual : mod.actualAdditive;
        max = mod.maxAdditive == null ? max : mod.maxAdditive;
        min = mod.minAdditive == null ? min : mod.minAdditive;
        delta = mod.deltaAdditive == null ? delta : mod.deltaAdditive;
        inertia = mod.inertiaAdditive == null ? inertia : mod.inertiaAdditive;

        base = mod.baseMultiply == null ? base : mod.baseMultiply;
        actual = mod.actualMultiply == null ? actual : mod.actualMultiply;
        max = mod.maxMultiply == null ? max : mod.maxMultiply;
        min = mod.minMultiply == null ? min : mod.minMultiply;
        delta = mod.deltaMultiply == null ? delta : mod.deltaMultiply;
        inertia = mod.inertiaMultiply == null ? inertia : mod.inertiaMultiply;
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

    public void actual(double actual) {
        this.actual = actual;
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

    private static class UnModifiableLiveValue extends LiveValue {

        private UnModifiableLiveValue(double d) {
            super(d);
        }

        @Override
        public void tick() {
            // noop
        }

        @Override
        public void modify(LiveValueModification mod) {
            // noop
        }

        @Override
        public void base(double base) {
            // noop
        }

        @Override
        public void max(double max) {
            // noop
        }

        @Override
        public void min(double min) {
            // noop
        }

        @Override
        public void actual(double actual) {
            // noop
        }

        @Override
        public void delta(double delta) {
            // noop
        }

        @Override
        public void inertia(double inertia) {
            // noop
        }

        @Override
        public void stable(boolean stable) {
            // noop
        }
    }

}
