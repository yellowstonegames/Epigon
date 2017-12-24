package squidpony.epigon.universe;

import squidpony.squidmath.Coord;

/**
 * The areas where things can be worn.
 */
public enum ClothingSlot {
    /*
    ˬ
    Ὼ
  ╭┬╨┬╮
  ││#││
  ╽╞═╡╽
   │ │
   ┙ ┕
     */
    HEAD("ˬ", 2, 0),
    FACE("Ὼ", 2, 1),
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
    TORSO("│#│", 1, 3),
    WAIST("╞═╡", 1, 4),
    LEFT_UPPER_LEG("│", 1, 5),
    RIGHT_UPPER_LEG("│", 3, 5),
    //LEFT_LOWER_LEG(" ", 2, 0),
    //RIGHT_LOWER_LEG(" ", 2, 0),
    //LEFT_ANKLE(" ", 2, 0),
    //RIGHT_ANKLE(" ", 2, 0),
    LEFT_FOOT("┙", 1, 6),
    RIGHT_FOOT("┕", 3, 6);

    public static final int width = 5;
    public static final int height = 6;

    public String drawn;
    public Coord location;

    private ClothingSlot(String drawn, int x, int y) {
        this.drawn = drawn;
        location = Coord.get(x, y);
    }
}
