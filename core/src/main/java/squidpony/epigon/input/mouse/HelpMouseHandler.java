package squidpony.epigon.input.mouse;

import com.badlogic.gdx.InputAdapter;

/**
 * Handles mouse input for the help screen
 */
public class HelpMouseHandler extends InputAdapter {

    @Override
    public boolean touchUp(int gridX, int gridY, int pointer, int button) {
        return false; // No-op for now
    }
}
