package squidpony.epigon.data.interfaces;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.EnumMap;
import java.util.TreeMap;

import squidpony.epigon.data.Stat;
import squidpony.epigon.data.blueprints.ConditionBlueprint;
import squidpony.epigon.data.blueprints.PhysicalBlueprint;
import squidpony.epigon.data.generic.Ability;
import squidpony.epigon.data.generic.Skill;
import squidpony.epigon.data.interfaceBlueprints.CreatureBlueprint;
import squidpony.epigon.data.specific.Condition;
import squidpony.epigon.data.specific.Physical;
import squidpony.epigon.universe.Rating;

/**
 * A specific creature in the world.
 *
 * @author Eben Howard - http://squidpony.com
 */
public class Creature {

    public CreatureBlueprint parent;
    public String knownName, trueName;
    public TreeMap<Skill, Rating> skills = new TreeMap<>();
    public ArrayList<Ability> abilities = new ArrayList<>();
    public EnumMap<Stat, Integer> baseStats = new EnumMap<>(Stat.class);
    public EnumMap<Stat, Integer> currentStats = new EnumMap<>(Stat.class);
    public Ability defaultAttack;
    public Deque<Physical> inventory = new ArrayDeque<>();
    public boolean aware;//has noticed the player

    public Ability getDefaultAttack() {
        if (defaultAttack == null) {
            defaultAttack = abilities.stream().filter(a -> a.isAttack()).findAny().orElse(null);
        }

        return defaultAttack;
    }

    public void setDefaultAttack(Ability defaultAttack) {
        this.defaultAttack = defaultAttack;
    }

    /**
     * Returns true if the listed item types are in the inventory of this Item.
     *
     * @param items
     * @return
     */
    public boolean hasItems(TreeMap<PhysicalBlueprint, Integer> items) {
        return items.keySet().stream().noneMatch((blueprint) -> (!hasItems(blueprint, items.get(blueprint))));
    }

    /**
     * Returns true if the listed item type is in the inventory of this object.
     *
     * @param blueprint
     * @param count
     * @return
     */
    public boolean hasItems(PhysicalBlueprint blueprint, int count) {
        return inventory.stream().filter((item) -> (item.hasParent(blueprint))).count() >= count;
    }

    /**
     * Checks if an item of the given type is equipped.
     *
     * @param blueprint The blueprint to be checked for
     * @return if an item created from the blueprint is found to be equipped
     */
    public boolean hasEquipped(PhysicalBlueprint blueprint) {
        return inventory.stream()
            .anyMatch((item) -> (((item.wearableData != null && item.wearableData.worn) || (item.wieldableData != null && item.wieldableData.wielded))
            && item.hasParent(blueprint)));
    }

    public boolean hasCondition(ConditionBlueprint parent) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public boolean immune(ConditionBlueprint parent) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void applyCondition(Condition condition) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
