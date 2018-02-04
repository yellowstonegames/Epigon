package squidpony.epigon.data.specific;

import squidpony.epigon.data.EpiData;
import squidpony.epigon.data.blueprint.ConditionBlueprint;
import squidpony.epigon.data.generic.Effect;
import squidpony.epigon.data.generic.Modification;
import squidpony.epigon.dm.RecipeMixer;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a specific Condition attached to a single physical object.
 *
 * @author Eben Howard - http://squidpony.com
 */
public class Condition extends EpiData {

    public ConditionBlueprint parent;
    public int currentTick = 0;
    public List<Condition> suppressedBys;//lists the specific conditions that are currently suppressing this one
    public Physical attachedTo;

    private Condition()
    {
    }
    public Condition(ConditionBlueprint blueprint)
    {
        this(blueprint, null);
    }

    public Condition(ConditionBlueprint blueprint, Physical attached){
        parent = blueprint;
        suppressedBys = new ArrayList<>();
        attach(attached);
    }
    public void attach(Physical attachTo)
    {
        if(attachedTo != null)
        {
            wearOff();
        }
        attachedTo = attachTo;
        RecipeMixer.applyModification(attachedTo, parent.modification);
        if(parent.changes != null)
            attachedTo.statEffects.add(parent.changes);
    }
    /**
     * Returns true if it has an ancestor that is the passed in blueprint.
     *
     * @param condition
     * @return
     */
    public boolean hasParent(ConditionBlueprint condition) {
        return parent.hasParent(condition);
    }

    public boolean wearOff()
    {
        if(attachedTo == null) return false;
        for (Effect e : parent.wearOffEffects)
        {
            for (Modification m : e.sourceModifications)
                RecipeMixer.applyModification(attachedTo, m);
        }
        if(parent.changes != null)
            attachedTo.statEffects.remove(parent.changes);
        attachedTo = null;
        return true;
    }
    public boolean update()
    {
        if(parent.period != 0 && currentTick % parent.period == 0)
        {
            for (Effect e : parent.tickEffects)
            {
                for (Modification m : e.sourceModifications)
                    RecipeMixer.applyModification(attachedTo, m);
            }
        }
        if(++currentTick >= parent.duration)
        {
            return wearOff();
        }
        return false;

    }
}
