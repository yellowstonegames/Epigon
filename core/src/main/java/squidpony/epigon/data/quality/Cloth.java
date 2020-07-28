package squidpony.epigon.data.quality;

import com.badlogic.gdx.graphics.Color;
import squidpony.epigon.ConstantKey;
import squidpony.squidgrid.gui.gdx.SColor;

/**
 * Created by Tommy Ettinger on 11/26/2017.
 */
public enum Cloth implements Material {
    VELVET(SColor.AURORA_BROWN_VELVET, SColor.AURORA_BROWN_VELVET, 100, 5, 4),
    WICKER(SColor.CW_FADED_YELLOW, SColor.CW_FADED_YELLOW, 10, 10, 1),
    LINEN(SColor.LINEN, SColor.LINEN, 12, 5, 2),
    DENIM(SColor.DB_DENIM, SColor.DB_DENIM, 50, 30, 10),
    LEATHER(SColor.CHESTNUT_LEATHER_BROWN, SColor.DB_DARK_LEATHER, 70, 35, 15);

    public Color front, back;
    public int value; //base material is 100
    public int hardness; //average hardness
    public int flammability; // lower values mean this is very flammable; -1 means it can't burn
    Cloth(Color front) {
        this(front, front, 50, 30);
    }

    Cloth(Color front, Color back) {
        this(front, back, 50, 30);
    }

    Cloth(Color front, Color back, int value) {
        this(front, back, value, 30);
    }

    Cloth(Color front, Color back, int value, int hardness)
    {
        this(front, back, value, hardness, -1);
    }
    Cloth(Color front, Color back, int value, int hardness, int flammability) {
    
        this.front = front;
        this.back = back;
        this.value = value;
        this.hardness = hardness;
        this.flammability = flammability;
        hash = ConstantKey.precomputeHash("material.Cloth", ordinal());
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


    @Override
    public String toString() {
        return name().toLowerCase().replace('_', ' ');
    }

    @Override
    public Color getColor() {
        return front;
    }

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

    @Override
    public char getGlyph()
    {
        return 'ᴥ';
    }

    public static final Cloth[] ALL = values();
}
