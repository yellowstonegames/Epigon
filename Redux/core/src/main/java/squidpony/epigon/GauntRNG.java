package squidpony.epigon;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import squidpony.squidmath.NumberTools;

import java.util.Collections;
import java.util.List;
import static squidpony.squidmath.LightRNG.*;
import static squidpony.squidgrid.gui.gdx.SColor.floatGet;
/**
 * Created by Tommy Ettinger on 1/4/2018.
 */
public final class GauntRNG {

//    public static void main(String[] args)
//    {
//        for (long i = 1000000000000L; i < 1000000000080L; i++) {
//            System.out.println(determine(i));
//        }
//    }

//    /**
//     * Experimental; if this doesn't work somehow, switch to {@link squidpony.squidmath.LightRNG#determine(long)}.
//     * Acts like SplitMix64 but instead of adding a large value to state every time (which needs a multiplication here),
//     * it uses a random left-xorshift (similar to the random right-xorshift used in PCG-Random) by at least 8 bits of
//     * distance, followed by multiply, store, right-xorshift, multiply, store, and return right-xorshift. This needs one
//     * less multiplication but changes a fixed xorshift to a random one, which may be a good trade here.
//     * @param state any long
//     * @return a pseudo-random long determined from state
//     */
//    public static long determine(long state) {
//        return ((state = ((state = (state ^ (state << (state | 8L))) * 0x6C8E9CF570932BD5L) ^ state >>> 24) * 0xAEF17502108EF2D9L) ^ state >>> 25);
//    }
//    public static int determineBounded(long state, final int bound) {
//        return (int)((bound * (((state = ((state = (state ^ (state << (state | 8L))) * 0x6C8E9CF570932BD5L) ^ state >>> 24) * 0xAEF17502108EF2D9L) ^ state >>> 25) & 0x7FFFFFFFL)) >> 31);
//    }
//    public static float determineFloat(long state) {
//        return (((state = (state ^ (state << (state | 8L))) * 0x6C8E9CF570932BD5L) ^ state >>> 24) * 0xAEF17502108EF2D9L >>> 40) * 0x1p-24f;
//    }
//    public static double determineDouble(long state) {
//        return (((state = ((state = (state ^ (state << (state | 8L))) * 0x6C8E9CF570932BD5L) ^ state >>> 24) * 0xAEF17502108EF2D9L) ^ state >>> 25) & 0x1FFFFFFFFFFFFFL) * 0x1p-53;
//    }
    
//    /**
//     * Experimental; if this doesn't work somehow, switch to {@link squidpony.squidmath.LightRNG#determine(long)}.
//     * Combines a XOR of state with two bitwise rotations of state by fixed amounts, multiplies, uses a random xorshift
//     * technique from PCG-Random, multiplies again, and xorshifts again by a fixed amount. Some multiplications or other
//     * steps may be possible to elide later. Even if two states are sequential, this should produce adequately random
//     * results for almost any states. Passes at least 16TB of PractRand, will probably pass 32TB (not sure yet). Should
//     * be reversible, but writing the reverse is only feasible if you are familiar with the algorithm.
//     * @param state any long
//     * @return a pseudo-random long determined from state
//     */
//    public static long determine(long state) { return ((state = ((state = (state ^ (state << 23 | state >>> 41) ^ (state << 59 | state >>> 5)) * 0x6C8E9CF570932BD5L) ^ state >>> (5 + (state >>> 59))) * 0xAEF17502108EF2D9L) ^ state >>> 25); }
//    //public static long determine(long state) { return ((state = ((state *= 0x6C8E9CF570932BD5L) ^ state >>> 25) * 0x352E9CF570932BDDL) ^ (state << 22 | state >>> 42) ^ (state << 47 | state >>> 17)); }
//
//    public static int determineBounded(long state, final int bound)
//    {
//        return (int)((bound * (((state = ((state = (state ^ (state << 23 | state >>> 41) ^ (state << 59 | state >>> 5)) * 0x6C8E9CF570932BD5L) ^ state >>> (5 + (state >>> 59))) * 0xAEF17502108EF2D9L) ^ state >>> 25) & 0x7FFFFFFFL)) >> 31);
//    }
//
//    /**
//     * Returns a random float that is deterministic based on state; if state is the same on two calls to this, this will
//     * return the same float. This is expected to be called with a changing variable, e.g. {@code determine(++state)},
//     * where the increment for state should be odd but otherwise doesn't really matter. Even an increment as small as 1
//     * produces high-quality results, and using a small increment won't be much different from using a very large one,
//     * as long as it is odd. The period is 2 to the 64 if you increment or decrement by 1, but there are only 2 to the
//     * 30 possible floats between 0 and 1, and only 2 to the 24 possible results of this method. This method has been
//     * optimized slightly to avoid some extra work {@link #determineDouble(long)} needs to do, because getting a float
//     * needs less pseudo-random bits.
//     * @param state a variable that should be different every time you want a different random result;
//     *              using {@code determine(++state)} is recommended to go forwards or {@code determine(--state)} to
//     *              generate numbers in reverse order
//     * @return a pseudo-random float between 0f (inclusive) and 1f (exclusive), determined by {@code state}
//     */
//    public static float determineFloat(long state) { return ((((state = (state ^ (state << 23 | state >>> 41) ^ (state << 59 | state >>> 5)) * 0x6C8E9CF570932BD5L) ^ state >>> (5 + (state >>> 59))) * 0xAEF17502108EF2D9L) >>> 40) * 0x1p-24f; }
//
//    /**
//     * Returns a random double that is deterministic based on state; if state is the same on two calls to this, this
//     * will return the same float. This is expected to be called with a changing variable, e.g.
//     * {@code determine(++state)}, where the increment for state should be odd but otherwise doesn't really matter. This
//     * multiplies state by {@code 0x9E3779B97F4A7C15L} within this method, so using a small increment won't be much
//     * different from using a very large one, as long as it is odd. The period is 2 to the 64 if you increment or
//     * decrement by 1, but there are only 2 to the 62 possible doubles between 0 and 1, and only 2 to the 53 possible
//     * results of this method.
//     * @param state a variable that should be different every time you want a different random result;
//     *              using {@code determine(++state)} is recommended to go forwards or {@code determine(--state)} to
//     *              generate numbers in reverse order
//     * @return a pseudo-random double between 0.0 (inclusive) and 1.0 (exclusive), determined by {@code state}
//     */
//    public static double determineDouble(long state) { return (((state = ((state = (state ^ (state << 23 | state >>> 41) ^ (state << 59 | state >>> 5)) * 0x6C8E9CF570932BD5L) ^ state >>> (5 + (state >>> 59))) * 0xAEF17502108EF2D9L) ^ state >>> 25) & 0x1FFFFFFFFFFFFFL) * 0x1p-53; }

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

