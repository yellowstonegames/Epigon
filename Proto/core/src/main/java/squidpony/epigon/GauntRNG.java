package squidpony.epigon;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import squidpony.squidmath.NumberTools;

import java.util.Collections;
import java.util.List;

import static squidpony.squidgrid.gui.gdx.SColor.floatGet;
import static squidpony.squidmath.ThrustAltRNG.*;

/**
 * Created by Tommy Ettinger on 1/4/2018.
 */
public final class GauntRNG {


    /**
     * Gets a random number with at most the given amount of bits; call with {@code ++state}.
     * @param state must be called with {@code ++state}
     * @param bits the number of bits to generate
     * @return a number using the given amount of random bits
     */
    public static int next(long state, int bits)
    {
        return (int)determine(state) >>> (32 - bits);
    }
    /**
     * Returns a value from an even distribution from min (inclusive) to max
     * (exclusive).
     *
     * @param state must be called with {@code ++state}
     * @param min the minimum bound on the return value (inclusive)
     * @param max the maximum bound on the return value (exclusive)
     * @return the found value
     */
    public static double between(long state, double min, double max) {
        return min + (max - min) * determineDouble(state);
    }

    /**
     * Returns a value between min (inclusive) and max (exclusive).
     * <p>
     * The inclusive and exclusive behavior is to match the behavior of the
     * similar method that deals with floating point values.
     *
     * @param state must be called with {@code ++state}
     * @param min the minimum bound on the return value (inclusive)
     * @param max the maximum bound on the return value (exclusive)
     * @return the found value
     */
    public static int between(long state, int min, int max) {
        return determineBounded(state, max - min) + min;
    }

    /**
     * Returns a random element from the provided array and maintains object
     * type.
     *
     * @param <T>   the type of the returned object
     * @param state must be called with {@code ++state}
     * @param array the array to get an element from
     * @return the randomly selected element
     */
    public static <T> T getRandomElement(long state, T[] array) {
        if (array.length < 1) {
            return null;
        }
        return array[determineBounded(state, array.length)];
    }

    /**
     * Returns a random element from the provided list. If the list is empty
     * then null is returned.
     *
     * @param <T>  the type of the returned object
     * @param state must be called with {@code ++state}
     * @param list the list to get an element from
     * @return the randomly selected element
     */
    public static  <T> T getRandomElement(long state, List<T> list) {
        if (list.isEmpty()) {
            return null;
        }
        return list.get(determineBounded(state, list.size()));
    }
    /**
     * Shuffles a List of T items in-place using the Fisher-Yates algorithm.
     * This only shuffles List data structures.
     * Unlike other methods in this class, this can change state by different amounts depending on the length of
     * elements. This must be called with {@code state += elements.size() - 1}.
     * <br>
     * <a href="https://en.wikipedia.org/wiki/Fisher%E2%80%93Yates_shuffle">Wikipedia has more on this algorithm</a>.
     *
     * @param state must be called with {@code state += elements.size() - 1}
     * @param elements a Collection of T; <b>will</b> be modified
     * @param <T>      can be any non-primitive type.
     * @return elements after shuffling it in-place
     */
    public static <T> List<T> shuffleInPlace(long state, List<T> elements) {
        final int n = elements.size();
        for (int i = n; i > 1; i--) {
            Collections.swap(elements, determineBounded(state--, i), i - 1);
        }
        return elements;
    }

