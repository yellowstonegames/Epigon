package squidpony.epigon.data.generic;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Set;

import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidmath.OrderedMap;
import squidpony.squidmath.ProbabilityTable;

import squidpony.epigon.data.EpiData;
import squidpony.epigon.data.ProbabilityTableEntry;
import squidpony.epigon.data.blueprint.ConditionBlueprint;
import squidpony.epigon.data.blueprint.RecipeBlueprint;
import squidpony.epigon.data.mixin.Creature;
import squidpony.epigon.data.mixin.Profession;
import squidpony.epigon.data.specific.Condition;
import squidpony.epigon.data.specific.Physical;
import squidpony.epigon.universe.Element;
import squidpony.epigon.universe.LiveValue;
import squidpony.epigon.universe.LiveValueModification;
import squidpony.epigon.universe.Rating;
import squidpony.epigon.universe.RatingValueModification;
import squidpony.epigon.universe.Stat;

/**
 * Represents a modification to another object.
 *
 * This can be quite extensive, changing all features of the given object. It can also change
 * different features depending on the object type and item interfaces implemented.
 *
 * For example, a modification of "Ice" might add certain resistances to a creature and modify the
 * liquidity of liquids.
 *
 * Once a Modification has been applied to the object, the Modification object itself does not need
 * to be maintained in reference to the object.
 */
public class Modification extends EpiData {
    public Modification parent;

    // Only one string out of the set of prefixes and postfixes should be used
    public List<String> possiblePrefix = new ArrayList<>();
    public List<String> possiblePostfix = new ArrayList<>();
    public List<String> possibleAliases = new ArrayList<>();
    public List<String> possibleAliasesAdd = new ArrayList<>();
    
    // Modification might change the effective heirarchy
    public Physical parentOverwrite;
    public Boolean parentBecomesNull; // For something that should no longer be considered a subset of some other thing
    public Set<Physical> countsAs;
    public Set<Physical> countsAsGained;
    public Set<Physical> countsAsLost;
    
    public Boolean generic;
    public Boolean unique;
    public Boolean buildingBlock;

    public Character symbol;

    public SColor color;

    public Double baseValue;
    public Double baseValueMultiplier;

    public Boolean large;

    public SColor lightEmitted;
    public LiveValueModification lightEmittedStrenghtChange;

    public List<Modification> whenUsedAsMaterial; // TODO - this might not make sense when more than one material is used
    public List<Modification> whenUsedAsMaterialAdditiive; // In addition to the recipe's result

    public List<Modification> requiredModifications;
    public List<Modification> requiredModificationsAdditive;
    public List<Modification> requiredModificationsSubtractive;

    public List<Modification> optionalModifications;
    public List<Modification> optionamModificationsAdditive;
    public List<Modification> optionalModificationsSubtractive;

//    public OrderedMap<Element, LiveValue> passthroughResistances = new OrderedMap<>(); // TODO - this needs to be different than Element probably
    public OrderedMap<Element, LiveValue> elementalDamageMultiplier = new OrderedMap<>();

    public List<Condition> conditions;
    public List<Condition> conditionsAdditive;
    public List<Condition> conditionsSubtractive;

    public List<Condition> optionalConditions;
    public List<Condition> optionalConditionsAdditive;
    public List<Condition> optionalConditionsSubtractive;

    public EnumMap<Stat, LiveValue> stats = new EnumMap<>(Stat.class);
    public EnumMap<Stat, LiveValueModification> statChanges = new EnumMap<>(Stat.class);
    public EnumMap<Stat, Rating> statProgression = new EnumMap<>(Stat.class);
    public EnumMap<Stat, RatingValueModification> statProgressionChanges = new EnumMap<>(Stat.class);

    public List<Physical> inventory;
    public List<Physical> inventoryAdditive;
    public List<Physical> inventorySubtractive; // TODO - priority on loss? exact match? what if it's not there?
    public List<Physical> optionalInventory;
    public List<Physical> optionalInventoryAdditive;
    public List<Physical> optionalInvntorySubtractive;

    // When destroyed, note that probability table entries can only be fully overwritten, not modified in place
    public List<ProbabilityTable<ProbabilityTableEntry<Physical>>> physicalDropsOverwrite;
    public EnumMap<Element, List<ProbabilityTable<ProbabilityTableEntry<Physical>>>> elementDropsOverwrite = new EnumMap<>(Element.class);

    public OrderedMap<Skill, OrderedMap<Rating, String>> identificationsOverwrite;
    public OrderedMap<Skill, OrderedMap<Rating, String>> identificationsAdditive;

    public EnumMap<Rating, List<Modification>> rarityModificationsOverwrite = new EnumMap<>(Rating.class); // Only for blueprints
    public EnumMap<Rating, List<Modification>> rarityModificationsAdditive = new EnumMap<>(Rating.class); // Only for blueprints

    // Creature changes
    public Creature overwriteCreature; // Become a new creature (or become one for the first time)
    public OrderedMap<Skill, RatingValueModification> skillChanges = new OrderedMap<>();
    public OrderedMap<Skill, RatingValueModification> skillProgressionChagnes = new OrderedMap<>();

    public OrderedMap<Element, LiveValueModification> elementDamageMultiplierChanges = new OrderedMap<>();

    public List<RecipeBlueprint> gainedRecipes;

    public Set<ConditionBlueprint> gainedPerks;
    public Set<ConditionBlueprint> lostPerks;
    public List<ConditionBlueprint> gainedConditions;
    public Set<ConditionBlueprint> lostConditions;

    public Set<Ability> gainedAbilities;
    public Set<Ability> lostAbilities;

    public Set<Profession> gainedProfessions; // Can only gain professions, never lose them

    // Ammunition changes
    public List<ConditionBlueprint> ammunitionCausesOverwrite;
    public Set<ConditionBlueprint> ammunitionCausesRemoved;
    public List<ConditionBlueprint> ammunitionCausesAdded;
    public Set<Physical> ammunitionLaunchersOverwrite;
    public Set<Physical> ammunitionLaunchersRemoved;
    public Set<Physical> ammunitionLaunchersAdded;
    public Boolean ammunitionThrowableOverwrite;
    public Double ammunitionHitChanceOverwrite;
    public Double ammunitionHitChanceDelta;
    public Double ammunitionDamageOverwrite;
    public Double ammunitionDamageDelta;
    public Double ammunitionDistanceOverwrite;
    public Double ammunitionDistanceDelta;

    // Container changes
    public Double capacityOverwrite;
    public Double capacityDelta;
    public List<Physical> contentsOverwrite;
    public List<Physical> contentsRemoved;
    public List<Physical> contentsAdded;

    // Grouping changes
    public Integer quantityOverwrite;
    public Integer quantityDelta;

    // Wearable changes
    public Boolean wornOverwrite;

    // Wieldable changes
    public List<ConditionBlueprint> wieldableCausesOverwrite;
    public Set<ConditionBlueprint> wieldableCausesRemoved;
    public List<ConditionBlueprint> wieldableCausesAdded;
    public Double wieldableHitChanceOverwrite;
    public Double wieldableHitChanceDelta;
    public Double wieldableDamageOverwrite;
    public Double wieldableDamageDelta;
    public Double wieldableDistanceOverwrite;
    public Double wieldableDistanceDelta;
}
