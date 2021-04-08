package squidpony.epigon.input.key;

import squidpony.squidgrid.gui.gdx.SquidInput;

import squidpony.epigon.Epigon;
import squidpony.epigon.files.Config;
import squidpony.epigon.input.ControlMapping;
import squidpony.epigon.input.Verb;

/**
 * Handles input for actively falling in Falling mode
 */
public class FallbackKeyHandler implements EpigonKeyHandler {

    private Epigon epigon;
    private Config config;

    @Override
    public FallbackKeyHandler setEpigon(Epigon epigon) {
        this.epigon = epigon;
        return this;
    }

    @Override
    public FallbackKeyHandler setConfig(Config config) {
        this.config = config;
        return this;
    }

    @Override
    public void handle(char key, boolean alt, boolean ctrl, boolean shift) {
        if (epigon.multiplexer.processedInput) {
            return;
        }
        Verb verb = ControlMapping.allMappings.get(SquidInput.combineModifiers(key, alt, ctrl, shift));
        String m;
        if (epigon.mapOverlaySLayers.isVisible()) {
            switch (epigon.mapOverlayHandler.getMode()) {
                case HELP:
                    if (!ControlMapping.defaultHelpViewMapping.contains(verb)) {
                        verb = null;
                    }
                    m = "help";
                    break;
                case CRAFTING:
                    if (!ControlMapping.defaultEquipmentViewMapping.contains(verb)) {
                        verb = null;
                    }
                    m = "crafting";
                    break;
                default:
                    if (!ControlMapping.defaultEquipmentViewMapping.contains(verb)) {
                        verb = null;
                    }
                    m = "equipment";
                    break;
            }
        } else {
            switch (config.settings.mode) {
                case DIVE:
                    if (!ControlMapping.defaultFallingViewMapping.contains(verb)) {
                        verb = null;
                    }
                    m = "dive";
                    break;
                default:
                    if (!ControlMapping.defaultMapViewMapping.contains(verb)) {
                        verb = null;
                    }
                    m = "map";
                    break;
            }
        }
        if (verb == null) {
            epigon.message("Unknown input for " + m + " mode: " + key);
        } else {
            epigon.message("Can't " + verb.name + " from " + m + " mode.");
        }
    }
}
