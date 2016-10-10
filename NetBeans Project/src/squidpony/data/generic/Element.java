package squidpony.data.generic;

import squidpony.squidcolor.SColor;

/**
 * Elements are simple keys to know when certain resistances and such apply.
 *
 * @author Eben Howard - http://squidpony.com - howard@squidpony.com
 */
public class Element {

    public static final Element //default elements
            FIRE = new Element(null, "fire", "hot hot hot", SColor.FIREBRICK),
            MAGMA = new Element(FIRE, "magma", "flowing hotness", SColor.BURNT_ORANGE);
    public Element parent;
    public String name, description;
    public SColor color;

    private Element(Element parent, String name, String description, SColor color) {
        this.parent = parent;
        this.name = name;
        this.description = description;
        this.color = color;
    }
}
