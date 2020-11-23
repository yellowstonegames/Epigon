package squidpony.epigon;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import squidpony.squidgrid.gui.gdx.SquidInput;

/** An {@link InputProcessor} that delegates to an ordered list of other InputProcessors. Delegation for an event stops if a
 * processor returns true, which indicates that the event was handled.
 * @author Nathan Sweet */
public class InputSpecialMultiplexer implements InputProcessor {
    public boolean processedInput = false;
    private SquidInput[] processors;
    private long lastKeyTime = -1000000L;
    private int lastKeyCode = -1;
    public long repeatGapMillis = 220L;

    public InputSpecialMultiplexer() {
        processors = new SquidInput[0];
    }

    public InputSpecialMultiplexer(SquidInput... processors) {
        this.processors = processors;
    }

    /**
     * @return the number of processors in this multiplexer
     */
    public int size() {
        return processors.length;
    }

    public boolean keyDown(int keycode) {
        processedInput = false;
        lastKeyTime = System.currentTimeMillis();
        lastKeyCode = keycode;
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
    public boolean scrolled(float amountX, float amountY) {
        for (int i = 0, n = processors.length; i < n; i++)
            if (processors[i].scrolled(amountX, amountY)) return true;
        return false;
    }
    
    public void process()
    {
        if(Gdx.input.isKeyPressed(Input.Keys.ANY_KEY)
                && !Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)
                && !Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)
                && !Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)
                && !Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT)
                && !Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT)
                && !Gdx.input.isKeyPressed(Input.Keys.ALT_RIGHT)
                && lastKeyCode >= 0
                && System.currentTimeMillis() - lastKeyTime > repeatGapMillis // defaults to 220 ms
                )
        {
            processedInput = false;
            lastKeyTime = System.currentTimeMillis();
            for (int i = 0, n = processors.length; i < n; i++)
                if (processors[i].keyDown(lastKeyCode) || processedInput) return;
        }
        else 
        {
            for (int i = 0, n = processors.length; i < n; i++)
            {
                if(processors[i].hasNext())
                    processors[i].next();
            }
        }

    }
}
