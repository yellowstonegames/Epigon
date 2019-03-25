package squidpony.epigon.data.quality;

import com.badlogic.gdx.graphics.Color;
import squidpony.epigon.ConstantKey;

/**
 * Created by Tommy Ettinger on 11/26/2017.
 */
public interface Material extends ConstantKey {
    Color getColor();
    int getValue();
    int getHardness();
    int getFlammability();
    char getGlyph();
}
