package squidpony.epigon.data.slot;

import squidpony.epigon.ConstantKey;
import squidpony.squidmath.Coord;

/**
 * Created by Tommy Ettinger on 6/7/2018.
 */
public interface BodySlot extends ConstantKey {
    Coord getLocation();
}
