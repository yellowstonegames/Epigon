package squidpony.epigon.data.mixin;

import com.badlogic.gdx.graphics.Color;
import squidpony.epigon.data.blueprint.Inclusion;
import squidpony.epigon.data.blueprint.Stone;

/**
 * A specific instance of a terrain unit.
 *
 * Should only be created if a generic instance was interacted with in a way that caused it to
 * become different than others of it's type, such as damaged.
 *
 * @author Eben Howard - http://squidpony.com
 */
public class Terrain {

    public Color background;

    public Stone stone;
    public Inclusion inclusion;
    public boolean extrusive;
    public boolean intrusive;
    public boolean metamorphic;
    public boolean sedimentary;
}
