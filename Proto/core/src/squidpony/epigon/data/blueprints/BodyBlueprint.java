package squidpony.epigon.data.blueprints;

import java.util.ArrayList;
import java.util.HashMap;
import squidpony.epigon.data.EpiData;

/**
 * This class represents a type of body that some creature might have.
 *
 * The internal class BodyPartBlueprint is used to create a graph of the body.
 *
 * If a critical body part is destroyed, then the creature dies. If a critical
 * part is part of a creature that becomes detached from the primary part then
 * the creature dies.
 *
 * The clothing, jewelry, and equip slot lists must contain only BodyParts that
 * are defined for this BodyBlueprint. The Integer portions of these indicate
 * how many of such items may occupy the given body part.
 *
 * When subclassing a Body the children can only add to the body, not remove.
 *
 * @author Eben Howard - http://squidpony.com
 */
public class BodyBlueprint extends EpiData {

    public BodyPartBlueprint primaryPart;
    public HashMap<BodyPartBlueprint, Integer> clothingSlots = new HashMap<>();
    public HashMap<BodyPartBlueprint, Integer> jewelrySlots = new HashMap<>();
    public HashMap<BodyPartBlueprint, Integer> equipSlots = new HashMap<>();

    /**
     * Node that holds a discrete body part.
     */
    public class BodyPartBlueprint extends EpiData{

        public ArrayList<BodyPartBlueprint> attachedTo = new ArrayList<>();
        public ArrayList<BodyPartBlueprint> contains = new ArrayList<>();//the parts that are inside
        public boolean critical;//if it's critical than the creature dies when it's destroyed
        public int maxCondition;
        public int clothingSlots, jewelrySlots, equipmentSlots;
    }
    
    
}
