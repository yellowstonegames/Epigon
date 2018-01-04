package squidpony.epigon.data.specific;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import squidpony.epigon.data.EpiData;
import squidpony.epigon.data.ProbabilityTableEntry;
import squidpony.epigon.data.blueprint.ConditionBlueprint;
import squidpony.epigon.data.generic.Modification;
import squidpony.epigon.data.generic.Skill;
import squidpony.epigon.data.mixin.*;
import squidpony.epigon.universe.*;
import squidpony.squidgrid.gui.gdx.TextCellFactory;
import squidpony.squidmath.*;

import java.util.*;

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

    public StatefulRNG chaos;

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

    /*
    * The rarity level applied.
    */
    public Rating rarity;

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
    public Weapon weaponData, unarmedData;
    public Zappable zappableData;

    // Non-action mixins
    public Terrain terrainData;

    public Physical() {
        stats.put(Stat.OPACITY, new LiveValue(1)); // default to opaque
        stats.put(Stat.MOBILITY, new LiveValue(0)); // default to not being able to move
        chaos = new StatefulRNG();
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

    /**
     * Takes the item out of inventory and places it into equipped areas. Anything already equipped in
     * the areas goes back into inventory.
     *
     * @param item The item to equip
     * @param slots All the slots that will be filled when the item is equipped
     */
    public void equip(Physical item, List<WieldSlot> slots) {
        if (creatureData == null) {
            System.err.println("Can't equip " + item.name + " on " + name);
            return;
        }

        if (!inventory.remove(item)) {
            System.err.println(name + " does not have " + item.name);
            return;
        }

        Set<Physical> removing = new HashSet<>();
        for (WieldSlot ws : slots) {
            Physical p = creatureData.equipment.get(ws);
            if (p != null) {
                removing.add(p);
            }
            creatureData.equipment.put(ws, item);
        }

        for (Physical p : removing) {
            inventory.add(p);
            for (WieldSlot subSlot : WieldSlot.values()) { // make sure to clear out mult-handed unequips
                if (p.equals(creatureData.equipment.get(subSlot))) {
                    creatureData.equipment.remove(subSlot);
                }
            }
        }
    }

    /**
     * Takes the item out of inventory and places it into equipped areas. Anything already equipped in
     * the areas goes back into inventory.
     *
     * @param slots All the slots that will be filled when the item is equipped
     * @return a List of the items that were unequipped
     */
    public List<Physical> unequip(List<WieldSlot> slots) {
        if (creatureData == null) {
            System.err.println("Can't unequip from the Physical " + name + "; it is not a creature");
            return Collections.emptyList();
        }
        List<Physical> removed = new ArrayList<>(slots.size());

        Set<Physical> removing = new HashSet<>();
        for (WieldSlot ws : slots) {
            Physical p = creatureData.equipment.remove(ws);
            if (p != null) {
                removing.add(p);
            }
        }

        for (Physical p : removing) {
            removed.add(p);
            for (WieldSlot subSlot : WieldSlot.values()) { // make sure to clear out mult-handed unequips
                if (p.equals(creatureData.equipment.get(subSlot))) {
                    creatureData.equipment.remove(subSlot);
                }
            }
        }
        if(!(creatureData.equipment.containsKey(WieldSlot.LEFT_HAND) || creatureData.equipment.containsKey(WieldSlot.RIGHT_HAND)))
        {
            weaponData = unarmedData != null ? unarmedData.copy() : Weapon.getUnarmedWeapons().randomValue(chaos);
        }
        return removed;
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
        if(target == null || target.creatureData == null)
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
        return amt;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.generic ? 1 : 0);
        hash = 59 * hash + (this.unique ? 1 : 0);
        hash = 59 * hash + (this.buildingBlock ? 1 : 0);
        hash = 59 * hash + this.symbol;
        hash = 59 * hash + Float.floatToIntBits(this.color);
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.baseValue) ^ (Double.doubleToLongBits(this.baseValue) >>> 32));
        hash = 59 * hash + (this.blocking ? 1 : 0);
        hash = 59 * hash + Float.floatToIntBits(this.lightEmitted);
        hash = 59 * hash + Objects.hashCode(this.rarity);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Physical other = (Physical) obj;
        if (this.generic != other.generic) {
            return false;
        }
        if (this.unique != other.unique) {
            return false;
        }
        if (this.buildingBlock != other.buildingBlock) {
            return false;
        }
        if (this.symbol != other.symbol) {
            return false;
        }
        if (Float.floatToIntBits(this.color) != Float.floatToIntBits(other.color)) {
            return false;
        }
        if (Double.doubleToLongBits(this.baseValue) != Double.doubleToLongBits(other.baseValue)) {
            return false;
        }
        if (this.blocking != other.blocking) {
            return false;
        }
        if (Float.floatToIntBits(this.lightEmitted) != Float.floatToIntBits(other.lightEmitted)) {
            return false;
        }
        if (this.rarity != other.rarity) {
            return false;
        }
        return true;
    }

    
}
