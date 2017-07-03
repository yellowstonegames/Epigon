package squidpony.epigon.data.specific;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidmath.Coord;
import squidpony.squidmath.OrderedMap;
import squidpony.squidmath.ProbabilityTable;

import squidpony.epigon.data.EpiData;
import squidpony.epigon.data.ProbabilityTableEntry;
import squidpony.epigon.data.blueprint.ConditionBlueprint;
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

/**
 * Base class for all instantiated physical objects in the world.
 *
 * Three booleans in this class control how it can be used. These allow the use of this class as
 * both a blueprint style and instantiated style object. Here are some examples:
 *
 * The player: generic = false, unique = true, buildingBlock = false;
 *
 * Base rock: generic = true, unique = false, buildingBlock = true;
 *
 * A longsword: generic = false, unique = false, buildingBlock = true;
 *
 * @author Eben Howard - http://squidpony.com
 */
public class Physical extends EpiData {
    // operational bits for live objects
    public Coord location;
    public boolean attached; // cannot be removed for it's location (or inventory pile) without special means
    public boolean instantiated;
    public boolean aware; // knows where the player is
    public boolean wasSeen;

    // backing data
    public Physical parent;   
    public List<String> possibleAliases = new ArrayList<>(); // One of these is picked when instantiated (maybe choice locked by world region?)

    public Set<Physical> countsAs = new HashSet<>();
    public Set<Physical> createdFrom = new HashSet<>();//only important items should track this since it will cause object lifetimes to extend
    public boolean generic; // should not be directly used, only available as a building block object
    public boolean unique; // should only have one in existance of exactly this type
    public boolean buildingBlock; // can be used as a building block

    public char symbol;
    public SColor color;
    public double baseValue;
    public boolean large;

    public SColor lightEmitted;
    public LiveValue lightEmittedStrength;

    public List<Modification> whenUsedAsMaterial = new ArrayList<>();
    public List<Modification> modifications = new ArrayList<>(); // modifications applied both during instantation and through later effects
    public List<Modification> requiredModifications = new ArrayList<>(); // Must apply all of these on instantiation
    public List<Modification> optionalModifications = new ArrayList<>(); // Zero or more of these may be applied on instantiation

    public OrderedMap<Element, LiveValue> elementalDamageMultiplyer = new OrderedMap<>();

    public List<Condition> conditions = new ArrayList<>();

    public EnumMap<Stat, LiveValue> stats = new EnumMap<>(Stat.class); // initial stats on instantiation come from required modification
    public EnumMap<Stat, Rating> statProgression = new EnumMap<>(Stat.class);

    public List<Physical> inventory = new ArrayList<>();

    /**
     * The list of physical objects it drops on destruction no matter what the source.
     */
    public List<ProbabilityTable<ProbabilityTableEntry<Physical>>> physicalDrops = new ArrayList<>();

    /**
     * A list of what the item might drop when a given element is used on it. This is in addition to
     * the regular drop table.
     */
    public OrderedMap<Element, List<ProbabilityTable<ProbabilityTableEntry<Physical>>>> elementDrops = new OrderedMap<>();

    /**
     * If the given skill is possessed then a given string will be presented as the identification.
     * The description will be used if no matching skill is available.
     */
    public OrderedMap<Skill, OrderedMap<Rating, String>> identification = new OrderedMap<>();

    /**
     * The changes to this object (if any) that happen as its rarity is increased. As rarity
     * increases each lower level modification is also included, so a given level's result will be
     * the compounded application of all rarity levels up to and including that level's
     * modification.
     */
    public EnumMap<Rating, List<Modification>> rarityModifications = new EnumMap<>(Rating.class);

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

    public Physical() {
        stats.put(Stat.OPACITY, new LiveValue(1)); // default to opaque
    }

    public boolean countsAs(Physical blueprint) {
        if (this.equals(blueprint) || countsAs.contains(blueprint)) {
            return true;
        } else if (parent == null) {
            return false;
        }

        // Any parent either direct or through something it counts as will work
        return parent.countsAs(blueprint) || countsAs.stream().parallel().anyMatch(bp -> bp.countsAs(blueprint));
    }

    public boolean hasParent(Physical blueprint) {
        return blueprint.countsAs(blueprint);
    }

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
}
