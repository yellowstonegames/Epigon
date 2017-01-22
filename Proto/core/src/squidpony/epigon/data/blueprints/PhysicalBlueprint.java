package squidpony.epigon.data.blueprints;

import java.util.EnumMap;
import java.util.List;

import squidpony.epigon.data.EpiData;
import squidpony.epigon.data.generic.Element;
import squidpony.epigon.data.generic.Skill;
import squidpony.epigon.data.generic.Stat;
import squidpony.epigon.data.interfaceBlueprints.AmmunitionBlueprint;
import squidpony.epigon.data.interfaceBlueprints.ContainerBlueprint;
import squidpony.epigon.data.interfaceBlueprints.CreatureBlueprint;
import squidpony.epigon.data.interfaceBlueprints.InteractableBlueprint;
import squidpony.epigon.data.interfaceBlueprints.LiquidBlueprint;
import squidpony.epigon.data.interfaceBlueprints.MaterialBlueprint;
import squidpony.epigon.data.interfaceBlueprints.ReadableBlueprint;
import squidpony.epigon.data.interfaceBlueprints.WearableBlueprint;
import squidpony.epigon.data.interfaceBlueprints.WieldableBlueprint;
import squidpony.epigon.data.interfaceBlueprints.ZappableBlueprint;
import squidpony.epigon.universe.Rating;

import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidmath.OrderedMap;
import squidpony.squidmath.ProbabilityTable;

/**
 * Base class for all classes that have physical properties in the world.
 *
 * @author Eben Howard - http://squidpony.com
 */
public class PhysicalBlueprint extends EpiData {

    public char symbol = ' '; // defautl to an empty character sine NUL is not fun in data
    public SColor color;
    public List<String> possibleAliases;
    public List<ModificationBlueprint> possibleModifications;
    public List<ModificationBlueprint> modifications;
    public OrderedMap<Element, Float> passthroughResistances;
    public List<ConditionBlueprint> possibleConditions;
    public List<ConditionBlueprint> conditions;
    public EnumMap<Stat, Integer> baseStats = new EnumMap<>(Stat.class);

    /**
     * The list of physical objects it drops on destruction no matter what the source.
     */
    public List<ProbabilityTable<PhysicalBlueprint>> physicalDrops;

    /**
     * A list of what the item might become when a given element is used on it.
     */
    public OrderedMap<Element, List<ProbabilityTable<PhysicalBlueprint>>> becomes;

    /**
     * If the given skill is possessed then a given string will be presented as the identification.
     * The description will be used if no matching skill is available.
     */
    public OrderedMap<Skill, OrderedMap<Rating, String>> identification;

    /**
     * When marked generic the item won't be created in the world.
     */
    public boolean generic;

    /**
     * When marked as unique the item will only be created once at most per world.
     */
    public boolean unique;

    public Rating rarity;

    public PhysicalSoundBlueprint sound;

    public PhysicalBlueprint parent;

    public int baseValue;
    public boolean large;

    public AmmunitionBlueprint ammunitionData;
    public ContainerBlueprint containerData;
    public CreatureBlueprint creatureData;
    public InteractableBlueprint interactableData;
    public LiquidBlueprint liquidData;
    public MaterialBlueprint materialData;
    public ReadableBlueprint readableData;
    public WearableBlueprint wearableData;
    public WieldableBlueprint wieldableData;
    public ZappableBlueprint zappableData;

    public boolean hasParent(PhysicalBlueprint blueprint) {
        if (this.equals(blueprint)) {
            return true;
        } else if (this.parent == null) {
            return false;
        }

        return parent.hasParent(blueprint);
    }
}
