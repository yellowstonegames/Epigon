package squidpony.epigon;

import com.badlogic.gdx.InputProcessor;

/** An {@link InputProcessor} that delegates to an ordered list of other InputProcessors. Delegation for an event stops if a
 * processor returns true, which indicates that the event was handled.
 * @author Nathan Sweet */
public class InputSpecialMultiplexer implements InputProcessor {
    public boolean processedInput = false;
    private InputProcessor[] processors;

    public InputSpecialMultiplexer() {
        processors = new InputProcessor[0];
    }

    public InputSpecialMultiplexer(InputProcessor... processors) {
        this.processors = processors;
    }

    /**
     * @return the number of processors in this multiplexer
     */
    public int size() {
        return processors.length;
    }

    public void setProcessors(InputProcessor... processors) {
        this.processors = processors;
    }

    public InputProcessor[] getProcessors() {
        return processors;
    }

    public boolean keyDown(int keycode) {
        processedInput = false;
        for (int i = 0, n = processors.length; i < n; i++)
            if (processors[i].keyDown(keycode) || processedInput) return true;
        return false;
    }

    public boolean keyUp(int keycode) {
        processedInput = false;
        for (int i = 0, n = processors.length; i < n; i++)
            if (processors[i].keyUp(keycode) || processedInput) return true;
        return false;
    }

    public boolean keyTyped(char character) {
        processedInput = false;
        for (int i = 0, n = processors.length; i < n; i++)
            if (processors[i].keyTyped(character) || processedInput) return true;
        return false;
    }

    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        for (int i = 0, n = processors.length; i < n; i++)
            if (processors[i].touchDown(screenX, screenY, pointer, button)) return true;
        return false;
    }

    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        for (int i = 0, n = processors.length; i < n; i++)
            if (processors[i].touchUp(screenX, screenY, pointer, button)) return true;
        return false;
    }

    public boolean touchDragged(int screenX, int screenY, int pointer) {
        for (int i = 0, n = processors.length; i < n; i++)
            if (processors[i].touchDragged(screenX, screenY, pointer)) return true;
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        for (int i = 0, n = processors.length; i < n; i++)
            if (processors[i].mouseMoved(screenX, screenY)) return true;
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        for (int i = 0, n = processors.length; i < n; i++)
            if (processors[i].scrolled(amount)) return true;
        return false;
    }
    
}
