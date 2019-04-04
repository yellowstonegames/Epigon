package squidpony.epigon.data;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import squidpony.Maker;
import squidpony.epigon.ConstantKey;
import squidpony.epigon.data.quality.Element;
import squidpony.epigon.data.quality.Material;
import squidpony.epigon.data.slot.BodySlot;
import squidpony.epigon.data.slot.ClothingSlot;
import squidpony.epigon.data.slot.WieldSlot;
import squidpony.epigon.data.trait.*;
import squidpony.squidgrid.gui.gdx.Radiance;
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
    public static final int PRECISION = 0, DAMAGE = 1, CRIT = 2, INFLUENCE = 3,
            EVASION = 4, DEFENSE = 5, STEALTH = 6, LUCK = 7, RANGE = 8, AREA= 9, PREPARE = 10;

    private static long createdQuantity = 0;
    
    // operational bits for live objects
    public Coord location;
    public boolean attached; // cannot be removed from its location (or inventory pile) without special means
    public boolean wasSeen;
    public TextCellFactory.Glyph appearance; // for things that move, we should use Glyph, which is a kind of Actor
    public TextCellFactory.Glyph overlayAppearance; // mainly for showing status effects over the existing appearance
    public char symbol;
    public char overlaySymbol = '\uffff';
    public float color;
    public float overlayColor = 0f; // if == 0f, this will be disregarded by SparseLayers
    public Radiance radiance = null;
    // backing data
    public Physical parent;
    public UnorderedSet<Physical> countsAs = new UnorderedSet<>();
    public UnorderedSet<Physical> createdFrom = new UnorderedSet<>();//only important items should track this since it will cause object lifetimes to extend
    public boolean generic; // should not be directly used, only available as a building block object
    public boolean unique; // should only have one in existence of exactly this type
    public boolean buildingBlock; // can be used as a building block

    public ArrayList<String> possibleAliases = new ArrayList<>(); // One of these is picked when instantiated (maybe choice locked by world region?)

    public double baseValue;
    public boolean blocking;
    
    public ArrayList<Modification> whenUsedAsMaterial = new ArrayList<>();
    public ArrayList<Modification> modifications = new ArrayList<>(); // modifications applied both during instantiation and through later effects
    public ArrayList<Modification> requiredModifications = new ArrayList<>(); // Must apply all of these on instantiation
    public ArrayList<Modification> optionalModifications = new ArrayList<>(); // Zero or more of these may be applied on instantiation

    public OrderedMap<Element, LiveValue> elementalDamageMultiplier = new OrderedMap<>(); // The change to incoming damage

    public OrderedSet<Condition> conditions = new OrderedSet<>();
    public VisualCondition visualCondition = null;
    private ArrayList<Condition> conditionsToRemove = new ArrayList<>();

    // initial stats on instantiation come from required modification
    public OrderedMap<ConstantKey, LiveValue> stats = new OrderedMap<ConstantKey, LiveValue>(32, 0.5f, ConstantKey.ConstantKeyHasher.instance);
    public OrderedSet<ChangeTable> statEffects = new OrderedSet<>(8, CrossHash.identityHasher);
    public OrderedMap<ConstantKey, Rating> statProgression = new OrderedMap<>(ConstantKey.ConstantKeyHasher.instance);
    public ArrayList<Physical> inventory = new ArrayList<>();
    public ArrayList<Physical> optionalInventory = new ArrayList<>(); // For use when this is a blueprint item

    /**
     * The list of physical objects it drops on destruction no matter what the damage source.
     */
    public ArrayList<WeightedTableWrapper<Physical>> physicalDrops = new ArrayList<>();

    /**
     * A list of what the item might drop when a given element is used on it. This is in addition to
     * the regular drop table.
     */
    public OrderedMap<Element, ArrayList<WeightedTableWrapper<Physical>>> elementDrops = new OrderedMap<>();

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
    public EnumOrderedMap<Rating, ArrayList<Modification>> rarityModifications = new EnumOrderedMap<>(Rating.class);

    public Creature creatureData;

    public Material mainMaterial;
    
    public Ammunition ammunitionData;
    public Container containerData;
    public Grouping groupingData;
    public ArrayList<Interactable> interactableData;
    public Liquid liquidData;
    public Legible legibleData;
    public Wearable wearableData;
    public Weapon weaponData;
    public Zappable zappableData;

    // Non-action mixins
    public Terrain terrainData;

    public Physical() {
        super();
        createdQuantity++;
        stats.put(Stat.OPACITY, new LiveValue(1)); // default to opaque
        stats.put(Stat.MOBILITY, new LiveValue(0)); // default to not being able to move
        name = "P-" + createdQuantity;

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
    public static final Physical basePhysical = new Physical();
    static {
        basePhysical.generic = true;
        basePhysical.unique = true;
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
    public boolean immuneToCondition(ConditionBlueprint condition) {
        for (Condition c : conditions) {
            if (c.suppressors.isEmpty() && c.parent != null) {//only active conditions can provide immunity //TODO -- ensure that when they become unsuppressed they remove things they provide immunity against
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
        if (immuneToCondition(condition.parent)) {//make sure it's not immune
            return false;
        } else {
            for (Condition c : conditions) {
                if (!c.parent.conflictsWith(condition.parent)) {
                    return false;
                }
            }
        }         
        condition.attach(this);
        return true;
    }
    
    public boolean removeCondition(ConditionBlueprint condition)
    {
        boolean removed = false;
        for (int i = 0; i < conditions.size(); i++) {
            if (conditions.getAt(i).hasParent(condition)) {
                conditions.removeAt(i--);
                removed = true;
            }
        }
        return removed;

    }

    public void equipItem(Physical item) {
        if (item == null) {
            System.err.println("Item requested for equip to " + name + " is null!");
            return; // How did this happen?
        }

        if (item.wearableData != null) {
            equip(item, item.wearableData.slotsUsed);
        }

        // Check if it's wielded as well as worn when equipped (spiked gauntlet for example)
        if (item.weaponData != null && creatureData != null) {
            switch (item.weaponData.hands) {
                case 0:
                    creatureData.weaponChoices.add(item.weaponData, 1);
                    creatureData.equippedDistinct.add(item);
                    break;
                case 1:
                    if (!creatureData.equippedBySlot.containsKey(WieldSlot.RIGHT_HAND)) {
                        equip(item, Maker.makeList(WieldSlot.RIGHT_HAND));
                    } else if (!creatureData.equippedBySlot.containsKey(WieldSlot.LEFT_HAND)) {
                        if (nextBoolean()) {
                            equip(item, Maker.makeList(WieldSlot.RIGHT_HAND));
                        } else {
                            equip(item, Maker.makeList(WieldSlot.LEFT_HAND));
                        }
                    } else {
                        equip(item, Maker.makeList(WieldSlot.LEFT_HAND));
                    }
                    break;
                case 2:
                    equip(item, Maker.makeList(WieldSlot.RIGHT_HAND, WieldSlot.LEFT_HAND));
                    break;
                case 3:
                        equip(item, Maker.makeList(WieldSlot.HEAD));
                    break;
                case 4:
                        equip(item, Maker.makeList(WieldSlot.NECK));
                    break;
                case 5:
                        equip(item, Maker.makeList(WieldSlot.LEFT_FOOT, WieldSlot.RIGHT_FOOT));
                    break;
            }
        }
    }

    /**
     * Takes the item out of inventory and places it into equipped areas. Anything already equipped in
     * the areas goes back into inventory.
     *
     * @param item The item to equip
     * @param slots All the slots that will be filled when the item is equipped
     */
    public void equip(Physical item, ArrayList<BodySlot> slots) {
        if (creatureData == null) {
            System.err.println("Can't equip " + item.name + " on " + name);
            return;
        }

        unequip(slots).stream().forEach(this::addToInventory); // put old equipment back into inventory

        removeFromInventory(item);
        if (item.wearableData != null){
            item.wearableData.worn = true;
        }

        for (BodySlot ws : slots) {
            Physical old = creatureData.equippedBySlot.put(ws, item);
            if(old != null)
                creatureData.weaponChoices.remove(old.weaponData);
            if(!item.equals(old) && item.weaponData != null)
                creatureData.weaponChoices.add(item.weaponData, 2);
            creatureData.equippedDistinct.add(item);
        }
        // TODO - apply changes based on adding equipment (may be done?)
    }

    /**
     * Unequips a single item if it is being equipped and places it back in inventory
     *
     * @param item the item to try to remove
     */
    public void unequip(Physical item) {
        if (item.wearableData != null) {
            unequip(item.wearableData.slotsUsed);
        }
        
        if (item.weaponData != null && creatureData != null) {
            unequip(Arrays.stream(WieldSlot.ALL)
                .filter(ws -> item.equals(creatureData.equippedBySlot.get(ws)))
                .<ArrayList<BodySlot>>collect(ArrayList::new, ArrayList::add, ArrayList::addAll));
        } 
    }

    /**
     * Takes the item out of inventory and places it into equipped areas. Anything already equipped in the areas goes
     * back into inventory.
     *
     * @param slots All the slots that will be filled when the item is equipped
     * @return an ArrayList of the items that were unequipped
     */
    public ArrayList<Physical> unequip(ArrayList<BodySlot> slots) {
        if (creatureData == null) {
            System.err.println("Can't unequip from the Physical " + name + "; it is not a creature");
            return new ArrayList<>(0);
        }

        // Make sure all slots that are used by the same thing as the request slot removals are also cleared
        UnorderedSet<BodySlot> totalSlots = new UnorderedSet<>();
        totalSlots.addAll(slots);
        slots.stream()
            .map(s -> creatureData.equippedBySlot.get(s))
            .filter(Objects::nonNull)
            .map(p -> p.wearableData)
            .filter(Objects::nonNull)
            .map(w -> w.slotsUsed)
            .filter(Objects::nonNull)
            .forEach(totalSlots::addAll);

        ArrayList<Physical> removed = new ArrayList<>(totalSlots.size());

        UnorderedSet<Physical> removing = new UnorderedSet<>(6);
        for (BodySlot ws : totalSlots) {
            Physical p = creatureData.equippedBySlot.remove(ws);
            if (p != null) {
                removing.add(p);
            }
        }

        for (Physical p : removing) {
            if (p.wearableData != null) {
                p.wearableData.worn = false;
            }

            if (!p.attached) {
                removed.add(p);
            }

            creatureData.equippedDistinct.remove(p);
            for (BodySlot subSlot : WieldSlot.ALL) { // make sure to clear out multi-handed unequips
                if (p.equals(creatureData.equippedBySlot.get(subSlot))) {
                    creatureData.equippedBySlot.remove(subSlot);
                    if (p.weaponData != null) {
                        creatureData.weaponChoices.remove(p.weaponData);
                    }
                }
            }
        }

        // TODO - apply changes based on unequipping items (may be done?)
        return removed;
    }

    public void addToInventory(Physical adding){
        Physical owned = inventory.stream().filter(p -> p.equals(adding)).findAny().orElse(null);
        if (owned == null){
            inventory.add(adding);
        } else { // This is meant to work with groupingData not counting in equality comparison
            int quantity = adding.groupingData == null ? 1 : adding.groupingData.quantity;
            if (adding.groupingData == null){
                owned.groupingData = new Grouping(1 + quantity);
            } else {
                owned.groupingData.quantity += quantity;
            }
        }
    }

    public Physical removeFromInventory(Physical removing) {
        return removeFromInventory(removing, 1);
    }

    public Physical removeFromInventory(Physical removing, int quantity) {
        // TODO - handle negatives
        // TODO - message that requested quantity not found
        // NOTE - during object creation the quantity may not exist as equipping it might be the first interaction with the item
        if (removing.groupingData == null || removing.groupingData.quantity <= quantity) {
            inventory.remove(removing);
            return removing;
        } else {
            quantity = MathUtils.clamp(quantity, 0, removing.groupingData.quantity);
            removing.groupingData.quantity -= quantity;
            Physical dropped = RecipeMixer.buildPhysical(removing);
            dropped.groupingData.quantity = quantity;
            return dropped;
        }
    }

    public void calculateStats() {
        for (int lim = next(3) + 4; lim >= 0; lim--) {
            CalcStat stat = getRandomElement(CalcStat.all);
            if(stats.containsKey(stat)) 
                stats.get(stat).addActual(nextCurvedDouble());
            else
                stats.put(stat, new LiveValue(0.0, 99.0));
        }
    }
    
    public double actualStat(ConstantKey stat)
    {
        return stats.getOrDefault(stat, LiveValue.ZERO).actual();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Physical physical = (Physical) o;
        if (name != null ? !name.equals(physical.name) : physical.name != null) return false;
        if (description != null ? !description.equals(physical.description) : physical.description != null) return false;
        if (attached != physical.attached) return false;
        if (symbol != physical.symbol) return false;
        if (overlaySymbol != physical.overlaySymbol) return false;
        if (Float.compare(physical.color, color) != 0) return false;
        if (Float.compare(physical.overlayColor, overlayColor) != 0) return false;
        if (generic != physical.generic) return false;
        if (unique != physical.unique) return false;
        if (buildingBlock != physical.buildingBlock) return false;
        if (Double.compare(physical.baseValue, baseValue) != 0) return false;
        if (blocking != physical.blocking) return false;
        if (radiance != null ? !radiance.equals(physical.radiance) : physical.radiance != null) return false;
        if (parent != null ? !parent.equals(physical.parent) : physical.parent != null) return false;
        if (possibleAliases != null ? !possibleAliases.equals(physical.possibleAliases) : physical.possibleAliases != null)
            return false;
        if (modifications != null ? !modifications.equals(physical.modifications) : physical.modifications != null)
            return false;
        if (elementalDamageMultiplier != null ? !elementalDamageMultiplier.equals(physical.elementalDamageMultiplier) : physical.elementalDamageMultiplier != null)
            return false;
        if (inventory != null ? !inventory.equals(physical.inventory) : physical.inventory != null) return false;
        if (optionalInventory != null ? !optionalInventory.equals(physical.optionalInventory) : physical.optionalInventory != null)
            return false;
        if (physicalDrops != null ? !physicalDrops.equals(physical.physicalDrops) : physical.physicalDrops != null)
            return false;
        if (elementDrops != null ? !elementDrops.equals(physical.elementDrops) : physical.elementDrops != null)
            return false;
        if (rarity != physical.rarity) return false;
        if (rarityModifications != null ? !rarityModifications.equals(physical.rarityModifications) : physical.rarityModifications != null)
            return false;
        if (creatureData != null ? !creatureData.equals(physical.creatureData) : physical.creatureData != null)
            return false;
        if (mainMaterial != null ? !mainMaterial.equals(physical.mainMaterial) : physical.mainMaterial != null)
            return false;
        if (ammunitionData != null ? !ammunitionData.equals(physical.ammunitionData) : physical.ammunitionData != null)
            return false;
        if (containerData != null ? !containerData.equals(physical.containerData) : physical.containerData != null)
            return false;
        if (groupingData != null ? !groupingData.equals(physical.groupingData) : physical.groupingData != null)
            return false;
        if (interactableData != null ? !interactableData.equals(physical.interactableData) : physical.interactableData != null)
            return false;
        if (liquidData != null ? !liquidData.equals(physical.liquidData) : physical.liquidData != null) return false;
        if (legibleData != null ? !legibleData.equals(physical.legibleData) : physical.legibleData != null)
            return false;
        if (wearableData != null ? !wearableData.equals(physical.wearableData) : physical.wearableData != null)
            return false;
        if (weaponData != null ? !weaponData.equals(physical.weaponData) : physical.weaponData != null) return false;
        if (zappableData != null ? !zappableData.equals(physical.zappableData) : physical.zappableData != null)
            return false;
        return terrainData != null ? terrainData.equals(physical.terrainData) : physical.terrainData == null;
    }

    @Override
    public int hashCode() {
        int result = 31 * CrossHash.hash(name) + CrossHash.hash(description);
        long temp;
        result = 31 * result + (attached ? 1 : 0);
        result = 31 * result + (int) symbol;
        result = 31 * result + (int) overlaySymbol;
        result = 31 * result + (color != +0.0f ? Float.floatToIntBits(color) : 0);
        result = 31 * result + (overlayColor != +0.0f ? Float.floatToIntBits(overlayColor) : 0);
        result = 31 * result + (radiance != null ? radiance.hashCode() : 0);
        result = 31 * result + (parent != null ? parent.hashCode() : 0);
        result = 31 * result + (generic ? 1 : 0);
        result = 31 * result + (unique ? 1 : 0);
        result = 31 * result + (buildingBlock ? 1 : 0);
        result = 31 * result + (possibleAliases != null ? possibleAliases.hashCode() : 0);
        temp = Double.doubleToLongBits(baseValue);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (blocking ? 1 : 0);
        result = 31 * result + (modifications != null ? modifications.hashCode() : 0);
        result = 31 * result + (elementalDamageMultiplier != null ? elementalDamageMultiplier.hashCode() : 0);
        result = 31 * result + (inventory != null ? inventory.hashCode() : 0);
        result = 31 * result + (optionalInventory != null ? optionalInventory.hashCode() : 0);
        result = 31 * result + (physicalDrops != null ? physicalDrops.hashCode() : 0);
        result = 31 * result + (elementDrops != null ? elementDrops.hashCode() : 0);
        result = 31 * result + (rarity != null ? rarity.hashCode() : 0);
        result = 31 * result + (rarityModifications != null ? rarityModifications.hashCode() : 0);
        result = 31 * result + (creatureData != null ? creatureData.hashCode() : 0);
        result = 31 * result + (mainMaterial != null ? mainMaterial.hashCode() : 0);
        result = 31 * result + (ammunitionData != null ? ammunitionData.hashCode() : 0);
        result = 31 * result + (containerData != null ? containerData.hashCode() : 0);
        result = 31 * result + (groupingData != null ? groupingData.hashCode() : 0);
        result = 31 * result + (interactableData != null ? interactableData.hashCode() : 0);
        result = 31 * result + (liquidData != null ? liquidData.hashCode() : 0);
        result = 31 * result + (legibleData != null ? legibleData.hashCode() : 0);
        result = 31 * result + (wearableData != null ? wearableData.hashCode() : 0);
        result = 31 * result + (weaponData != null ? weaponData.hashCode() : 0);
        result = 31 * result + (zappableData != null ? zappableData.hashCode() : 0);
        result = 31 * result + (terrainData != null ? terrainData.hashCode() : 0);
        return result;
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
