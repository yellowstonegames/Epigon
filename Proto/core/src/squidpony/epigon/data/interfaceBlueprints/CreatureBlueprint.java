package squidpony.epigon.data.interfaceBlueprints;

import java.util.EnumMap;
import java.util.List;
import java.util.Set;

import squidpony.epigon.data.generic.Stat;
import squidpony.epigon.data.blueprints.BodyBlueprint;
import squidpony.epigon.data.blueprints.ConditionBlueprint;
import squidpony.epigon.data.blueprints.PhysicalBlueprint;
import squidpony.epigon.data.generic.Ability;
import squidpony.epigon.data.generic.Skill;
import squidpony.epigon.universe.Rating;
import squidpony.squidmath.OrderedMap;

/**
 * Represents an object which can act and has stats, abilities, skills, and so on just like the
 * player.
 *
 * @author Eben Howard - http://squidpony.com
 */
public class CreatureBlueprint {

    public CreatureBlueprint parent;
    public BodyBlueprint body;
    public double opacity;//0.0 is transparent, 1.0 is opaque
    public OrderedMap<Skill, Rating> skills;
    public Set<Ability> abilities;
    public List<PhysicalBlueprint> commonInventory;
    public Set<ConditionBlueprint> preexistingConditions;

    /*
     * Properties Creatures Need to model real life
     * 
     * taxonomy
     * habitation areas (with frequency / likelyhood)
     * behavior
     * size of territory
     * breeding method
     * gestation time
     * number of children
     * years to maturity
     * lifespan
     * food type (and specific animals / plants preferred)
     * needed food intake per day
     */
}
