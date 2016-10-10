package squidpony.data.blueprints;

import squidpony.data.EpiData;

/**
 * Represents a modification to another object.
 *
 * This can be quite extensive, changing all features of the given object. It
 * can also change different features depending on the object type and item
 * interfaces implemented.
 *
 * For example, a modification of "Ice" might add certain resistances to a
 * creature and modify the liquidity of liquids.
 *
 * @author Eben Howard - http://squidpony.com - howard@squidpony.com
 */
public class ModificationBlueprint extends EpiData {

    public boolean perk = false;//defaults to being permanent
}
