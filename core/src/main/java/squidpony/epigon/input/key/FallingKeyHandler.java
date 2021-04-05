package squidpony.epigon.input.key;

import com.badlogic.gdx.Gdx;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import squidpony.squidgrid.Direction;
import squidpony.squidgrid.gui.gdx.SquidInput;

import squidpony.epigon.Epigon;
import squidpony.epigon.input.ControlMapping;
import squidpony.epigon.input.Verb;

/**
 * Handles input for actively falling in Falling mode
 */
public class FallingKeyHandler implements EpigonKeyHandler {

    private Epigon epigon;

    @Override
    public FallingKeyHandler setEpigon(Epigon epigon) {
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
        if (!ControlMapping.defaultFallingViewMapping.contains(verb)) {
            return;
        }
        switch (verb) {
            case MOVE_UP:
                if (!epigon.paused) {
                    epigon.nextInput = Instant.now().plusMillis(epigon.inputDelay);
                    epigon.fallingHandler.move(Direction.UP);
                }
                break;
            case MOVE_DOWN:
                if (!epigon.paused) {
                    epigon.nextInput = Instant.now().plusMillis(epigon.inputDelay);
                    epigon.fallingHandler.move(Direction.DOWN);
                }
                break;
            case MOVE_LEFT:
                if (!epigon.paused) {
                    epigon.nextInput = Instant.now().plusMillis(epigon.inputDelay);
                    epigon.fallingHandler.move(Direction.LEFT);
                }
                break;
            case MOVE_RIGHT:
                if (!epigon.paused) {
                    epigon.nextInput = Instant.now().plusMillis(epigon.inputDelay);
                    epigon.fallingHandler.move(Direction.RIGHT);
                }
                break;
            case PAUSE:
                epigon.paused = !epigon.paused;
                if (epigon.paused) {
                    epigon.pausedAt = Instant.now();
                    epigon.message("You are hovering, have a look around!");
                } else { // need to calculate time offsets
                    long pausedFor = epigon.pausedAt.until(Instant.now(), ChronoUnit.MILLIS);
                    epigon.nextFall = epigon.nextFall.plusMillis(pausedFor);
                    epigon.message("Falling once more!");
                }
                break;
            case SAVE:
                // TODO
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
