package squidpony.epigon.data.blueprint;

import com.badlogic.gdx.graphics.Color;
import squidpony.squidgrid.gui.gdx.SColor;

/**
 * Created by Tommy Ettinger on 11/26/2017.
 */
public enum Wood implements Material {
    BIRCH(SColor.BIRCH_BROWN, SColor.ALOEWOOD),
    ASPEN(SColor.CW_ALMOST_WHITE, SColor.WILLOW_GREY),
    CHERRY(SColor.RED_BEAN, SColor.RED_BIRCH, 120, 170),
    MAPLE(SColor.CLOVE_BROWN, SColor.BAIKO_BROWN, 110),
    WALNUT(SColor.WALNUT, SColor.WILLOW_GREY),
    KOA(SColor.FLATTERY_BROWN, SColor.DB_SEAL_BROWN, 150, 200);

    public Color front, back;
    public int value; //base material is 100
    public int hardness; //average hardness

    Wood(Color front, Color back) {
        this(front, back, 100, 100);
    }

    Wood(Color front, Color back, int value) {
        this(front, back, value, 100);
    }

    Wood(Color front, Color back, int value, int hardness) {
        this.front = front;
        this.back = back;
        this.value = value;
        this.hardness = hardness;
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
        return '=';
    }

}
