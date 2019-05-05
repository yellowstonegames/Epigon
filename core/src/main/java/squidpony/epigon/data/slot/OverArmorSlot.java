package squidpony.epigon.data.slot;

import squidpony.epigon.ConstantKey;
import squidpony.epigon.util.Utilities;
import squidpony.squidmath.Coord;

public enum OverArmorSlot implements BodySlot {

    BACKPACK(2, 2),
    BANDOLIER(2, 3),
    BELT(2, 4);

    OverArmorSlot(int x, int y) {
        location = Coord.get(x, y);
        hash = ConstantKey.precomputeHash("slot.overarmor", ordinal());
    }

    /**
     * Meant to match the point on the {@link ClothingSlot} figure where the item is worn.
     */
    public Coord location;

    public final long hash;

    @Override
    public long hash64() {
        return hash;
    }

    @Override
    public int hash32() {
        return (int) (hash & 0xFFFFFFFFL);
    }

    @Override
    public String toString() {
        return Utilities.lower(name(), "_");
    }

    public static final OverArmorSlot[] ALL = values();

}
