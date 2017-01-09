package squidpony.epigon.data.blueprints;

import java.util.List;

import squidpony.epigon.data.EpiData;

/**
 * This class represents a type of body that some creature might have.
 *
 * The internal class BodyPartBlueprint is used to create a graph of the body.
 *
 * If a critical body part is destroyed, then the creature dies. If a critical part is part of a
 * creature that becomes detached from the primary part then the creature dies.
 *
 * When childing a Body the child can only add to the body, not remove.
 *
 * @author Eben Howard - http://squidpony.com
 */
public class BodyBlueprint extends EpiData {
    public BodyPartBlueprint primaryPart;
    public List<BodyPartBlueprint> otherParts;
}
