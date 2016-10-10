package squidpony.actions;

import squidpony.mapping.EpiMap;
import squidpony.squidgrid.fov.FOVSolver;
import squidpony.squidgrid.util.RadiusStrategy;

/**
 * Represents an Area-Of-Effect.
 *
 * Includes a notion of radius, directionality, spread style, spread resistance
 * key, animation key.
 *
 * Given an EpiMap, can return a binary array structure in the same coordinate
 * system as the map representing what locations are affected by this AOE.
 *
 * Unlike other systems, this uses the coordinate system relative to the map
 * passed in rather than the world coordinate system.
 *
 * @author Eben Howard - http://squidpony.com - howard@squidpony.com
 */
public class AOE {

    private RadiusStrategy radius;
    private FOVSolver spread;
    private String resistanceKey;
    private String animationKey;

    /**
     * Builds an AOE object that can indicate what locations are effected by it.
     *
     * @param radius the strategy to use to determine radius from its center
     * point
     * @param spread the strategy to use to determine how it spreads from its
     * point of origin
     * @param resistanceKey the key to use when running the FOVStrategy to
     * determine spread
     * @param animationKey the key to use for the display to show the AOE in
     * action
     */
    public AOE(RadiusStrategy radius, FOVSolver spread, String resistanceKey, String animationKey) {
        this.radius = radius;
        this.spread = spread;
        this.resistanceKey = resistanceKey;
        this.animationKey = animationKey;
    }

    /**
     * Returns a map of tiles affected by this AOE.
     *
     * @param map true values indicate tiles affected
     * @param startx the x coordinate center of the effect
     * @param starty the y coordinate center of the effect
     * @param angle the horizontal angle
     * @return
     */
    public boolean[][] effectedArea(EpiMap[][] map, int startx, int starty, float angle) {
        //TODO - fill in
    }
}
