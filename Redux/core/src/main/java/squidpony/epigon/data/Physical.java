package squidpony.epigon.data;

import com.badlogic.gdx.graphics.Color;
import squidpony.epigon.ConstantKey;
import squidpony.epigon.data.quality.Element;
import squidpony.epigon.data.quality.Material;
import squidpony.epigon.data.slot.BodySlot;
import squidpony.epigon.data.slot.ClothingSlot;
import squidpony.epigon.data.trait.*;
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
    
    // operational bits for live objects
    public Coord location;
    public boolean attached; // cannot be removed from its location (or inventory pile) without special means
    public boolean aware; // knows where the player is
    public boolean wasSeen;
    public TextCellFactory.Glyph appearance; // for things that move, we should use Glyph, which is a kind of Actor
    public TextCellFactory.Glyph overlayAppearance; // mainly for showing status effects over the existing appearance
    public char symbol;
    public char overlaySymbol = '\uffff';
    public float color;
    public float overlayColor = 0f; // if == 0f, this will be disregarded by SparseLayers

    // backing data
    public Physical parent;
    public Set<Physical> countsAs = new HashSet<>();
    public Set<Physical> createdFrom = new HashSet<>();//only important items should track this since it will cause object lifetimes to extend
    public boolean generic; // should not be directly used, only available as a building block object
    public boolean unique; // should only have one in existence of exactly this type
    public boolean buildingBlock; // can be used as a building block

    public List<String> possibleAliases = new ArrayList<>(); // One of these is picked when instantiated (maybe choice locked by world region?)

    public double baseValue;
    public boolean blocking;

    public float lightEmitted;
    public LiveValue lightEmittedStrength;
    
    public List<Modification> whenUsedAsMaterial = new ArrayList<>();
    public List<Modification> modifications = new ArrayList<>(); // modifications applied both during instantiation and through later effects
    public List<Modification> requiredModifications = new ArrayList<>(); // Must apply all of these on instantiation
    public List<Modification> optionalModifications = new ArrayList<>(); // Zero or more of these may be applied on instantiation

    public OrderedMap<Element, LiveValue> elementalDamageMultiplier = new OrderedMap<>(); // The change to incoming damage

    public List<Condition> conditions = new ArrayList<>();
    private ArrayList<Condition> conditionsToRemove = new ArrayList<>();

    // initial stats on instantiation come from required modification
    public OrderedMap<ConstantKey, LiveValue> stats = new OrderedMap<ConstantKey, LiveValue>(32, 0.5f, ConstantKey.ConstantKeyHasher.instance);
    public OrderedSet<ChangeTable> statEffects = new OrderedSet<>(8, CrossHash.identityHasher);
    public OrderedMap<ConstantKey, Rating> statProgression = new OrderedMap<>(ConstantKey.ConstantKeyHasher.instance);
    public List<Physical> inventory = new ArrayList<>();
    public List<Physical> optionalInventory = new ArrayList<>(); // For use when this is a blueprint item

    /**
     * The list of physical objects it drops on destruction no matter what the damage source.
     */
    public List<WeightedTableWrapper<Physical>> physicalDrops = new ArrayList<>();

    /**
     * A list of what the item might drop when a given element is used on it. This is in addition to
     * the regular drop table.
     */
    public OrderedMap<Element, List<WeightedTableWrapper<Physical>>> elementDrops = new OrderedMap<>();

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

    public Material mainMaterial;
    
    public Ammunition ammunitionData;
    public Container containerData;
    public Grouping groupingData;
    public List<Interactable> interactableData;
    public Liquid liquidData;
    public Legible legibleData;
    public Wearable wearableData;
    public Weapon weaponData;
    public Zappable zappableData;

    // Non-action mixins
    public Terrain terrainData;

    public Physical() {
        super();
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

    public void update()
    {
        for(Condition c : conditions)
        {
            if(c.update())
                conditionsToRemove.add(c);
        }
        conditions.removeAll(conditionsToRemove);
        conditionsToRemove.clear();
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
    public void equip(Physical item, List<BodySlot> slots) {
        if (creatureData == null) {
            System.err.println("Can't equip " + item.name + " on " + name);
            return;
        }
        inventory.remove(item);
//        if (!inventory.remove(item)) {
//            System.err.println(name + " does not have " + item.name);
//            return;
//        }

        UnorderedSet<Physical> removing = new UnorderedSet<>(6);
        for (BodySlot ws : slots) {
            Physical p = creatureData.equippedBySlot.get(ws);
            if (p != null) {
                removing.add(p);
                creatureData.weaponChoices.remove(p.weaponData);
            }
            if(!item.equals(p))
                creatureData.weaponChoices.add(item.weaponData, 2);
            creatureData.equippedBySlot.put(ws, item);
            creatureData.equippedDistinct.add(item);

        }

        for (Physical p : removing) {
            inventory.add(p);
            creatureData.equippedDistinct.remove(p);

            for (BodySlot subSlot : ClothingSlot.values()) { // make sure to clear out multi-handed unequips
                if (p.equals(creatureData.equippedBySlot.get(subSlot))) {
                    creatureData.equippedBySlot.remove(subSlot);
                }
            }
        }
    }
    public static final List<BodySlot> RIGHT = Collections.singletonList(ClothingSlot.RIGHT_HAND),
            LEFT = Collections.singletonList(ClothingSlot.LEFT_HAND),
            BOTH = Arrays.asList(ClothingSlot.RIGHT_HAND, ClothingSlot.LEFT_HAND),
            HEAD = Collections.singletonList(ClothingSlot.HEAD),
            NECK = Collections.singletonList(ClothingSlot.NECK),
            FEET = Arrays.asList(ClothingSlot.LEFT_FOOT, ClothingSlot.RIGHT_FOOT);

    public void equipItem(Physical item) {
        switch (item.weaponData.hands) {
            case 2:
                equip(item, BOTH);
                break;
            case 0:
                creatureData.weaponChoices.add(item.weaponData, 1);
                creatureData.equippedDistinct.add(item);
                break;
            case 3:
                if (!creatureData.equippedBySlot.containsKey(ClothingSlot.HEAD))
                    equip(item, HEAD);
                break;
            case 4:
                if (!creatureData.equippedBySlot.containsKey(ClothingSlot.NECK))
                    equip(item, NECK);
                break;
            case 5:
                if (!creatureData.equippedBySlot.containsKey(ClothingSlot.LEFT_FOOT) &&
                        !creatureData.equippedBySlot.containsKey(ClothingSlot.RIGHT_FOOT))
                    equip(item, FEET);
                break;
            case 1:
                if (!creatureData.equippedBySlot.containsKey(ClothingSlot.RIGHT_HAND))
                    equip(item, RIGHT);
                else
                    equip(item, LEFT);
                break;
        }
    }

    /**
     * Takes the item out of inventory and places it into equipped areas. Anything already equipped in
     * the areas goes back into inventory.
     *
     * @param slots All the slots that will be filled when the item is equipped
     * @return a List of the items that were unequipped
     */
    public List<Physical> unequip(List<BodySlot> slots) {
        if (creatureData == null) {
            System.err.println("Can't unequip from the Physical " + name + "; it is not a creature");
            return Collections.emptyList();
        }
        List<Physical> removed = new ArrayList<>(slots.size());

        UnorderedSet<Physical> removing = new UnorderedSet<>(6);
        for (BodySlot ws : slots) {
            Physical p = creatureData.equippedBySlot.remove(ws);
            if (p != null) {
                removing.add(p);
            }
        }

        for (Physical p : removing) {
            removed.add(p);
            creatureData.equippedDistinct.remove(p);
            for (BodySlot subSlot : ClothingSlot.values()) { // make sure to clear out multi-handed unequips
                if (p.equals(creatureData.equippedBySlot.get(subSlot))) {
                    creatureData.equippedBySlot.remove(subSlot);
                    if(p.weaponData != null)
                        creatureData.weaponChoices.remove(p.weaponData);
                }
            }
        }
//        if(!(creatureData.equipment.containsKey(WieldSlot.LEFT_HAND) || creatureData.equipment.containsKey(WieldSlot.RIGHT_HAND)))
//        {
//            if(statEffects.contains(weaponData.calcStats))
//                statEffects.alter(weaponData.calcStats, (weaponData = unarmedData != null ? unarmedData.copy() : Weapon.randomUnarmedWeapon(++chaos)).calcStats);
//            else
//                statEffects.add((weaponData = unarmedData != null ? unarmedData.copy() : Weapon.randomUnarmedWeapon(++chaos)).calcStats);
//        }
        return removed;
    }

    public void addToInventory(Physical adding){
        Physical owned = inventory.stream().filter(p -> p.equals(adding)).findAny().orElse(null);
        if (owned == null){
            inventory.add(adding);
        } else { // This is meant to work with groupingData not counting in equality comparison
            int quantity = owned.groupingData == null ? 1 : owned.groupingData.quantity;
            if (adding.groupingData == null){
                adding.groupingData = new Grouping(1 + quantity);
            } else {
                adding.groupingData.quantity += quantity;
            }
        }
    }

    public void removeFromInventory(Physical removing) {
        removeFromInventory(removing, 1);
    }

    public void removeFromInventory(Physical removing, int quantity) {
        // TODO - handle negatives
        // TODO - message that requested quantity not found
        if (removing.groupingData == null || removing.groupingData.quantity <= quantity) {
            inventory.remove(removing);
        } else {
            removing.groupingData.quantity -= quantity;
        }
    }

    public void calculateStats() {
        LiveValue lv, calc;
        int current;
        if ((lv = stats.get(Stat.AIM)) != null) {
            current = (int) lv.actual();

            calc = stats.get(CalcStat.PRECISION);
            if(calc == null) stats.put(CalcStat.PRECISION, new LiveValue(current, 99.0));
            else calc.addActual(current);

            calc = stats.get(CalcStat.CRIT);
            if(calc == null) stats.put(CalcStat.CRIT, new LiveValue(current * 0.5, 99.0));
            else calc.addActual(current * 0.5);
        }
        if ((lv = stats.get(Stat.IMPACT)) != null) {
            current = (int) lv.actual();

            calc = stats.get(CalcStat.DAMAGE);
            if(calc == null) stats.put(CalcStat.DAMAGE, new LiveValue(current, 99.0));
            else calc.addActual(current);

            calc = stats.get(CalcStat.CRIT);
            if(calc == null) stats.put(CalcStat.CRIT, new LiveValue(current * 0.5, 99.0));
            else calc.addActual(current * 0.5);
        }
        if ((lv = stats.get(Stat.DODGE)) != null) {
            current = (int) lv.actual();

            calc = stats.get(CalcStat.EVASION);
            if(calc == null) stats.put(CalcStat.EVASION, new LiveValue(current, 99.0));
            else calc.addActual(current);

            calc = stats.get(CalcStat.STEALTH);
            if(calc == null) stats.put(CalcStat.STEALTH, new LiveValue(current * 0.5, 99.0));
            else calc.addActual(current * 0.5);
        }
        if ((lv = stats.get(Stat.TOUGHNESS)) != null) {
            current = (int) lv.actual();

            calc = stats.get(CalcStat.DEFENSE);
            if(calc == null) stats.put(CalcStat.DEFENSE, new LiveValue(current, 99.0));
            else calc.addActual(current);

            calc = stats.get(CalcStat.DAMAGE);
            if(calc == null) stats.put(CalcStat.DAMAGE, new LiveValue(current * 0.5, 99.0));
            else calc.addActual(current * 0.5);
        }
        if ((lv = stats.get(Stat.POTENCY)) != null) {
            current = (int) lv.actual();

            calc = stats.get(CalcStat.INFLUENCE);
            if(calc == null) stats.put(CalcStat.INFLUENCE, new LiveValue(current, 99.0));
            else calc.addActual(current);

            calc = stats.get(CalcStat.PRECISION);
            if(calc == null) stats.put(CalcStat.PRECISION, new LiveValue(current * 0.5, 99.0));
            else calc.addActual(current * 0.5);
        }
        if ((lv = stats.get(Stat.ATTUNEMENT)) != null) {
            current = (int) lv.actual();
            calc = stats.get(CalcStat.LUCK);
            if(calc == null) stats.put(CalcStat.LUCK, new LiveValue(current, 99.0));
            else calc.addActual(current);

            calc = stats.get(CalcStat.STEALTH);
            if(calc == null) stats.put(CalcStat.STEALTH, new LiveValue(current * 0.5, 99.0));
            else calc.addActual(current * 0.5);
        }
    }
    
    public double actualStat(ConstantKey stat)
    {
        return stats.getOrDefault(stat, LiveValue.ZERO).actual();
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
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
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


    public String disarm() {
        if (creatureData == null) {
            System.err.println("Can't disarm the Physical " + name + "; it is not a creature");
            return "";
        }
        if(!creatureData.equippedBySlot.containsKey(ClothingSlot.LEFT_HAND) && !creatureData.equippedBySlot.containsKey(ClothingSlot.RIGHT_HAND))
            return "";
        Physical p = nextBoolean()
                ? creatureData.equippedBySlot.remove(ClothingSlot.LEFT_HAND)
                : creatureData.equippedBySlot.remove(ClothingSlot.RIGHT_HAND);
        if (p != null) {
            creatureData.equippedDistinct.remove(p);
            if (p.weaponData != null)
                creatureData.weaponChoices.remove(p.weaponData);
            for (BodySlot subSlot : ClothingSlot.values()) { // make sure to clear out multi-handed unequips
                if (p.equals(creatureData.equippedBySlot.get(subSlot))) {
                    creatureData.equippedBySlot.remove(subSlot);
                }
            }
            addToInventory(p);
            return "The " + p.name + " falls to the floor!";
        }
        return "";
    }

    public String sunder(double power) {
        if (creatureData == null) {
            System.err.println("Can't sunder the equipment of Physical " + name + "; it is not a creature");
            return "";
        }
        if(creatureData.equippedBySlot.isEmpty())
            return "";
        int pos = nextInt(creatureData.equippedBySlot.size());
        Physical p = creatureData.equippedBySlot.getAt(pos);
        if (p != null) {
            if(p.mainMaterial == null || p.mainMaterial.getHardness() > nextDouble(power * 500))
                return ""; // didn't break anything
            creatureData.equippedDistinct.remove(p);
            if (p.weaponData != null)
                creatureData.weaponChoices.remove(p.weaponData);
            //creatureData.equippedBySlot.removeAt(pos);
            for (BodySlot subSlot : ClothingSlot.values()) { // make sure to clear out multi-handed unequips
                if (p.equals(creatureData.equippedBySlot.get(subSlot))) {
                    creatureData.equippedBySlot.remove(subSlot);
                }
            }
            Physical p2 = RecipeMixer.buildMaterial(p.mainMaterial);
            p2.rarity = p.rarity;
            addToInventory(p2);
            return "The " + p.name + " breaks beyond repair!";
        }
        return "";
    }
}
