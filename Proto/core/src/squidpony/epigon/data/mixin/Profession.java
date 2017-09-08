package squidpony.epigon.data.mixin;

import java.util.EnumMap;

import squidpony.squidmath.OrderedMap;

import squidpony.epigon.data.EpiData;
import squidpony.epigon.data.generic.Modification;
import squidpony.epigon.data.generic.Skill;
import squidpony.epigon.universe.Stat;
import squidpony.epigon.universe.Rating;

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

    public EnumMap<Stat, Double> initialStatRequirements = new EnumMap<>(Stat.class); // base value of the stat, temporary buffs and debuffs shouldn't affect what profession can be learned
    public OrderedMap<Skill, Rating> initialSkillRequirements;

    /**
     * Each rating level may or may not have some special modifications that come with it, often in
     * the form of new or improved abilities. Additionally increasing profession rating may increase
     * the stat progression ratings, although not retroactively.
     */
    public EnumMap<Rating, Modification> improvements = new EnumMap<>(Rating.class);

    // Starting parts only for when generating a creature with this profession
    public Modification startingModification;
}
