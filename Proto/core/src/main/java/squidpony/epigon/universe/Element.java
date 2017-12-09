package squidpony.epigon.universe;

import com.badlogic.gdx.graphics.Color;
import squidpony.StringKit;
import squidpony.squidgrid.gui.gdx.SColor;

/**
 * Elements are keys to know when certain resistances and such apply.
 */
public enum Element { // TODO - make this a full EpiData class with external data source
    // Mundane
    MUNDANE(SColor.MOUNTBATTEN_PINK),
    PIERCING(MUNDANE),
    SLASHING(MUNDANE),
    BLUNT(MUNDANE),
    // Natural
    NATURE(SColor.FOREST_GREEN),
    EARTH(NATURE, SColor.SLATE_GRAY),
    CRYSTAL(EARTH, new Color(0xE0FFFF99)),
    FIRE(NATURE, "fire", "hot hot hot", SColor.ORANGE_PEEL),
    MAGMA(FIRE, "magma", "flowing hotness", SColor.CW_BRIGHT_ORANGE),
    AIR(NATURE, SColor.BRIGHT_TURQUOISE),
    // if you have magma, then smoke makes sense.
    SOUND(NATURE, "sonic", "THEN THE BASS GOES BRRRRR WRRRR WOOP WOOP WOOP", new Color(0xE3D7B455)),
    WATER(NATURE, "water", "go with the flow", SColor.CW_FLUSH_AZURE),
    ICE(WATER, "ice", "frosty chillin", SColor.CW_PALE_AZURE),
    LIGHTNING(NATURE, "lightning", "DON'T TASE ME BRO", SColor.CW_LIGHT_VIOLET),
    POISON(NATURE, SColor.PEAR),
    ACID(POISON, "acid", "*Kssshh...* AYEEEE, IT BURNS!", SColor.CW_BRIGHT_LIME),
    CAUSTIC(POISON, "caustic", "lay in the lye, just may die", SColor.CW_SEAFOAM),
    DISEASE(POISON, SColor.MOUSY_WISTERIA),
    RADIATION(POISON, SColor.LIME),
    // Magical
    MAGIC(SColor.CW_FLUSH_AZURE), // Do Magic!
    SHINING(MAGIC, SColor.WHITE), // light that burns! especially dungeon-dwelling monsters!
    SHADOW(MAGIC, new Color(0x111111BB)), // darkness doesn't really do damage normally, so this is magic
    GEOMANCY(MAGIC, SColor.DISTANT_RIVER_BROWN), // Is this ley line location magic, feng-shui-like sacred geometry, or just dirt magic? Answer: just dirt
    CHRONOMANCY(MAGIC, SColor.DB_EGGSHELL), // wibbly-wobbly timey-wimey bits
    FATEFUL(MAGIC, SColor.VEGAS_GOLD), // luck, fortune and destined damage
    PURE(MAGIC, new Color(0xCCEEFF88)), // negatively affects horrible things (undead, demons), does some damage to despoilers of this world
    // transforming (polymorph) attacks work well as an element that, instead of killing, changes the target's shape
    CHANNELING(MAGIC, SColor.INTERNATIONAL_KLEIN_BLUE),
    // may want magic based on authority/bond-of-word/pacts, to fit the theme of a relative of royalty
    CONTRACTUAL(CHANNELING, SColor.STEEL_BLUE), // pact magic or magic that damages based on some powerful authority
    SINISTER(CHANNELING, SColor.BURGUNDY), // stealing power from gods and/or demons and diverting the punishment to others
    DIVINE(CHANNELING, SColor.LEMON_CHIFFON), // faith-based power granted by gods to their followers
    // Astronomical
    ASTRONOMICAL(SColor.CW_PALE_YELLOW), // a little bit of everything in space
    SOLAR(ASTRONOMICAL, SColor.CW_LIGHT_GOLD), // The sun is just like any other star except it holds at least one being interested in "us"
    LUNAR(ASTRONOMICAL, SColor.SILVER), // Tleco, the shut_tleco_ck-shaped Earth-like world, has a few moons that affect monsters in many ways
    STELLAR(ASTRONOMICAL, SColor.BLUE_GREEN), // Stars may be home to god-like beings with no comprehension of mortals
    COMETARY(ASTRONOMICAL, SColor.DB_PLATINUM), // Comets would be messages between celestial beings; this intercepts them
    GRAVITY(ASTRONOMICAL, SColor.DB_STORM_CLOUD), // Drop it like it's hot, and then drop everything else on top of that
    PLANETARY(ASTRONOMICAL, SColor.CW_FADED_APRICOT), // Knowledge of other planets and being able to mimic their conditions here
    METEORIC(ASTRONOMICAL, SColor.BRIGHT_GOLDEN_YELLOW), // Meteors are space debris that falls to ground, this moves them to attack enemies
    VACUOUS(ASTRONOMICAL, SColor.DB_CERULEAN), // EXPLOSIVE DECOMPRESSION COMMENCE
    WARP(ASTRONOMICAL, SColor.RED_VIOLET), // teleporting bits and pieces of enemies and their equipment
    // Misc
    LIGHT(null, "light", "Basic lighting, not related to magical or other effects.", SColor.ALICE_BLUE),
    DEATH(SColor.SISKIN_SPROUT_YELLOW),
    LIFE(SColor.CW_BRIGHT_RED),
    LANGUAGE(SColor.FLORAL_LEAF),
    COLOR(SColor.INDIGO_WHITE);

    public Element parent;
    public String name, styledName;
    public String description;
    public Color color;

    private Element() {
        this(SColor.WHITE);
    }

    private Element(Color color) {
        this(null, "", "", color);
    }

    private Element(Element parent) {
        this(parent, "", "", parent == null ? SColor.WHITE : parent.color);
    }

    private Element(Element parent, Color color) {
        this(parent, "", "", color);
    }

    private Element(Element parent, String name, String description, Color color) {
        this.parent = parent;
        this.name = name == null || name.isEmpty() ? name().toLowerCase() : name;
        this.styledName = "[#"+ StringKit.hex(Color.rgba8888(color)) + "]" + this.name + "[White]";
        this.description = description;
        this.color = color;
    }

    @Override
    public String toString() {
        return name;
    }
    public String toStyledString() {
        return styledName;
    }
}
