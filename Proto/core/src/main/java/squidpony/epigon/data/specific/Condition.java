package squidpony.epigon.data.specific;

import java.util.List;
import squidpony.epigon.data.EpiData;
import squidpony.epigon.data.blueprint.ConditionBlueprint;

/**
 * Represents a specific Condition attached to a single physical object.
 *
 * @author Eben Howard - http://squidpony.com
 */
public class Condition extends EpiData {

    public ConditionBlueprint parent;
    public int currentTick;
    public List<Condition> suppresedBys;//lists the specific conditions that are currently suppressing this one
    public Physical attachedTo;

    /**
     * Returns true if it has an ancestor that is the passed in blueprint.
     *
     * @param condition
     * @return
     */
    public boolean hasParent(ConditionBlueprint condition) {
        return parent.hasParent(condition);
    }
}
