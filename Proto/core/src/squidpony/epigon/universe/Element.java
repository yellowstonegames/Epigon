package squidpony.epigon.universe;

import squidpony.squidgrid.gui.gdx.SColor;

/**
 * Elements are keys to know when certain resistances and such apply.
 */
public enum Element { // TODO - make this a full EpiData class with external data source
    // Mundane
    MUNDANE(),
    PIERCE(MUNDANE),
    LACERATE(MUNDANE),
    BLUNT(MUNDANE),
    // Natural
    NATURE(),
    STONE(NATURE),
    CRYSTAL(STONE),
    FIRE(NATURE, "fire", "hot hot hot", SColor.FIREBRICK),
    MAGMA(FIRE, "magma", "flowing hotness", SColor.BURNT_ORANGE),
    AIR(NATURE),
    SONIC(NATURE),
    ACID(NATURE),
    ALKALAI(NATURE),
    ICE(NATURE),
    WATER(NATURE),
    // Magical
    MAGIC(),
    GEOMANCY(MAGIC),
    CHRONOMANCY(MAGIC),
    // Astronomical
    ASTRONOMICAL(),
    SOLAR(ASTRONOMICAL),
    LUNAR(ASTRONOMICAL),
    STELLAR(ASTRONOMICAL),
    COMETARY(ASTRONOMICAL),
    PLANETARY(ASTRONOMICAL),
    METEORIC(ASTRONOMICAL),
    // Misc
    DEATH(),
    LIFE(),
    LANGUAGE(),
    COLOR();

    public Element parent;
    public String name;
    public String description;
    public SColor color;

    private Element() {
        this(null);
    }

    private Element(Element parent) {
        this(parent, "", "", SColor.BLACK);
        name = name();
    }

    private Element(Element parent, String name, String description, SColor color) {
        this.parent = parent;
        this.name = name;
        this.description = description;
        this.color = color;
    }
}
