package squidpony.epigon.dm;

import java.util.Map.Entry;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidmath.OrderedMap;
import squidpony.squidmath.StatefulRNG;

import squidpony.epigon.data.blueprint.ConditionBlueprint;
import squidpony.epigon.data.blueprint.PhysicalBlueprint;
import squidpony.epigon.data.blueprint.RecipeBlueprint;
import squidpony.epigon.data.generic.Modification;
import squidpony.epigon.data.mixin.Creature;
import squidpony.epigon.data.specific.Condition;
import squidpony.epigon.data.specific.Physical;
import squidpony.epigon.data.specific.Recipe;
import squidpony.epigon.Epigon;
import squidpony.epigon.data.blueprint.Stone;
import squidpony.epigon.data.blueprint.TerrainBlueprint;
import squidpony.epigon.data.specific.Terrain;
import squidpony.epigon.universe.LiveValue;
import squidpony.epigon.universe.Rating;

/**
 * This class does all the recipe mixing. It has methods for creating objects based on recipes in
 * various categories.
 *
 * Results may be based on using a specific recipe with specific items, or by looking for a result
 * in a recipe and then building it with that recipe.
 *
 * @author Eben Howard - http://squidpony.com
 */
public class RecipeMixer {

    private StatefulRNG rng = Epigon.rng;

    public List<RecipeBlueprint> recipes;

    public Stream<RecipeBlueprint> blueprintsContainingIngredient(PhysicalBlueprint ingredient) {
        return recipes.stream().filter(r -> r.uses(ingredient));
    }

    public Recipe createFrom(RecipeBlueprint blueprint) {
        Recipe recipe = new Recipe();
        recipe.consumed = new OrderedMap<>(blueprint.requiredConsumed);

        // TODO - flesh out into larger grabbing of optionals
        if (blueprint.optionalConsumed != null && !blueprint.optionalConsumed.isEmpty()) {
            Entry<PhysicalBlueprint, Integer> entry = blueprint.optionalConsumed.randomEntry(rng);
            recipe.consumed.merge(entry.getKey(), entry.getValue(), Integer::sum);
        }

        recipe.catalyst = new OrderedMap<>(blueprint.requiredCatalyst);

        // TODO - flesh out into larger grabbing of optionals
        if (blueprint.optionalCatalyst != null && !blueprint.optionalCatalyst.isEmpty()) {
            Entry<PhysicalBlueprint, Integer> entry = blueprint.optionalCatalyst.randomEntry(rng);
            recipe.catalyst.merge(entry.getKey(), entry.getValue(), Integer::sum);
        }

        recipe.result = new OrderedMap<>();

        // TODO - modify results based on chosen optionals
        recipe.result.putAll(blueprint.result);

        return recipe;
    }

    public TerrainBlueprint createFrom(Stone stone){
        TerrainBlueprint blueprint = new TerrainBlueprint();
        blueprint.color = stone.front;
        blueprint.background = stone.back;
        blueprint.stone = stone;
        blueprint.extrusive = stone.extrusive;
        blueprint.intrusive = stone.intrusive;
        blueprint.metamorphic = stone.metamorphic;
        blueprint.sedimentary = stone.sedimentary;
        blueprint.name = stone.toString();
        blueprint.baseValue = stone.value;
        blueprint.symbol = '.';
        return blueprint;
    }

    public Terrain createFrom(TerrainBlueprint blueprint){
        Terrain terrain = (Terrain)createFrom((PhysicalBlueprint)blueprint);
        terrain.background = blueprint.background;
        terrain.extrusive = blueprint.extrusive;
        terrain.metamorphic = blueprint.metamorphic;
        terrain.sedimentary = blueprint.sedimentary;
        return terrain;
    }

    public Physical createFrom(PhysicalBlueprint blueprint) {
        return createFrom(blueprint, Rating.NONE);
    }

    /**
     * Creates a specific instance of the provided blueprint.
     */
    public Physical createFrom(PhysicalBlueprint blueprint, Rating rarity) {
        if (blueprint.generic) {
            throw new IllegalArgumentException("Physical blueprint " + blueprint.name + " marked generic, cannot create.");
        }

        if (blueprint.unique) {
            // TODO - check for whether one has been created
        }

        Physical physical = new Physical();
        physical.parent = blueprint;
        physical.symbol = blueprint.symbol;
        physical.baseValue = blueprint.baseValue;
        physical.color = blueprint.color == null ? SColor.GRAY : blueprint.color;
        physical.large = blueprint.large;

        List<String> possibleNames = new ArrayList<>();
        possibleNames.addAll(blueprint.possibleAliases);
        possibleNames.add(blueprint.name);
        physical.name = rng.getRandomElement(possibleNames);

        physical.description = blueprint.description;
        physical.notes = blueprint.notes; // TODO - probably don't need these transfered

        physical.whenUsedAsMaterial.addAll(blueprint.whenUsedAsMaterial);

        physical.passthroughResistances = new OrderedMap<>(blueprint.passthroughResistances);
        physical.elementalDamageMultiplyer = new OrderedMap<>(blueprint.elementalDamageMultiplyer);

        physical.lightEmitted = blueprint.lightEmitted;
        physical.lightEmittedStrength = blueprint.lightEmittedStrength;

        for (ConditionBlueprint c : blueprint.conditions) {
            physical.applyCondition(createFrom(c));
        }

        if (!blueprint.possibleConditions.isEmpty()) {
            physical.applyCondition(createFrom(rng.getRandomElement(blueprint.possibleConditions)));
        }

        blueprint.initialStats.entrySet().stream().forEach(kvp -> {
            physical.stats.put(kvp.getKey(), new LiveValue(kvp.getValue()));
        });

        physical.statProgression.putAll(blueprint.statProgression);

        blueprint.commonInventory.stream().forEach(i -> {
            physical.inventory.add(createFrom(i));
        });

        physical.physicalDrops = blueprint.physicalDrops;
        physical.elementDrops = blueprint.elementDrops;

        physical.identification.putAll(blueprint.identification);

        physical.creatureData = createFrom(blueprint.creatureData);

        // TODO - add rest of mixins
        // finally work any modifications
        for (Modification m : blueprint.modifications) {
            applyModification(physical, m);
        }

        if (!blueprint.possibleModifications.isEmpty()) {
            applyModification(physical, rng.getRandomElement(blueprint.possibleModifications));
        }

        for (Rating rating : Rating.values()) {
            List<Modification> mods = blueprint.rarityModifications.get(rating);
            if (mods != null) {
                for (Modification m : mods) {
                    applyModification(physical, m);
                }
            }
            if (rarity == rating) { // Only process up to expected rarity level
                break;
            }
        }

        return physical;
    }

    /**
     * Creates a specific Condition from a blueprint.
     */
    public Condition createFrom(ConditionBlueprint blueprint) {
        // TODO - create condition
        return new Condition();
    }

    public Creature createFrom(Creature other) {
        if (other == null) {
            return null;
        }

        Creature creature = new Creature();
        creature.parent = other.parent;
        creature.skills.putAll(other.skills);
        creature.abilities.addAll(other.abilities); // TODO - copy into new abilities
        creature.equippedData = other.equippedData; // TODO - copy into new equippedData

        return creature;
    }

    /**
     * Applies the provided modification to the provided physical in place.
     */
    public void applyModification(Physical physical, Modification modification) {
        // TODO - apply modification
        physical.appliedModifications.add(modification.name);
    }
}
