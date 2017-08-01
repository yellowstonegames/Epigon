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
    FIRE(NATURE, "fire", "hot hot hot", SColor.ORANGE_PEEL),
    MAGMA(FIRE, "magma", "flowing hotness", SColor.CW_BRIGHT_ORANGE),
    AIR(NATURE),
    // if you have magma, then smoke makes sense.
    SONIC(NATURE, "sonic", "THEN THE BASS GOES BRRRRR WRRRR WOOP WOOP WOOP", new SColor(0xE3, 0xD7, 0xB4, 0x55, "Smoky Glass")),
    ACID(NATURE, "acid", "*Kssshh...* AYEEEE, IT BURNS!", SColor.CW_BRIGHT_LIME),
    CAUSTIC(NATURE, "caustic", "lay in the lye, just may die", SColor.CW_SEAFOAM),
    ICE(NATURE, "ice", "frosty chillin", SColor.CW_PALE_AZURE),
    WATER(NATURE, "water", "go with the flow", SColor.CW_FLUSH_AZURE),
    LIGHTNING(NATURE, "lightning", "DON'T TASE ME BRO", SColor.CW_LIGHT_VIOLET),
    POISON(NATURE),
    DISEASE(NATURE),
    RADIATION(NATURE),

    // Magical
    MAGIC(),
    GEOMANCY(MAGIC), // Is this ley line location magic, feng-shui-like sacred geometry, or just dirt magic? Answer: just dirt
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
    GRAVITY(ASTRONOMICAL),
    PLANETARY(ASTRONOMICAL),
    METEORIC(ASTRONOMICAL),
    VACUOUS(ASTRONOMICAL),
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
