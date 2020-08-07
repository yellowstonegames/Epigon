package squidpony.epigon.data.quality;

import com.badlogic.gdx.graphics.Color;
import squidpony.epigon.ConstantKey;
import squidpony.epigon.util.Utilities;
import squidpony.epigon.data.CalcStat;
import squidpony.epigon.data.Condition;
import squidpony.epigon.data.ConditionBlueprint;
import squidpony.epigon.data.Stat;
import squidpony.epigon.data.trait.Interactable;
import squidpony.squidgrid.gui.gdx.SColor;

/**
 * Created by Tommy Ettinger on 12/28/2018.
 */
//¸  default grass
//ᵧ  special grass
//˛ᵩ root
//˳˚ fruit
//ˬ  leaf
//˒  thorn
//˷  vine
//∗  flower
//˔  fungus
//∝  pond plants
public enum Vegetable implements Material {
    BITTER_YARROW('∗', SColor.COSMIC_LATTE, "a small flower on a long stem", 1, 1, 15,
            new Interactable("chew", true, false, (actor, target, level) -> 
                    actor.removeCondition(ConditionBlueprint.CONDITIONS.get("Afflict"))
                            ? "Chewing the bitter yarrow cures @my poison affliction."
                            : "@Name chew$ the bitter yarrow and make$ a face.")),
    MOSSMELON('˳', SColor.AURORA_FERN_GREEN, "a strange round melon with a mossy rind", "¸≁", 2, 2, -1,
            new Interactable("eat", true, false, (actor, target, level) -> {
                actor.stats.get(Stat.NUTRITION).addActual(10);
                return "@Name eat$ the mossmelon with gusto.";})),
    SAINT_JOHNʼS_WORT('ˬ', SColor.AURORA_ASPARAGUS, "a squat, round-leafed plant", 1, 1, -1),
    ANGELCRESS('∝', SColor.AURORA_SAGE_GREEN, "a water plant with lobes that suggest angel wings", "~≁",
            5, 1, -1,
            new Interactable("worship", true, false, (actor, target, level) ->
                    actor.removeCondition(ConditionBlueprint.CONDITIONS.get("Curse"))
                            ? "Saying a prayer to the angelcress lifts @name_s curse."
                            : "@Name look$ crazy, talking to a plant.")),
    SNAKEBERRY('˳', SColor.AURORA_EGGPLANT, "a shrub with deep-purple berries that have a scaly texture.", 2, 3, -1),
    GRAY_DOVETHORN('˒', SColor.LAVENDER_GRAY, "a vine that intermingles thorns with gray feathery leaves",
            1, 4, 3,
            new Interactable("grasp", true, false, (actor, target, level) -> {
                actor.applyCondition(new Condition(ConditionBlueprint.CONDITIONS.get("Bleed"), actor));
                actor.stats.get(CalcStat.STEALTH).addActual(1.0);
                return "@Name begin$ to bleed profusely, but the blood disappears before it touches the ground!";})),
    RED_RATBANE('˷', SColor.RED_BEAN, "an ugly red vine known to deter rodents", "≁", 4, 2, 4),
    RASPUTINʼS_SORROW('ᵩ', SColor.AURORA_ZUCCHINI, "a dark vine purported to help men cheat death",
            10, 2, -1,
            new Interactable("eat", true, false, (actor, target, level) -> {
                if(actor.stats.get(Stat.VIGOR).actual() * 10.0 < actor.stats.get(Stat.VIGOR).max())
                {
                    actor.stats.get(Stat.VIGOR).set(actor.stats.get(Stat.VIGOR).max() * 0.4);
                    return "@Name_s heart race$ as @i gobble$ down the Rasputin's sorrow, and @my wounds begin to close!";
                }
                else 
                {
                    actor.stats.get(Stat.VIGOR).multiplyActual(0.5);
                    return "@Name eat$ the Rasputin's sorrow, only to feel agonizing pain!";
                }
            })),
    DESERT_SAGE('ᵧ', SColor.AURORA_SAGE_GREEN, "a pleasant-smelling dry grass", 2, 1, 1),
    ALOE_VERA('˒', SColor.AURORA_SILVER_GREEN, "a thorny succulent that hoards moisture in dry deserts",
            1, 1, -1,
            new Interactable("eat", true, false, (actor, target, level) ->
            {
                actor.stats.get(Stat.NUTRITION).addActual(1);
                actor.stats.get(Stat.HYDRATION).addActual(9);
                return "@Name slurp$ the clear, pure gel from the aloe vera.";}),
            new Interactable("apply to skin", true, false, (actor, target, level) ->
                actor.removeCondition(ConditionBlueprint.CONDITIONS.get("Ignite"))
                        ? "@Name rub$ aloe vera sap on @my skin, soothing @my burns." 
                        : "@Name decide$ a nice aloe vera spa treatment is the appropriate course of action."
            )),
    FRAGRANT_CLOVEˉHAZEL('ᵩ', SColor.CLOVE_BROWN, "a rich-brown root with an enticing scent", 
            0, 1, 7,
            new Interactable("eat", true, false, (actor, target, level) -> {
                if(actor.nextBoolean())
                {
                    actor.applyCondition(new Condition(ConditionBlueprint.CONDITIONS.get("Afflict"), actor));
                    return "@Name eat$ the fragrant clove-hazel, only to find part of it was dog poo...";
                }
                else
                {
                    actor.stats.get(Stat.REST).addActual(20);
                    return "@Name eat$ the fragrant clove-hazel and feel$ a surge of energy!";
                }
    })),
    FLYˉAGARIC_MUSHROOM('˔', SColor.RED_PIGMENT, "a toadstool that is said to bring men to Heaven and Hell",
            8, 1, -1,
            new Interactable("eat", true, false, (actor, target, level) -> {
                actor.applyCondition(new Condition(ConditionBlueprint.CONDITIONS.get("Afflict"), actor));
                actor.applyCondition(new Condition(ConditionBlueprint.CONDITIONS.get("Intoxicate"), actor));
                return "@Name begin$ to... uggh... woah... AsCeNd InTo A hIgHeR pLaNe Of ExIsTeNcE!";
            })),
    SKULLMALLOW('∝', SColor.CW_ALMOST_WHITE, "a reed that oozes a sticky white sap in the pattern of a skull", "¸≁",
            1, 1, -1,
            new Interactable("eat", true, false, (actor, target, level) -> {
                actor.applyCondition(new Condition(ConditionBlueprint.CONDITIONS.get("Wither"), actor));
                return "@Name turn$ deathly pale after eating the skullmallow, as one might expect.";})),
    NOBLE_LOTUS('∗', SColor.HELIOTROPE, "a beautiful purple flower on a lily pad", "~",
            15, 1, -1, 
            new Interactable("contemplate", false, false, (actor, target, level) -> {
        actor.stats.get(Stat.SANITY).addActual(5);
        return "@Name pause$ to marvel at the perfection of the noble lotus, and calm$ down.";
    })),
    BLUE_SWEETLEAF('ˬ', SColor.AURORA_SHARP_AZURE, "a low-lying, bright-blue-leafed shrub that smells like honey",
            3, 1, 11,
            new Interactable("chew", true, false, (actor, target, level) -> {
        actor.stats.get(Stat.SLEEP).addActual(8);
        actor.stats.get(Stat.NUTRITION).addActual(2);
        return "@Name chew$ on the tasty, tasty blue sweetleaf and feel$ more awake!";
    })),
    BLOODˉOFˉTHIEVES('˒', SColor.AURORA_FRESH_BLOOD, "a thorny thicket said to prick only those with ill intent",
            1, 7, 6),
    LORDʼS_LILY('∗', SColor.WHITE, "a pure-white flower shaped something like a crown", "~",
            5, 1, -1,
            new Interactable("wear", false, false, (actor, target, level) ->
                    "@Name put$ the sopping-wet lord's lily on @my head@s! @Name@m the lord@s of the nitwits!")),
    GHOST_ACORN('˳', SColor.AURORA_CELADON, "an acorn that seems almost weightless", "¸",
            1, 10, -1,
            new Interactable("eat", true, false, (actor, target, level) -> {
                actor.stats.get(Stat.NUTRITION).addActual(-1);
                return "@Name eat$ the ghost acorn, but feel$ even hungrier...";})),
    FROST_WALNUT('˳', SColor.AURORA_AQUAMARINE, "a ripe but very hard walnut that feels icy-cold to the touch", "",
            5, 90, -1,
            new Interactable("open", true, false, (actor, target, level) -> {
                actor.applyCondition(new Condition(ConditionBlueprint.CONDITIONS.get("Chill"), actor));
                actor.stats.get(CalcStat.DEFENSE).addActual(1.0);
                return "@Name shiver$ with wintry cold after cracking open the frost walnut!";})),
    ROASTERʼS_PECAN('˳', SColor.CW_RICH_ORANGE, "a ripe pecan in a hard shell that seems hot to the touch", "",
            5, 90, 1,
            new Interactable("open", true, false, (actor, target, level) -> {
                actor.applyCondition(new Condition(ConditionBlueprint.CONDITIONS.get("Ignite"), actor));
                actor.stats.get(CalcStat.DAMAGE).addActual(1.0);
                return "@Name catch$$ on fire after cracking open the roaster's pecan!";})),
    LOBSTER_APPLE('˳', SColor.DB_LOBSTER, "an apple with a thick, shell-like rind", "",
            2, 20, -1,
            new Interactable("eat", true, false, (actor, target, level) -> {
                actor.stats.get(Stat.NUTRITION).addActual(10);
                return "@Name eat$ the lobster apple, ignoring the bits of rind in @my teeth.";})),
    THUNDER_CHERRY('˳', SColor.CRIMSON, "a cluster of bright-red cherries with sparks arcing across their skins", "",
            2, 1, -1, new Interactable("eat", true, false, (actor, target, level) -> {
        actor.applyCondition(new Condition(ConditionBlueprint.CONDITIONS.get("Electrify"), actor));
        actor.stats.get(CalcStat.EVASION).addActual(1.0);
        return "@Name @am shocked by the unexpectedly-electric cherries!";
    })),
    SALTBLOSSOM('∗', SColor.CW_ALMOST_WHITE, "a thin, pale shrub with large cubical blossoms surrounding a white jelly",
            1, 1, -1,
            new Interactable("eat", true, false, (actor, target, level) ->
            {
                actor.stats.get(Stat.NUTRITION).addActual(5);
                actor.stats.get(Stat.HYDRATION).addActual(-5);
                return "@Name slurp$ gel from the blossom, but it's so salty that you feel more thirsty.";}));
    
