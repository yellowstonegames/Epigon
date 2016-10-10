package squidpony.epigon.data.specific;

import java.util.ArrayList;
import java.util.HashMap;
import squidpony.epigon.data.EpiData;
import squidpony.epigon.data.blueprints.BodyBlueprint;

/**
 * This class represents a specific creature's body.
 *
 * The condition of each part is tracked, along with the clothing, jewelry, and
 * equipped items.
 *
 * @author Eben Howard - http://squidpony.com
 */
public class Body extends EpiData {

    public HashMap<BodyBlueprint.BodyPartBlueprint, ArrayList<Item>> clothingSlots = new HashMap<>();
    public HashMap<BodyBlueprint.BodyPartBlueprint, ArrayList<Item>> jewelrySlots = new HashMap<>();
    public HashMap<BodyBlueprint.BodyPartBlueprint, ArrayList<Item>> equipSlots = new HashMap<>();
    public HashMap<BodyBlueprint.BodyPartBlueprint, Integer> condition = new HashMap<>();
}
