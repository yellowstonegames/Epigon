package squidpony.epigon.data.blueprints;

import java.util.ArrayList;

import squidpony.epigon.data.EpiData;

/**
 * Node that holds a discrete body part.
 */
public class BodyPartBlueprint extends EpiData {

    public ArrayList<BodyPartBlueprint> attachedTo;
    public ArrayList<BodyPartBlueprint> contains; //the parts that are inside

    public boolean critical; //if it's critical than the creature dies when it's destroyed
    public double conditionMultiplier; // For when the part is stronger or weaker than the base material, defaults to 1.0
    public int clothingSlots;
    public int jewelrySlots;
    public int equipmentSlots;

}
