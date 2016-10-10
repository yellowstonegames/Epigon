package squidpony.data.generic;

/**
 * Represents the types of energy pools in the game.
 *
 * Includes methods to determine what pool damage is rolled over to if a
 * specific type of energy pool has been depleted.
 *
 * @author Eben Howard - http://squidpony.com - howard@squidpony.com
 */
public enum Energy {

    LIFE_FORCE, VITALITY, SPIRIT, INTELLECT, CHARM;

    /**
     * Returns the type of energy pool that damage to the passed in pool should
     * be rolled over to when the passed in energy pool is depleted.
     *
     * If roll over damage should destroy the creature who's energy pool is
     * being checked, then null is returned.
     *
     * @param health
     * @return
     */
    public Energy getRollover(Energy energy) {
        switch (energy) {
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
}
