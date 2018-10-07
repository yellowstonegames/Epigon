package squidpony.epigon.data.slot;

import squidpony.epigon.ConstantKey;
import squidpony.epigon.Utilities;

/**
 * The areas where things can be held.
 */
public enum WieldSlot implements BodySlot {
    LEFT_HAND,  // 0
    RIGHT_HAND, // 1
    HEAD,       // 2
    NECK,       // 3
    FEET,        // 4
    LEFT_FOOT,
    RIGHT_FOOT;

    WieldSlot()
    {
        hash = ConstantKey.precomputeHash("slot.wield", ordinal());
    }
    public final long hash;
    @Override
    public long hash64() {
        return hash;
    }
    @Override
    public int hash32() {
        return (int)(hash & 0xFFFFFFFFL);
    }

    @Override
    public String toString() {
        return Utilities.lower(name(), "_");
    }
    public static final WieldSlot[] ALL = values();
}
