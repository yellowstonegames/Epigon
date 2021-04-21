package squidpony.epigon.input.key;

import java.util.List;

import squidpony.Messaging;
import squidpony.squidgrid.Direction;
import squidpony.squidmath.Coord;
import squidpony.squidgrid.gui.gdx.SquidInput;
import squidpony.squidgrid.gui.gdx.SquidInput.KeyHandler;

import squidpony.epigon.game.Crawl;
import squidpony.epigon.data.Physical;
import squidpony.epigon.data.trait.Interactable;
import squidpony.epigon.display.MapOverlayHandler;
import squidpony.epigon.input.ControlMapping;
import squidpony.epigon.input.Verb;

/**
 * Handles input for actively falling in Falling mode
 */
public class EquipmentKeyHandler implements KeyHandler {

    private final Crawl crawl;

    public EquipmentKeyHandler(Crawl crawl) {
        this.crawl = crawl;
    }

    @Override
    public void handle(char key, boolean alt, boolean ctrl, boolean shift) {
        if (crawl.multiplexer.processedInput) {
            return;
        }
        int combined = SquidInput.combineModifiers(key, alt, ctrl, shift);
        Verb verb = ControlMapping.allMappings.get(combined);
        if (!ControlMapping.defaultEquipmentViewMapping.contains(verb)) {
            return;
        }
        if (crawl.showingMenu) {
            if (crawl.mapOverlayHandler.getSelected() == null || crawl.mapOverlayHandler.getSelected().interactableData == null
                || crawl.mapOverlayHandler.getSelected().interactableData.isEmpty()) {
                crawl.showingMenu = false;
                crawl.menuLocation = null;
                crawl.mapOverlayHandler.setSubselection(null);
                crawl.maneuverOptions.clear();
                crawl.interactionOptions.clear();
                crawl.currentTarget = null;
                crawl.mapOverlaySLayers.clear(1);
                crawl.mapOverlaySLayers.clear(2);
                crawl.multiplexer.processedInput = true;
                crawl.infoHandler.updateDisplay();
                return;
            }
            List<Interactable> interactableData = crawl.mapOverlayHandler.getSelected().interactableData;
            Coord sub = crawl.mapOverlayHandler.getSubselection();
            switch (verb) {
                case MOVE_DOWN:
                    if (sub.y + 1 < interactableData.size()) {
                        crawl.mapOverlayHandler.setSubselection(sub.x, sub.y + 1);
                    }
                    break;
                case MOVE_UP:
                    if (sub.y > 0) {
                        crawl.mapOverlayHandler.setSubselection(sub.x, sub.y - 1);
                    }
                    break;
                case CLOSE_SCREEN:
                case MOVE_LEFT:
                    crawl.showingMenu = false;
                    crawl.menuLocation = null;
                    crawl.mapOverlayHandler.setSubselection(null);
                    crawl.maneuverOptions.clear();
                    crawl.interactionOptions.clear();
                    crawl.currentTarget = null;
                    crawl.mapOverlaySLayers.clear(1);
                    crawl.mapOverlaySLayers.clear(2);
                    crawl.multiplexer.processedInput = true;
                    crawl.infoHandler.updateDisplay();
                    return;
                case MOVE_RIGHT:
                case INTERACT:
                    if (crawl.mapOverlayHandler.getSubselection() != null) {
                        Interactable interaction = crawl.interactionOptions.getAt(sub.y);
                        if (interaction == null) {
                            break;
                        }
                        Physical selected = crawl.mapOverlayHandler.getSelected();
                        if (interaction.consumes) {
                            crawl.player.removeFromInventory(selected);
                        }
                        crawl.message(Messaging.transform(interaction.interaction.interact(crawl.player, selected, crawl),
                            crawl.player.name, Messaging.NounTrait.SECOND_PERSON_SINGULAR));
                        crawl.showingMenu = false;
                        crawl.menuLocation = null;
                        crawl.mapOverlayHandler.setSubselection(null);
                        crawl.maneuverOptions.clear();
                        crawl.interactionOptions.clear();
                        crawl.currentTarget = null;
                        crawl.mapOverlaySLayers.clear(1);
                        crawl.mapOverlaySLayers.clear(2);
                    }
                    break;
                default:
                    return;
            }

            crawl.multiplexer.processedInput = true;
            crawl.infoHandler.updateDisplay();
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
            case WIELD:
                crawl.equipItem(crawl.mapOverlayHandler.getSelected());
                break;
            case DROP:
                crawl.map.contents[crawl.player.location.x][crawl.player.location.y].contents.add(crawl.player.removeFromInventory(crawl.mapOverlayHandler.getSelected()));
                break;
            case INTERACT:
                Physical selected = crawl.mapOverlayHandler.getSelected();
                if (selected == null) {
                    break;
                }
                if (selected.interactableData != null && !selected.interactableData.isEmpty()) {
                    crawl.buildInteractOptions(selected);
                    crawl.menuLocation = crawl.showInteractOptions(selected, crawl.player, crawl.mapOverlayHandler.getSelection(), crawl.map);
                    crawl.mapOverlayHandler.setSubselection(0, 0);
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
                    if (crawl.player.creatureData.equippedDistinct.contains(selected)) {
                        crawl.player.unequip(selected);
                        crawl.player.addToInventory(selected); // Equip pulls from inventory if needed, but unequip does not put it back
                    } else {
                        crawl.player.equipItem(selected);
                    }
                } else {
                    crawl.message("No interaction for " + selected.name);
                }
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
            case HELP:
                crawl.mapOverlayHandler.setMode(MapOverlayHandler.PrimaryMode.HELP);
                crawl.crawlInput.setKeyHandler(crawl.helpKeys);
                crawl.crawlInput.setMouse(crawl.helpMouse);
                break;
            case EQUIPMENT:
            case CLOSE_SCREEN:
                crawl.crawlInput.setKeyHandler(crawl.mapKeys);
                crawl.crawlInput.setMouse(crawl.mapMouse);
                crawl.mapOverlayHandler.hide();
                break;
            default:
                //epigon.message("Can't " + verb.name + " from equipment view.");
                return; // note, this will not change processedInput
        }
        crawl.multiplexer.processedInput = true;
        crawl.infoHandler.updateDisplay();
    }
}
