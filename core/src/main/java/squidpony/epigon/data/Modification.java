package squidpony.epigon.data;

import com.badlogic.gdx.graphics.Color;
import squidpony.epigon.util.ConstantKey;
import squidpony.epigon.data.quality.Element;
import squidpony.epigon.data.trait.Creature;
import squidpony.epigon.data.trait.Interactable;
import squidpony.epigon.data.trait.Profession;
import squidpony.squidgrid.gui.gdx.Radiance;
import squidpony.squidmath.*;

import java.util.ArrayList;

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
    public ArrayList<String> possiblePrefix = new ArrayList<>();
    public ArrayList<String> possibleSuffix = new ArrayList<>();
    public ArrayList<String> possibleAliases;
    public ArrayList<String> possibleAliasesAdd = new ArrayList<>();
    
    // Modification might change the effective hierarchy
    public Physical parent;
    public Boolean parentBecomesNull; // For something that should no longer be considered a subset of some other thing
    public Boolean retainPreviousParent; // When true the previous parent gets added to the countsAs list
    public UnorderedSet<Physical> countsAs;
    public UnorderedSet<Physical> countsAsGained;
    public UnorderedSet<Physical> countsAsLost;

    public Boolean attached;
    public Boolean generic;
    public Boolean unique;
    public Boolean buildingBlock;

    public char symbol = '\uffff';
    public char overlaySymbol = '\uffff';
    public Color color;
    public Color overlayColor;
    public Radiance radiance;
    public Double baseValue;
    public Double baseValueMultiplier;

    public Boolean large;

    public ArrayList<Modification> whenUsedAsMaterial; // TODO - this might not make sense when more than one material is used
    public ArrayList<Modification> whenUsedAsMaterialAdditive; // In addition to the recipe's result
    public ArrayList<Modification> requiredModifications;
    public ArrayList<Modification> requiredModificationsAdditive;
    public ArrayList<Modification> requiredModificationsSubtractive;
    public ArrayList<Modification> optionalModifications;
    public ArrayList<Modification> optionalModificationsAdditive;
    public ArrayList<Modification> optionalModificationsSubtractive;

//    public OrderedMap<Element, LiveValue> passthroughResistances = new OrderedMap<>(); // TODO - this needs to be different than Element probably
    public OrderedMap<Element, LiveValue> elementalDamageMultiplier = new OrderedMap<>();
    public OrderedMap<Element, LiveValueModification> elementDamageMultiplierChanges = new OrderedMap<>();

    public ArrayList<Condition> conditions;
    public ArrayList<Condition> conditionsAdditive;
    public ArrayList<Condition> conditionsSubtractive;
    public ArrayList<Condition> optionalConditions;
    public ArrayList<Condition> optionalConditionsAdditive;
    public ArrayList<Condition> optionalConditionsSubtractive;

    public OrderedMap<ConstantKey, LiveValue> stats = new OrderedMap<>(ConstantKey.ConstantKeyHasher.instance);
    public OrderedMap<ConstantKey, LiveValueModification> statChanges = new OrderedMap<>(ConstantKey.ConstantKeyHasher.instance);
    public OrderedMap<ConstantKey, Rating> statProgression = new OrderedMap<>(ConstantKey.ConstantKeyHasher.instance);
    public OrderedMap<ConstantKey, RatingValueModification> statProgressionChanges = new OrderedMap<>(ConstantKey.ConstantKeyHasher.instance);
    public OrderedSet<ChangeTable> statEffectsAdditive = new OrderedSet<>(CrossHash.identityHasher);
    public OrderedSet<ChangeTable> statEffectsSubtractive = new OrderedSet<>(CrossHash.identityHasher);
    public ArrayList<Physical> inventory;
    public ArrayList<Physical> inventoryAdditive;
    public ArrayList<Physical> inventorySubtractive; // Removes everything that counts as an item in this list
    public ArrayList<Physical> optionalInventory;
    public ArrayList<Physical> optionalInventoryAdditive;
    public ArrayList<Physical> optionalInventorySubtractive;

    // When destroyed, note that probability table entries can only be fully overwritten, not modified in place
    public ArrayList<WeightedTableWrapper<Physical>> physicalDrops;
    public EnumOrderedMap<Element, ArrayList<WeightedTableWrapper<Physical>>> elementDrops;

    public OrderedMap<Skill, OrderedMap<Rating, String>> identifications;
    public OrderedMap<Skill, OrderedMap<Rating, String>> identificationsAdditive;

    public EnumOrderedMap<Rating, ArrayList<Modification>> rarityModifications = new EnumOrderedMap<>(Rating.class); // Only for blueprints
    public EnumOrderedMap<Rating, ArrayList<Modification>> rarityModificationsAdditive = new EnumOrderedMap<>(Rating.class); // Only for blueprints

    // Creature changes
    public Creature creature; // Become a new creature (or become one for the first time)
    public Boolean removeCreature; // No longer a creature
    public OrderedMap<Skill, RatingValueModification> skillChanges = new OrderedMap<>();
    public OrderedMap<Skill, RatingValueModification> skillProgressionChanges = new OrderedMap<>();
    public ArrayList<Ability> abilities;
    public ArrayList<Ability> abilitiesAdditive;
    public ArrayList<Ability> abilitiesSubtractive;

    public ArrayList<RecipeBlueprint> knownRecipesAdditive; // Can only gain known recipes, never lose them

    public ArrayList<Profession> professionsAdditive; // Can only gain professions, never lose them

    // Ammunition changes
    public ArrayList<ConditionBlueprint> ammunitionCauses;
    public ArrayList<ConditionBlueprint> ammunitionCausesAdditive;
    public ArrayList<ConditionBlueprint> ammunitionCausesSubtractive;
    public ArrayList<Physical> ammunitionLaunchers;
    public ArrayList<Physical> ammunitionLaunchersAdditive;
    public ArrayList<Physical> ammunitionLaunchersSubtractive;
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
    public ArrayList<Physical> contents;
    public ArrayList<Physical> contentsAdditive;
    public ArrayList<Physical> contentsSubtractive;

    // Grouping changes
    public Integer quantity;
    public Integer quantityDelta;

    // Interactable changes
    public ArrayList<Interactable> interactable;
    public ArrayList<Interactable> interactableAdditive;
    public ArrayList<Interactable> interactableSubtractive;

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
    public ArrayList<String> weaponStatusesAdditive;
    public ArrayList<String> weaponStatusesSubtractive;
    public ArrayList<String> weaponManeuversAdditive;
    public ArrayList<String> weaponManeuversSubtractive;

    public static Modification makeBasicChangeTable(ChangeTable changes)
    {
        Modification m = new Modification();
        m.statEffectsAdditive.add(changes);
        return m;
    }
}
