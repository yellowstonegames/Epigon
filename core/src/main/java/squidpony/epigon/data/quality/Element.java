package squidpony.epigon.data.quality;

import com.badlogic.gdx.graphics.Color;
import squidpony.squidgrid.gui.gdx.Radiance;
import squidpony.epigon.Utilities;
import squidpony.epigon.data.LiveValue;
import squidpony.epigon.data.Modification;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidmath.OrderedMap;

/**
 * Elements are keys to know when certain resistances and such apply.
 */
public enum Element { // TODO - make this a full EpiData class with external data source
    // Mundane
    MUNDANE(null, "mundane", "strike$", SColor.MOUNTBATTEN_PINK, 0f, 0f),
    PIERCING(MUNDANE, "stabbing", "pierce$", SColor.CW_DRAB_SEAFOAM, 0f, 0f),
    SLASHING(MUNDANE, "slashing", "cut$", SColor.CW_DRAB_ROSE, 0f, 0f),
    BLUNT(MUNDANE, "blunt", "batter$", SColor.CW_DRAB_VIOLET, 0f, 0f),
    // Natural
    NATURE(null, "natural", "roar$", SColor.FOREST_GREEN, 0.1f, 0f),
    EARTH(NATURE, "earthen", "bur$$$", SColor.SLATE_GRAY, 0f, 0f),
    CRYSTAL(EARTH, "crystalline", "slice$", new SColor(0xD0F2FFFF), 0f, 0f),
    FIRE(NATURE, "fiery", "burn$", SColor.ORANGE_PEEL, 0.8f, 0f),
    //MAGMA(FIRE, "magma", "melt$", SColor.CW_BRIGHT_ORANGE), // removed because it seems to overlap with fire heavily
    AIR(NATURE, "airy", "toss$$", SColor.BRIGHT_TURQUOISE, 0.85f, 0f),
    SOUND(NATURE, "sonic", "deafen$", new SColor(0xB3A784FF), 0f, 1.8f),
    WATER(NATURE, "watery", "flood$", SColor.CW_FLUSH_AZURE, 0f, 0.5f),
    ICE(WATER, "icy", "freeze$", SColor.CW_PALE_AZURE, 0f, 0.1f),
    LIGHTNING(NATURE, "stormy", "shock$", SColor.CW_LIGHT_VIOLET, 0.925f, 0f),
    POISON(NATURE, "poisonous", "poison$", SColor.PEAR, 0f, 0f),
    ACID(POISON, "acidic", "mar$", SColor.CW_BRIGHT_LIME, 0.5f, 0f),
    CAUSTIC(POISON, "caustic", "bleach$$", SColor.CW_SEAFOAM, 0.2f, 0f),
    DISEASE(POISON, "virulent", "infect$", SColor.MOUSY_WISTERIA, 0.3f, 0f),
    // Magical
    MAGIC(null, "magical", "zap$", SColor.CW_FLUSH_AZURE, 0f, 0.6f), // Do Magic!
    DEATH(MAGIC, "necrotic", "wither$", SColor.SISKIN_SPROUT_YELLOW, 0.04f, 0f),
    LIFE(MAGIC, "vital", "heal$", SColor.CW_BRIGHT_RED, 0f, 0f),
    SHINING(MAGIC, "shining", "blind$", SColor.WHITE, 0f, 1.2f), // light that burns! especially dungeon-dwelling monsters!
    SHADOW(MAGIC, "shadowy", "dim$", new SColor(0x363033FF), 0f, 0f), // darkness doesn't really do damage normally, so this is magic
    PRIMAL(MAGIC, "worldly", "trap$", SColor.DISTANT_RIVER_BROWN, 0.1f, 0f), // Using this as Primal magic (druids, shamans, etc.) when it needs to be used directly
    TEMPORAL(MAGIC, "temporal", "wobble$", SColor.DB_EGGSHELL, 0f, 0.15f), // wibbly-wobbly timey-wimey bits
    FATEFUL(MAGIC, "fateful", "curse$", SColor.VEGAS_GOLD, 0.2f, 0f), // luck, fortune and destined damage
    PURE(MAGIC, "pure", "purif$$$", new SColor(0xCCEEFFFF), 0f, 0f), // negatively affects horrible things (undead, demons), does some damage to despoilers of this world
    // transforming (polymorph) attacks work well as an element that, instead of killing, changes the target's shape
    CHANNELING(MAGIC, "channeling", "channel$", SColor.INTERNATIONAL_KLEIN_BLUE, 0f, 0.08f),
    // may want magic based on authority/bond-of-word/pacts, to fit the theme of a relative of royalty
    CONTRACTUAL(CHANNELING, "contractual", "bind$", SColor.STEEL_BLUE, 0f, 0f), // pact magic or magic that damages based on some powerful authority
    SINISTER(CHANNELING, "sinister", "hex$$", SColor.BURGUNDY, 0.7f, 0f), // stealing power from gods and/or demons and diverting the punishment to others
    DIVINE(CHANNELING, "divine", "judge$", SColor.LEMON_CHIFFON, 0f, 0.9f), // faith-based power granted by gods to their followers
    // Physical
    PHYSICAL(null, "physical", "manipulate$", SColor.CW_DRAB_AZURE, 0f, 0f), // more sci-fi elements that most people don't understand like NATURAL ones
    GRAVITY(PHYSICAL, "gravitational", "crush$$", SColor.DB_STORM_CLOUD, 0f, 0.03f), // Drop it like it's hot, and then drop everything else on top of that
    VACUOUS(PHYSICAL, "vacuous", "decompress$$", SColor.DB_CERULEAN, 0f, 0f), // EXPLOSIVE DECOMPRESSION COMMENCE
    WARP(PHYSICAL, "warping", "warp$", SColor.RED_VIOLET, 0.3f, 0f), // teleporting bits and pieces of enemies and their equipment
    RADIATION(PHYSICAL, "radioactive", "irradiate$", SColor.LIME, 0f, 0.4f), // THE DUKE AND DUCHESS OF NUKEM HAVE ARRIVED!
    // Cosmic
    COSMIC(null, "cosmic", "madden$", SColor.CW_PALE_YELLOW), // a little bit of everything in space
    SOLAR(COSMIC, "solar", "madden$", SColor.CW_LIGHT_GOLD), // The sun is just like any other star except it holds at least one being interested in "us"
    LUNAR(COSMIC, "lunar", "madden$", SColor.SILVER), // Tleco, the shut_tleco_ck-shaped Earth-like world, has a few moons that affect monsters in many ways
    STELLAR(COSMIC, "stellar", "madden$", SColor.BLUE_GREEN), // Stars may be home to god-like beings with no comprehension of mortals
    COMETARY(COSMIC, "cometary", "madden$", SColor.DB_PLATINUM), // Comets would be messages between celestial beings; this intercepts them
    PLANETARY(COSMIC, "planetary", "madden$", SColor.CW_FADED_APRICOT), // Knowledge of other planets and being able to mimic their conditions here
    METEORIC(COSMIC, "meteoric", "madden$", SColor.BRIGHT_GOLDEN_YELLOW), // Meteors are space debris that falls to ground, this moves them to attack enemies
    // Misc
    LIGHT(null, "light", "illuminate$", SColor.ALICE_BLUE, 0.5f, 0f); //Basic lighting, not related to magical or other effects.

