package squidpony.epigon.data.generic;

import java.util.EnumMap;
import java.util.List;

import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidmath.OrderedMap;

import squidpony.epigon.data.EpiData;
import squidpony.epigon.data.blueprint.TerrainBlueprint;
import squidpony.epigon.data.mixin.Creature;
import squidpony.epigon.universe.Element;
import squidpony.epigon.universe.LiveValueModification;
import squidpony.epigon.universe.RatingValueModification;
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

    // Only one string out of the set of prefixes and postfixes should be used
    public List<String> possiblePrefix;
    public List<String> possiblePostfix;
    
    public Character symbol;

    // Only one color change should be used
    public SColor colorOverwrite;
    public SColor colorMultiply;

    public List<Modification> whenUsedAsMaterialOverwrite;
    public List<Modification> whenUsedAsMaterialAdditive;

    public EnumMap<Stat, LiveValueModification> statChanges = new EnumMap<>(Stat.class);

    // Creature changes
    public Creature overwriteCreature; // Become a new creature (or become one for the first time)
    public OrderedMap<Skill, RatingValueModification> skillChanges = new OrderedMap<>();

    public OrderedMap<Element, Integer> elementResistanceChanges = new OrderedMap<>();
    public OrderedMap<TerrainBlueprint, Integer> movementChanges = new OrderedMap<>();
    public OrderedMap<String, Integer> lightEmitted = new OrderedMap<>();
}
