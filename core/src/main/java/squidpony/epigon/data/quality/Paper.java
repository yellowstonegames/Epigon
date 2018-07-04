package squidpony.epigon.data.quality;

import com.badlogic.gdx.graphics.Color;
import squidpony.epigon.ConstantKey;
import squidpony.squidgrid.gui.gdx.SColor;

/**
 * Created by Tommy Ettinger on 11/26/2017.
 */
public enum Paper implements Material {
    PAPYRUS(SColor.DB_DAFFODIL, 95, 4),
    VELLUM(SColor.LEMON_CHIFFON, 115, 1),
    PARCHMENT(SColor.COSMIC_LATTE, 105, 2),
    SKIN(SColor.DB_NUDE, 150, 5),
    EGGSHELL_PAPER(SColor.EGGSHELL_PAPER, 120, 3),
    ASPEN_BARK(SColor.CW_ALMOST_WHITE, 80, 18),
    PALM_LEAF(SColor.BUFF, 70, 25),
    INSCRIBED_TABLET(SColor.SLATE_GRAY, 130, 150),
    CLAY_TABLET(SColor.DB_PUTTY, 105, 60),
    GLOW_LETTER(new SColor(0xD1, 0x3D, 0xF1, 0xB0, "Translucent Bright Purple"), 1000, 1000);

    public Color front;
    public int value; //base material is 100
    public int hardness; //average hardness

    Paper(Color front) {
        this(front, 100, 10);
    }
    
    Paper(Color front, int value) {
        this(front, value, 10);
    }

    Paper(Color front, int value, int hardness) {
        this.front = front;
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
