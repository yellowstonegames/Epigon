package squidpony.epigon.data.trait;

import squidpony.epigon.ConstantKey;
import squidpony.epigon.data.*;
import squidpony.epigon.data.slot.BodySlot;
import squidpony.squidmath.OrderedMap;
import squidpony.squidmath.OrderedSet;
import squidpony.squidmath.ProbabilityTable;

/**
 * A specific creature in the world.
 *
 * @author Eben Howard - http://squidpony.com
 */
public class Creature {

    public Creature parent;
    public OrderedMap<Skill, Rating> skills = new OrderedMap<>();
    public OrderedMap<Skill, Rating> skillProgression = new OrderedMap<>();
    public OrderedSet<Ability> abilities = new OrderedSet<>();

    public OrderedSet<Recipe> knownRecipes = new OrderedSet<>();
    public OrderedMap<Profession, Rating> professions = new OrderedMap<>();

    // TODO - add validity list for slots on a per-creature type (Humanoid, Quadruped) basis
    // validation could be done by calling OrderedMap.keySet().retainAll(validSlots) , I think
    public OrderedMap<BodySlot, Physical> equippedBySlot = new OrderedMap<>(20, 0.7f, ConstantKey.ConstantKeyHasher.instance);
    public OrderedSet<Physical> equippedDistinct = new OrderedSet<>(20, 0.7f);
    public ProbabilityTable<Weapon> weaponChoices;

    public Weapon lastWieldedWeapon = null;
    public Physical lastUsedItem = null;
    
    public int skillWithWeapon(Weapon w)
    {
        int skill = 1;
        for (int i = 0; i < w.skills.length; i++) {
            skill += skills.getOrDefault(w.skills[i], Rating.NONE).ordinal();
        }
        return skill;
    }
    
    // Runtime values
    public boolean aware;//has noticed the player

    /*
     * Properties Creatures Need to model real life
     * 
     * taxonomy
     * habitation areas (with frequency / likelihood)
     * behavior
     * size of territory
     * breeding method
     * gestation time
     * number of children
     * years to maturity
     * lifespan
     * food type (and specific animals / plants preferred)
     * needed food intake per day
     * quality as livestock (dairy, egg-laying, meat, wool/fur)
     */
}