    public static final Element[] allMundane = {BLUNT, PIERCING, SLASHING},
    allNatural = {EARTH, CRYSTAL, FIRE, AIR, SOUND, WATER, ICE, LIGHTNING, POISON, ACID, CAUSTIC, DISEASE},
    allMagic = {DEATH, LIFE, SHINING, SHADOW, TEMPORAL, FATEFUL, PURE, PRIMAL, CONTRACTUAL, SINISTER, DIVINE},
    allPhysical = {GRAVITY, VACUOUS, WARP, RADIATION},
    allCosmic = {SOLAR, LUNAR, STELLAR, COMETARY, PLANETARY, METEORIC},
    allDamage = {BLUNT, PIERCING, SLASHING,
            EARTH, CRYSTAL, FIRE, AIR, SOUND, WATER, ICE, LIGHTNING, POISON, ACID, CAUSTIC, DISEASE,
            DEATH, SHINING, SHADOW, TEMPORAL, FATEFUL, PURE, PRIMAL, CONTRACTUAL, SINISTER, DIVINE,
            GRAVITY, VACUOUS, WARP, RADIATION},
    /**
     * Different from allDamage because it only contains magic-based elements, no blunt, slashing, or life.
     * Meant for Modifications.
     */
    allEnergy = {EARTH, CRYSTAL, FIRE, AIR, SOUND, WATER, ICE, LIGHTNING, POISON, ACID, CAUSTIC, DISEASE,
            DEATH, SHINING, SHADOW, TEMPORAL, FATEFUL, PURE, PRIMAL, CONTRACTUAL, SINISTER, DIVINE,
            GRAVITY, VACUOUS, WARP, RADIATION};

    public Element parent;
    public String name, styledName;
    public String verb;
    public Color color;
    public float floatColor;
    public float flicker, strobe;
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

    Element(Element parent, String name, String verb, Color color) {
        this(parent, name, verb, color, 0f, 0.1f);
    }
    Element(Element parent, String name, String verb, Color color, float flicker, float strobe) {
        this.parent = parent;
        this.name = name == null || name.isEmpty() ? name().toLowerCase() : name;
        this.styledName = Utilities.colorize(name, color);
        this.verb = verb;
        this.color = color;
        this.floatColor = color.toFloatBits();
        this.flicker = flicker;
        this.strobe = strobe;
    }

    @Override
    public String toString() {
        return name;
    }
    public String toStyledString() {
        return styledName;
    }
    public Modification weaponModification()
    {
        Modification mod = new Modification();
        LiveValue lv = mod.elementalDamageMultiplier.get(this);
        if(lv != null)
            lv.addActual(0.5); // increases damage by extra 50%
        else
        {
            mod.elementalDamageMultiplier.put(this, new LiveValue(1.5));
        }
        if(mod.weaponElementsAdditive != null)
            mod.weaponElementsAdditive.put(this, 5.0);
        else
            mod.weaponElementsAdditive = OrderedMap.makeMap(this, 5.0);
        mod.possiblePrefix.add(name);
        if(mod.color != null)
            mod.color = mod.color.cpy().lerp(color, 0.5f);
        else
            mod.color = color;
        mod.radiance = new Radiance(3.4f, SColor.toEditedFloat(floatColor, 0f, -0.1f, 0.3f, 0f), flicker, strobe);
        return mod;
    }
}
