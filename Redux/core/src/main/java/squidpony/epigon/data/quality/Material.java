package squidpony.epigon.data.quality;

import com.badlogic.gdx.graphics.Color;
import squidpony.epigon.ImmutableKey;

/**
 * Created by Tommy Ettinger on 11/26/2017.
 */
public interface Material extends ImmutableKey {
    Color getMaterialColor();
    int getValue();
    int getHardness();
    char getGlyph();
}
