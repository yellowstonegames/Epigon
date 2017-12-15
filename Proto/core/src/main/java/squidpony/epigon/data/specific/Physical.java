package squidpony.epigon.data.specific;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import squidpony.epigon.data.EpiData;
import squidpony.epigon.data.ProbabilityTableEntry;
import squidpony.epigon.data.blueprint.ConditionBlueprint;
import squidpony.epigon.data.generic.Modification;
import squidpony.epigon.data.generic.Skill;
import squidpony.epigon.data.mixin.*;
import squidpony.epigon.universe.Element;
import squidpony.epigon.universe.LiveValue;
import squidpony.epigon.universe.Rating;
import squidpony.epigon.universe.Stat;
import squidpony.squidgrid.gui.gdx.TextCellFactory;
import squidpony.squidmath.*;

import java.util.*;

import static squidpony.epigon.Epigon.chaos;

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
    public static final Physical basePhysical = new Physical();
    static {
        basePhysical.generic = true;
        basePhysical.unique = true;
    }
    public static final int PRECISION = 0, DAMAGE = 1, CRIT = 2, INFLUENCE = 3,
            EVASION = 4, DEFENSE = 5, STEALTH = 6, LUCK = 7, RANGE = 8, AREA= 9, PREPARE = 10;

    // operational bits for live objects
    public Coord location;
    public boolean attached; // cannot be removed for it's location (or inventory pile) without special means
    public boolean instantiated;
    public boolean aware; // knows where the player is
    public boolean wasSeen;
    public TextCellFactory.Glyph appearance; // for things that move, we should use Glyph, which is a kind of Actor

    // backing data
    public Physical parent;   
    public List<String> possibleAliases = new ArrayList<>(); // One of these is picked when instantiated (maybe choice locked by world region?)

    public Set<Physical> countsAs = new HashSet<>();
    public Set<Physical> createdFrom = new HashSet<>();//only important items should track this since it will cause object lifetimes to extend
    public boolean generic; // should not be directly used, only available as a building block object
    public boolean unique; // should only have one in existence of exactly this type
    public boolean buildingBlock; // can be used as a building block

    public char symbol;
    public float color;
    public double baseValue;
    public boolean blocking;

    public float lightEmitted;
    public LiveValue lightEmittedStrength;

    public List<Modification> whenUsedAsMaterial = new ArrayList<>();
    public List<Modification> modifications = new ArrayList<>(); // modifications applied both during instantiation and through later effects
    public List<Modification> requiredModifications = new ArrayList<>(); // Must apply all of these on instantiation
    public List<Modification> optionalModifications = new ArrayList<>(); // Zero or more of these may be applied on instantiation

    public OrderedMap<Element, LiveValue> elementalDamageMultiplier = new OrderedMap<>();

    public List<Condition> conditions = new ArrayList<>();

    public EnumOrderedMap<Stat, LiveValue> stats = new EnumOrderedMap<>(Stat.class); // initial stats on instantiation come from required modification
    public EnumOrderedMap<Stat, Rating> statProgression = new EnumOrderedMap<>(Stat.class);
    public int[] calcStats = new int[11];
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
    public EnumOrderedMap<Rating, List<Modification>> rarityModifications = new EnumOrderedMap<>(Rating.class);

    public Creature creatureData;

    public Ammunition ammunitionData;
    public Container containerData;
    public Grouping groupingData;
    public Interactable interactableData;
    public Liquid liquidData;
    public Legible legibleData;
    public Wearable wearableData;
    public Weapon weaponData;
    public Zappable zappableData;

    // Non-action mixins
    public Terrain terrainData;

    public Physical() {
        stats.put(Stat.OPACITY, new LiveValue(1)); // default to opaque
        stats.put(Stat.MOBILITY, new LiveValue(0)); // default to not being able to move
    }
    public static Physical makeBasic(String name, char symbol, Color color)
    {
        return makeBasic(name, symbol, color.toFloatBits());
    }

    public static Physical makeBasic(String name, char symbol, float color)
    {
        Physical p = new Physical();
        p.name = name;
        p.symbol = symbol;
        p.color = color;
        return p;
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
            if (c.suppressedBys.isEmpty() && c.parent != null) {//only active conditions can provide immunity //TODO -- ensure that when they become unsuppressed they remove things they provide immunity against
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
    public void calculateStats() {
        LiveValue lv;
        int current;
        Arrays.fill(calcStats, 0);
        if ((lv = stats.get(Stat.AIM)) != null) {
            current = (int) lv.actual();
            calcStats[PRECISION] += current;
            calcStats[CRIT] += current >> 1;
        }
        if ((lv = stats.get(Stat.IMPACT)) != null) {
            current = (int) lv.actual();
            calcStats[DAMAGE] += current;
            calcStats[CRIT] += current >> 1;
        }
        if ((lv = stats.get(Stat.DODGE)) != null) {
            current = (int) lv.actual();
            calcStats[EVASION] += current;
            calcStats[STEALTH] += current >> 1;
        }
        if ((lv = stats.get(Stat.TOUGHNESS)) != null) {
            current = (int) lv.actual();
            calcStats[DEFENSE] += current;
            calcStats[DAMAGE] += current >> 1;
        }
        if ((lv = stats.get(Stat.POTENCY)) != null) {
            current = (int) lv.actual();
            calcStats[INFLUENCE] += current;
            calcStats[PRECISION] += current >> 1;
        }
        if ((lv = stats.get(Stat.ATTUNEMENT)) != null) {
            current = (int) lv.actual();
            calcStats[LUCK] += current;
            calcStats[STEALTH] += current >> 1;
        }
    }
    public boolean hitRoll(Physical target) {
        if(target.creatureData == null)
            return true;

        return (67 + 5 * (calcStats[PRECISION] + weaponData.calcStats[PRECISION] - target.calcStats[EVASION] - target.weaponData.calcStats[EVASION])) >= chaos.next(7);
    }
    public double hitProbability(Physical target)
    {
        return (67 + 5 * (calcStats[PRECISION] + weaponData.calcStats[PRECISION] - target.calcStats[EVASION] - target.weaponData.calcStats[EVASION])) * 0x1p-7;
    }

    public int damageRoll(Physical target) {
        long r = chaos.nextLong();
        int amt = Math.min(0, MathUtils.floor((NumberTools.randomFloatCurved(r) * 0.4f - 0.45f) * (calcStats[DAMAGE] + weaponData.calcStats[DAMAGE]) +
                (NumberTools.randomFloatCurved(r + 1) * 0.3f + 0.35f) * (target.calcStats[DEFENSE] + target.weaponData.calcStats[DEFENSE])));
        target.stats.get(Stat.VIGOR).addActual(amt);
        return -amt;
    }

}
