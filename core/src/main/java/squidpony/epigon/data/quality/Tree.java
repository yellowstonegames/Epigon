package squidpony.epigon.data.quality;

import com.badlogic.gdx.graphics.Color;
import squidpony.epigon.ConstantKey;
import squidpony.epigon.util.Utilities;
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
public enum Tree implements ConstantKey {

    OAK('¥', SColor.GOLDEN_OAK, "A gnarled tree that could be bearing some acorns", Wood.OAK, Vegetable.GHOST_ACORN),
    PECAN('¥', SColor.TAN, "A tall tree with some little brown nuts in it", Wood.PECAN, Vegetable.ROASTERʼS_PECAN),
    MAPLE('¥', SColor.CLOVE_BROWN, "A tree with some beautiful leaves and thick branches", Wood.MAPLE, null),
    WALNUT('¥', SColor.WALNUT, "A large tree with dark wood and some small white nuts high up on it", Wood.WALNUT, Vegetable.FROST_WALNUT),
    APPLE('¥', SColor.BRUSHWOOD_DYED, "A small tree bearing bright-red apples", Wood.OAK, Vegetable.LOBSTER_APPLE),
    CHERRY('¥', SColor.RED_BEAN, "A small tree bearing scintillating red cherries", Wood.CHERRY, Vegetable.THUNDER_CHERRY);
    private final Color color;
    private final char symbol;
    private final String description;
    private final String prettyName;
    private final String terrains;
    private final Wood lumber;
    private final Vegetable fruit;
    
    Tree(char symbol, Color color, String description, Wood lumber, Vegetable fruit) {
        this(symbol, color, description, "¸", lumber, fruit);
    }
    Tree(char symbol, Color color, String description, String terrains, Wood lumber, Vegetable fruit) {
        this.symbol = symbol;
        this.color = color;
        this.description = description;
        prettyName = Utilities.lower(name(), "_").replace('ˉ', '-') + " tree";
        this.terrains = terrains;
        this.lumber = lumber;
        this.fruit = fruit;
        hash = ConstantKey.precomputeHash("material.Tree", ordinal());
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

    public Wood lumber() {
        return lumber;
    }

    public Vegetable fruit() {
        return fruit;
    }

    public static final Tree[] ALL = values();

}
