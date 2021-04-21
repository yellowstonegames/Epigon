package squidpony.epigon.input.mouse;

import com.badlogic.gdx.InputAdapter;

import squidpony.Messaging;

import squidpony.epigon.game.Epigon;
import squidpony.epigon.data.Physical;
import squidpony.epigon.data.trait.Interactable;

/**
 * Handles mouse input for the equipment screen
 */
public class EquipmentMouseHandler extends InputAdapter {

    private Epigon epigon;

    public EquipmentMouseHandler setEpigon(Epigon epigon) {
        this.epigon = epigon;
        return this;
    }

    @Override
    public boolean touchUp(int gridX, int gridY, int pointer, int button) {
        if (epigon.showingMenu) {
            if (epigon.menuLocation.x <= gridX + 2 && epigon.menuLocation.y <= gridY
                && gridY - epigon.menuLocation.y < epigon.interactionOptions.size()
                && epigon.mapOverlaySLayers.getLayer(1).getChar(gridX + 2, gridY, '\uFFFF') == '\0') {
                Interactable interaction = epigon.interactionOptions.getAt(gridY - epigon.menuLocation.y);
                if (interaction == null) {
                    return false;
                }
                Physical selected = epigon.mapOverlayHandler.getSelected();
                if (interaction.consumes) {
                    epigon.player.removeFromInventory(selected);
                }
                epigon.message(Messaging.transform(interaction.interaction.interact(epigon.player, selected, epigon),
                    epigon.player.name, Messaging.NounTrait.SECOND_PERSON_SINGULAR));
            }
            epigon.showingMenu = false;
            epigon.menuLocation = null;
            epigon.maneuverOptions.clear();
            epigon.interactionOptions.clear();
            epigon.currentTarget = null;
            epigon.mapOverlaySLayers.clear(1);
            epigon.mapOverlaySLayers.clear(2);
            return true;
        }

        if (!epigon.mapOverlayHandler.setSelection(gridX, gridY)) {
            return false;
        }
        Physical selected = epigon.mapOverlayHandler.getSelected();
        if (selected.interactableData != null && !selected.interactableData.isEmpty()) {
            epigon.buildInteractOptions(selected);
            if (epigon.interactionOptions == null || epigon.interactionOptions.isEmpty()) {
                epigon.message("Cannot interact with the " + selected.name);
            } else {
                epigon.menuLocation = epigon.showInteractOptions(selected, epigon.player, epigon.mapOverlayHandler.getSelection(), epigon.map);
                epigon.mapOverlayHandler.setSubselection(0, 0);
            }
            return true;

//                message("Interactions for " + selected.name + ": " + selected.interactableData
//                        .stream()
//                        .map(interact -> interact.phrasing)
//                        .collect(Collectors.joining(", ")));
//                Interactable interaction = selected.interactableData.get(0);
//                if (interaction.consumes) {
//                    player.removeFromInventory(selected);
//                }
//                message(Messaging.transform(interaction.interaction.interact(player, selected, map),
//                        player.name, Messaging.NounTrait.SECOND_PERSON_SINGULAR));
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
        return true;
    }

    @Override
    public boolean mouseMoved(int gridX, int gridY) {
        if (!epigon.showingMenu) {
            epigon.mapOverlayHandler.setSelection(gridX, gridY);
        }
        return false;
    }
}