    private final Color color;
    private final char symbol;
    private final int value, hardness, flammability;
    private final String description;
    private final String prettyName;
    private final String terrains;
    private final Interactable[] interactables;
    
    Vegetable(char symbol, Color color, String description,
              int value, int hardness, int flammability, Interactable... interactables) {
        this(symbol, color, description, "¸", value, hardness, flammability, interactables);
    }
    Vegetable(char symbol, Color color, String description, String terrains,
              int value, int hardness, int flammability, Interactable... interactables) {
        this.symbol = symbol;
        this.color = color;
        this.description = description;
        this.value = value;
        this.hardness = hardness;
        this.flammability = flammability;
        prettyName = Utilities.lower(name(), "_").replace('ˉ', '-').replace('ʼ', '\'');
        this.terrains = terrains;
        this.interactables = interactables == null ? new Interactable[0] : interactables;
        hash = ConstantKey.precomputeHash("material.Vegetable", ordinal());
    }
    public final long hash;
    @Override
    public long hash64() {
        return hash;
    }
    @Override
    public int hash32() {
        return (int)(hash);
    }

    public String description() {
        return description;
    }

    public String prettyName() {
        return prettyName;
    }

    public Color getColor() {
        return color;
    }
    public char getGlyph() {
        return symbol;
    }
    public String terrains()
    {
        return terrains;
    }

    public Interactable[] interactables() {
        return interactables;
    }

    public static final Vegetable[] ALL = values();

    @Override
    public int getValue() {
        return value;
    }

    @Override
    public int getHardness() {
        return hardness;
    }

    @Override
    public int getFlammability() {
        return flammability;
    }
}
