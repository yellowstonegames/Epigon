package squidpony.epigon.data;

import squidpony.epigon.util.ConstantKey;
import squidpony.epigon.util.Utilities;
import squidpony.squidmath.OrderedMap;

import static squidpony.epigon.data.CalcStat.*;

/**
 * Represents all of the possible base stats.
 *
 * Range of stats is 0 to 20 for physicals, magicals, and socials. Typically a 0 means that
 * there is no ability whatsoever while a 20 is demi-god like abilities.
 *
 * @author Eben Howard
 */
public enum Stat implements ConstantKey {
    // Physical Offense
    AIM("AI", "Controls chance of hitting targets with attacks."),
    IMPACT("IM", "Controls how much damage is dealt with attacks."),
    //PRECISION("PR", "Controls chance of critical damage with physical attacks"), // turned into skill

    // Physical Defense
    DODGE("DO", "Controls chance to completely avoid enemy attacks."),
    //DEFLECTION("DE", "Controls chance to reduce the base damage dealt by enemy physical attacks."), // turned into skill
    TOUGHNESS("TO", "Controls damage resistance against attacks."),

    // Magical
    POTENCY("PO", "Controls the effectiveness of inflicting ailments."),
    ATTUNEMENT("AT", "Controls resilience against ailments."),
    //DOMINION("DO", "Controls chance to cause critical damage or effects with spiritual attacks."), // should be skill

    // Intellect
    // IQ("IQ", "Controls rate at which some skills can be learned."), // this seems unnecessary
    CREATIVITY("CR", "Controls chance to get critical success when using skills."),
    KNOWLEDGE("KN", "Controls the rate at which skills are learned."),

    // Social
    APPEARANCE("AP", "Controls reactions from some NPCs who care how you look."),
    DEVOTION("DE", "Controls reactions from some NPCs with significant religious beliefs."),
    AUTHORITY("AU", "Controls reactions from some NPCs who are either for or against the government."),
    //These last 3 stats affect the true pillars of society: church, state, and strip-club.

    // Health
    VIGOR("VI", "Amount of physical injury you can sustain; includes pain tolerance and general health."),
    ENDURANCE("EN", "Amount of physical energy you have; used to survive combats before injuries are received."), // I think this is like exhaustion/stamina?
    SPIRIT("SP", "Amount of spirit energy; used to power magic spells and items, as well as resist some magic."),
    INTELLECT("IN", "Amount of mental energy; used when performing certain difficult skills."),
    CHARM("CH", "Amount of social energy; used when engaging in social interactions."),
    SANITY("SA", "How sane one is; used to resist fear and horror."),
    
    // Needs
    NUTRITION("NU", "How full of nutrition."), // objects with this are perishable; low nutrition spoils quickly
    HYDRATION("HY", "How full of liquids."), // objects with this are flammable; low hydration catches fire easily 
    REST("RE", "How well rested, needed for mental and social healing."),
    SLEEP("SL", "How much sleep, needed for physical and magic healing."),

    // Senses
    SIGHT("SI", "Controls how far optical awareness extends."),
    HEARING("HE", "Controls how far auditory awareness extends."),

    // Utility (Both animate and inanimate objects have these)
    MOBILITY("MO", "How far movement can take you."),
    VOLUME("VO", "Generalized accounting of approximate size. A human has a Volume of 100."),
    MASS("MA", "Approximate mass."),
    OPACITY("OP", "How much light is blocked."),
    STRUCTURE("ST", "How much of itself there is. When this is zero the object no longer exists.");

//    public static final Stat[] physicals = {AIM, IMPACT, DODGE, TOUGHNESS};
//
//    public static final Stat[] magicals = {POTENCY, ATTUNEMENT};
//
//    public static final Stat[] intellects = {CREATIVITY, KNOWLEDGE};
//
//    public static final Stat[] socials = {APPEARANCE, DEVOTION, AUTHORITY};

//    public static final Stat[] bases = {AIM, IMPACT, DODGE, TOUGHNESS, POTENCY, ATTUNEMENT, CREATIVITY, KNOWLEDGE, APPEARANCE, DEVOTION, AUTHORITY};

//    public static final Stat[] oldHealths = {VIGOR, ENDURANCE, SPIRIT, INTELLECT, CHARM, SANITY};

    public static final Stat[] healths = {VIGOR, ENDURANCE, SPIRIT, SANITY};

    public static final Stat[] needs = {NUTRITION, HYDRATION, REST, SLEEP};

    public static final Stat[] senses = {SIGHT, HEARING};

    public static final Stat[] utilities = {MOBILITY, VOLUME, MASS, OPACITY, STRUCTURE};

    public static final Stat[] rolloverProcessOrder = {REST, SLEEP, HYDRATION, NUTRITION, SANITY, /*CHARM, INTELLECT,*/ SPIRIT, ENDURANCE};

    public static final ConstantKey[] combatStats = {PRECISION, DAMAGE, INFLUENCE, CRIT, EVASION, DEFENSE,
        LUCK, STEALTH, QUICKNESS, RANGE, AREA};

    public static final OrderedMap<String, ConstantKey[]> groups;

    static {
        groups = new OrderedMap<>();
        groups.putAll(new String[]{
            "healths", "needs", "senses", "utilities", "combat"
        }, new ConstantKey[][]{
            healths, needs, senses, utilities, combatStats
        });
    }

    private final String nick;
    private final String description;
    private final String prettyName;

    Stat(String nick, String description) {
        this.nick = nick;
        this.description = description;
        prettyName = Utilities.caps(name(), "_");
        hash = ConstantKey.precomputeHash("creature.Stat", ordinal());
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
    
    public String nick(){
        return nick;
    }

    public String description() {
        return description;
    }

    /**
     * Returns the type of energy pool that damage to the passed in pool should be rolled over to
     * when the passed in energy pool is depleted.
     *
     * If roll over damage cannot be rolled over, then null is returned.
     */
    public Stat getRollover() {
        switch (this) {
            case VIGOR:
                return null;//creature is dead if no life force remains
            case ENDURANCE:
                return VIGOR;
            case SPIRIT:
                return ENDURANCE;
            case INTELLECT:
                return SPIRIT;
            case CHARM:
                return SPIRIT;
            case NUTRITION:
                return ENDURANCE;
            case HYDRATION:
                return ENDURANCE;
            case REST:
                return SANITY;
            case SLEEP:
                return SPIRIT;
            case SANITY:
                return SPIRIT;
            default:
                return null;
        }
//        switch (this) {
//            case VIGOR:
//                return null;//creature is dead if no life force remains
//            case ENDURANCE:
//                return VIGOR;
//            case SPIRIT:
//                return ENDURANCE;
//            case INTELLECT:
//                return SPIRIT;
//            case CHARM:
//                return INTELLECT;
//            case NUTRITION:
//                return ENDURANCE;
//            case HYDRATION:
//                return ENDURANCE;
//            case REST:
//                return INTELLECT;
//            case SLEEP:
//                return SANITY;
//            case SANITY:
//                return CHARM;
//            default:
//                return null;
//        }

    }

    @Override
    public String toString() {
        return prettyName;
        //return name().substring(0, 1) + name().substring(1).toLowerCase().replace('_', ' ');
    }
}
