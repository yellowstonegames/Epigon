package squidpony.data.generic;

import squidpony.data.blueprints.ItemBlueprint;
import squidpony.data.blueprints.Stone;
import squidpony.squidcolor.SColor;

/**
 * The basic floor or filling of a grid space in the world. Because any terrain
 * may be walked through by the right terrain walk condition, nothing can be
 * considered absolutely solid.
 *
 * @author Eben Howard - http://squidpony.com - howard@squidpony.com
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
