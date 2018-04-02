package squidpony.epigon.data;

import squidpony.epigon.data.quality.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a specific Condition attached to a single physical object.
 * TODO: This should go away and be replaced with expanded features in Modification
 * @author Eben Howard - http://squidpony.com
 */
public class Condition extends EpiData {

    public ConditionBlueprint parent;
    public int currentTick = 0;
    public List<Condition> suppressedBys;//lists the specific conditions that are currently suppressing this one
    public Physical attachedTo;
    public Element overrideElement;
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

    public Condition(ConditionBlueprint blueprint, Physical attached, Element element){
        parent = blueprint;
        suppressedBys = new ArrayList<>();
        overrideElement = element;
        attach(attached);
    }
    public void attach(Physical attachTo)
    {
        if(attachedTo != null)
        {
            wearOff();
        }
        attachedTo = attachTo;
        //RecipeMixer.applyModification(attachedTo, parent.modification);
        if(parent.overlaySymbol != null) {
            attachedTo.overlaySymbol = parent.overlaySymbol;
            attachedTo.overlayColor = overrideElement == null ? -0x1.0101p126F : overrideElement.floatColor; // SColor.GRAY
        }
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
        for (Modification m : parent.wearOffEffects)
        {             
            RecipeMixer.applyModification(attachedTo, m);
        }
        if(parent.changes != null)
            attachedTo.statEffects.remove(parent.changes);
        attachedTo.overlaySymbol = null;
        attachedTo = null;
        return true;
    }
    public boolean update()
    {
        if(parent.period != 0 && currentTick % parent.period == 0) {
            for (Modification m : parent.tickEffects)
            {
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
