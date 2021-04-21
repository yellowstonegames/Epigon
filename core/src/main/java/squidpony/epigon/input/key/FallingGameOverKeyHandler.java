package squidpony.epigon.input.key;

import squidpony.squidgrid.gui.gdx.SquidInput;
import squidpony.squidgrid.gui.gdx.SquidInput.KeyHandler;

import squidpony.epigon.game.Dive;
import squidpony.epigon.input.ControlMapping;
import squidpony.epigon.input.Verb;

/**
 * Handles input for actively falling in Falling mode
 */
public class FallingGameOverKeyHandler implements KeyHandler {

    private final Dive dive;

    public FallingGameOverKeyHandler(Dive dive) {
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
                dive.exit();
                break;
            default:
                return;
        }
        dive.multiplexer.processedInput = true;
    }
}
