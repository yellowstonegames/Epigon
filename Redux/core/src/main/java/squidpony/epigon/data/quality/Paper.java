package squidpony.epigon.data.quality;

import com.badlogic.gdx.graphics.Color;
import squidpony.epigon.ConstantKey;
import squidpony.squidgrid.gui.gdx.SColor;

/**
 * Created by Tommy Ettinger on 11/26/2017.
 */
public enum Paper implements Material {
    PAPYRUS(SColor.DB_DAFFODIL),
    VELLUM(SColor.LEMON_CHIFFON),
    PARCHMENT(SColor.COSMIC_LATTE),
    SKIN(SColor.DB_NUDE),
    EGGSHELL_PAPER(SColor.EGGSHELL_PAPER),
    ASPEN_BARK(SColor.CW_ALMOST_WHITE);

    public Color front, back;
    public int value; //base material is 100
    public int hardness; //average hardness

    Paper(Color front) {
        this(front, front, 100, 10);
    }

    Paper(Color front, Color back) {
        this(front, back, 100, 10);
    }

    Paper(Color front, Color back, int value) {
        this(front, back, value, 10);
    }

    Paper(Color front, Color back, int value, int hardness) {
        this.front = front;
        this.back = back;
        this.value = value;
        this.hardness = hardness;
        hash = ConstantKey.precomputeHash("material.Paper", ordinal());
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
    public Color getMaterialColor() {
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
    public char getGlyph()
    {
        return '⌷';
    }

}
