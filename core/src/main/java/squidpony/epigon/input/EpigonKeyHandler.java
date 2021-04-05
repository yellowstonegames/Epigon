package squidpony.epigon.input;

import squidpony.epigon.Epigon;
import squidpony.squidgrid.gui.gdx.SquidInput.KeyHandler;

/**
 * An interface to manage multiple key handlers used for Epigon.
 */
public interface EpigonKeyHandler extends KeyHandler {

    /**
     * Sets the Epigon for direct referencing where needed and returns itself for chaining.
     * @param epigon
     * @return 
     */
    public EpigonKeyHandler setEpigon(Epigon epigon);
}
