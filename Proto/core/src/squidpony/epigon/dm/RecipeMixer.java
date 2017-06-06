package squidpony.epigon.dm;

import java.util.ArrayList;
import java.util.List;
import squidpony.epigon.Epigon;
import squidpony.epigon.data.blueprint.PhysicalBlueprint;
import squidpony.epigon.data.blueprint.RecipeBlueprint;
import squidpony.epigon.data.specific.Recipe;
import squidpony.squidmath.OrderedMap;
import squidpony.squidmath.StatefulRNG;

import java.util.Map.Entry;
import java.util.stream.Stream;
import squidpony.epigon.data.generic.Modification;
import squidpony.epigon.data.specific.Physical;
import squidpony.squidgrid.gui.gdx.SColor;

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

        // TODO - modify resulsts based on chosen optionals
        recipe.result.putAll(blueprint.result);

        return recipe;
    }

    /**
     * Creates a specific instance of the provided blueprint.
     */
    public Physical createFrom(PhysicalBlueprint blueprint) {
        Physical physical = new Physical();
        physical.parent = blueprint;
        physical.symbol = blueprint.symbol;
        physical.color = blueprint.color == null ? SColor.GRAY : blueprint.color;

        List<String> possibleNames = new ArrayList<>();
        possibleNames.addAll(blueprint.possibleAliases);
        possibleNames.add(blueprint.name);
        physical.name = rng.getRandomElement(possibleNames);

        physical.description = blueprint.description;
        physical.notes = blueprint.notes; // TODO - probably don't need these transfered

        for (Modification m : blueprint.modifications) {
            applyModification(physical, m);
        }

        if (!blueprint.possibleModifications.isEmpty()) {
            applyModification(physical, rng.getRandomElement(blueprint.possibleModifications));
        }

        physical.passthroughResistances = new OrderedMap<>(blueprint.passthroughResistances);
        physical.elementalDamageMultiplyer = new OrderedMap<>(blueprint.elementalDamageMultiplyer);

        

        return physical;
    }

    /**
     * Applies the provided modification to the provided physical in place.
     */
    public void applyModification(Physical physical, Modification modification) {
        // TODO - apply modification
    }
}
