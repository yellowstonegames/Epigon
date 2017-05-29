package squidpony.epigon.universe;

/**
 * The bundle that represents a stat at a point in time.
 *
 * @author Eben Howard
 */
public class LiveValue {

    public double base;
    public double max;
    public double min;
    public double actual;
    public double delta; // change per turn
    public double inertia; // change in delta per turn
    public boolean stable; // when true inertia will not reverse the delta's sign whne it crosses zero

    public LiveValue(double base) {
        this.base = base;
        max = base;
        min = 0;
        actual = base;
        delta = 0;
        inertia = 0;
        stable = true;
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
            actual = Double.max(actual, min);
            actual = Double.min(actual, max);
        }
    }
}
