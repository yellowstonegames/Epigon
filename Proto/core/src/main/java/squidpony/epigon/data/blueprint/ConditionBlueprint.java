package squidpony.epigon.data.blueprint;

import squidpony.epigon.data.EpiData;
import squidpony.epigon.data.generic.ChangeTable;
import squidpony.epigon.data.generic.Effect;
import squidpony.epigon.data.generic.Modification;
import squidpony.epigon.universe.CalcStat;
import squidpony.epigon.universe.Element;
import squidpony.squidmath.OrderedMap;
import squidpony.squidmath.UnorderedSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

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
    public int duration;//how many turns until it wears off -- 0 means it lasts until removed some other way
    public int period;//how often ticks trigger -- 0 means it's constant, 1 means once per round, 2 means every other round, etc.
    public Element element;
    public Modification modification;
    public ChangeTable changes;
    public List<Effect> tickEffects = new ArrayList<>();
    public List<Effect> wearOffEffects = new ArrayList<>();//what happens when it wears off
    public List<Effect> canceledEffects = new ArrayList<>();//what happens when it's cancelled
    public Set<ConditionBlueprint> conflicts = new UnorderedSet<>();//can't exist at the same time, new one cancels the old one
    public Set<ConditionBlueprint> immunizes = new UnorderedSet<>();//can't exist at the same time, old one prevents the new one from being applies
    public Set<ConditionBlueprint> suppresses = new UnorderedSet<>();//can both exist, but only newest one has effect

    public ConditionBlueprint(String name, int duration, int period, Element element, ChangeTable changes,
                              Collection<Effect> onTick, Collection<Effect> onWearOff, Collection<Effect> onCancel)
    {
        super();
        this.name = name;
        this.duration = duration;
        this.period = period;
        this.element = element;
        this.modification = new Modification();
        this.changes = changes;
        if(onTick != null)
            tickEffects.addAll(onTick);
        if(onWearOff != null)
            wearOffEffects.addAll(onWearOff);
        if(onCancel != null)
            canceledEffects.addAll(onCancel);

    }
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
    public static OrderedMap<String, ConditionBlueprint> CONDITIONS = OrderedMap.makeMap(
            "Confounded", new ConditionBlueprint("Confounded", 3, 0, Element.BLUNT, ChangeTable.makeCT(CalcStat.PRECISION, '-', 4.0, CalcStat.INFLUENCE, '-', 4.0, CalcStat.CRIT, '-', 2.0), null, null, null)
    );
}
