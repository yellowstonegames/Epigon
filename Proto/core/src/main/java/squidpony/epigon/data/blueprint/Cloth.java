package squidpony.epigon.data.blueprint;

import com.badlogic.gdx.graphics.Color;
import squidpony.squidgrid.gui.gdx.SColor;

/**
 * Created by Tommy Ettinger on 11/26/2017.
 */
public enum Cloth implements Material {
    VELVET(SColor.VELVET),
    WICKER(SColor.CW_FADED_YELLOW, SColor.CW_FADED_YELLOW, 10, 10),
    LINEN(SColor.LINEN, SColor.LINEN, 12, 5),
    DENIM(SColor.DB_DENIM),
    LEATHER(SColor.CHESTNUT_LEATHER_BROWN, SColor.DB_DARK_LEATHER, 30, 15);

    public Color front, back;
    public int value; //base material is 100
    public int hardness; //average hardness

    Cloth(Color front) {
        this(front, front, 100, 30);
    }

    Cloth(Color front, Color back) {
        this(front, back, 100, 30);
    }

    Cloth(Color front, Color back, int value) {
        this(front, back, value, 30);
    }

    Cloth(Color front, Color back, int value, int hardness) {
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
        return 'ᴥ';
    }

}
