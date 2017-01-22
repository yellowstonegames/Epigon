package squidpony.epigon.universe;

import squidpony.squidgrid.gui.gdx.SColor;

/**
 * Elements are keys to know when certain resistances and such apply.
 */
public enum Element {

    FIRE(null, "fire", "hot hot hot", SColor.FIREBRICK),
    MAGMA(FIRE, "magma", "flowing hotness", SColor.BURNT_ORANGE);

    public Element parent;
    public String name;
    public String description;
    public SColor color;

    private Element(Element parent, String name, String description, SColor color) {
        this.parent = parent;
        this.name = name;
        this.description = description;
        this.color = color;
    }
}
