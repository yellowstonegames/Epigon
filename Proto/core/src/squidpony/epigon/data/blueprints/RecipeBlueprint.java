package squidpony.epigon.data.blueprints;

import java.util.List;

import squidpony.epigon.data.EpiData;

import squidpony.squidmath.ProbabilityTable;

/**
 * This is used to create specific recipes in a game world.
 *
 * A blueprint is used instead of direct recipes so that more randomness in the recipes is
 * available. Multiple in-game recipes may be created from a single blueprint, each with somewhat
 * different end results based on this blueprint's parameters.
 *
 * @author Eben Howard - http://squidpony.com
 */
public class RecipeBlueprint extends EpiData {

    public List<PhysicalBlueprint> requiredConsumed;
    public List<PhysicalBlueprint> requiredCatalyst; // ie: a Forge (not consumed)
    public List<PhysicalBlueprint> optionalConsumed; // can add various properties
    public List<PhysicalBlueprint> optionalCatalyst;
    public ProbabilityTable<EpiData> result;

    public boolean uses(PhysicalBlueprint ingredient) {
        return requiredConsumed.contains(ingredient)
            || requiredCatalyst.contains(ingredient)
            || optionalConsumed.contains(ingredient)
            || optionalCatalyst.contains(ingredient);
    }
}
