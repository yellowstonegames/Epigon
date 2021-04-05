package squidpony.epigon.input.mouse;

import com.badlogic.gdx.InputAdapter;

import squidpony.epigon.Epigon;

/**
 * An interface to manage multiple mouse handlers used for Epigon.
 */
public abstract class EpigonMouseHandler extends InputAdapter {

    /**
     * Sets the Epigon for direct referencing where needed and returns itself for chaining.
     *
     * @param epigon
     * @return
     */
    public abstract EpigonMouseHandler setEpigon(Epigon epigon);
}
