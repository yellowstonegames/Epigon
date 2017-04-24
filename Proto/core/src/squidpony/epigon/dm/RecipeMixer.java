package squidpony.epigon.dm;

import squidpony.epigon.Epigon;
import squidpony.epigon.data.blueprint.PhysicalBlueprint;
import squidpony.epigon.data.blueprint.RecipeBlueprint;
import squidpony.epigon.data.specific.Recipe;
import squidpony.squidmath.OrderedMap;
import squidpony.squidmath.StatefulRNG;

import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Stream;

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
}
