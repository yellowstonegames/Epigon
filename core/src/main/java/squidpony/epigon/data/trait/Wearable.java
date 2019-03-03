package squidpony.epigon.data.trait;

import squidpony.epigon.data.slot.BodySlot;

import java.util.ArrayList;

/**
 * Holds the data for things that can be worn. Basically similar to wielding except that generally the
 * bonuses provided are from simply being placed on the body rather than through manipulation with hands.
 *
 * @author Eben
 */
public class Wearable {

    public boolean worn;

    public ArrayList<BodySlot> slotsUsed = new ArrayList<>();
}
