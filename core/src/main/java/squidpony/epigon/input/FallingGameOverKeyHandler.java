package squidpony.epigon.input;

import com.badlogic.gdx.Gdx;

import squidpony.squidgrid.gui.gdx.SquidInput;

import squidpony.epigon.Epigon;

/**
 * Handles input for actively falling in Falling mode
 */
public class FallingGameOverKeyHandler implements EpigonKeyHandler {

    private Epigon epigon;

    @Override
    public FallingGameOverKeyHandler setEpigon(Epigon epigon) {
        this.epigon = epigon;
        return this;
    }

    @Override
    public void handle(char key, boolean alt, boolean ctrl, boolean shift) {
        if (epigon.multiplexer.processedInput) {
            return;
        }
        int combined = SquidInput.combineModifiers(key, alt, ctrl, shift);
        Verb verb = ControlMapping.allMappings.get(combined);
        if (!ControlMapping.defaultFallingViewGameOverMapping.contains(verb)) {
            return;
        }
        switch (verb) {
            case TRY_AGAIN:
                epigon.initPlayer();
                epigon.prepFall();
                break;
            case QUIT:
                Gdx.app.exit();
                break;
            default:
                //message("Can't " + verb.name + " from falling view.");
                return;
        }
        epigon.multiplexer.processedInput = true;
    }
}
