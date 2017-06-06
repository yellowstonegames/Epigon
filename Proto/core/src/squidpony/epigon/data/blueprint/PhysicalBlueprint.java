package squidpony.epigon.data.blueprint;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidmath.OrderedMap;
import squidpony.squidmath.ProbabilityTable;

import squidpony.epigon.data.EpiData;
import squidpony.epigon.data.ProbabilityTableEntry;
import squidpony.epigon.data.generic.Modification;
import squidpony.epigon.universe.Element;
import squidpony.epigon.data.generic.Skill;
import squidpony.epigon.universe.Stat;
import squidpony.epigon.data.mixin.*;
import squidpony.epigon.universe.LiveValue;
import squidpony.epigon.universe.Rating;

/**
 * Base class for all classes that have physical properties in the world.
 *
 * @author Eben Howard - http://squidpony.com
 */
public class PhysicalBlueprint extends EpiData {

    public PhysicalBlueprint parent;
    public Set<PhysicalBlueprint> countsAs = new HashSet<>();

    public char symbol = ' '; // default to an empty character since NUL is not fun in data
    public SColor color;

    public List<String> possibleAliases = new ArrayList<>();
    public List<Modification> modifications = new ArrayList<>();
    public List<Modification> possibleModifications = new ArrayList<>();
    public List<Modification> whenUsedAsMaterial = new ArrayList<>();

    public OrderedMap<Element, LiveValue> passthroughResistances = new OrderedMap<>();
    public OrderedMap<Element, LiveValue> elementalDamageMultiplyer = new OrderedMap<>();

    public SColor lightEmitted;
    public LiveValue lightEmittedStrength;

    public List<ConditionBlueprint> conditions = new ArrayList<>();
    public List<ConditionBlueprint> possibleConditions = new ArrayList<>();

    public EnumMap<Stat, LiveValue> initialStats = new EnumMap<>(Stat.class);
    public List<PhysicalBlueprint> commonInventory = new ArrayList<>();

    /**
     * The list of physical objects it drops on destruction no matter what the source.
     */
    public List<ProbabilityTable<ProbabilityTableEntry<PhysicalBlueprint>>> physicalDrops = new ArrayList<>();

    /**
     * A list of what the item might drop when a given element is used on it. This is in addition to
     * the regular drop table.
     */
    public OrderedMap<Element, List<ProbabilityTable<ProbabilityTableEntry<PhysicalBlueprint>>>> elementDrops = new OrderedMap<>();

    /**
     * If the given skill is possessed then a given string will be presented as the identification.
     * The description will be used if no matching skill is available.
     */
    public OrderedMap<Skill, OrderedMap<Rating, String>> identification = new OrderedMap<>();

    /**
     * When marked generic the item won't be created in the world.
     */
    public boolean generic;

    /**
     * When marked as unique the item will only be created once at most per world.
     */
    public boolean unique;

    /**
     * The changes to this object (if any) that happen as its rarity is increased. As rarity
     * increases each lower level modification is also included, so a given level's result will be
     * the compounded application of all rarity levels up to and including that level's
     * modification.
     */
    public EnumMap<Rating, List<Modification>> rarityModifications = new EnumMap<>(Rating.class);

    public double baseValue;

    /**
     * Only one large thing can be in a tile at once
     */
    public boolean large;

    public Creature creatureData;
    public List<Set<Profession>> possibleProfessions = new ArrayList<>();

    // NOTE - Ammunition and Wieldable need to be sets of how to treat them based on Abilities or CountsAs (not sure which yet)
    public Ammunition ammunitionData;
    public Container containerData;
    public Grouping groupingData;
    public Interactable interactableData;
    public Liquid liquidData;
    public Legible legibileData;
    public Wearable wearableData;
    public Wieldable wieldableData;
    public Zappable zappableData;

    public boolean countsAs(PhysicalBlueprint blueprint) {
        if (this.equals(blueprint) || countsAs.contains(blueprint)) {
            return true;
        } else if (parent == null) {
            return false;
        }

        // Any parent either direct or through something it counts as will work
        return parent.countsAs(blueprint) || countsAs.stream().parallel().anyMatch(bp -> bp.countsAs(blueprint));
    }
}
