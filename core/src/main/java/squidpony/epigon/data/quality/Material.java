package squidpony.epigon.data.quality;

import com.badlogic.gdx.graphics.Color;
import squidpony.epigon.ConstantKey;

/**
 * Created by Tommy Ettinger on 11/26/2017.
 */
public interface Material extends ConstantKey {
    Color getMaterialColor();
    int getValue();
    int getHardness();
    char getGlyph();
}
