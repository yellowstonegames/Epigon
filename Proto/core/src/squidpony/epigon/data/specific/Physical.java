package squidpony.epigon.data.specific;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidmath.Coord;
import squidpony.squidmath.OrderedMap;

import squidpony.epigon.data.EpiData;
import squidpony.epigon.data.ProbabilityTableEntry;
import squidpony.epigon.data.blueprint.ConditionBlueprint;
import squidpony.epigon.data.blueprint.PhysicalBlueprint;
import squidpony.epigon.data.generic.Modification;
import squidpony.epigon.data.generic.Skill;
import squidpony.epigon.data.mixin.Ammunition;
import squidpony.epigon.data.mixin.Container;
import squidpony.epigon.data.mixin.Creature;
import squidpony.epigon.data.mixin.Grouping;
import squidpony.epigon.data.mixin.Interactable;
import squidpony.epigon.data.mixin.Liquid;
import squidpony.epigon.data.mixin.Profession;
import squidpony.epigon.data.mixin.Legible;
import squidpony.epigon.data.mixin.Terrain;
import squidpony.epigon.data.mixin.Wearable;
import squidpony.epigon.data.mixin.Wieldable;
import squidpony.epigon.data.mixin.Zappable;
import squidpony.epigon.universe.Element;
import squidpony.epigon.universe.LiveValue;
import squidpony.epigon.universe.Rating;
import squidpony.epigon.universe.Stat;
import squidpony.squidmath.ProbabilityTable;

/**
 * Base class for all instantiated physical objects in the world.
 *
 * @author Eben Howard - http://squidpony.com
 */
public class Physical extends EpiData {

    public PhysicalBlueprint parent;
    public Set<PhysicalBlueprint> countsAs = new HashSet<>();
    public Set<Physical> createdFrom = new HashSet<>();//only important items should track this since it will cause object lifetimes to extend

    public char symbol;
    public SColor color;
    public double baseValue;
    public boolean large;

    public List<Modification> whenUsedAsMaterial = new ArrayList<>();
    public List<String> appliedModifications = new ArrayList<>();

    public OrderedMap<Element, LiveValue> passthroughResistances = new OrderedMap<>();
    public OrderedMap<Element, LiveValue> elementalDamageMultiplyer = new OrderedMap<>();

    public SColor lightEmitted;
    public LiveValue lightEmittedStrength;

    public List<Condition> conditions = new ArrayList<>();

    public EnumMap<Stat, LiveValue> stats = new EnumMap<>(Stat.class);
    public EnumMap<Stat, Rating> statProgression = new EnumMap<>(Stat.class);

    public List<Physical> inventory = new ArrayList<>();
    public Coord location;//world location

    public List<ProbabilityTable<ProbabilityTableEntry<PhysicalBlueprint>>> physicalDrops = new ArrayList<>();
    public OrderedMap<Element, List<ProbabilityTable<ProbabilityTableEntry<PhysicalBlueprint>>>> elementDrops = new OrderedMap<>();
    public OrderedMap<Skill, OrderedMap<Rating, String>> identification = new OrderedMap<>();

    public Creature creatureData;
    public Set<Profession> professions = new HashSet<>();

    public Ammunition ammunitionData;
    public Container containerData;
    public Grouping groupingData;
    public Interactable interactableData;
    public Liquid liquidData;
    public Legible legibleData;
    public Wearable wearableData;
    public Wieldable wieldableData;
    public Zappable zappableData;

    // Non-action mixins
    public Terrain terrainData;

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
        return blueprint.countsAs(blueprint);
    }
}
