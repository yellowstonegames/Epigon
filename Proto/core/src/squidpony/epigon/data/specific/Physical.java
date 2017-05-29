package squidpony.epigon.data.specific;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Set;

import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidmath.Coord;
import squidpony.squidmath.OrderedMap;

import squidpony.epigon.data.EpiData;
import squidpony.epigon.data.blueprint.ConditionBlueprint;
import squidpony.epigon.data.blueprint.PhysicalBlueprint;
import squidpony.epigon.data.mixin.Ammunition;
import squidpony.epigon.data.mixin.Container;
import squidpony.epigon.data.mixin.Creature;
import squidpony.epigon.data.mixin.Grouping;
import squidpony.epigon.data.mixin.Interactable;
import squidpony.epigon.data.mixin.Liquid;
import squidpony.epigon.data.mixin.Profession;
import squidpony.epigon.data.mixin.Readable;
import squidpony.epigon.data.mixin.Wearable;
import squidpony.epigon.data.mixin.Wieldable;
import squidpony.epigon.data.mixin.Zappable;
import squidpony.epigon.universe.Element;
import squidpony.epigon.universe.LiveValue;
import squidpony.epigon.universe.Stat;

/**
 * Base class for all instantiated physical objects in the world.
 *
 * @author Eben Howard - http://squidpony.com
 */
public class Physical extends EpiData {

    public char symbol;
    public SColor color;
    public OrderedMap<Element, Double> passthroughResistances;
    public OrderedMap<Element, Double> elementalDamageMultiplyer;
    public EnumMap<Stat, LiveValue> stats = new EnumMap<>(Stat.class);
    public List<String> appliedModifications = new ArrayList<>();
    public List<Condition> conditions = new ArrayList<>();
    public Coord location;//world location

    public PhysicalBlueprint parent;
    public Set<Physical> createdFrom;//only important items should track this since it will cause object lifetimes to extend

    public Creature creatureData;
    public Set<Profession> professions;

    public Ammunition ammunitionData;
    public Container containerData;
    public Grouping groupingData;
    public Interactable interactableData;
    public Liquid liquidData;
    public Readable readableData;
    public Wearable wearableData;
    public Wieldable wieldableData;
    public Zappable zappableData;

    /**
     * Returns true if this Creature has the condition or a parent of the condition.
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

    public boolean hasParent(PhysicalBlueprint blueprint) {
        return blueprint.hasParent(blueprint);
    }
}
