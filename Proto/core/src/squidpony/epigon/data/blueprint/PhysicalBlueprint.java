package squidpony.epigon.data.blueprint;

import squidpony.epigon.data.generic.Modification;
import java.util.EnumMap;
import java.util.List;
import java.util.Set;

import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidmath.OrderedMap;
import squidpony.squidmath.ProbabilityTable;

import squidpony.epigon.data.EpiData;
import squidpony.epigon.universe.Element;
import squidpony.epigon.data.generic.Skill;
import squidpony.epigon.universe.Stat;
import squidpony.epigon.data.mixin.Ammunition;
import squidpony.epigon.data.mixin.Container;
import squidpony.epigon.data.mixin.Creature;
import squidpony.epigon.data.mixin.Grouping;
import squidpony.epigon.data.mixin.Interactable;
import squidpony.epigon.data.mixin.Liquid;
import squidpony.epigon.data.mixin.Profession;
import squidpony.epigon.data.mixin.Wearable;
import squidpony.epigon.data.mixin.Wieldable;
import squidpony.epigon.data.mixin.Zappable;
import squidpony.epigon.universe.LiveValue;
import squidpony.epigon.universe.Rating;

/**
 * Base class for all classes that have physical properties in the world.
 *
 * @author Eben Howard - http://squidpony.com
 */
public class PhysicalBlueprint extends EpiData {

    public PhysicalBlueprint parent;

    public char symbol = ' '; // default to an empty character sine NUL is not fun in data
    public SColor color;

    public List<String> possibleAliases;
    public List<Modification> possibleModifications;
    public List<Modification> modifications;
    public List<Modification> whenUsedAsMaterial;

    public OrderedMap<Element, LiveValue> passthroughResistances;
    public OrderedMap<Element, LiveValue> elementalDamageMultiplyer;
    public SColor lightEmitted;
    public LiveValue lightEmittedStrength;

    public List<ConditionBlueprint> possibleConditions;
    public List<ConditionBlueprint> conditions;

    public EnumMap<Stat, LiveValue> initialStats = new EnumMap<>(Stat.class);
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

    /**
     * The changes to this object (if any) that happen as its rarity is increased. As rarity increases
     * each lower level modification is also included, so a given level's result will be the compounded
     * application of all rarity levels up to and including that level's modification.
     */
    public EnumMap<Rating, List<Modification>> rarityModifications = new EnumMap<>(Rating.class);

    public double baseValue;

    /**
     * Only one large thing can be in a tile at once
     */
    public boolean large;

    public Creature creatureData;
    public List<Set<Profession>> possibleProfessions;

    public Ammunition ammunitionData;
    public Container containerData;
    public Grouping groupingData;
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
