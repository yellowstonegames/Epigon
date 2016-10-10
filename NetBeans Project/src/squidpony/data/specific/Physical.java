package squidpony.data.specific;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import squidpony.data.EpiData;
import squidpony.data.blueprints.ConditionBlueprint;
import squidpony.data.blueprints.ModificationBlueprint;

/**
 * Base class for all instantiated physical objects in the world.
 *
 * @author Eben Howard - http://squidpony.com - howard@squidpony.com
 */
public class Physical extends EpiData implements Cloneable {

    public ArrayList<ModificationBlueprint> appliedModifications;
    public HashMap<String, Float> resistances;
    public ArrayList<Condition> conditions = new ArrayList<>();
    public Point location;//world location

    /**
     * Returns true if this Creature has the condition or a parent of the
     * condition.
     *
     * @param condition
     * @return
     */
    public boolean hasCondition(ConditionBlueprint condition) {
        for (Condition c : conditions) {
            if (c.hasParent(condition)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if the given condition cannot be applied due to immunities.
     *
     * @param condition
     * @return
     */
    public boolean immune(ConditionBlueprint condition) {
        for (Condition c : conditions) {
            if (c.suppresedBys.isEmpty() && c.parent != null) {//only active conditions can provide immunity //TODO -- ensure that when they become unsuppresed they remove things they provide immunity against
                for (ConditionBlueprint cb : c.parent.immunizes) {
                    if (cb.hasParent(condition)) {
                        return true;//found an immunity
                    }
                }
            }
        }
        return false;//no immunities found
    }

    /**
     * Attempts to apply the provided Condition to this creature.
     *
     * Returns true if it was successfully applied and false if not.
     *
     * @param condition
     * @return
     */
    public boolean applyCondition(Condition condition) {
        boolean conflicted = false;
        if (immune(condition.parent)) {//make sure it's not immune
            conflicted = true;
        } else {
            for (Condition c : conditions) {
                if (!c.parent.conflictsWith(condition.parent)) {
                    conflicted = true;
                    break;
                }
            }
        }
        if (!conflicted) {
            conditions.add(condition);
            condition.attachedTo = this;
            return true;
        }

        return false;//can't be applied
    }

    @Override
    public Physical clone() {
        return (Physical) super.clone();
    }
}
