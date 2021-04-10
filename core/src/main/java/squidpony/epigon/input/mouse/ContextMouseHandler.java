package squidpony.epigon.input.mouse;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;

import squidpony.epigon.display.ContextHandler;

/**
 * Handles mouse input for the falling game mode
 */
public class ContextMouseHandler extends InputAdapter {

    private ContextHandler contextHandler;

    public ContextMouseHandler setContextHandler(ContextHandler contextHandler) {
        this.contextHandler = contextHandler;
        return this;
    }

    @Override
    public boolean touchUp(int gridX, int gridY, int pointer, int button) {
        switch (button) {
            case Input.Buttons.LEFT:
                if (gridX == contextHandler.arrowLeft.x && gridY == contextHandler.arrowLeft.y) {
                    contextHandler.prior();
                } else if (gridX == contextHandler.arrowRight.x && gridY == contextHandler.arrowRight.y) {
                    contextHandler.next();
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