    public static long nextLong(long state)
    {
        return determine(state);
    }
    public static int nextInt(long state)
    {
        return (int)determine(state);
    }
    
    public static int nextInt(long state, int bound) {
        return determineBounded(state, bound);
    }
    
    public static float nextFloat(long state)
    {
        return determineFloat(state);
    }
    
    public static double nextDouble(long state) { return determineDouble(state); }
    
    public static double nextDouble(long state, double bound) {
        return determineDouble(state) * bound;
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
     * Generates a random permutation of the range from 0 (inclusive) to length (exclusive).
     * Useful for passing to OrderedMap or OrderedSet's reorder() methods.
     * Unlike other methods in this class, this can change state by different amounts depending on the length of
     * elements. This must be called with {@code state += length - 1}.
     * @param length the size of the ordering to produce
     * @return a random ordering containing all ints from 0 to length (exclusive)
     */
    public static int[] randomOrdering(long state, int length) {
        if (length <= 0)
            return new int[0];
        return randomOrdering(state, length, new int[length]);
    }

    /**
     * Generates a random permutation of the range from 0 (inclusive) to length (exclusive) and stores it in
     * the dest parameter, avoiding allocations.
     * Useful for passing to OrderedMap or OrderedSet's reorder() methods.
     * Unlike other methods in this class, this can change state by different amounts depending on the length of
     * elements. This must be called with {@code state += Math.min(length, dest.length) - 1}.
     * @param length the size of the ordering to produce
     * @param dest   the destination array; will be modified, must not be null
     * @return dest, filled with a random ordering containing all ints from 0 to length (exclusive)
     */
    public static int[] randomOrdering(long state, int length, int[] dest) {
        final int n = Math.min(length, dest.length);
        for (int i = 0; i < n; i++) {
            dest[i] = i;
        }
        for (int i = n - 1; i > 0; i--) {
            final int r = determineBounded(state--, i+1),
                    t = dest[r];
            dest[r] = dest[i];
            dest[i] = t;
        }
        return dest;
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
