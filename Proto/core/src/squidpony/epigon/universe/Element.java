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
    // if you have magma, then smoke makes sense.
    SONIC(NATURE),
    ACID(NATURE),
    ALKALAI(NATURE), // Misspelled. A correct alternative would be ALKALI, ALKALINE, or possibly CAUSTIC
    ICE(NATURE),
    WATER(NATURE),
    // You probably want electrical/lightning. This is a bizarre omission as it stands.
    // Maybe you want a disease element? Maybe radiation (possibly magical or nuclear)?
    // Magical
    MAGIC(),
    GEOMANCY(MAGIC), // Is this ley line location magic, feng-shui-like sacred geometry, or just dirt magic?
    CHRONOMANCY(MAGIC),
    // transforming (polymorph) attacks work well as an element that, instead of killing, changes the target's shape
    // may want divine/morally-based magic, which might just smite anyone who isn't the user
    // may want magic based on authority/bond-of-word/pacts, to fit the theme of a relative of royalty
    // may want attacks via teleporting the target to be an element
    // Astronomical
    ASTRONOMICAL(),
    SOLAR(ASTRONOMICAL),
    LUNAR(ASTRONOMICAL),
    STELLAR(ASTRONOMICAL),
    COMETARY(ASTRONOMICAL), // Why both cometary and meteoric but no black hole magic? SINGULAR, I'd say.
    PLANETARY(ASTRONOMICAL),
    METEORIC(ASTRONOMICAL),
    // maybe you want a VACUOUS element for the empty void between cosmic bodies?
    // Misc
    LIGHT(null, "light", "Basic lighting, not related to magical or other effects.", SColor.ALICE_BLUE),
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
        name = name().toLowerCase();
    }

    private Element(Element parent, String name, String description, SColor color) {
        this.parent = parent;
        this.name = name;
        this.description = description;
        this.color = color;
    }
}
