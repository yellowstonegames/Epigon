package squidpony.epigon.input.key;

import squidpony.squidgrid.Radius;
import squidpony.squidgrid.gui.gdx.SquidInput.KeyHandler;
import squidpony.squidmath.Coord;

import squidpony.epigon.game.Epigon;
import squidpony.epigon.GauntRNG;
import squidpony.epigon.data.quality.Element;
import squidpony.epigon.files.Config;

/**
 * Handles input for debug actions
 */
public class DebugKeyHandler implements KeyHandler {

    private final char debugToggleKey = '~';

    private final Epigon epigon;
    private final Config config;

    public DebugKeyHandler(Epigon epigon, Config config) {
        this.epigon = epigon;
        this.config = config;
    }

    @Override
    public void handle(char key, boolean alt, boolean ctrl, boolean shift) {
        if (epigon.multiplexer.processedInput) {
            return;
        }

        if (!config.debugConfig.debugActive && key != debugToggleKey) {
            return; // only process if debug is turned on or attempting to toggle debug on
        }

        Element el;
        switch (key) {
            case 'x':
                el = epigon.rng.getRandomElement(Element.allEnergy);
                epigon.message("Sector blast " + el.styledName);
                epigon.fxHandler.sectorBlast(epigon.player.location, el, 7, Radius.CIRCLE);
                break;
            case 'X':
                el = epigon.rng.getRandomElement(Element.allEnergy);
                epigon.message("Zap boom " + el.styledName);
                epigon.fxHandler.zapBoom(epigon.player.location, epigon.player.location.translateCapped(epigon.rng.between(-20, 20), epigon.rng.between(-10, 10), epigon.map.width, epigon.map.height), el);
                break;
            case 'R':
                epigon.message("Raining");
                int quantity = 8000;
                float totalTime = 5; // in seconds
                // chooses an element for the rain by the player's current position
                Element drops = GauntRNG.getRandomElement(epigon.player.location.hashCode(), Element.allEnergy);
                for (int i = 0; i < quantity; i++) {
                    Coord end = epigon.rng.nextCoord(config.settings.worldWidth, config.settings.worldHeight);
                    if (epigon.map.contents[end.x][end.y].blockage != null) {
                        continue; // skip hitting blocking areas
                    }
                    int length = epigon.rng.between(4, 8);
                    Coord origin = Coord.get(end.x + length, end.y - length);
                    epigon.fxHandlerPassive.rain(origin, end, drops, totalTime * ((float) i / quantity));
                }
                break;
            case 'z':
                epigon.message("Fritzzzz");
                epigon.fxHandlerPassive.fritz(epigon.player.location, Element.ICE, 7, Radius.CIRCLE);
                break;
            case 'Z':
                epigon.message("Twinkle time");
                for (Coord c : epigon.rng.getRandomUniqueCells(0, 0, config.settings.worldWidth, config.settings.worldHeight, 400)) {
                    epigon.fxHandlerPassive.twinkle(c, Element.LIGHT);
                }
                break;
            case '=':
                epigon.message("Layered sparkle small");
                epigon.fxHandlerPassive.layeredSparkle(epigon.player.location, 4, Radius.CIRCLE);
                break;
            case '+':
                epigon.message("Layered sparkle large");
                epigon.fxHandlerPassive.layeredSparkle(epigon.player.location, 8, Radius.CIRCLE);
                break;
            case '|':
                if (config.debugConfig.debugActive) {
                    epigon.setOdinView(!config.debugConfig.odinView);
                } else {
                    epigon.setOdinView(false);
                }
                break;
            case debugToggleKey:
                epigon.toggleDebug();
                break;
            default:
                return;
        }
        epigon.multiplexer.processedInput = true;
    }
}
