package squidpony.epigon.data.blueprints;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import squidpony.epigon.data.EpiData;
import squidpony.epigon.data.generic.Element;
import squidpony.epigon.data.generic.Skill;
import squidpony.epigon.data.interfaceBlueprints.AmmunitionBlueprint;
import squidpony.epigon.data.interfaceBlueprints.ContainerBlueprint;
import squidpony.epigon.data.interfaceBlueprints.CreatureBlueprint;
import squidpony.epigon.data.interfaceBlueprints.InteractableBlueprint;
import squidpony.epigon.data.interfaceBlueprints.LiquidBlueprint;
import squidpony.epigon.data.interfaceBlueprints.ReadableBlueprint;
import squidpony.epigon.data.interfaceBlueprints.WearableBlueprint;
import squidpony.epigon.data.interfaceBlueprints.WieldableBlueprint;
import squidpony.epigon.data.interfaceBlueprints.ZappableBlueprint;
import squidpony.epigon.data.specific.Name;
import squidpony.epigon.universe.Rating;

import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidmath.ProbabilityTable;

/**
 * Base class for all classes that have physical properties in the world.
 *
 * @author Eben Howard - http://squidpony.com
 */
public class PhysicalBlueprint extends EpiData {

    public char symbol;
    public SColor color;
    public boolean colorFromMaterial;
    public List<Name> possibleAliases;
    public List<ModificationBlueprint> possibleModifications;
    public List<ModificationBlueprint> modifications;
    public boolean passthroughFromMaterial;
    public Map<String, Float> passthroughResistances = new HashMap<>();
    public List<ConditionBlueprint> possibleConditions;
    public List<ConditionBlueprint> conditions;

    /**
     * The list of physical objects it drops on destruction no matter what the source
     */
    public ProbabilityTable<PhysicalBlueprint> physicalDrops = new ProbabilityTable<>();

    /**
     * A list of what the item might become when a given element is used on it.
     */
    public HashMap<Element, ProbabilityTable<ArrayList<PhysicalBlueprint>>> becomes = new HashMap<>();

    /**
     * If the given skill is possessed then a given string will be presented as the identification.
     * The description will be used if no matching skill is available.
     */
    public HashMap<Skill, HashMap<Rating, String>> identification;

    /**
     * When marked generic the item won't be created in the world.
     */
    public boolean generic = false;

    /**
     * When marked as unique the item will only be created once at most per world.
     */
    public boolean unique;
    public int rarity;

    public String destructionSound, idleSound, movementSound;
    public int destructionVolume, idleVolume, movementVolume;

    public PhysicalBlueprint parent;
    public int fragility; // TODO - how does this work with a creature mixin?
    public int maxCondition; // TODO - how does this work with a creature mixin?
    public int value; // TODO - calculated by mixins?
    public boolean large;
    //
    //possible interfaces
    public AmmunitionBlueprint ammunitionData;
    public ContainerBlueprint containerData;
    public CreatureBlueprint creatureData;
    public InteractableBlueprint interactableData;
    public LiquidBlueprint liquidData;
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
