package squidpony.epigon.data.blueprints;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import squidpony.epigon.data.Stat;
import squidpony.epigon.data.generic.Ability;
import squidpony.epigon.data.generic.Skill;
import squidpony.epigon.universe.Rating;

/**
 * Represents an object which can act and has stats, abilities, skills, and so
 * on just like the player.
 *
 * @author Eben Howard - http://squidpony.com
 */
public class CreatureBlueprint extends PhysicalBlueprint {

    public CreatureBlueprint parent;
    public BodyBlueprint body;
    public double opacity;//0.0 is transparent, 1.0 is opaque
    public Map<Skill, Rating> skills = new HashMap<>();
    public ArrayList<Ability> abilities = new ArrayList<>();
    public Stack<ItemBlueprint> inventory = new Stack<>();
    public EnumMap<Stat, Integer> maxStats = new EnumMap<>(Stat.class);
    public ArrayList<ConditionBlueprint> preexistingAuras = new ArrayList<>();
    public float visionRange, hearingRange;
    
    /* Properties Creatures Need to model real life 
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
     * size
     * weight
     * food type (and specific animals / plants preferred)
     * needed food intake per day
     * 
     */
}
