package squidpony.epigon.input.key;

import java.util.List;

import squidpony.Messaging;
import squidpony.squidgrid.Direction;
import squidpony.squidmath.Coord;
import squidpony.squidgrid.gui.gdx.SquidInput;
import squidpony.squidgrid.gui.gdx.SquidInput.KeyHandler;

import squidpony.epigon.game.Epigon;
import squidpony.epigon.data.Physical;
import squidpony.epigon.data.trait.Interactable;
import squidpony.epigon.display.MapOverlayHandler;
import squidpony.epigon.input.ControlMapping;
import squidpony.epigon.input.Verb;

/**
 * Handles input for actively falling in Falling mode
 */
public class EquipmentKeyHandler implements KeyHandler {

    private final Epigon epigon;

    public EquipmentKeyHandler(Epigon epigon) {
        this.epigon = epigon;
    }

    @Override
    public void handle(char key, boolean alt, boolean ctrl, boolean shift) {
        if (epigon.multiplexer.processedInput) {
            return;
        }
        int combined = SquidInput.combineModifiers(key, alt, ctrl, shift);
        Verb verb = ControlMapping.allMappings.get(combined);
        if (!ControlMapping.defaultEquipmentViewMapping.contains(verb)) {
            return;
        }
        if (epigon.showingMenu) {
            if (epigon.mapOverlayHandler.getSelected() == null || epigon.mapOverlayHandler.getSelected().interactableData == null
                || epigon.mapOverlayHandler.getSelected().interactableData.isEmpty()) {
                epigon.showingMenu = false;
                epigon.menuLocation = null;
                epigon.mapOverlayHandler.setSubselection(null);
                epigon.maneuverOptions.clear();
                epigon.interactionOptions.clear();
                epigon.currentTarget = null;
                epigon.mapOverlaySLayers.clear(1);
                epigon.mapOverlaySLayers.clear(2);
                epigon.multiplexer.processedInput = true;
                epigon.infoHandler.updateDisplay();
                return;
            }
            List<Interactable> interactableData = epigon.mapOverlayHandler.getSelected().interactableData;
            Coord sub = epigon.mapOverlayHandler.getSubselection();
            switch (verb) {
                case MOVE_DOWN:
                    if (sub.y + 1 < interactableData.size()) {
                        epigon.mapOverlayHandler.setSubselection(sub.x, sub.y + 1);
                    }
                    break;
                case MOVE_UP:
                    if (sub.y > 0) {
                        epigon.mapOverlayHandler.setSubselection(sub.x, sub.y - 1);
                    }
                    break;
                case CLOSE_SCREEN:
                case MOVE_LEFT:
                    epigon.showingMenu = false;
                    epigon.menuLocation = null;
                    epigon.mapOverlayHandler.setSubselection(null);
                    epigon.maneuverOptions.clear();
                    epigon.interactionOptions.clear();
                    epigon.currentTarget = null;
                    epigon.mapOverlaySLayers.clear(1);
                    epigon.mapOverlaySLayers.clear(2);
                    epigon.multiplexer.processedInput = true;
                    epigon.infoHandler.updateDisplay();
                    return;
                case MOVE_RIGHT:
                case INTERACT:
                    if (epigon.mapOverlayHandler.getSubselection() != null) {
                        Interactable interaction = epigon.interactionOptions.getAt(sub.y);
                        if (interaction == null) {
                            break;
                        }
                        Physical selected = epigon.mapOverlayHandler.getSelected();
                        if (interaction.consumes) {
                            epigon.player.removeFromInventory(selected);
                        }
                        epigon.message(Messaging.transform(interaction.interaction.interact(epigon.player, selected, epigon),
                            epigon.player.name, Messaging.NounTrait.SECOND_PERSON_SINGULAR));
                        epigon.showingMenu = false;
                        epigon.menuLocation = null;
                        epigon.mapOverlayHandler.setSubselection(null);
                        epigon.maneuverOptions.clear();
                        epigon.interactionOptions.clear();
                        epigon.currentTarget = null;
                        epigon.mapOverlaySLayers.clear(1);
                        epigon.mapOverlaySLayers.clear(2);
                    }
                    break;
                default:
                    return;
            }

            epigon.multiplexer.processedInput = true;
            epigon.infoHandler.updateDisplay();
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
            case WIELD:
                epigon.equipItem(epigon.mapOverlayHandler.getSelected());
                break;
            case DROP:
                epigon.map.contents[epigon.player.location.x][epigon.player.location.y].contents.add(epigon.player.removeFromInventory(epigon.mapOverlayHandler.getSelected()));
                break;
            case INTERACT:
                Physical selected = epigon.mapOverlayHandler.getSelected();
                if (selected == null) {
                    break;
                }
                if (selected.interactableData != null && !selected.interactableData.isEmpty()) {
                    epigon.buildInteractOptions(selected);
                    epigon.menuLocation = epigon.showInteractOptions(selected, epigon.player, epigon.mapOverlayHandler.getSelection(), epigon.map);
                    epigon.mapOverlayHandler.setSubselection(0, 0);
//                        message("Interactions for " + selected.name + ": " + selected.interactableData
//                            .stream()
//                            .map(interact -> interact.phrasing)
//                            .collect(Collectors.joining(", ")));
//                        Interactable interaction = selected.interactableData.get(0);
//                        if (interaction.consumes) {
//                            player.removeFromInventory(selected);
//                        }
//                        message(Messaging.transform(interaction.interaction.interact(player, selected, map),
//                                player.name, Messaging.NounTrait.SECOND_PERSON_SINGULAR));
                } else if (selected.wearableData != null || selected.weaponData != null) {
                    if (epigon.player.creatureData.equippedDistinct.contains(selected)) {
                        epigon.player.unequip(selected);
                        epigon.player.addToInventory(selected); // Equip pulls from inventory if needed, but unequip does not put it back
                    } else {
                        epigon.player.equipItem(selected);
                    }
                } else {
                    epigon.message("No interaction for " + selected.name);
                }
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
            case HELP:
                epigon.mapOverlayHandler.setMode(MapOverlayHandler.PrimaryMode.HELP);
                epigon.mapInput.setKeyHandler(epigon.helpKeys);
                epigon.mapInput.setMouse(epigon.helpMouse);
                break;
            case EQUIPMENT:
            case CLOSE_SCREEN:
                epigon.mapInput.setKeyHandler(epigon.mapKeys);
                epigon.mapInput.setMouse(epigon.mapMouse);
                epigon.mapOverlayHandler.hide();
                break;
            default:
                //epigon.message("Can't " + verb.name + " from equipment view.");
                return; // note, this will not change processedInput
            }
        epigon.multiplexer.processedInput = true;
        epigon.infoHandler.updateDisplay();
    }
}
