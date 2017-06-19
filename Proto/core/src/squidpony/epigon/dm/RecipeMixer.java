package squidpony.epigon.dm;

import java.util.Map.Entry;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidmath.OrderedMap;

import squidpony.epigon.data.blueprint.ConditionBlueprint;
import squidpony.epigon.data.blueprint.PhysicalBlueprint;
import squidpony.epigon.data.blueprint.RecipeBlueprint;
import squidpony.epigon.data.generic.Modification;
import squidpony.epigon.data.mixin.Creature;
import squidpony.epigon.data.specific.Condition;
import squidpony.epigon.data.specific.Physical;
import squidpony.epigon.data.specific.Recipe;
import static squidpony.epigon.Epigon.rng;
import squidpony.epigon.data.blueprint.Stone;
import squidpony.epigon.data.mixin.Terrain;
import squidpony.epigon.universe.LiveValue;
import squidpony.epigon.universe.Rating;
import squidpony.squidgrid.gui.gdx.SquidColorCenter;

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

    public List<RecipeBlueprint> recipes;

    public Stream<RecipeBlueprint> blueprintsContainingIngredient(PhysicalBlueprint ingredient) {
        return recipes.stream().filter(r -> r.uses(ingredient));
    }

    public Recipe createRecipe(RecipeBlueprint blueprint) {
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

    public PhysicalBlueprint createBlueprint(Stone stone) {
        PhysicalBlueprint blueprint = new PhysicalBlueprint();
        blueprint.color = stone.front;
        blueprint.name = stone.toString();
        blueprint.baseValue = stone.value;
        blueprint.symbol = '.';
        Terrain terrain = new Terrain();
        blueprint.terrainData = terrain;
        terrain.background = stone.back;
        terrain.stone = stone;
        terrain.extrusive = stone.extrusive;
        terrain.intrusive = stone.intrusive;
        terrain.metamorphic = stone.metamorphic;
        terrain.sedimentary = stone.sedimentary;
        return blueprint;
    }

    public Physical buildPhysical(PhysicalBlueprint blueprint) {
        return buildPhysical(blueprint, Rating.NONE);
    }

    public Physical buildPhysical(PhysicalBlueprint blueprint, Rating rarity) {
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
            physical.applyCondition(createCondition(c));
        }

        if (!blueprint.possibleConditions.isEmpty()) {
            physical.applyCondition(createCondition(rng.getRandomElement(blueprint.possibleConditions)));
        }

        blueprint.initialStats.entrySet().stream().forEach(kvp -> {
            physical.stats.put(kvp.getKey(), new LiveValue(kvp.getValue()));
        });

        physical.statProgression.putAll(blueprint.statProgression);

        blueprint.commonInventory.stream().forEach(i -> {
            physical.inventory.add(buildPhysical(i));
        });

        physical.physicalDrops = blueprint.physicalDrops;
        physical.elementDrops = blueprint.elementDrops;

        physical.identification.putAll(blueprint.identification);

        physical.creatureData = createCreature(blueprint.creatureData);

        physical.terrainData = blueprint.terrainData;

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
    public Condition createCondition(ConditionBlueprint blueprint) {
        // TODO - createRecipe condition
        return new Condition();
    }

    public Creature createCreature(Creature other) {
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
        SquidColorCenter colorCenter = new SquidColorCenter();
        physical.appliedModifications.add(modification.name);

        if (modification.parentOverwrite != null) {
            physical.parent = modification.parentOverwrite;
        } else if (modification.parentBecomesNull != null && modification.parentBecomesNull) {
            physical.parent = null;
        }

        if (modification.contentsOverwrite != null) {
            physical.countsAs = new HashSet<>(modification.countsAsOverwrite);
        } else {
            if (modification.countsAsGained != null) {
                physical.countsAs.addAll(modification.countsAsGained);
            }
            if (modification.countsAsLost != null) {
                physical.countsAs.removeAll(modification.countsAsLost);
            }
        }

        int totalSize = modification.possiblePrefix.size() + modification.possiblePostfix.size();
        if (totalSize > 0) {
            int i = rng.nextInt(totalSize);
            if (i < modification.possiblePrefix.size()) {
                physical.name = modification.possiblePrefix.get(i) + " ";
            } else {
                i -= modification.possiblePrefix.size();
                physical.name += " " + modification.possiblePostfix.get(i);
            }
        }

        if (modification.symbol != null) {
            physical.symbol = modification.symbol;
        }

        if (modification.colorOverwrite != null) {
            physical.color = modification.colorOverwrite;
        }

        if (modification.lightEmittedOverwrite != null) {
            physical.lightEmitted = modification.lightEmittedOverwrite;
        }

        if (modification.lightEmittedStrenghtChanges != null) {
            physical.lightEmittedStrength.modify(modification.lightEmittedStrenghtChanges);
        }

        physical.passthroughResistances.putAll(modification.passthroughResistancesOverwrite);
        physical.elementalDamageMultiplyer.putAll(modification.elementalDamageMultiplierOverwrite);

        if (modification.whenUsedAsMaterialOverwrite != null) {
            physical.whenUsedAsMaterial = new ArrayList<>(modification.whenUsedAsMaterialOverwrite);
        }
    }
}
