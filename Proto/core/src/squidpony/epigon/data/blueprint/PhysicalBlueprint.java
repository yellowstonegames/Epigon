package squidpony.epigon.data.blueprint;

import squidpony.epigon.data.generic.Modification;
import java.util.EnumMap;
import java.util.List;

import squidpony.epigon.data.EpiData;
import squidpony.epigon.universe.Element;
import squidpony.epigon.data.generic.Skill;
import squidpony.epigon.universe.Stat;
import squidpony.epigon.data.mixin.Ammunition;
import squidpony.epigon.data.mixin.Container;
import squidpony.epigon.data.mixin.Creature;
import squidpony.epigon.data.mixin.Interactable;
import squidpony.epigon.data.mixin.Liquid;
import squidpony.epigon.data.mixin.Wearable;
import squidpony.epigon.data.mixin.Wieldable;
import squidpony.epigon.data.mixin.Zappable;
import squidpony.epigon.universe.Rating;

import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidmath.OrderedMap;
import squidpony.squidmath.ProbabilityTable;

/**
 * Base class for all classes that have physical properties in the world.
 *
 * @author Eben Howard - http://squidpony.com
 */
public class PhysicalBlueprint extends EpiData {

    public PhysicalBlueprint parent;
    public PhysicalSoundBlueprint sound;

    public char symbol = ' '; // defautl to an empty character sine NUL is not fun in data
    public SColor color;

    public List<String> possibleAliases;
    public List<Modification> possibleModifications;
    public List<Modification> modifications;
    public List<Modification> whenUsedAsMaterial;
    public OrderedMap<Element, Float> passthroughResistances;
    public List<ConditionBlueprint> possibleConditions;
    public List<ConditionBlueprint> conditions;

    public EnumMap<Stat, Integer> baseStats = new EnumMap<>(Stat.class);
    public List<PhysicalBlueprint> commonInventory;

    /**
     * The list of physical objects it drops on destruction no matter what the source.
     */
    public List<ProbabilityTable<PhysicalBlueprint>> physicalDrops;

    /**
     * A list of what the item might become when a given element is used on it.
     */
    public OrderedMap<Element, List<ProbabilityTable<PhysicalBlueprint>>> becomes;

    /**
     * If the given skill is possessed then a given string will be presented as the identification.
     * The description will be used if no matching skill is available.
     */
    public OrderedMap<Skill, OrderedMap<Rating, String>> identification;

    /**
     * When marked generic the item won't be created in the world.
     */
    public boolean generic;

    /**
     * When marked as unique the item will only be created once at most per world.
     */
    public boolean unique;

    public Rating rarity;

    public int baseValue;
    public boolean large;

    public Ammunition ammunitionData;
    public Container containerData;
    public Creature creatureData;
    public Interactable interactableData;
    public Liquid liquidData;
    public Readable readableData;
    public Wearable wearableData;
    public Wieldable wieldableData;
    public Zappable zappableData;

    public boolean hasParent(PhysicalBlueprint blueprint) {
        if (this.equals(blueprint)) {
            return true;
        } else if (this.parent == null) {
            return false;
        }

        return parent.hasParent(blueprint);
    }
}
