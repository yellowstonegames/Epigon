package squidpony.epigon.input.mouse;


import squidpony.epigon.Epigon;

/**
 * Handles mouse input for the falling game mode
 */
public class FallingMouseHandler extends EpigonMouseHandler {

    private Epigon epigon;

    @Override
    public EpigonMouseHandler setEpigon(Epigon epigon) {
        this.epigon = epigon;
        return this;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false; // No-op for now
    }
}
