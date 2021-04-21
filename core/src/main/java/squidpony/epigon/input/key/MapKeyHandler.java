package squidpony.epigon.input.key;

import com.badlogic.gdx.Gdx;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.stream.Collectors;

import squidpony.Maker;
import squidpony.squidgrid.Direction;
import squidpony.squidgrid.gui.gdx.SquidInput;
import squidpony.squidgrid.gui.gdx.SquidInput.KeyHandler;
import squidpony.squidmath.Coord;

import squidpony.epigon.data.Physical;
import squidpony.epigon.data.control.RecipeMixer;
import squidpony.epigon.data.slot.WieldSlot;
import squidpony.epigon.display.MapOverlayHandler;
import squidpony.epigon.game.Crawl;
import squidpony.epigon.input.ControlMapping;
import squidpony.epigon.input.Verb;
import squidpony.epigon.mapping.EpiTile;
import squidpony.epigon.util.Utilities;

/**
 * Handles input for when the main map is displayed.
 */
public class MapKeyHandler implements KeyHandler {

    private final Crawl crawl;

    public MapKeyHandler(Crawl epigon) {
        this.crawl = epigon;
    }

    @Override
    public void handle(char key, boolean alt, boolean ctrl, boolean shift) {
        if (crawl.multiplexer.processedInput) {
            return;
        }
        if (ctrl && shift && key == SquidInput.BACKSPACE && !alt) // ctrl-shift-backspace
        {
            crawl.multiplexer.processedInput = true;
            crawl.startGame();
            return;
        }
        Verb verb = ControlMapping.allMappings.get(SquidInput.combineModifiers(key, alt, ctrl, shift));
        if (!ControlMapping.defaultMapViewMapping.contains(verb)) {
            return;
        }
        switch (verb) {
            case MOVE_DOWN:
                crawl.scheduleMove(Direction.DOWN);
                break;
            case MOVE_UP:
                crawl.scheduleMove(Direction.UP);
                break;
            case MOVE_LEFT:
                crawl.scheduleMove(Direction.LEFT);
                break;
            case MOVE_RIGHT:
                crawl.scheduleMove(Direction.RIGHT);
                break;
            case MOVE_DOWN_LEFT:
                crawl.scheduleMove(Direction.DOWN_LEFT);
                break;
            case MOVE_DOWN_RIGHT:
                crawl.scheduleMove(Direction.DOWN_RIGHT);
                break;
            case MOVE_UP_LEFT:
                crawl.scheduleMove(Direction.UP_LEFT);
                break;
            case MOVE_UP_RIGHT:
                crawl.scheduleMove(Direction.UP_RIGHT);
                break;
            case MOVE_LOWER:// up '≤', down '≥'
                //  if (map.contents[player.location.x][player.location.y].getSymbolUninhabited() == '≥') {
                if (crawl.depth >= crawl.world.length - 1) {
                    crawl.message("Theses down stairs turn out to lead nowhere.");
                } else {
                    crawl.changeLevel(crawl.depth + 1, crawl.player.location);
                }
//                    } else {
//                        message("You're not on stairs going down.");
//                    }
                break;
            case MOVE_HIGHER:// up '≤', down '≥'
//                    if (map.contents[player.location.x][player.location.y].getSymbolUninhabited() == '≤') {
                if (crawl.depth <= 0) {
                    crawl.message("Theses up stairs turn out to lead nowhere.");
                } else {
                    crawl.changeLevel(crawl.depth - 1, crawl.player.location);
                }
//                    } else {
//                        message("You're not on stairs going up.");
//                    }
                break;
            case OPEN: // Open all the doors nearby
                crawl.message("Opening nearby doors");
                for (Direction d : Direction.OUTWARDS) {
                    Coord c = crawl.player.location.translate(d);
                    if (!crawl.map.inBounds(c)) {
                        continue;
                    }
                    if (crawl.map.lighting.fovResult[c.x][c.y] <= 0) {
                        continue;
                    }
                    EpiTile tile = crawl.map.contents[c.x][c.y];
                    if (tile.blockage != null && tile.blockage.countsAs(crawl.dataStarter.baseClosedDoor)) {
                        RecipeMixer.applyModification(tile.blockage, crawl.dataStarter.openDoor);
                        tile.contents.add(tile.blockage);
                        tile.blockage = null;
                    }
                }
                crawl.calcFOV(crawl.player.location.x, crawl.player.location.y);
                crawl.calcDijkstra();
                break;
            case SHUT: // Close all the doors nearby
                crawl.message("Closing nearby doors");
                for (Direction d : Direction.OUTWARDS) {
                    Coord c = crawl.player.location.translate(d);
                    if (!crawl.map.inBounds(c)) {
                        continue;
                    }
                    if (crawl.map.lighting.fovResult[c.x][c.y] <= 0) {
                        continue;
                    }
                    EpiTile tile = crawl.map.contents[c.x][c.y];
                    for (Physical p : tile.contents) {
                        if (p.countsAs(crawl.dataStarter.baseOpenDoor)) {
                            if (tile.blockage != null) {
                                crawl.message("Can't shut the door to the " + d.toString() + " there's a " + tile.blockage.name + " in the way!");
                                continue;
                            }
                            RecipeMixer.applyModification(p, crawl.dataStarter.closeDoor);
                            tile.remove(p);
                            tile.blockage = p;
                        }
                    }
                }
                crawl.calcFOV(crawl.player.location.x, crawl.player.location.y);
                crawl.calcDijkstra();
                break;
            case GATHER: // Pick everything nearby up
                List<Physical> pickedUp = new ArrayList<>();
                for (Direction dir : Direction.values()) {
                    Coord c = crawl.player.location.translate(dir);
                    if (crawl.map.inBounds(c) && crawl.map.lighting.fovResult[c.x][c.y] > 0) {
                        EpiTile tile = crawl.map.contents[c.x][c.y];
                        ListIterator<Physical> it = tile.contents.listIterator();
                        Physical p;
                        while (it.hasNext()) {
                            p = it.next();
                            if (p.attached || p.creatureData != null) {
                                continue;
                            }
                            crawl.player.addToInventory(p);
                            pickedUp.add(p);
                            it.remove();
                        }
                    }
                }
                if (pickedUp.isEmpty()) {
                    crawl.message("Nothing to pick up nearby.");
                } else {
                    crawl.message(pickedUp.stream().map(p -> Utilities.colorize(p.name, p.rarity.color())).collect(Collectors.joining(", ", "Picked up ", ".")));
                }
                break;
            case EQUIPMENT:
                crawl.mapOverlayHandler.setMode(MapOverlayHandler.PrimaryMode.EQUIPMENT);
                crawl.crawlInput.setKeyHandler(crawl.equipmentKeys);
                crawl.toCursor.clear();
                crawl.crawlInput.setMouse(crawl.equipmentMouse);
                break;
            case WIELD:
                crawl.equipItem();
                break;
            case DROP:
                crawl.message("Dropping all held items");
                for (Physical dropped : crawl.player.unequip(Maker.makeList(WieldSlot.RIGHT_HAND, WieldSlot.LEFT_HAND))) {
                    for (int i = 0, offset = crawl.player.next(3); i < 8; i++) {
                        Coord c = crawl.player.location.translate(Direction.OUTWARDS[i + offset & 7]);
                        if (crawl.map.inBounds(c) && crawl.map.lighting.fovResult[c.x][c.y] > 0) {
                            crawl.map.contents[c.x][c.y].add(dropped);
                            break;
                        }
                    }
                }
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
            case MESSAGE_PRIOR:
                crawl.scrollMessages(-1);
                break;
            case MESSAGE_NEXT:
                crawl.scrollMessages(1);
                break;
            case HELP:
                crawl.mapOverlayHandler.setMode(MapOverlayHandler.PrimaryMode.HELP);
                crawl.crawlInput.setKeyHandler(crawl.helpKeys);
                crawl.toCursor.clear();
                crawl.crawlInput.setMouse(crawl.helpMouse);
                break;
            case QUIT:
                // TODO - confirmation
                Gdx.app.exit();
                return;
            case WAIT:
                crawl.scheduleMove(Direction.NONE);
                break;
            case REST:
                // TODO - why was this previously: epigon.prepFall();
                break;
            case INTERACT:
                Optional<Physical> t;
                if ((t = crawl.player.inventory.stream().filter(ph -> ph.symbol == 'ῗ').findFirst()).isPresent()) {
                    if (crawl.player.creatureData.lastUsedItem != null && crawl.player.creatureData.lastUsedItem.symbol == 'ῗ') {
                        crawl.player.creatureData.lastUsedItem = null;
                    } else {
                        crawl.player.creatureData.lastUsedItem = t.get();
                    }
                }
                break;
            default:
                //message("Can't " + verb.name + " from main view.");
                return;
        }
        crawl.multiplexer.processedInput = true;
        crawl.infoHandler.updateDisplay();
        // check if the turn clock needs to run
        if (verb.isAction()) {
            crawl.runTurn();
        }
    }
}
