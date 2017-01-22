package squidpony.epigon.data.generic;

import java.util.EnumMap;
import java.util.List;
import java.util.Set;

import squidpony.epigon.data.EpiData;
import squidpony.epigon.data.Stat;
import squidpony.epigon.data.blueprints.ConditionBlueprint;
import squidpony.epigon.data.blueprints.PhysicalBlueprint;
import squidpony.epigon.data.blueprints.RecipeBlueprint;
import squidpony.epigon.universe.Rating;
import squidpony.squidmath.OrderedMap;

/**
 * A grouping of starting skills and stats plus an aptitude towards increasing them, starting
 * equipment, recipes known, starting perks, starting conditions, and starting abilities.
 *
 * Creatures should have a list of what kinds of professions they can have. Modifications may change
 * their list.
 *
 * @author Eben Howard - http://squidpony.com
 */
public class Profession extends EpiData {

    public OrderedMap<Skill, Rating> initialSkillRatings;
    public OrderedMap<Skill, Rating> skillProgress;
    public EnumMap<Stat, Integer> initialStatLevels = new EnumMap<>(Stat.class);
    public EnumMap<Stat, Rating> statProgress = new EnumMap<>(Stat.class);
    public EnumMap<Energy, Integer> initialEnergyLevels = new EnumMap<>(Energy.class);
    public EnumMap<Energy, Rating> energyLevelProgress = new EnumMap<>(Energy.class);
    public List<PhysicalBlueprint> items;
    public Set<RecipeBlueprint> recipes;
    public Set<ConditionBlueprint> perks;
    public List<ConditionBlueprint> conditions;
    public Set<Ability> abilities;
}
