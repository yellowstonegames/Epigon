package squidpony.epigon.data.blueprints;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import squidpony.epigon.actions.Action;
import squidpony.epigon.data.EpiData;
import squidpony.epigon.data.Stat;
import squidpony.epigon.data.generic.Element;
import squidpony.epigon.data.generic.Energy;
import squidpony.epigon.data.generic.Skill;

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
    public EnumMap<Stat, Integer> statChanges = new EnumMap<>(Stat.class);
    public HashMap<Skill, Integer> skillChanges = new HashMap<>();
    public HashMap<Element, Integer> elementResistanceChanges = new HashMap<>();
    public HashMap<TerrainBlueprint, Integer> movementChanges = new HashMap<>();
    public HashMap<Energy, Integer> maxEnergyChanges = new HashMap<>();
    public HashMap<Energy, Integer> periodicEnergyChanges = new HashMap<>();
    public HashMap<String, Integer> lightEmitted = new HashMap<>();
    public ArrayList<Action> tickActions = new ArrayList<>();
    public ArrayList<Action> wearsOffActions = new ArrayList<>();//what happens when it wears off
    public ArrayList<Action> cancelledActions = new ArrayList<>();//what happens when it's cancelled
    public ArrayList<ConditionBlueprint> conflicts = new ArrayList<>();//can't exist at the same time, new one cancels the old one
    public ArrayList<ConditionBlueprint> immunizes = new ArrayList<>();//can't exist at the same time, old one prevents the new one from being applies
    public ArrayList<ConditionBlueprint> suppresses = new ArrayList<>();//can both exist, but only newest one has effect

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
