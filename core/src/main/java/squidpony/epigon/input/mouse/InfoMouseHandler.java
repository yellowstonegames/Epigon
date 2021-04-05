package squidpony.epigon.input.mouse;

import com.badlogic.gdx.Input;
import squidpony.epigon.Epigon;

/**
 * Handles mouse input for the help screen
 */
public class InfoMouseHandler extends EpigonMouseHandler {

    private Epigon epigon;

    @Override
    public EpigonMouseHandler setEpigon(Epigon epigon) {
        this.epigon = epigon;
        return this;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        //System.out.println("info: " + screenX + ", " + screenY);
        switch (button) {
            case Input.Buttons.LEFT:
                if (screenX == epigon.infoHandler.arrowLeft.x && screenY == epigon.infoHandler.arrowLeft.y) {
                    epigon.infoHandler.prior();
                } else if (screenX == epigon.infoHandler.arrowRight.x && screenY == epigon.infoHandler.arrowRight.y) {
                    epigon.infoHandler.next();
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
