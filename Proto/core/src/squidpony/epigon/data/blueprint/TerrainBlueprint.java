package squidpony.epigon.data.blueprint;

import squidpony.squidgrid.gui.gdx.SColor;

/**
 * The basic floor or filling of a grid space in the world. Because any terrain may be walked
 * through by the right terrain walk condition, nothing can be considered absolutely solid.
 *
 * Unlike other objects, Terrain has both a background and regular color. The background color is
 * used when the terrain is the actual background of the map and the regular color is used when bits
 * of the terrain are individually available, such as a boulder or pebble. Often the regular color
 * is simply a lighter version of the background color to provide contrast.
 *
 * @author Eben Howard - http://squidpony.com
 */
public class TerrainBlueprint extends PhysicalBlueprint {

    public SColor background;

    public boolean extrusive;
    public boolean intrusive;
    public boolean metamorphic;
    public boolean sedimentary;
}
