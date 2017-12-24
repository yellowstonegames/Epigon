package squidpony.epigon.universe;

import squidpony.squidmath.Coord;

/**
 * The areas where things can be worn.
 */
public enum ClothingSlot {
    /*
    Ω
  ╭┬╨┬╮
  ││#││
  ╽╞═╡╽
   │ │
   ┙ ┕
     */
    HEAD("Ω", 2, 0),
    //FACE("Ω", 2, 0),
    NECK("╨", 2, 1),
    LEFT_SHOULDER("┬", 1, 1),
    RIGHT_SHOULDER("┬", 3, 1),
    LEFT_UPPER_ARM("╭", 0, 1),
    RIGHT_UPPER_ARM("╮", 4, 1),
    LEFT_LOWER_ARM("│", 0, 2),
    RIGHT_LOWER_ARM("│", 4, 2),
    //LEFT_WRIST(" ", 2, 0),
    //RIGHT_WRIST(" ", 2, 0),
    LEFT_HAND("╽", 0, 3),
    RIGHT_HAND("╽", 4, 3),
    TORSO("│#│", 1, 2),
    WAIST("╞═╡", 1, 3),
    LEFT_UPPER_LEG("│", 1, 4),
    RIGHT_UPPER_LEG("│", 3, 4),
    //LEFT_LOWER_LEG(" ", 2, 0),
    //RIGHT_LOWER_LEG(" ", 2, 0),
    //LEFT_ANKLE(" ", 2, 0),
    //RIGHT_ANKLE(" ", 2, 0),
    LEFT_FOOT("┙", 1, 5),
    RIGHT_FOOT("┕", 3, 5);

    public static final int width = 5;
    public static final int height = 5;

    public String drawn;
    public Coord location;

    private ClothingSlot(String drawn, int x, int y) {
        this.drawn = drawn;
        location = Coord.get(x, y);
    }
}
