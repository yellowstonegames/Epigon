package squidpony.epigon.dm;

import java.util.Map.Entry;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidgrid.gui.gdx.SquidColorCenter;
import squidpony.squidmath.OrderedMap;

import squidpony.epigon.data.blueprint.ConditionBlueprint;
import squidpony.epigon.data.blueprint.RecipeBlueprint;
import squidpony.epigon.data.generic.Modification;
import squidpony.epigon.data.mixin.Creature;
import squidpony.epigon.data.specific.Condition;
import squidpony.epigon.data.specific.Physical;
import squidpony.epigon.data.specific.Recipe;
import squidpony.epigon.data.blueprint.Stone;
import squidpony.epigon.data.mixin.Terrain;
import squidpony.epigon.universe.LiveValue;
import squidpony.epigon.universe.Rating;

import static squidpony.epigon.Epigon.rng;

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

    public Stream<RecipeBlueprint> blueprintsContainingIngredient(Physical ingredient) {
        return recipes.stream().filter(r -> r.uses(ingredient));
    }

    public Recipe createRecipe(RecipeBlueprint blueprint) {
        Recipe recipe = new Recipe();
        recipe.consumed = new OrderedMap<>(blueprint.requiredConsumed);

        // TODO - flesh out into larger grabbing of optionals
        if (blueprint.optionalConsumed != null && !blueprint.optionalConsumed.isEmpty()) {
            Entry<Physical, Integer> entry = blueprint.optionalConsumed.randomEntry(rng);
            recipe.consumed.merge(entry.getKey(), entry.getValue(), Integer::sum);
        }

        recipe.catalyst = new OrderedMap<>(blueprint.requiredCatalyst);

        // TODO - flesh out into larger grabbing of optionals
        if (blueprint.optionalCatalyst != null && !blueprint.optionalCatalyst.isEmpty()) {
            Entry<Physical, Integer> entry = blueprint.optionalCatalyst.randomEntry(rng);
            recipe.catalyst.merge(entry.getKey(), entry.getValue(), Integer::sum);
        }

        recipe.result = new OrderedMap<>();

        // TODO - modify results based on chosen optionals
        recipe.result.putAll(blueprint.result);

        return recipe;
    }

    public Physical createBlueprint(Stone stone) {
        Physical blueprint = new Physical();
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

    public Physical buildPhysical(Physical blueprint) {
        return buildPhysical(blueprint, Rating.NONE);
    }

    public Physical buildPhysical(Physical blueprint, Rating rarity) {
        if (blueprint.generic) {
            throw new IllegalArgumentException("Physical blueprint " + blueprint.name + " marked generic, cannot create.");
        }

        if (blueprint.unique) {
            // TODO - check for whether one has been created
        }

        Physical physical = new Physical();

        physical.description = blueprint.description;
        physical.notes = blueprint.notes; // TODO - probably don't need these transfered
        physical.parent = blueprint;

        List<String> possibleNames = new ArrayList<>();
        possibleNames.addAll(blueprint.possibleAliases);
        possibleNames.add(blueprint.name);
        physical.name = rng.getRandomElement(possibleNames);
        physical.possibleAliases.addAll(blueprint.possibleAliases); // TODO - lock it to the one made once it's made?
        
        physical.countsAs.addAll(blueprint.countsAs);
        physical.createdFrom.add(blueprint); // TODO - limit to "important" items
        physical.generic = blueprint.generic;
        physical.unique = blueprint.unique;
        physical.buildingBlock = blueprint.buildingBlock;
        
        physical.symbol = blueprint.symbol;
        physical.color = blueprint.color == null ? SColor.GRAY : blueprint.color;
        physical.baseValue = blueprint.baseValue;
        physical.large = blueprint.large;

        physical.lightEmitted = blueprint.lightEmitted;
        physical.lightEmittedStrength = blueprint.lightEmittedStrength;

        physical.whenUsedAsMaterial.addAll(blueprint.whenUsedAsMaterial);

        physical.elementalDamageMultiplyer = new OrderedMap<>(blueprint.elementalDamageMultiplyer);

        // TODO - figure out whether conditions should be copied or only come from modifications
//        for (ConditionBlueprint c : blueprint.conditions) {
//            physical.applyCondition(createCondition(c));
//        }

        blueprint.stats.entrySet().stream().forEach(kvp -> {
            physical.stats.put(kvp.getKey(), new LiveValue(kvp.getValue()));
        });

        physical.statProgression.putAll(blueprint.statProgression);

        blueprint.inventory.stream().forEach(i -> {
            physical.inventory.add(buildPhysical(i));
        });

        physical.physicalDrops = blueprint.physicalDrops;
        physical.elementDrops = blueprint.elementDrops;

        physical.identification.putAll(blueprint.identification);

        physical.creatureData = createCreature(blueprint.creatureData);

        physical.terrainData = blueprint.terrainData;

        // TODO - add rest of mixins
        
        // finally work any modifications
        for (Modification m : blueprint.requiredModifications) {
            applyModification(physical, m);
        }

        int count = rng.nextInt(blueprint.optionalModifications.size());
        Set<Integer> ints = new HashSet<>();
        for (int i = 0; i < count; i++){
            int n;
            do{
                n = rng.nextInt(count);
            } while (ints.contains(n));
            ints.add(n);
            applyModification(physical, blueprint.optionalModifications.get(n));
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
        physical.modifications.add(modification);

        if (modification.parentOverwrite != null) {
            physical.parent = modification.parentOverwrite;
        } else if (modification.parentBecomesNull != null && modification.parentBecomesNull) {
            physical.parent = null;
        }

        if (modification.contentsOverwrite != null) {
            physical.countsAs = new HashSet<>(modification.countsAs);
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

        if (modification.color != null) {
            physical.color = modification.color;
        }

        if (modification.lightEmitted != null) {
            physical.lightEmitted = modification.lightEmitted;
        }

        if (modification.lightEmittedStrenghtChange != null) {
            physical.lightEmittedStrength.modify(modification.lightEmittedStrenghtChange);
        }

        physical.elementalDamageMultiplyer.putAll(modification.elementalDamageMultiplier);

        if (modification.whenUsedAsMaterial != null) {
            physical.whenUsedAsMaterial = new ArrayList<>(modification.whenUsedAsMaterial);
        }
    }
}
