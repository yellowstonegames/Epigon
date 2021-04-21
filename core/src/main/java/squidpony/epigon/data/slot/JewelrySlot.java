package squidpony.epigon.data.slot;

import squidpony.epigon.util.ConstantKey;
import squidpony.epigon.util.Utilities;
import squidpony.squidmath.Coord;

public enum JewelrySlot implements BodySlot {

        /*
  01234
 0  ˬ
 1  ◯
 2╭┬╨┬╮
 3││▒││
 4╽╞═╡╽
 5 │ │
 6 ┙ ┕
        */


    BROW(2,0),
    LEFT_EARRING(1,1),
    RIGHT_EARRING(3,1),
    MASK(2,1),
    NECKLACE(2,2),
    LEFT_UPPER_ARM(0,2),
    RIGHT_UPPER_ARM(4,2),
    LEFT_WRIST(0,3),
    RIGHT_WRIST(4,3),
    LEFT_RING(0,4),
    RIGHT_RING(4,4),
    LEFT_ANKLE(1,6),
    RIGHT_ANKLE(3,6);

    JewelrySlot(int x, int y)
    {
        location = Coord.get(x, y);
        hash = ConstantKey.precomputeHash("slot.jewelry", ordinal());
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
        return (int)(hash);
    }

    @Override
    public String toString() {
        return Utilities.lower(name(), "_");
    }
    
    public static final JewelrySlot[] ALL = values();

}
