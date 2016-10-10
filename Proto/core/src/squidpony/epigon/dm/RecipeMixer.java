package squidpony.epigon.dm;

import java.util.ArrayList;
import java.util.TreeMap;
import squidpony.epigon.data.blueprints.ItemBlueprint;
import squidpony.epigon.data.blueprints.RecipeBlueprint;

/**
 * This class does all the recipe mixing. It has methods for creating objects
 * based on recipes in various categories.
 *
 * Results may be based on using a specific recipe with specific items, or by
 * looking for a result in a recipe and then building it with that recipe.
 *
 * @author Eben Howard - http://squidpony.com
 */
public class RecipeMixer {

    TreeMap<ItemBlueprint, ArrayList<RecipeBlueprint>> resulteMap = new TreeMap<>();//holds a list of all recipes that he object type can be made from
    TreeMap<RecipeBlueprint, ArrayList<ItemBlueprint>> recipeMape = new TreeMap<>();//holds a list of all the object types that a given recipe can make
    TreeMap<ItemBlueprint, ArrayList<RecipeBlueprint>> ammunitionMap = new TreeMap<>();
    TreeMap<ItemBlueprint, ArrayList<RecipeBlueprint>> animateMape = new TreeMap<>();
    TreeMap<ItemBlueprint, ArrayList<RecipeBlueprint>> containerMap = new TreeMap<>();
    TreeMap<ItemBlueprint, ArrayList<RecipeBlueprint>> creatureMap = new TreeMap<>();
    TreeMap<ItemBlueprint, ArrayList<RecipeBlueprint>> interactableMap = new TreeMap<>();
    TreeMap<ItemBlueprint, ArrayList<RecipeBlueprint>> liquidMap = new TreeMap<>();
    TreeMap<ItemBlueprint, ArrayList<RecipeBlueprint>> readableMap = new TreeMap<>();
    TreeMap<ItemBlueprint, ArrayList<RecipeBlueprint>> wearableMap = new TreeMap<>();
}
