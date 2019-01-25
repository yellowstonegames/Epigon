package squidpony.epigon.data.quality;

import com.badlogic.gdx.graphics.Color;
import squidpony.epigon.ConstantKey;
import squidpony.epigon.Utilities;
import squidpony.epigon.data.ConditionBlueprint;
import squidpony.epigon.data.Stat;
import squidpony.epigon.data.trait.Interactable;
import squidpony.squidgrid.gui.gdx.SColor;

/**
 * Created by Tommy Ettinger on 12/28/2018.
 */
//¸ grass
//˛ root
//˳ fruit
//ˬ leaf
//˒ thorn
//˷ vine
//∗ flower
//˔ fungus
//∝ cress
public enum Vegetable implements ConstantKey {
    BITTER_YARROW('∗', SColor.COSMIC_LATTE, "a small flower on a long stem",
            new Interactable("chew", true, false, (actor, target, level) -> 
                    actor.removeCondition(ConditionBlueprint.CONDITIONS.get("Afflict"))
                            ? "Chewing the bitter yarrow cures @my poison affliction."
                            : "@Name chew$ the bitter yarrow and make$ a face.")),
    MOSSMELON('˳', SColor.AURORA_FERN_GREEN, "a strange round melon with a mossy rind", "¸≁",
            new Interactable("eat", true, false, (actor, target, level) ->
            {
                actor.stats.get(Stat.HUNGER).addActual(10);
                return "@Name eat$ the mossmelon with gusto.";})),
    SAINT_JOHNʼS_WORT('ˬ', SColor.AURORA_ASPARAGUS, "a squat, round-leafed plant"),
    ANGELCRESS('∝', SColor.AURORA_SAGE_GREEN, "a water plant with lobes that suggest angel wings", "~≁",
            new Interactable("worship", true, false, (actor, target, level) ->
                    actor.removeCondition(ConditionBlueprint.CONDITIONS.get("Curse"))
                            ? "Saying a prayer to the angelcress lifts @name_s curse."
                            : "@Name look$ crazy, talking to a plant.")),
    SNAKEBERRY('˳', SColor.AURORA_EGGPLANT, "a shrub with deep-purple berries that have a scaly texture."),
    GRAY_DOVETHORN('˒', SColor.LAVENDER_GRAY, "a vine that intermingles thorns with gray feathery leaves"),
    RED_RATBANE('˷', SColor.RED_BEAN, "an ugly red vine known to deter rodents", "≁"),
    RASPUTINʼS_SORROW('˛', SColor.AURORA_ZUCCHINI, "a dark vine purported to help men cheat death"),
    DESERT_SAGE('¸', SColor.AURORA_SAGE_GREEN, "a pleasant-smelling dry grass"),
    ALOE_VERA('˒', SColor.AURORA_SILVER_GREEN, "a thorny succulent that hoards moisture in dry deserts",
            new Interactable("eat", true, false, (actor, target, level) ->
            {
                actor.stats.get(Stat.HUNGER).addActual(1);
                actor.stats.get(Stat.THIRST).addActual(9);
                return "@Name slurp$ the clear, pure gel from the aloe vera.";}),
            new Interactable("apply to skin", true, false, (actor, target, level) ->
                actor.removeCondition(ConditionBlueprint.CONDITIONS.get("Ignite"))
                        ? "@Name rub$ aloe vera sap on @my skin, soothing @my burns." 
                        : "@Name decide$ a nice aloe vera spa treatment is the appropriate course of action."
            )),
    FRAGRANT_CLOVEˉHAZEL('˛', SColor.CLOVE_BROWN, "a rich-brown root with an enticing scent"),
    FLYˉAGARIC_MUSHROOM('˔', SColor.RED_PIGMENT, "a toadstool that is said to bring men to Heaven and Hell"),
    SKULLMALLOW('∝', SColor.CW_ALMOST_WHITE, "a reed that oozes a sticky white sap in the pattern of a skull", "¸≁"),
    NOBLE_LOTUS('∗', SColor.HELIOTROPE, "a beautiful purple flower on a lily pad", "~"),
    BLUE_SWEETLEAF('ˬ', SColor.AURORA_SHARP_AZURE, "a low-lying, bright-blue-leafed shrub that smells like honey"),
    BLOODˉOFˉTHIEVES('˒', SColor.AURORA_FRESH_BLOOD, "a thorny thicket said to prick only those with ill intent"),
    LORDʼS_LILY('∗', SColor.WHITE, "a pure-white flower shaped something like a crown", "~",
            new Interactable("wear", false, false, (actor, target, level) ->
                    "@Name put$ the sopping-wet lordʼs lily on @my head@s! @Name@m the lord@s of the nitwits!"));
    
    private final Color color;
    private final char symbol;
    private final String description;
    private final String prettyName;
    private final String terrains;
    private final Interactable[] interactables;
    
    Vegetable(char symbol, Color color, String description, Interactable... interactables) {
        this(symbol, color, description, "¸", interactables);
    }
    Vegetable(char symbol, Color color, String description, String terrains, Interactable... interactables) {
        this.symbol = symbol;
        this.color = color;
        this.description = description;
        prettyName = Utilities.lower(name(), "_").replace('ˉ', '-');
        this.terrains = terrains;
        this.interactables = interactables == null ? new Interactable[0] : interactables;
        hash = ConstantKey.precomputeHash("creature.Vegetable", ordinal());
    }
    public final long hash;
    @Override
    public long hash64() {
        return hash;
    }
    @Override
    public int hash32() {
        return (int)(hash & 0xFFFFFFFFL);
    }

    public String description() {
        return description;
    }

    public String prettyName() {
        return prettyName;
    }

    public Color color() {
        return color;
    }
    public char symbol() {
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

}
