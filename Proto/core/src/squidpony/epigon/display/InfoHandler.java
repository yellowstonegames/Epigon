package squidpony.epigon.display;

import squidpony.ArrayTools;
import squidpony.squidgrid.gui.gdx.SquidLayers;
import squidpony.squidgrid.gui.gdx.SquidPanel;

/**
 * Handles the content relevant to the current stat mode.
 *
 * @author Eben Howard
 */
public class InfoHandler {

    private SquidPanel back;
    private SquidPanel front;
    private int width;
    private int height;



    public InfoHandler(SquidLayers layers) {
        width = layers.getGridWidth();
        height = layers.getGridHeight();
        back = layers.getBackgroundLayer();
        front = layers.getForegroundLayer();
        ArrayTools.fill(back.colors, back.getDefaultForegroundColor().toFloatBits());
        ArrayTools.fill(front.colors, front.getDefaultForegroundColor().toFloatBits());
        ArrayTools.fill(front.contents, ' ');
    }
}
