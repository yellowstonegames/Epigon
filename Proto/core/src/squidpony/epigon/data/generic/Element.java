package squidpony.epigon.data.generic;

import squidpony.epigon.data.EpiData;

import squidpony.squidgrid.gui.gdx.SColor;

/**
 * Elements are keys to know when certain resistances and such apply.
 *
 * @author Eben Howard - http://squidpony.com
 */
public class Element extends EpiData {

    public static final Element //default elements
        FIRE = new Element(null, "fire", "hot hot hot", SColor.FIREBRICK),
        MAGMA = new Element(FIRE, "magma", "flowing hotness", SColor.BURNT_ORANGE);
    public Element parent;
    public SColor color;

    private Element(Element parent, String name, String description, SColor color) {
        this.parent = parent;
        this.name = name;
        this.description = description;
        this.color = color;
    }
}
