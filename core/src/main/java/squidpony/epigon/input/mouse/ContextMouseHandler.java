package squidpony.epigon.input.mouse;

import com.badlogic.gdx.Input;

import squidpony.epigon.Epigon;

/**
 * Handles mouse input for the falling game mode
 */
public class ContextMouseHandler extends EpigonMouseHandler {

    private Epigon epigon;

    @Override
    public EpigonMouseHandler setEpigon(Epigon epigon) {
        this.epigon = epigon;
        return this;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (screenX < 0 || screenX >= epigon.contextSize.gridWidth || screenY < 0 || screenY >= epigon.contextSize.gridHeight) {
            return false;
        }
        switch (button) {
            case Input.Buttons.LEFT:
                if (screenX == epigon.contextHandler.arrowLeft.x && screenY == epigon.contextHandler.arrowLeft.y) {
                    epigon.contextHandler.prior();
                } else if (screenX == epigon.contextHandler.arrowRight.x && screenY == epigon.contextHandler.arrowRight.y) {
                    epigon.contextHandler.next();
                }
                return true;
            case Input.Buttons.RIGHT:
            default:
                return false;
        }
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return mouseMoved(screenX, screenY);
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }
}
