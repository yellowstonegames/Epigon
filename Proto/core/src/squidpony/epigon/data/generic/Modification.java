package squidpony.epigon.data.generic;

import java.util.EnumMap;

import squidpony.epigon.data.EpiData;
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
    public EnumMap<Stat, Integer> statChanges = new EnumMap<>(Stat.class);
}
