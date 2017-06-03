package squidpony.epigon.universe;

/**
 * Represents all of the possible base stats.
 *
 * @author Eben Howard
 */
public enum Stat {
    // Physical Offense
    AIM("AI", "Controls chance of hitting targets with physical attacks."),
    IMPACT("IM", "Controls how much damage is dealt with physical attacks."),
    PRECISION("PR", "Controls chance of critical damage with physical attacks"),

    // Physical Defense
    AGILITY("AG", "Controls chance to completely avoid enemy physical attacks."), // TODO- change to DODGE ?
    DEFLECTION("DE", "Controls chance to reduce the base damage dealt by enemy physical attacks."),
    TOUGHNESS("TO", "Controls chance to reduce the critical damage dealt by enemy physical attacks."),

    // Magical
    POTENCY("PO", "Controls how much damage or how big the effect is for spiritual abilities."),
    ATTUNEMENT("AT", "Controls damage resistance to spiritual attacks."),
    DOMINION("DO", "Controls chance to cause critical damage or effects with spiritual attacks."),

    // Intellect
    IQ("IQ", "Controls rate at which some skills can be learned."),
    CREATIVITY("CR", "Controls the rate at which som skills can be learned."),
    KNOWLEDGE("KN", "Controls chance to get critical success when using skills."),

    // Social
    APPEARANCE("AP", "Controls reactions from some NPCs"),
    CONVICTION("CO", "Controls reactions from some NPCs."),
    AUTHORITY("AU", "Controls reactions from some NPCs."),

    // Health
    LIFE_FORCE("LI", "Ammount of physical endurance."),
    VITALITY("VI", "Amount of physical energy."),
    SPIRIT("SP", "Amount of spirit energy."),
    INTELLECT("IN", "Amount of mental energy"),
    CHARM("CH", "Amount of social energy"),
    SANITY("SA", "How sane one is. When this is reduced too far, actions are no longer under direct control."),
    
    // Needs
    HUNGER("HU", "How full of nutrition."),
    THIRST("TH", "How full of liquids."),
    REST("RE", "How well rested, needed for intellectual and social healing."),
    SLEEP("SL", "How much sleep, needed for physical and magic healing."),

    // Senses
    SIGHT("SI", "Controls how far optical awareness extends."),
    HEARING("HE", "Controls how far auditory awareness extends."),

    // Utility (Both animate and inanimate objects have these)
    VOLUME("VO", "Generalized accounting of approximate size."),
    MASS("MA", "Approximate mass."),
    OPACITY("OP", "How much light is blocked."),
    STRUCTURE("ST", "How much of itself there is. When this is zero the object no longer exists.");

    public static final Stat[] physicals = {AIM, IMPACT, PRECISION, AGILITY, DEFLECTION, TOUGHNESS};

    public static final Stat[] magicals = {POTENCY, ATTUNEMENT, DOMINION};

    public static final Stat[] socials = {APPEARANCE, CONVICTION, AUTHORITY};

    public static final Stat[] healths = {LIFE_FORCE, VITALITY, SPIRIT, INTELLECT, CHARM, SANITY};

    public static final Stat[] needs = {HUNGER, THIRST, REST, SLEEP};

    public static final Stat[] senses = {SIGHT, HEARING};

    public static final Stat[] utility = {VOLUME, MASS, OPACITY, STRUCTURE};

    private final String nick;
    private final String description;

    private Stat(String nick, String description) {
        this.nick = nick;
        this.description = description;
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
    public Stat getRollover(Stat stat) {
        switch (stat) {
            case LIFE_FORCE:
                return null;//creature is dead if no life force remains
            case VITALITY:
                return LIFE_FORCE;
            case SPIRIT:
                return VITALITY;
            case INTELLECT:
                return SPIRIT;
            case CHARM:
                return VITALITY;
            default:
                return null;
        }
    }

    @Override
    public String toString() {
        return (name().substring(0, 1).toUpperCase() + name().substring(1)).replace('_', ' ');
    }
}
