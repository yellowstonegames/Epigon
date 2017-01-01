package squidpony.epigon.dm;

import java.util.List;
import java.util.stream.Stream;

import squidpony.epigon.data.blueprints.PhysicalBlueprint;
import squidpony.epigon.data.blueprints.RecipeBlueprint;

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

    public Stream<RecipeBlueprint> containingIngredient(PhysicalBlueprint ingredient) {
        return recipes.stream().filter(r -> r.uses(ingredient));
    }

    public Stream<RecipeBlueprint> creates(PhysicalBlueprint creation) {
        return recipes.stream().filter(r -> r.result.contains(creation));
    }

    public PhysicalBlueprint mix(RecipeBlueprint recipe) {
        return recipe.result.roll(rng);
    }
}
