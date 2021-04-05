package squidpony.epigon.input.key;

import squidpony.squidgrid.Radius;
import squidpony.squidmath.Coord;

import squidpony.epigon.Epigon;
import squidpony.epigon.GauntRNG;
import squidpony.epigon.data.quality.Element;

/**
 * Handles input for debug actions
 */
public class DebugKeyHandler implements EpigonKeyHandler {

    private Epigon epigon;

    @Override
    public DebugKeyHandler setEpigon(Epigon epigon) {
        this.epigon = epigon;
        return this;
    }

    @Override
    public void handle(char key, boolean alt, boolean ctrl, boolean shift) { // TODO - only the first 2 seem to be working currently
        if (epigon.multiplexer.processedInput) {
            return;
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
                    Coord end = epigon.rng.nextCoord(Epigon.worldWidth, Epigon.worldHeight);
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
                for (Coord c : epigon.rng.getRandomUniqueCells(0, 0, Epigon.worldWidth, Epigon.worldHeight, 400)) {
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
                if (epigon.odinView) {
                    epigon.message("Odinview disabled.");
                    epigon.odinView = false;
                } else {
                    epigon.message("Showing all");
                    epigon.odinView = true;
                }
                epigon.calcFOV(epigon.player.location.x, epigon.player.location.y);
            default:
                return;
        }
        epigon.multiplexer.processedInput = true;
    }
}
