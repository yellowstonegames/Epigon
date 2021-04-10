package squidpony.epigon.input.mouse;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;

import squidpony.epigon.Epigon;

/**
 * Handles mouse input for the falling game mode
 */
public class MessageMouseHandler extends InputAdapter {

    private Epigon epigon;

    public MessageMouseHandler setEpigon(Epigon epigon) {
        this.epigon = epigon;
        return this;
    }

    @Override
    public boolean touchUp(int gridX, int gridY, int pointer, int button) {
        //System.out.println("message: " + screenX + ", " + screenY);
        switch (button) {
            case Input.Buttons.LEFT:
                if (gridY <= 0) {
                    epigon.scrollMessages(-1);
                } else if (gridY >= epigon.messageSize.gridHeight - 1) {
                    epigon.scrollMessages(1);
                }
                return true;
            case Input.Buttons.RIGHT:
            default:
                return false;
        }
    }

    @Override
    public boolean touchDragged(int gridX, int gridY, int pointer) {
        return mouseMoved(gridX, gridY);
    }

    @Override
    public boolean mouseMoved(int gridX, int gridY) {
        return false;
    }
}
