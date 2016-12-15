package squidpony.epigon.data;

/**
 * Represents all of the possible base stats and health bars.
 *
 * @author Eben Howard - http://squidpony.com
 */
public enum Stat {

    AIM("Controls chance of hitting targets with physical attacks."),
    ATTACK_POWER("Controls how much damage is dealt with physical attacks."),
    PRECISION("Controls chance of critical damage with physical attacks"),
    AGILITY("Controls chance to completely avoid enemy physical attacks."),
    DEFLECTION("Controls chance to reduce the base damage dealt by enemy physical attacks."),
    TOUGHNESS("Controls chance to reduce the critical damage dealt by enemy physical attacks."),
    SPIRIT_POWER("Controls how much damage or how big the effect is for spiritual abilities."),
    SPIRIT_CONNECTIVITY("Controls damage resistance to spiritual attacks."),
    SPIRIT_CONTROL("Controls chance to cause critical damage or effects with spiritual attacks."),
    IQ("Controls rate at which some skills can be learned."),
    CREATIVITY("Controls the rate at which som skills can be learned."),
    KNOWLEDGE("Controls chance to get critical success when using skills."),
    APPEARANCE("Controls reactions from some NPCs"),
    CONVICTION("Controls reactions from some NPCs.");
    
    private final String description;
    
    private Stat(String description){
        this.description = description;
    }
    
    public String description() {
        return description;
    }
    
    @Override
    public String toString() {
        return (name().substring(0, 1).toUpperCase() + name().substring(1)).replace('_', ' ');
    }
}
