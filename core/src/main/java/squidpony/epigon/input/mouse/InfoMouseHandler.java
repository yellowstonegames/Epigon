package squidpony.epigon.input.mouse;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;

import squidpony.epigon.display.InfoHandler;

/**
 * Handles mouse input for the help screen
 */
public class InfoMouseHandler extends InputAdapter {

    private InfoHandler infoHandler;

    public InfoMouseHandler setInfoHandler(InfoHandler infoHandler) {
        this.infoHandler = infoHandler;
        return this;
    }

    @Override
    public boolean touchUp(int gridX, int gridY, int pointer, int button) {
        //System.out.println("info: " + screenX + ", " + screenY);
        switch (button) {
            case Input.Buttons.LEFT:
                if (gridX == infoHandler.arrowLeft.x && gridY == infoHandler.arrowLeft.y) {
                    infoHandler.prior();
                } else if (gridX == infoHandler.arrowRight.x && gridY == infoHandler.arrowRight.y) {
                    infoHandler.next();
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