    /**
     * Gets a variation on the Color basis as a packed float that can have its hue, saturation, value, and opacity
     * changed to specified degrees using a random number generator. Takes a state that will be used to generate random
     * values, where the state must be called with {@code state += 3}, as well as floats representing the amounts of
     * change that can be applied to hue, saturation, value, and opacity. Returns a float that can be used as a packed
     * or encoded color with methods like {@link com.badlogic.gdx.graphics.g2d.Batch#setColor(float)}, or in various
     * SquidLib classes like SparseLayers or SquidLayers. The float is likely to be different than the result of
     * {@link Color#toFloatBits()} unless hue, saturation, value, and opacity are all 0. This won't modify the given
     * Color, nor will it allocate any objects.
     * <br>
     * The parameters this takes (other than random) all specify spans that the value can change by, spread halfway
     * toward higher values and halfway towards lower values. This is truncated if it would make a value lower than 0 or
     * higher than 1, with an exception for hue, which can rotate around somewhat if lower or higher hues would be used.
     * As an example, if you give this 0.4f for saturation, and the current color has saturation 0.7f, then the possible
     * saturations that could be used would have the specified 0.4f range centered on 0.7f, or 0.5f to 0.9f. If you gave
     * this 1f for saturation and the current color still has saturation 0.7f, then the range would be truncated at the
     * upper end, for a possible range of 0.2f to 1.0f. If truncation of the range occurs, then values are more likely
     * to be at the truncated max or min amount.
     *
     * @param basis      a Color or SColor to use as the starting point; will not be modified itself
     * @param state      must be called with {@code state += 3}
     * @param hue        0f to 2f, the span of hue change that can be applied to the new float color
     * @param saturation 0f to 2f, the span of saturation change that can be applied to the new float color
     * @param value      0f to 2f, the span of value/brightness change that can be applied to the new float color
     * @return a float encoding a variation of this SColor with changes up to the given properties
     */
    public static float toRandomizedFloat(Color basis, long state, float hue, float saturation, float value) {
        final float h, s, r = basis.r, g = basis.g, b = basis.b;
        final float min = Math.min(Math.min(r, g), b);   //Min. value of RGB
        final float max = Math.max(Math.max(r, g), b);   //Max value of RGB, equivalent to value
        final float delta = max - min;                   //Delta RGB value
        if ( delta < 0.0039f )                           //This is a gray, no chroma...
        {
            s = 0f;
            h = 0f;
            hue = 1f;
        }
        else                                             //Chromatic data...
        {
            s = delta / max;
            final float rDelta = (((max - r) / 6f) + (delta / 2f)) / delta;
            final float gDelta = (((max - g) / 6f) + (delta / 2f)) / delta;
            final float bDelta = (((max - b) / 6f) + (delta / 2f)) / delta;

            if      (r == max) h = (bDelta - gDelta + 1f) % 1f;
            else if (g == max) h = ((1f / 3f) + rDelta - bDelta + 1f) % 1f;
            else               h = ((2f / 3f) + gDelta - rDelta + 1f) % 1f;
        }
        saturation = MathUtils.clamp(s + (determineFloat(state--) - 0.5f) * saturation, 0f, 1f);
        value = MathUtils.clamp(max + (determineFloat(state--) - 0.5f) * value, 0f, 1f);
        hue = ((h + (determineFloat(state) - 0.5f) * hue + 6f) % 1f) * 6f;
        if (saturation <= 0.0039f) {
            return floatGet(value, value, value, basis.a);
        } else if (value <= 0.0039f) {
            return NumberTools.intBitsToFloat((int) (basis.a * 255f) << 24 & 0xFE000000);
        } else {
            final int i = (int) hue;
            final float x = value * (1 - saturation);
            final float y = value * (1 - saturation * (hue - i));
            final float z = value * (1 - saturation * (1 - (hue - i)));

            switch (i) {
                case 0:
                    return floatGet(value, z, x, basis.a);
                case 1:
                    return floatGet(y, value, x, basis.a);
                case 2:
                    return floatGet(x, value, z, basis.a);
                case 3:
                    return floatGet(x, y, value, basis.a);
                case 4:
                    return floatGet(z, x, value, basis.a);
                default:
                    return floatGet(value, x, y, basis.a);
            }
        }
    }
    /**
     * Gets a variation on the Color basis as a packed float that can have its hue, saturation, value, and opacity
     * changed to specified degrees using a random number generator. Takes a state that will be used to generate random
     * values, where the state must be called with {@code state += 4}, as well as floats representing the amounts of
     * change that can be applied to hue, saturation, value, and opacity. Returns a float that can be used as a packed
     * or encoded color with methods like {@link com.badlogic.gdx.graphics.g2d.Batch#setColor(float)}, or in various
     * SquidLib classes like SparseLayers or SquidLayers. The float is likely to be different than the result of
     * {@link Color#toFloatBits()} unless hue, saturation, value, and opacity are all 0. This won't modify the given
     * Color, nor will it allocate any objects.
     * <br>
     * The parameters this takes (other than random) all specify spans that the value can change by, spread halfway
     * toward higher values and halfway towards lower values. This is truncated if it would make a value lower than 0 or
     * higher than 1, with an exception for hue, which can rotate around somewhat if lower or higher hues would be used.
     * As an example, if you give this 0.4f for saturation, and the current color has saturation 0.7f, then the possible
     * saturations that could be used would have the specified 0.4f range centered on 0.7f, or 0.5f to 0.9f. If you gave
     * this 1f for saturation and the current color still has saturation 0.7f, then the range would be truncated at the
     * upper end, for a possible range of 0.2f to 1.0f. If truncation of the range occurs, then values are more likely
     * to be at the truncated max or min amount.
     *
     * @param basis      a Color or SColor to use as the starting point; will not be modified itself
     * @param state      must be called with {@code state += 4}
     * @param hue        0f to 2f, the span of hue change that can be applied to the new float color
     * @param saturation 0f to 2f, the span of saturation change that can be applied to the new float color
     * @param value      0f to 2f, the span of value/brightness change that can be applied to the new float color
     * @param opacity    0f to 2f, the span of opacity/alpha change that can be applied to the new float color
     * @return a float encoding a variation of this SColor with changes up to the given properties
     */
    public static float toRandomizedFloat(Color basis, long state, float hue, float saturation, float value, float opacity) {
        final float h, s, r = basis.r, g = basis.g, b = basis.b;
        final float min = Math.min(Math.min(r, g), b);   //Min. value of RGB
        final float max = Math.max(Math.max(r, g), b);   //Max value of RGB, equivalent to value
        final float delta = max - min;                   //Delta RGB value
        if ( delta < 0.0039f )                           //This is a gray, no chroma...
        {
            s = 0f;
            h = 0f;
            hue = 1f;
        }
        else                                             //Chromatic data...
        {
            s = delta / max;
            final float rDelta = (((max - r) / 6f) + (delta / 2f)) / delta;
            final float gDelta = (((max - g) / 6f) + (delta / 2f)) / delta;
            final float bDelta = (((max - b) / 6f) + (delta / 2f)) / delta;

            if      (r == max) h = (bDelta - gDelta + 1f) % 1f;
            else if (g == max) h = ((1f / 3f) + rDelta - bDelta + 1f) % 1f;
            else               h = ((2f / 3f) + gDelta - rDelta + 1f) % 1f;
        }
        saturation = MathUtils.clamp(s + (determineFloat(state--) - 0.5f) * saturation, 0f, 1f);
        value = MathUtils.clamp(max + (determineFloat(state--) - 0.5f) * value, 0f, 1f);
        opacity = MathUtils.clamp(basis.a + (determineFloat(state--) - 0.5f) * opacity, 0f, 1f);
        hue = ((h + (determineFloat(state) - 0.5f) * hue + 6f) % 1f) * 6f;
        if (saturation <= 0.0039f) {
            return floatGet(value, value, value, opacity);
        } else if (value <= 0.0039f) {
            return NumberTools.intBitsToFloat((int) (opacity * 255f) << 24 & 0xFE000000);
        } else {
            final int i = (int) hue;
            final float x = value * (1 - saturation);
            final float y = value * (1 - saturation * (hue - i));
            final float z = value * (1 - saturation * (1 - (hue - i)));

            switch (i) {
                case 0:
                    return floatGet(value, z, x, opacity);
                case 1:
                    return floatGet(y, value, x, opacity);
                case 2:
                    return floatGet(x, value, z, opacity);
                case 3:
                    return floatGet(x, y, value, opacity);
                case 4:
                    return floatGet(z, x, value, opacity);
                default:
                    return floatGet(value, x, y, opacity);
            }
        }
    }

}
