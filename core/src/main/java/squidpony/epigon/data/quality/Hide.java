package squidpony.epigon.data.quality;

import com.badlogic.gdx.graphics.Color;
import squidpony.epigon.ConstantKey;
import squidpony.squidgrid.gui.gdx.SColor;

/**
 * Created by Tommy Ettinger on 11/26/2017.
 */
public enum Hide implements Material {
    BEAR_HIDE(SColor.CW_DARK_BROWN, SColor.DB_SEAL_BROWN, 130, 40),
    TIGER_HIDE(SColor.BRIGHT_GOLD_BROWN, SColor.CW_ALMOST_BLACK, 220, 32),
    WOLF_HIDE(SColor.CW_DARK_GRAY, SColor.CW_LIGHT_GRAY, 120, 35),
    RABBIT_HIDE(SColor.WHITE_MOUSE, SColor.SILVER_GREY, 20, 9),
    RHINOCEROS_HIDE(SColor.DB_ELEPHANT, SColor.DB_ELEPHANT, 200, 80),
    CROCODILE_HIDE(SColor.SISKIN_SPROUT_YELLOW, SColor.WILLOW_DYE, 140, 65);

    public Color front, back;
    public int value; //base material is 100
    public int hardness; //average hardness

    Hide(Color front) {
        this(front, front, 100, 30);
    }

    Hide(Color front, Color back) {
        this(front, back, 100, 30);
    }

    Hide(Color front, Color back, int value) {
        this(front, back, value, 30);
    }

    Hide(Color front, Color back, int value, int hardness) {
        this.front = front;
        this.back = back;
        this.value = value;
        this.hardness = hardness;
        hash = ConstantKey.precomputeHash("material.Hide", ordinal());
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
        return 'ᴥ';
    }

}
