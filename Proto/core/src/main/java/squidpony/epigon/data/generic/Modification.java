package squidpony.epigon.data.generic;

import com.badlogic.gdx.graphics.Color;
import squidpony.epigon.ImmutableKey;
import squidpony.epigon.data.EpiData;
import squidpony.epigon.data.WeightedTableWrapper;
import squidpony.epigon.data.blueprint.ConditionBlueprint;
import squidpony.epigon.data.blueprint.RecipeBlueprint;
import squidpony.epigon.data.mixin.Creature;
import squidpony.epigon.data.mixin.Interactable;
import squidpony.epigon.data.mixin.Profession;
import squidpony.epigon.data.specific.Condition;
import squidpony.epigon.data.specific.Physical;
import squidpony.epigon.data.specific.Weapon;
import squidpony.epigon.universe.*;
import squidpony.squidmath.EnumOrderedMap;
import squidpony.squidmath.OrderedMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

    // Only one string out of the set of prefixes and suffixes should be used
    public List<String> possiblePrefix = new ArrayList<>();
    public List<String> possibleSuffix = new ArrayList<>();
    public List<String> possibleAliases;
    public List<String> possibleAliasesAdd = new ArrayList<>();
    
    // Modification might change the effective hierarchy
    public Physical parent;
    public Boolean parentBecomesNull; // For something that should no longer be considered a subset of some other thing
    public Boolean retainPreviousParent; // When true the previous parent gets added to the countsAs list
    public Set<Physical> countsAs;
    public Set<Physical> countsAsGained;
    public Set<Physical> countsAsLost;

    public Boolean attached;
    public Boolean generic;
    public Boolean unique;
    public Boolean buildingBlock;

    public Character symbol;
    public Character overlaySymbol;
    public Color color;
    public Color overlayColor;

    public Double baseValue;
    public Double baseValueMultiplier;

    public Boolean large;

    public float lightEmitted;
    public LiveValueModification lightEmittedStrengthChange;

    public List<Modification> whenUsedAsMaterial; // TODO - this might not make sense when more than one material is used
    public List<Modification> whenUsedAsMaterialAdditive; // In addition to the recipe's result

    public List<Modification> requiredModifications;
    public List<Modification> requiredModificationsAdditive;
    public List<Modification> requiredModificationsSubtractive;

    public List<Modification> optionalModifications;
    public List<Modification> optionalModificationsAdditive;
    public List<Modification> optionalModificationsSubtractive;

//    public OrderedMap<Element, LiveValue> passthroughResistances = new OrderedMap<>(); // TODO - this needs to be different than Element probably
    public OrderedMap<Element, LiveValue> elementalDamageMultiplier = new OrderedMap<>();
    public OrderedMap<Element, LiveValueModification> elementDamageMultiplierChanges = new OrderedMap<>();

    public List<Condition> conditions;
    public List<Condition> conditionsAdditive;
    public List<Condition> conditionsSubtractive;

    public List<Condition> optionalConditions;
    public List<Condition> optionalConditionsAdditive;
    public List<Condition> optionalConditionsSubtractive;

    public OrderedMap<ImmutableKey, LiveValue> stats = new OrderedMap<>(ImmutableKey.ImmutableKeyHasher.instance);
    public OrderedMap<ImmutableKey, LiveValueModification> statChanges = new OrderedMap<>(ImmutableKey.ImmutableKeyHasher.instance);
    public OrderedMap<ImmutableKey, Rating> statProgression = new OrderedMap<>(ImmutableKey.ImmutableKeyHasher.instance);
    public OrderedMap<ImmutableKey, RatingValueModification> statProgressionChanges = new OrderedMap<>(ImmutableKey.ImmutableKeyHasher.instance);

    public List<Physical> inventory;
    public List<Physical> inventoryAdditive;
    public List<Physical> inventorySubtractive; // Removes everything that counts as an item in this list
    public List<Physical> optionalInventory;
    public List<Physical> optionalInventoryAdditive;
    public List<Physical> optionalInventorySubtractive;

    // When destroyed, note that probability table entries can only be fully overwritten, not modified in place
    public List<WeightedTableWrapper<Physical>> physicalDrops;
    public EnumOrderedMap<Element, List<WeightedTableWrapper<Physical>>> elementDrops;

    public OrderedMap<Skill, OrderedMap<Rating, String>> identifications;
    public OrderedMap<Skill, OrderedMap<Rating, String>> identificationsAdditive;

    public EnumOrderedMap<Rating, List<Modification>> rarityModifications = new EnumOrderedMap<>(Rating.class); // Only for blueprints
    public EnumOrderedMap<Rating, List<Modification>> rarityModificationsAdditive = new EnumOrderedMap<>(Rating.class); // Only for blueprints

    // Creature changes
    public Creature creature; // Become a new creature (or become one for the first time)
    public Boolean removeCreature; // No longer a creature
    public OrderedMap<Skill, RatingValueModification> skillChanges = new OrderedMap<>();
    public OrderedMap<Skill, RatingValueModification> skillProgressionChanges = new OrderedMap<>();
    public List<Ability> abilities;
    public List<Ability> abiliitiesAdditive;
    public List<Ability> abilitiesSubtractive;

    public List<RecipeBlueprint> knownRecipesAdditive; // Can only gain known recipes, never lose them

    public List<Profession> professionsAdditive; // Can only gain professions, never lose them

    // Ammunition changes
    public List<ConditionBlueprint> ammunitionCauses;
    public List<ConditionBlueprint> ammunitionCausesAdditive;
    public List<ConditionBlueprint> ammunitionCausesSubtractive;
    public List<Physical> ammunitionLaunchers;
    public List<Physical> ammunitionLaunchersAdditive;
    public List<Physical> ammunitionLaunchersSubtractive;
    public Boolean ammunitionThrowable;
    public Double ammunitionDistance;
    public Double ammunitionDistanceAdditive;
    public Double ammunitionDistanceMultiplier;

    // TODO - have stats when used as ammunition work the same way as when wielded in hand
    public Double ammunitionHitChance;
    public Double ammunitionHitChanceDelta;
    public Double ammunitionDamage;
    public Double ammunitionDamageDelta;

    // Container changes
    public Double capacity;
    public Double capacityDelta;
    public List<Physical> contents;
    public List<Physical> contentsAdditive;
    public List<Physical> contentsSubtractive;

    // Grouping changes
    public Integer quantity;
    public Integer quantityDelta;

    // Interactable changes
    public List<Interactable> interactable;
    public List<Interactable> interactableAdditive;
    public List<Interactable> interactableSubtractive;

    // Wearable changes
    public Boolean worn;

    // Wieldable changes
//    public List<ConditionBlueprint> wieldableCausesOverwrite;
//    public Set<ConditionBlueprint> wieldableCausesRemoved;
//    public List<ConditionBlueprint> wieldableCausesAdded;
    public WeightedTableWrapper<Element> weaponElements;
    public OrderedMap<Element, Double> weaponElementsAdditive;
//    public Double wieldableHitChanceOverwrite;
//    public Double wieldableHitChanceDelta;
//    public Integer wieldableDamageOverwrite;
//    public Integer wieldableDamageDelta;
//    public Integer wieldableRangeOverwrite;
//    public Integer wieldableRangeDelta;
    public Weapon weaponData;
    public int[] weaponCalcDelta;
    public List<String> weaponStatusesAdditive;
    public List<String> weaponStatusesSubtractive;
    public List<String> weaponManeuversAdditive;
    public List<String> weaponManeuversSubtractive;
}
