package squidpony.epigon.input.key;

import squidpony.squidgrid.gui.gdx.SquidInput.KeyHandler;

import squidpony.epigon.Epigon;
import squidpony.epigon.files.Config;

/**
 * An interface to manage multiple key handlers used for Epigon.
 */
public interface EpigonKeyHandler extends KeyHandler {

    /**
     * Sets the Epigon for direct referencing where needed and returns itself for chaining.
     *
     * @param epigon
     * @return
     */
    public EpigonKeyHandler setEpigon(Epigon epigon);

    /**
     * Sets the Config for use in this handler. Defaults to a no-op for when the handler does not need such access.
     *
     * @param config
     * @return
     */
    public default EpigonKeyHandler setConfig(Config config) {
        return this;
    }
}
