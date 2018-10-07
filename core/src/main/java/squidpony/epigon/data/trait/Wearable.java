package squidpony.epigon.data.trait;

import java.util.ArrayList;
import java.util.List;
import squidpony.epigon.data.slot.BodySlot;

/**
 * Holds the data for things that can be worn. Basically similar to wielding except that generally the
 * bonuses provided are from simply being placed on the body rather than through manipulation with hands.
 *
 * @author Eben
 */
public class Wearable {

    public boolean worn;

    public List<BodySlot> slotsUsed = new ArrayList<>();
}
