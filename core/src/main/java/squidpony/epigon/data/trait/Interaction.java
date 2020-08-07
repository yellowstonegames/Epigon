package squidpony.epigon.data.trait;

import squidpony.epigon.Epigon;
import squidpony.epigon.data.Physical;

/**
 * The function part of an {@link Interactable}. Usually this should be a lambda.
 * <br>
 * Created by Tommy Ettinger on 1/5/2019.
 */
@FunctionalInterface
public interface Interaction {
    /**
     * @param actor the Physical doing the interaction, usually a creature
     * @param target the target of the interaction, which may be an object or creature
     * @param main the main state of the {@link Epigon} program, allowing interactions significant flexibility
     * @return any text to print, which may have markup that should be processed by {@link squidpony.Messaging} and {@link squidpony.squidgrid.gui.gdx.GDXMarkup}
     */
    String interact(Physical actor, Physical target, Epigon main);
}
