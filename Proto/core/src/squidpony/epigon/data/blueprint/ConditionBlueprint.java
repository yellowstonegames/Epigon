package squidpony.epigon.data.blueprint;

import java.util.ArrayList;
import java.util.List;


import squidpony.epigon.actions.Action;
import squidpony.epigon.data.EpiData;
import squidpony.epigon.data.generic.Modification;
import squidpony.epigon.universe.Element;

/**
 * Blueprint for a Condition.
 *
 * The element associated with a Condition is inherited from the Effect that
 * applied it.
 *
 * Conditions are temporary (although perhaps indefinite) and can cause any
 * action to be indeterminately applied (such as stat and maximum energy changes
 * and ability availability) or applied on a frequency basis (such as energy
 * damage/healing or movement).
 *
 * When the condition wears off or is canceled there may be some list of actions
 * that take place. Being suppressed does not count as canceled, although if it
 * wears off while being suppressed the effects of wearing off take place.
 *
 * @author Eben Howard - http://squidpony.com
 */
public class ConditionBlueprint extends EpiData {

    public ConditionBlueprint parent;
    public int duration;//how many turns until it wears off
    public int period;//how often effects trigger -- 0 means it's constant
    public Element element;
    public Modification modification;
    public List<Action> tickActions = new ArrayList<>();
    public List<Action> wearsOffActions = new ArrayList<>();//what happens when it wears off
    public List<Action> canceledActions = new ArrayList<>();//what happens when it's cancelled
    public List<ConditionBlueprint> conflicts = new ArrayList<>();//can't exist at the same time, new one cancels the old one
    public List<ConditionBlueprint> immunizes = new ArrayList<>();//can't exist at the same time, old one prevents the new one from being applies
    public List<ConditionBlueprint> suppresses = new ArrayList<>();//can both exist, but only newest one has effect

    public boolean conflictsWith(ConditionBlueprint check) {
        ConditionBlueprint working = check;
        while (working != null) {//go until no more parent found
            if (conflicts.contains(working)) {
                return true;
            }
            working = working.parent;
        }
        return false;//no conflicts found
    }

    public boolean hasParent(ConditionBlueprint blueprint) {
        if (this == blueprint) {
            return true;
        } else if (this.parent == null) {
            return false;
        } else {
            return parent.hasParent(blueprint);
        }
    }
}
