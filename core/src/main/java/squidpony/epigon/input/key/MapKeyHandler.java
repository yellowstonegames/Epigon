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

import squidpony.epigon.game.Epigon;
import squidpony.epigon.data.Physical;
import squidpony.epigon.data.control.RecipeMixer;
import squidpony.epigon.data.slot.WieldSlot;
import squidpony.epigon.display.MapOverlayHandler;
import squidpony.epigon.input.ControlMapping;
import squidpony.epigon.input.Verb;
import squidpony.epigon.mapping.EpiTile;
import squidpony.epigon.util.Utilities;

/**
 * Handles input for when the main map is displayed.
 */
public class MapKeyHandler implements KeyHandler {

    private final Epigon epigon;

    public MapKeyHandler(Epigon epigon) {
        this.epigon = epigon;
    }

    @Override
    public void handle(char key, boolean alt, boolean ctrl, boolean shift) {
        if (epigon.multiplexer.processedInput) {
            return;
        }
        if (ctrl && shift && key == SquidInput.BACKSPACE && !alt) // ctrl-shift-backspace
        {
            epigon.multiplexer.processedInput = true;
            epigon.startGame();
            return;
        }
        Verb verb = ControlMapping.allMappings.get(SquidInput.combineModifiers(key, alt, ctrl, shift));
        if (!ControlMapping.defaultMapViewMapping.contains(verb)) {
            return;
        }
        switch (verb) {
            case MOVE_DOWN:
                epigon.scheduleMove(Direction.DOWN);
                break;
            case MOVE_UP:
                epigon.scheduleMove(Direction.UP);
                break;
            case MOVE_LEFT:
                epigon.scheduleMove(Direction.LEFT);
                break;
            case MOVE_RIGHT:
                epigon.scheduleMove(Direction.RIGHT);
                break;
            case MOVE_DOWN_LEFT:
                epigon.scheduleMove(Direction.DOWN_LEFT);
                break;
            case MOVE_DOWN_RIGHT:
                epigon.scheduleMove(Direction.DOWN_RIGHT);
                break;
            case MOVE_UP_LEFT:
                epigon.scheduleMove(Direction.UP_LEFT);
                break;
            case MOVE_UP_RIGHT:
                epigon.scheduleMove(Direction.UP_RIGHT);
                break;
            case MOVE_LOWER:// up '≤', down '≥'
                //  if (map.contents[player.location.x][player.location.y].getSymbolUninhabited() == '≥') {
                if (epigon.depth >= epigon.world.length - 1) {
                    epigon.message("Theses down stairs turn out to lead nowhere.");
                } else {
                    epigon.changeLevel(epigon.depth + 1, epigon.player.location);
                }
//                    } else {
//                        message("You're not on stairs going down.");
//                    }
                break;
            case MOVE_HIGHER:// up '≤', down '≥'
//                    if (map.contents[player.location.x][player.location.y].getSymbolUninhabited() == '≤') {
                if (epigon.depth <= 0) {
                    epigon.message("Theses up stairs turn out to lead nowhere.");
                } else {
                    epigon.changeLevel(epigon.depth - 1, epigon.player.location);
                }
//                    } else {
//                        message("You're not on stairs going up.");
//                    }
                break;
            case OPEN: // Open all the doors nearby
                epigon.message("Opening nearby doors");
                for (Direction d : Direction.OUTWARDS) {
                    Coord c = epigon.player.location.translate(d);
                    if (!epigon.map.inBounds(c)) {
                        continue;
                    }
                    if (epigon.map.lighting.fovResult[c.x][c.y] <= 0) {
                        continue;
                    }
                    EpiTile tile = epigon.map.contents[c.x][c.y];
                    if (tile.blockage != null && tile.blockage.countsAs(epigon.dataStarter.baseClosedDoor)) {
                        RecipeMixer.applyModification(tile.blockage, epigon.dataStarter.openDoor);
                        tile.contents.add(tile.blockage);
                        tile.blockage = null;
                    }
                }
                epigon.calcFOV(epigon.player.location.x, epigon.player.location.y);
                epigon.calcDijkstra();
                break;
            case SHUT: // Close all the doors nearby
                epigon.message("Closing nearby doors");
                for (Direction d : Direction.OUTWARDS) {
                    Coord c = epigon.player.location.translate(d);
                    if (!epigon.map.inBounds(c)) {
                        continue;
                    }
                    if (epigon.map.lighting.fovResult[c.x][c.y] <= 0) {
                        continue;
                    }
                    EpiTile tile = epigon.map.contents[c.x][c.y];
                    for (Physical p : tile.contents) {
                        if (p.countsAs(epigon.dataStarter.baseOpenDoor)) {
                            if (tile.blockage != null) {
                                epigon.message("Can't shut the door to the " + d.toString() + " there's a " + tile.blockage.name + " in the way!");
                                continue;
                            }
                            RecipeMixer.applyModification(p, epigon.dataStarter.closeDoor);
                            tile.remove(p);
                            tile.blockage = p;
                        }
                    }
                }
                epigon.calcFOV(epigon.player.location.x, epigon.player.location.y);
                epigon.calcDijkstra();
                break;
            case GATHER: // Pick everything nearby up
                List<Physical> pickedUp = new ArrayList<>();
                for (Direction dir : Direction.values()) {
                    Coord c = epigon.player.location.translate(dir);
                    if (epigon.map.inBounds(c) && epigon.map.lighting.fovResult[c.x][c.y] > 0) {
                        EpiTile tile = epigon.map.contents[c.x][c.y];
                        ListIterator<Physical> it = tile.contents.listIterator();
                        Physical p;
                        while (it.hasNext()) {
                            p = it.next();
                            if (p.attached || p.creatureData != null) {
                                continue;
                            }
                            epigon.player.addToInventory(p);
                            pickedUp.add(p);
                            it.remove();
                        }
                    }
                }
                if (pickedUp.isEmpty()) {
                    epigon.message("Nothing to pick up nearby.");
                } else {
                    epigon.message(pickedUp.stream().map(p -> Utilities.colorize(p.name, p.rarity.color())).collect(Collectors.joining(", ", "Picked up ", ".")));
                }
                break;
            case EQUIPMENT:
                epigon.mapOverlayHandler.setMode(MapOverlayHandler.PrimaryMode.EQUIPMENT);
                epigon.mapInput.setKeyHandler(epigon.equipmentKeys);
                epigon.toCursor.clear();
                epigon.mapInput.setMouse(epigon.equipmentMouse);
                break;
            case WIELD:
                epigon.equipItem();
                break;
            case DROP:
                epigon.message("Dropping all held items");
                for (Physical dropped : epigon.player.unequip(Maker.makeList(WieldSlot.RIGHT_HAND, WieldSlot.LEFT_HAND))) {
                    for (int i = 0, offset = epigon.player.next(3); i < 8; i++) {
                        Coord c = epigon.player.location.translate(Direction.OUTWARDS[i + offset & 7]);
                        if (epigon.map.inBounds(c) && epigon.map.lighting.fovResult[c.x][c.y] > 0) {
                            epigon.map.contents[c.x][c.y].add(dropped);
                            break;
                        }
                    }
                }
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
            case MESSAGE_PRIOR:
                epigon.scrollMessages(-1);
                break;
            case MESSAGE_NEXT:
                epigon.scrollMessages(1);
                break;
            case HELP:
                epigon.mapOverlayHandler.setMode(MapOverlayHandler.PrimaryMode.HELP);
                epigon.mapInput.setKeyHandler(epigon.helpKeys);
                epigon.toCursor.clear();
                epigon.mapInput.setMouse(epigon.helpMouse);
                break;
            case QUIT:
                // TODO - confirmation
                Gdx.app.exit();
                return;
            case WAIT:
                epigon.scheduleMove(Direction.NONE);
                break;
            case REST:
                // TODO - why was this previously: epigon.prepFall();
                break;
            case INTERACT:
                Optional<Physical> t;
                if ((t = epigon.player.inventory.stream().filter(ph -> ph.symbol == 'ῗ').findFirst()).isPresent()) {
                    if (epigon.player.creatureData.lastUsedItem != null && epigon.player.creatureData.lastUsedItem.symbol == 'ῗ') {
                        epigon.player.creatureData.lastUsedItem = null;
                    } else {
                        epigon.player.creatureData.lastUsedItem = t.get();
                    }
                }
                break;
            default:
                //message("Can't " + verb.name + " from main view.");
                return;
        }
        epigon.multiplexer.processedInput = true;
        epigon.infoHandler.updateDisplay();
        // check if the turn clock needs to run
        if (verb.isAction()) {
            epigon.runTurn();
        }
    }
}
