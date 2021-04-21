package squidpony.epigon.input.key;

import com.badlogic.gdx.Gdx;

import squidpony.squidgrid.gui.gdx.SquidInput;
import squidpony.squidgrid.gui.gdx.SquidInput.KeyHandler;

import squidpony.epigon.game.Dive;
import squidpony.epigon.input.ControlMapping;
import squidpony.epigon.input.Verb;

/**
 * Handles input for actively falling in Falling mode
 */
public class FallingGameOver implements KeyHandler {

    private final Dive dive;

    public FallingGameOver(Dive dive) {
        this.dive = dive;
    }

    @Override
    public void handle(char key, boolean alt, boolean ctrl, boolean shift) {
        if (dive.multiplexer.processedInput) {
            return;
        }
        int combined = SquidInput.combineModifiers(key, alt, ctrl, shift);
        Verb verb = ControlMapping.allMappings.get(combined);
        if (!ControlMapping.defaultFallingViewGameOverMapping.contains(verb)) {
            return;
        }
        switch (verb) {
            case TRY_AGAIN:
                dive.initPlayer();
                dive.prepFall();
                break;
            case QUIT:
                Gdx.app.exit();
                break;
            default:
                //message("Can't " + verb.name + " from falling view.");
                return;
        }
        dive.multiplexer.processedInput = true;
    }
}
