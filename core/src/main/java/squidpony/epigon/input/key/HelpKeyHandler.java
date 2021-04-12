package squidpony.epigon.input.key;

import squidpony.squidgrid.Direction;
import squidpony.squidgrid.gui.gdx.SquidInput;
import squidpony.squidgrid.gui.gdx.SquidInput.KeyHandler;

import squidpony.epigon.Epigon;
import squidpony.epigon.display.MapOverlayHandler;
import squidpony.epigon.input.ControlMapping;
import squidpony.epigon.input.Verb;

/**
 * Handles input for actively falling in Falling mode
 */
public class HelpKeyHandler implements KeyHandler {

    private final Epigon epigon;

    public HelpKeyHandler(Epigon epigon) {
        this.epigon = epigon;
    }

    @Override
    public void handle(char key, boolean alt, boolean ctrl, boolean shift) {
        if (epigon.multiplexer.processedInput) {
            return;
        }
        int combined = SquidInput.combineModifiers(key, alt, ctrl, shift);
        Verb verb = ControlMapping.allMappings.get(combined);
        if (!ControlMapping.defaultHelpViewMapping.contains(verb)) {
            return;
        }
        switch (verb) {
            case MOVE_DOWN:
                epigon.mapOverlayHandler.move(Direction.DOWN);
                break;
            case MOVE_UP:
                epigon.mapOverlayHandler.move(Direction.UP);
                break;
            case MOVE_LEFT:
                epigon.mapOverlayHandler.move(Direction.LEFT);
                break;
            case MOVE_RIGHT:
                epigon.mapOverlayHandler.move(Direction.RIGHT);
                break;
            case MESSAGE_PRIOR:
                epigon.scrollMessages(-1);
                break;
            case MESSAGE_NEXT:
                epigon.scrollMessages(1);
                break;
            case CONTEXT_PRIOR:
                epigon.contextHandler.prior();
                break;
            case CONTEXT_NEXT:
                epigon.contextHandler.next();
                break;
            case INFO_PRIOR:
                epigon.infoHandler.prior();
                break;
            case INFO_NEXT:
                epigon.infoHandler.next();
                break;
            case EQUIPMENT:
                epigon.mapOverlayHandler.setMode(MapOverlayHandler.PrimaryMode.EQUIPMENT);
                epigon.mapInput.setKeyHandler(epigon.equipmentKeys);
                epigon.mapInput.setMouse(epigon.equipmentMouse);
                break;
            case HELP:
            case CLOSE_SCREEN:
                epigon.mapInput.setKeyHandler(epigon.mapKeys);
                epigon.mapInput.setMouse(epigon.mapMouse);
                epigon.mapOverlayHandler.hide();
                break;
            default:
                //message("Can't " + verb.name + " from help view.");
                return;
        }
        epigon.multiplexer.processedInput = true;
        epigon.infoHandler.updateDisplay();
    }
}
