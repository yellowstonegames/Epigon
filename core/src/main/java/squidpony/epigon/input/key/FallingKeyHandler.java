package squidpony.epigon.input.key;

import com.badlogic.gdx.Gdx;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import squidpony.squidgrid.Direction;
import squidpony.squidgrid.gui.gdx.SquidInput;
import squidpony.squidgrid.gui.gdx.SquidInput.KeyHandler;

import squidpony.epigon.game.Dive;
import squidpony.epigon.input.ControlMapping;
import squidpony.epigon.input.Verb;

/**
 * Handles input for actively falling in Falling mode
 */
public class FallingKeyHandler implements KeyHandler {

    private final Dive dive;

    public FallingKeyHandler(Dive dive) {
        this.dive = dive;
    }

    @Override
    public void handle(char key, boolean alt, boolean ctrl, boolean shift) {
        if (dive.multiplexer.processedInput) {
            return;
        }
        int combined = SquidInput.combineModifiers(key, alt, ctrl, shift);
        Verb verb = ControlMapping.allMappings.get(combined);
        if (!ControlMapping.defaultFallingViewMapping.contains(verb)) {
            return;
        }
        switch (verb) {
            case MOVE_UP:
                if (!dive.paused) {
                    dive.nextInput = Instant.now().plusMillis(dive.inputDelay);
                    dive.fallingHandler.move(Direction.UP);
                }
                break;
            case MOVE_DOWN:
                if (!dive.paused) {
                    dive.nextInput = Instant.now().plusMillis(dive.inputDelay);
                    dive.fallingHandler.move(Direction.DOWN);
                }
                break;
            case MOVE_LEFT:
                if (!dive.paused) {
                    dive.nextInput = Instant.now().plusMillis(dive.inputDelay);
                    dive.fallingHandler.move(Direction.LEFT);
                }
                break;
            case MOVE_RIGHT:
                if (!dive.paused) {
                    dive.nextInput = Instant.now().plusMillis(dive.inputDelay);
                    dive.fallingHandler.move(Direction.RIGHT);
                }
                break;
            case PAUSE:
                dive.paused = !dive.paused;
                if (dive.paused) {
                    dive.pausedAt = Instant.now();
                    dive.message("You are hovering, have a look around!");
                } else { // need to calculate time offsets
                    long pausedFor = dive.pausedAt.until(Instant.now(), ChronoUnit.MILLIS);
                    dive.nextFall = dive.nextFall.plusMillis(pausedFor);
                    dive.message("Falling once more!");
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
        dive.multiplexer.processedInput = true;
    }
}
