package squidpony.epigon.data.trait;

import squidpony.epigon.data.Physical;
import squidpony.epigon.mapping.EpiMap;

/**
 * Created by Tommy Ettinger on 1/5/2019.
 */
@FunctionalInterface
public interface Interaction {
    /**
     * 
     * @param actor the Physical doing the interaction, usually a creature
     * @param target the target of the interaction, which may be an object or creature
     * @param level the current level of the world, so the interaction can have effects on the surroundings
     * @return any text to print, which may have markup that should be processed by {@link squidpony.squidgrid.gui.gdx.GDXMarkup}
     */
    String interact(Physical actor, Physical target, EpiMap level);
}
