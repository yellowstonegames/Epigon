package squidpony.epigon.data;

import squidpony.epigon.ConstantKey;
import squidpony.epigon.Utilities;
import squidpony.squidmath.Arrangement;

/**
 * List of the possible skills such as Woodchopping, Archery, etc.
 *
 * @author Eben Howard - http://squidpony.com
 */
public enum Skill implements ConstantKey {
    COOKING, BAKING(COOKING), FRYING(COOKING), BOILING(COOKING),
    CANNING(COOKING), FOOD_DRYING(COOKING),
    FOOD_PREP(COOKING), FOOD_CHOPPING(FOOD_PREP), FOOD_MIXING(FOOD_PREP),
    
    GATHERING, HERBALISM(GATHERING), POISON_SEEKING(HERBALISM),
    LUMBERJACKING(GATHERING), MINING(GATHERING), PROSPECTING(MINING),
    HUNTING(GATHERING), TRAPPING(HUNTING), FISHING(HUNTING), TRACKING(HUNTING),
    
    FARMING, HERDING(FARMING), EXOTIC_HERDING(HERDING), BUTCHERING(FARMING),
    AGRICULTURE(FARMING), CROP_PRESERVATION(AGRICULTURE), COMPOSTING(AGRICULTURE), CROP_SELECTION(AGRICULTURE),
    
    COMBAT,
    ASSASSIN(COMBAT), BRAWLER(COMBAT), BRUTE(COMBAT), DERVISH(COMBAT), DUELIST(COMBAT),
    GUARDIAN(COMBAT), HUNTER(COMBAT), JESTER(COMBAT), MARKSMAN(COMBAT), SWORDSMAN(COMBAT), 
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

    Skill() {
        this(null);
    }

    Skill(Skill parent) {
        prettyName = Utilities.lower(name(), "_");
        hash = ConstantKey.precomputeHash("creature.Skill", ordinal());
        this.parent = parent;
    }
    public final long hash;
    @Override
    public long hash64() {
        return hash;
    }
    @Override
    public int hash32() {
        return (int)(hash & 0xFFFFFFFFL);
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

    public Skill getParent() {
        return parent;
    }
}
