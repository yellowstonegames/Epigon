package squidpony.epigon.input.key;

import squidpony.squidgrid.Direction;
import squidpony.squidgrid.gui.gdx.SquidInput;
import squidpony.squidgrid.gui.gdx.SquidInput.KeyHandler;

import squidpony.epigon.game.Crawl;
import squidpony.epigon.display.MapOverlayHandler;
import squidpony.epigon.input.ControlMapping;
import squidpony.epigon.input.Verb;

/**
 * Handles input for actively falling in Falling mode
 */
public class HelpKeyHandler implements KeyHandler {

    private final Crawl crawl;

    public HelpKeyHandler(Crawl crawl) {
        this.crawl = crawl;
    }

    @Override
    public void handle(char key, boolean alt, boolean ctrl, boolean shift) {
        if (crawl.multiplexer.processedInput) {
            return;
        }
        int combined = SquidInput.combineModifiers(key, alt, ctrl, shift);
        Verb verb = ControlMapping.allMappings.get(combined);
        if (!ControlMapping.defaultHelpViewMapping.contains(verb)) {
            return;
        }
        switch (verb) {
            case MOVE_DOWN:
                crawl.mapOverlayHandler.move(Direction.DOWN);
                break;
            case MOVE_UP:
                crawl.mapOverlayHandler.move(Direction.UP);
                break;
            case MOVE_LEFT:
                crawl.mapOverlayHandler.move(Direction.LEFT);
                break;
            case MOVE_RIGHT:
                crawl.mapOverlayHandler.move(Direction.RIGHT);
                break;
            case MESSAGE_PRIOR:
                crawl.scrollMessages(-1);
                break;
            case MESSAGE_NEXT:
                crawl.scrollMessages(1);
                break;
            case CONTEXT_PRIOR:
                crawl.contextHandler.prior();
                break;
            case CONTEXT_NEXT:
                crawl.contextHandler.next();
                break;
            case INFO_PRIOR:
                crawl.infoHandler.prior();
                break;
            case INFO_NEXT:
                crawl.infoHandler.next();
                break;
            case EQUIPMENT:
                crawl.mapOverlayHandler.setMode(MapOverlayHandler.PrimaryMode.EQUIPMENT);
                crawl.crawlInput.setKeyHandler(crawl.equipmentKeys);
                crawl.crawlInput.setMouse(crawl.equipmentMouse);
                break;
            case HELP:
            case CLOSE_SCREEN:
                crawl.crawlInput.setKeyHandler(crawl.mapKeys);
                crawl.crawlInput.setMouse(crawl.mapMouse);
                crawl.mapOverlayHandler.hide();
                break;
            default:
                return;
        }
        crawl.multiplexer.processedInput = true;
        crawl.infoHandler.updateDisplay();
    }
}
