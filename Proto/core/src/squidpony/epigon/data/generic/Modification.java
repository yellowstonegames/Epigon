package squidpony.epigon.data.generic;

import java.util.EnumMap;
import java.util.List;
import java.util.Set;

import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidmath.OrderedMap;

import squidpony.epigon.data.EpiData;
import squidpony.epigon.data.blueprint.ConditionBlueprint;
import squidpony.epigon.data.blueprint.PhysicalBlueprint;
import squidpony.epigon.data.blueprint.RecipeBlueprint;
import squidpony.epigon.data.blueprint.TerrainBlueprint;
import squidpony.epigon.data.mixin.Creature;
import squidpony.epigon.universe.Element;
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

    // Only one string out of the set of prefixes and postfixes should be used
    public List<String> possiblePrefix;
    public List<String> possiblePostfix;

    public Character symbol;

    // Only one color change should be used
    public SColor colorOverwrite;
    public SColor colorMultiply;

    public OrderedMap<String, LiveValueModification> lightEmittedStrenghtChanges = new OrderedMap<>();
    public SColor lightEmittedOverwrite;
    public SColor lightEmittedMultiply;

    public List<Modification> whenUsedAsMaterialOverwrite; // TODO - this might not make sense when more than one material is used
    public List<Modification> whenUsedAsMaterialAdditive; // In addition to the recipe's result

    // When destroyed
    public List<PhysicalBlueprint> physicalDropsOverwrite; // TODO - this needs to work with the base ProbabilityTable
    public List<PhysicalBlueprint> physicalDropsAdditive; // TODO - same regarding ProbabilityTable
    public EnumMap<Element, List<PhysicalBlueprint>> elementDropsOverwrit = new EnumMap<>(Element.class); // TODO - same
    public EnumMap<Element, List<PhysicalBlueprint>> elementDropsAdditive = new EnumMap<>(Element.class); // TODO - same

    public EnumMap<Stat, LiveValueModification> statChanges = new EnumMap<>(Stat.class);
    public EnumMap<Stat, RatingValueModification> statProgressionChanges = new EnumMap<>(Stat.class);

    public OrderedMap<Skill, OrderedMap<Rating, String>> identificationsOverwrite;
    public OrderedMap<Skill, OrderedMap<Rating, String>> identificationsAdditive;

    public EnumMap<Rating, List<Modification>> rarityModificationsOverwrite = new EnumMap<>(Rating.class);
    public EnumMap<Rating, List<Modification>> rarityModificationsAdditive = new EnumMap<>(Rating.class);

    public Boolean largeOverwrite;

    public List<PhysicalBlueprint> gainedItems;
    public List<PhysicalBlueprint> lostItems; // TODO - priority on loss? exact match? what if it's not there?

    // Creature changes
    public Creature overwriteCreature; // Become a new creature (or become one for the first time)
    public OrderedMap<Skill, RatingValueModification> skillChanges = new OrderedMap<>();
    public OrderedMap<Skill, RatingValueModification> skillProgressionChagnes = new OrderedMap<>();

    public OrderedMap<Element, LiveValueModification> elementDamageMultiplierChanges = new OrderedMap<>();
    public OrderedMap<TerrainBlueprint, LiveValueModification> movementChanges = new OrderedMap<>();

    public List<RecipeBlueprint> gainedRecipes;

    public Set<ConditionBlueprint> gainedPerks;
    public Set<ConditionBlueprint> lostPerks;
    public List<ConditionBlueprint> gainedConditions;
    public Set<ConditionBlueprint> lostConditions;

    public Set<Ability> gainedAbilities;
    public Set<Ability> lostAbilities;
}
