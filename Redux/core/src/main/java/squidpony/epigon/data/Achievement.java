package squidpony.epigon.data;

/**
 * Represents an achievement that the player can earn in the course of the game.
 *
 * @author Eben Howard - http://squidpony.com
 */
public class Achievement {

    /**
     * Represents the possible types of achievements.
     */
    public enum AchievementType {//TODO -- consider making this three classes that implement an Achievement interface

        META, WORLD, CHARACTER
    };
}
