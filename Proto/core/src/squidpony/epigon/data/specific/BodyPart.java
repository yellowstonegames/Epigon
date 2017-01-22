package squidpony.epigon.data.specific;

import java.util.List;

import squidpony.epigon.data.EpiData;
import squidpony.epigon.data.blueprints.BodyPartBlueprint;

/**
 * Node that holds a discrete body part.
 */
public class BodyPart extends EpiData {

    public BodyPartBlueprint parent;

    public List<BodyPart> attachedTo;
    public List<BodyPart> contains; //the parts that are inside

    public boolean critical; //if it's critical than the creature dies when it's destroyed
    public double conditionMultiplier; // For when the part is stronger or weaker than the base material, defaults to 1.0

}
