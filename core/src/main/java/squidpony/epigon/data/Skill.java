package squidpony.epigon.data;

import squidpony.epigon.ConstantKey;
import squidpony.epigon.util.Utilities;
import squidpony.squidmath.Arrangement;
import squidpony.squidmath.Hashers;
import squidpony.squidmath.OrderedMap;
import squidpony.squidmath.OrderedSet;

/**
 * List of the possible skills such as Woodchopping, Archery, etc.
 *
 * @author Eben Howard - http://squidpony.com
 */
public enum Skill implements ConstantKey {
    COOKING, BAKING(COOKING), FRYING(COOKING), BOILING(COOKING),
    CANNING(COOKING), FOOD_DRYING(COOKING),
    FOOD_PREP(COOKING), FOOD_CHOPPING(FOOD_PREP), FOOD_MIXING(FOOD_PREP), GARNISHING(FOOD_PREP),
    BREWING(COOKING), CHEESEMAKING(COOKING),
    
    GATHERING, HERBALISM(GATHERING), POISON_SEEKING(HERBALISM),
    LUMBERJACKING(GATHERING), MINING(GATHERING), PROSPECTING(MINING),
    
    HUNTING, TRAPPING(HUNTING), FISHING(HUNTING), TRACKING(HUNTING),
    BUTCHERING(HUNTING), TANNING(BUTCHERING),

    FARMING, HERDING(FARMING), EXOTIC_HERDING(HERDING), DAIRY_FARMING(HERDING), SHEARING(HERDING),
    AGRICULTURE(FARMING), CROP_PRESERVATION(AGRICULTURE), COMPOSTING(AGRICULTURE), CROP_SELECTION(AGRICULTURE),
    
    COMBAT,
    WEAPONRY(COMBAT),
    ASSASSIN(WEAPONRY), BRAWLER(WEAPONRY), BRUTE(WEAPONRY), DERVISH(WEAPONRY), DUELIST(WEAPONRY),
    GUARDIAN(WEAPONRY), HUNTER(WEAPONRY), JESTER(WEAPONRY), MARKSMAN(WEAPONRY), SWORDSMAN(WEAPONRY), 
    MAGIC(COMBAT),
    SEER(MAGIC), SHAMAN(MAGIC), PRIEST(MAGIC), DIABOLIST(MAGIC), WIZARD(MAGIC), SORCERER(MAGIC);

    //    public Skill cooking = new Skill("cooking");
//    public Skill baking = new Skill("baking", cooking);
//    public Skill frying = new Skill("frying", cooking);
//    public Skill boiling = new Skill("boiling", cooking);
//    public Skill foodPrep = new Skill("food prep", cooking);
//    public Skill foodChopping = new Skill("food chopping", foodPrep);
//    public Skill foodMixing = new Skill("food mixing", foodPrep);
//    public Skill canning = new Skill("canning", cooking);
//    public Skill foodDrying = new Skill("food drying", cooking);
//
//    // Gathering skills
//    public Skill gathering = new Skill("gathering");
//    public Skill butchering = new Skill("butchering", gathering);
//    public Skill farming = new Skill("farming", gathering);
//    public Skill fishing = new Skill("fishing", gathering);
//    public Skill herbalism = new Skill("herbalism", gathering);
//    public Skill hunting = new Skill("hunting", gathering);
//    public Skill mining = new Skill("mining", gathering);
//    public Skill woodcutting = new Skill("wood cutting", gathering);
//    ;
    private final String prettyName;
    private final Skill parent;
    private final OrderedSet<Skill> parentChain;
    Skill() {
        this(null);
    }

    Skill(Skill parent) {
        prettyName = Utilities.lower(name(), "_");
        hash = ConstantKey.precomputeHash("creature.Skill", ordinal());
        this.parent = parent;
        parentChain = new OrderedSet<>(3, ConstantKeyHasher.instance);
        while (parent != null)
        {
            parentChain.add(parent);
            parent = parent.parent;
        }
    }
    public final long hash;
    @Override
    public long hash64() {
        return hash;
    }
    @Override
    public int hash32() {
        return (int)(hash);
    }
    
    @Override
    public String toString() {
        return prettyName;
    }
    public static final Skill[] all = values();
    public static Arrangement<Skill> combatSkills = new Arrangement<>(new Skill[]{
            ASSASSIN
            , BRAWLER
            , BRUTE
            , DERVISH
            , DUELIST
            , GUARDIAN
            , HUNTER
            , JESTER
            , MARKSMAN
            , SWORDSMAN
            , SEER
            , SHAMAN
            , PRIEST
            , DIABOLIST
            , WIZARD
            , SORCERER}, ConstantKeyHasher.instance);
    public static final OrderedMap<String, Skill> skillsByName = new OrderedMap<>(all.length, Hashers.caseInsensitiveStringHasher);
    static {
        for (Skill a : all) {
            skillsByName.put(a.prettyName, a);
        }
    }

    public Skill getParent() {
        return parent;
    }

    /**
     * Returns true if this Skill has parentSkill as a possibly-indirect ancestor, or false otherwise.
     * @param parentSkill a Skill that could be a parent, grandparent, etc. of this Skill
     * @return true if this Skill descends from parentSkill, directly or indirectly; false otherwise
     */
    public boolean descendsFrom(Skill parentSkill)
    {
        return parentChain.contains(parentSkill);
    }

    /**
     * Returns the approximate level of how specific this Skill is, measured by how many more-general ancestors it has.
     * Top-level Skills have specificity 0.
     * @return how specific this Skill is, from 0 to any positive int (though probably no higher than 5 or so)
     */
    public int specificity()
    {
        return parentChain.size();
    }
}
