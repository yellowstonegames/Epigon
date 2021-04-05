package squidpony.epigon.input.mouse;

import com.badlogic.gdx.Input;

import squidpony.epigon.Epigon;

/**
 * Handles mouse input for the falling game mode
 */
public class MessageMouseHandler extends EpigonMouseHandler {

    private Epigon epigon;

    @Override
    public EpigonMouseHandler setEpigon(Epigon epigon) {
        this.epigon = epigon;
        return this;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        //System.out.println("message: " + screenX + ", " + screenY);
        switch (button) {
            case Input.Buttons.LEFT:
                if (screenY <= 0) {
                    epigon.scrollMessages(-1);
                } else if (screenY >= epigon.messageSize.gridHeight - 1) {
                    epigon.scrollMessages(1);
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
