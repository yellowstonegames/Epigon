package squidpony.epigon.data.generic;

import squidpony.epigon.data.blueprints.ItemBlueprint;
import squidpony.epigon.data.blueprints.Stone;
import squidpony.squidgrid.gui.gdx.SColor;

/**
 * The basic floor or filling of a grid space in the world. Because any terrain
 * may be walked through by the right terrain walk condition, nothing can be
 * considered absolutely solid.
 *
 * @author Eben Howard - http://squidpony.com
 */
public class TerrainBlueprint extends ItemBlueprint {

    public Stone stone;

    public SColor getBackground() {
        return stone == null ? SColor.BLACK : stone.back;
    }

    public SColor getColor() {
        return stone == null ? SColor.GRAY : stone.front;
    }
}