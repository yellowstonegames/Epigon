package squidpony.epigon.data.trait;

import squidpony.epigon.ConstantKey;
import squidpony.epigon.data.EpiData;
import squidpony.epigon.data.Modification;
import squidpony.epigon.data.Rating;
import squidpony.epigon.data.Skill;
import squidpony.squidmath.EnumOrderedMap;
import squidpony.squidmath.OrderedMap;

/**
 * A grouping of starting skills and stats plus an aptitude towards increasing them, starting
 * equipment, recipes known, starting perks, starting conditions, and starting abilities.
 *
 * Creatures should have a list of what kinds of professions they can have. Modifications may change
 * their list.
 *
 * In general the title of the Profession with the highest rating should be used when a title is shown.
 */
public class Profession extends EpiData {

    // What someone with this profession should be called
    public String titlePrefix;
    public String titleSuffix;

    public OrderedMap<ConstantKey, Double> initialStatRequirements = new OrderedMap<>(ConstantKey.ConstantKeyHasher.instance);
    // base value of the stat, temporary buffs and debuffs shouldn't affect what profession can be learned
    public OrderedMap<Skill, Rating> initialSkillRequirements = new OrderedMap<>();

    /**
     * Each rating level may or may not have some special modifications that come with it, often in
     * the form of new or improved abilities. Additionally increasing profession rating may increase
     * the stat progression ratings, although not retroactively.
     *
     * When gaining a profession for the first time, modifications from Rating.SLIGHT are applied as that
     * is the starting rating for any Profession.
     */
    public EnumOrderedMap<Rating, Modification> improvements = new EnumOrderedMap<>(Rating.class);

    // Starting parts only for when generating a creature with this profession
    public Modification startingModification;
}
