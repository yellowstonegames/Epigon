package squidpony.epigon.data.mixin;

import squidpony.epigon.data.generic.Ability;
import squidpony.epigon.data.generic.Skill;
import squidpony.epigon.data.specific.Physical;
import squidpony.epigon.universe.ClothingSlot;
import squidpony.epigon.universe.JewelrySlot;
import squidpony.epigon.universe.OverArmorSlot;
import squidpony.epigon.universe.Rating;
import squidpony.epigon.universe.WieldSlot;
import squidpony.squidmath.EnumOrderedMap;
import squidpony.squidmath.OrderedMap;

import java.util.HashSet;
import java.util.Set;
import squidpony.epigon.data.specific.Recipe;

/**
 * A specific creature in the world.
 *
 * @author Eben Howard - http://squidpony.com
 */
public class Creature {

    public Creature parent;
    public OrderedMap<Skill, Rating> skills = new OrderedMap<>();
    public OrderedMap<Skill, Rating> skillProgression = new OrderedMap<>();
    public Set<Ability> abilities = new HashSet<>();

    public Set<Recipe> knownRecipes = new HashSet<>();
    public OrderedMap<Profession, Rating> professions = new OrderedMap<>();

    // TODO - add validity list for slots on a per-creature type (Humanoid, Quadreped) basis
    public EnumOrderedMap<JewelrySlot, Physical> jewelry = new EnumOrderedMap<>(JewelrySlot.class);
    public EnumOrderedMap<ClothingSlot, Physical> clothing = new EnumOrderedMap<>(ClothingSlot.class);
    public EnumOrderedMap<ClothingSlot, Physical> armor = new EnumOrderedMap<>(ClothingSlot.class);
    public EnumOrderedMap<OverArmorSlot, Physical> overArmor = new EnumOrderedMap<>(OverArmorSlot.class);
    public EnumOrderedMap<WieldSlot, Physical> equipment = new EnumOrderedMap<>(WieldSlot.class);

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
     */
}
