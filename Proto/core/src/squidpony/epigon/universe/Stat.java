package squidpony.epigon.universe;

/**
 * Represents all of the possible base stats.
 *
 * @author Eben Howard - http://squidpony.com
 */
public enum Stat {
    // Physical Offense
    AIM("Controls chance of hitting targets with physical attacks."),
    ATTACK_POWER("Controls how much damage is dealt with physical attacks."),
    PRECISION("Controls chance of critical damage with physical attacks"),

    // Physical Defense
    AGILITY("Controls chance to completely avoid enemy physical attacks."), // TODO- change to DODGE ?
    DEFLECTION("Controls chance to reduce the base damage dealt by enemy physical attacks."),
    TOUGHNESS("Controls chance to reduce the critical damage dealt by enemy physical attacks."),

    // Magical
    POTENCY("Controls how much damage or how big the effect is for spiritual abilities."),
    ATTUNEMENT("Controls damage resistance to spiritual attacks."),
    DOMINION("Controls chance to cause critical damage or effects with spiritual attacks."),

    // Intellect
    IQ("Controls rate at which some skills can be learned."),
    CREATIVITY("Controls the rate at which som skills can be learned."),
    KNOWLEDGE("Controls chance to get critical success when using skills."),

    // Social
    APPEARANCE("Controls reactions from some NPCs"),
    CONVICTION("Controls reactions from some NPCs."),
    AUTHORITY("Controls reactions from some NPCs."),

    // Health
    LIFE_FORCE("Ammount of physical endurance."),
    VITALITY("Amount of physical energy."),
    SPIRIT("Amount of spirit energy."),
    INTELLECT("Amount of mental energy"),
    CHARM("Amount of social energy"),
    
    // Needs
    HUNGER("How full of nutrition."),
    THIRST("How full of liquids."),
    REST("How well rested, needed for intellectual and social healing."),
    SLEEP("How much sleep, needed for physical and magic healing."),

    // Senses
    VISION("Controls how far optical awareness extends."),
    HEARING("Controls how far auditory awareness extends."),

    // Utility
    VOLUME("Generalized accounting of approximate size."),
    MASS("Approximate mass."),
    OPACITY("How much light is blocked.");

    private final String description;

    private Stat(String description) {
        this.description = description;
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
