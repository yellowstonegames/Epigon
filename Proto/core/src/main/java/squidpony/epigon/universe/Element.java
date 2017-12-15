package squidpony.epigon.universe;

import com.badlogic.gdx.graphics.Color;
import squidpony.StringKit;
import squidpony.squidgrid.gui.gdx.SColor;

/**
 * Elements are keys to know when certain resistances and such apply.
 */
public enum Element { // TODO - make this a full EpiData class with external data source
    // Mundane
    MUNDANE(null, "mundane", "strike$", SColor.MOUNTBATTEN_PINK),
    PIERCING(MUNDANE, "stabbing", "pierce$", SColor.CW_DRAB_SEAFOAM),
    SLASHING(MUNDANE, "slashing", "cut$", SColor.CW_DRAB_ROSE),
    BLUNT(MUNDANE, "blunt", "batter$", SColor.CW_DRAB_VIOLET),
    // Natural
    NATURE(null, "natural", "roar$", SColor.FOREST_GREEN),
    EARTH(NATURE, "earthen", "bur$$$", SColor.SLATE_GRAY),
    CRYSTAL(EARTH, "crystalline", "slice$", new Color(0xE0FFFF99)),
    FIRE(NATURE, "fiery", "burn$", SColor.ORANGE_PEEL),
    MAGMA(FIRE, "magma", "melt$", SColor.CW_BRIGHT_ORANGE),
    AIR(NATURE, "airy", "toss$$", SColor.BRIGHT_TURQUOISE),
    SOUND(NATURE, "sonic", "deafen$", new Color(0xE3D7B455)),
    WATER(NATURE, "watery", "flood$", SColor.CW_FLUSH_AZURE),
    ICE(WATER, "icy", "freeze$", SColor.CW_PALE_AZURE),
    LIGHTNING(NATURE, "stormy", "shock$", SColor.CW_LIGHT_VIOLET),
    POISON(NATURE, "poisonous", "poison$", SColor.PEAR),
    ACID(POISON, "acidic", "mar$", SColor.CW_BRIGHT_LIME),
    CAUSTIC(POISON, "caustic", "bleach$$", SColor.CW_SEAFOAM),
    DISEASE(POISON, "virulent", "infect$", SColor.MOUSY_WISTERIA),
    RADIATION(POISON, "radioactive", "irradiate$", SColor.LIME),
    // Magical
    MAGIC(null, "magical", "zap$", SColor.CW_FLUSH_AZURE), // Do Magic!
    DEATH(MAGIC, "necrotic", "wither$", SColor.SISKIN_SPROUT_YELLOW),
    LIFE(MAGIC, "vital", "heal$", SColor.CW_BRIGHT_RED),
    SHINING(MAGIC, "shining", "blind$", SColor.WHITE), // light that burns! especially dungeon-dwelling monsters!
    SHADOW(MAGIC, "shadowy", "dim$", new Color(0x111111BB)), // darkness doesn't really do damage normally, so this is magic
    GEOMANCY(MAGIC, "worldly", "trap$", SColor.DISTANT_RIVER_BROWN), // Is this ley line location magic, feng-shui-like sacred geometry, or just dirt magic? Answer: just dirt
    CHRONOMANCY(MAGIC, "temporal", "shift$", SColor.DB_EGGSHELL), // wibbly-wobbly timey-wimey bits
    FATEFUL(MAGIC, "fateful", "curse$", SColor.VEGAS_GOLD), // luck, fortune and destined damage
    PURE(MAGIC, "pure", "purif$$$", new Color(0xCCEEFF88)), // negatively affects horrible things (undead, demons), does some damage to despoilers of this world
    // transforming (polymorph) attacks work well as an element that, instead of killing, changes the target's shape
    CHANNELING(MAGIC, "channeling", "channel$", SColor.INTERNATIONAL_KLEIN_BLUE),
    // may want magic based on authority/bond-of-word/pacts, to fit the theme of a relative of royalty
    CONTRACTUAL(CHANNELING, "contractual", "bind$", SColor.STEEL_BLUE), // pact magic or magic that damages based on some powerful authority
    SINISTER(CHANNELING, "sinister", "hex$$", SColor.BURGUNDY), // stealing power from gods and/or demons and diverting the punishment to others
    DIVINE(CHANNELING, "divine", "judge$", SColor.LEMON_CHIFFON), // faith-based power granted by gods to their followers
    // Astronomical
    ASTRONOMICAL(null, "astronomical", "madden$", SColor.CW_PALE_YELLOW), // a little bit of everything in space
    SOLAR(ASTRONOMICAL, "solar", "madden$", SColor.CW_LIGHT_GOLD), // The sun is just like any other star except it holds at least one being interested in "us"
    LUNAR(ASTRONOMICAL, "lunar", "madden$", SColor.SILVER), // Tleco, the shut_tleco_ck-shaped Earth-like world, has a few moons that affect monsters in many ways
    STELLAR(ASTRONOMICAL, "stellar", "madden$", SColor.BLUE_GREEN), // Stars may be home to god-like beings with no comprehension of mortals
    COMETARY(ASTRONOMICAL, "cometary", "madden$", SColor.DB_PLATINUM), // Comets would be messages between celestial beings; this intercepts them
    GRAVITY(ASTRONOMICAL, "gravitational", "crush$$", SColor.DB_STORM_CLOUD), // Drop it like it's hot, and then drop everything else on top of that
    PLANETARY(ASTRONOMICAL, "planetary", "madden$", SColor.CW_FADED_APRICOT), // Knowledge of other planets and being able to mimic their conditions here
    METEORIC(ASTRONOMICAL, "meteoric", "madden$", SColor.BRIGHT_GOLDEN_YELLOW), // Meteors are space debris that falls to ground, this moves them to attack enemies
    VACUOUS(ASTRONOMICAL, "vacuous", "decompress$$", SColor.DB_CERULEAN), // EXPLOSIVE DECOMPRESSION COMMENCE
    WARP(ASTRONOMICAL, "warping", "warp$", SColor.RED_VIOLET), // teleporting bits and pieces of enemies and their equipment
    // Misc
    LIGHT(null, "light", "illuminate$", SColor.ALICE_BLUE); //Basic lighting, not related to magical or other effects.

    public Element parent;
    public String name, styledName;
    public String verb;
    public Color color;

//    private Element() {
//        this(SColor.WHITE);
//    }
//
//    private Element(Color color) {
//        this(null, "", "", color);
//    }
//
//    private Element(Element parent) {
//        this(parent, "", "", parent == null ? SColor.WHITE : parent.color);
//    }
//
//    private Element(Element parent, Color color) {
//        this(parent, "", "", color);
//    }

    private Element(Element parent, String name, String verb, Color color) {
        this.parent = parent;
        this.name = name == null || name.isEmpty() ? name().toLowerCase() : name;
        this.styledName = "[#"+ StringKit.hex(Color.rgba8888(color)) + "]" + this.name + "[White]";
        this.verb = verb;
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
