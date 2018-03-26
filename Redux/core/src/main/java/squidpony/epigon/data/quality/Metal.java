package squidpony.epigon.data.quality;

import com.badlogic.gdx.graphics.Color;
import squidpony.epigon.ImmutableKey;
import squidpony.squidgrid.gui.gdx.SColor;

/**
 * Created by Tommy Ettinger on 11/26/2017.
 */
public enum Metal implements Material {
    BRASS(SColor.BRASS, SColor.CW_GOLD, 170, 1600),
    LEAD(SColor.DB_LEAD, SColor.DB_LEAD, 110, 700),
    COPPER(SColor.COPPER, SColor.COPPER_ROSE, 230, 1100),
    SILVER(SColor.SILVER, SColor.SILVER, 500, 1000),
    GOLD(SColor.CW_BRIGHT_GOLD, SColor.OLD_GOLD, 600, 700),
    TIN(SColor.CW_GRAY_WHITE, SColor.CW_GRAY_WHITE, 140, 1100),
    IRON(SColor.DB_IRON, SColor.DB_IRON, 250, 2500),
    STEEL(SColor.CW_GRAY_WHITE, SColor.STEEL_BLUE, 310, 2900),
    PLATINUM(SColor.PLATINUM, SColor.DB_PLATINUM, 750, 1900);

    public Color front, back;
    public int value; //base material is 100, metals are 200
    public int hardness; //average hardness

    Metal(Color front) {
        this(front, front, 200, 2000);
    }

    Metal(Color front, Color back) {
        this(front, back, 200, 2000);
    }

    Metal(Color front, Color back, int value) {
        this(front, back, value, 2000);
    }

    Metal(Color front, Color back, int value, int hardness) {
        this.front = front;
        this.back = back;
        this.value = value;
        this.hardness = hardness;
        hash = ImmutableKey.precomputeHash("material.Metal", ordinal());
    }

    public long hash;
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
        return '⊕';
    }

}
