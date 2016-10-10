package squidpony.data.specific;

import java.util.ArrayList;
import squidpony.data.EpiData;
import squidpony.data.blueprints.ConditionBlueprint;

/**
 * Represents a specific Condition attached to a single physical object.
 *
 * @author Eben Howard - http://squidpony.com - howard@squidpony.com
 */
public class Condition extends EpiData {

    public ConditionBlueprint parent;
    public int currentTick;
    public ArrayList<Condition> suppresedBys = new ArrayList<>();//lists the specific conditions that are currently suppressing this one
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
