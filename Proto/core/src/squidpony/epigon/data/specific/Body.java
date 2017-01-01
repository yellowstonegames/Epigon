package squidpony.epigon.data.specific;

import java.util.ArrayList;
import java.util.Map;

import squidpony.epigon.data.EpiData;
import squidpony.epigon.data.blueprints.BodyPartBlueprint;

/**
 * This class represents a specific creature's body.
 *
 * The condition of each part is tracked, along with the clothing, jewelry, and equipped items.
 *
 * @author Eben Howard - http://squidpony.com
 */
public class Body extends EpiData {

    public Map<BodyPartBlueprint, ArrayList<Physical>> clothingSlots;
    public Map<BodyPartBlueprint, ArrayList<Physical>> jewelrySlots;
    public Map<BodyPartBlueprint, ArrayList<Physical>> equipSlots;
    public Map<BodyPartBlueprint, Integer> condition; // current health for each part
}
