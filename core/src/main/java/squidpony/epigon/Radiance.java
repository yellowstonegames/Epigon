package squidpony.epigon;

import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidmath.NumberTools;

/**
 * Grouping of qualities related to glow and light emission. When a Radiance variable in some object is null, it
 * means that object doesn't emit light; if a Radiance variable is non-null, it still might not emit light.  
 * This object has 4 fields:
 * <ul>
 * <li>range, how far the light extends; 0f is "just this cell"</li>
 * <li>color, the color of the light as a float; typically opaque and lighter than the glowing object's color</li>
 * <li>flicker, the rate of random continuous change to radiance range</li>
 * <li>strobe, the rate of non-random continuous change to radiance range</li>
 * </ul>
 * These all have defaults; if no parameters are specified the light will be white, affect only the current cell, and
 * won't flicker or strobe.
 * <br>
 * Created by Tommy Ettinger on 6/16/2018.
 */
public class Radiance {
    /**
     * How far the radiated light extends; 0f is "just this cell", anything higher can go into neighboring cells.
     */
    public float range = 0f;
    /**
     * The color of light as a float; typically opaque and lighter than the glowing object's symbol.
     */
    public float color = SColor.FLOAT_WHITE;
    /**
     * The rate of random continuous change to radiance range, like the light from a campfire.
     */
    public float flicker = 0f;
    /**
     * The rate of non-random continuous change to radiance range, like a mechanical strobe effect.
     */
    public float strobe = 0f;
    
    public float delay = 0f;
    /**
     * A temporary increase to the minimum radiance range, meant to brighten a glow during an effect.
     * This should be a float between 0f and 1f, with 0f meaning no change and 1f meaning always max radius.
     */
    public float flare = 0f;
    public Radiance()
    {
    }
    public Radiance(float range)
    {
        this.range = range;
    }
    public Radiance(float range, float color)
    {
        this.range = range;
        this.color = color;
    }
    public Radiance(float range, float color, float flicker)
    {
        this.range = range;
        this.color = color;
        this.flicker = flicker;
    }
    public Radiance(float range, float color, float flicker, float strobe)
    {
        this.range = range;
        this.color = color;
        this.flicker = flicker;
        this.strobe = strobe;
    }
    public Radiance(float range, float color, float flicker, float strobe, float flare)
    {
        this.range = range;
        this.color = color;
        this.flicker = flicker;
        this.strobe = strobe;
        this.flare = flare;
    }

    public Radiance(Radiance other)
    {
        this(other.range, other.color, other.flicker, other.strobe, other.flare);
    }

    /**
     * Provides the calculated current range adjusted for flicker and strobe at the current time in milliseconds, with
     * flicker seeded with the identity hash code of this Radiance. Higher values of flicker and strobe will increase
     * the frequency at which the range changes but will not allow it to exceed its starting range, only to diminish
     * temporarily. If both flicker and strobe are non-0, the range will usually be smaller than if only one was non-0,
     * and if both are 0, this simply returns range.
     * @return the current range, adjusting for flicker and strobe using the current time
     */
    public float currentRange()
    {
        final float time = (System.currentTimeMillis() & 0x3ffffL) * 0x1.9p-9f;
        float current = range;
        if(flicker != 0f) 
            current *= NumberTools.swayRandomized(System.identityHashCode(this), time * flicker + delay) * 0.25f + 0.75f;
        if(strobe != 0f)
            current *= NumberTools.swayTight(time * strobe + delay) * 0.25f + 0.75f;
        return Math.max(current, range * flare);
    }
    
    public static Radiance[] makeChain(int length, float range, float color, float strobe)
    {
        if(length <= 1)
            return new Radiance[]{new Radiance(range, color, 0f, strobe, 0f)};
        Radiance[] chain = new Radiance[length];
        float d = -2f / (length);
        for (int i = 0; i < length; i++) {
            chain[i] = new Radiance(range, color, 0f, strobe, 0f);
            chain[i].delay = d * i;
        }
        return chain;
    }
    public static final Radiance[] softWhiteChain = makeChain(8, 1.2f, SColor.FLOAT_WHITE, 1f); 
}
