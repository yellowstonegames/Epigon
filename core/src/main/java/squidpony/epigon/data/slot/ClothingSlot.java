package squidpony.epigon.data.slot;

import squidpony.epigon.ConstantKey;
import squidpony.epigon.Utilities;
import squidpony.squidmath.Coord;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * The areas where things can be worn.
 */
public enum ClothingSlot implements BodySlot {
    /*
    ˬ
    ◯   //Ὼ
  ╭┬╨┬╮
  ││▒││
  ╽╞═╡╽
   │ │
   ┙ ┕
     */
    HEAD("ˬ", 2, 0),
    FACE("◯", 2, 1),
    NECK("╨", 2, 2),
    LEFT_SHOULDER("┬", 1, 2),
    RIGHT_SHOULDER("┬", 3, 2),
    LEFT_UPPER_ARM("╭", 0, 2),
    RIGHT_UPPER_ARM("╮", 4, 2),
    LEFT_LOWER_ARM("│", 0, 3),
    RIGHT_LOWER_ARM("│", 4, 3),
    //LEFT_WRIST(" ", 2, 0),
    //RIGHT_WRIST(" ", 2, 0),
    LEFT_HAND("╽", 0, 4),
    RIGHT_HAND("╽", 4, 4),
    TORSO("│▒│", 1, 3),
    WAIST("╞═╡", 1, 4),
    LEFT_LEG("│", 1, 5),
    RIGHT_LEG("│", 3, 5),
    //LEFT_LOWER_LEG(" ", 2, 0),
    //RIGHT_LOWER_LEG(" ", 2, 0),
    //LEFT_ANKLE(" ", 2, 0),
    //RIGHT_ANKLE(" ", 2, 0),
    LEFT_FOOT("┙", 1, 6),
    RIGHT_FOOT("┕", 3, 6);

    public static final int width = 5;
    public static final int height = 7;

    public String drawn;
    public Coord location;

    ClothingSlot(String drawn, int x, int y) {
        this.drawn = drawn;
        location = Coord.get(x, y);
        hash = ConstantKey.precomputeHash("slot.clothing", ordinal());
    }
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
    public static final ClothingSlot[] ALL = values();

    /**
     * Returns the short code for the slot name.
     */
    public String shortCode() {
        return Arrays.stream(name().split("_"))
            .map(s -> s.substring(0, 1))
            .collect(Collectors.joining());
    }
}
