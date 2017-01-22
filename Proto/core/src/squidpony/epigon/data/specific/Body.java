package squidpony.epigon.data.specific;

import java.util.List;

import squidpony.epigon.data.EpiData;
import squidpony.epigon.data.blueprints.BodyPartBlueprint;
import squidpony.squidmath.OrderedMap;

/**
 * This class represents a specific creature's body.
 *
 * The condition of each part is tracked, along with the clothing, jewelry, and equipped items.
 *
 * @author Eben Howard - http://squidpony.com
 */
public class Body extends EpiData {

    public OrderedMap<BodyPartBlueprint, List<Physical>> clothingSlots;
    public OrderedMap<BodyPartBlueprint, List<Physical>> jewelrySlots;
    public OrderedMap<BodyPartBlueprint, List<Physical>> equipSlots;
    public OrderedMap<BodyPartBlueprint, Integer> condition; // current health for each part
}
