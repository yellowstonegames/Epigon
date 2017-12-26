package squidpony.epigon.mapping;

import squidpony.squidmath.NumberTools;
import squidpony.squidmath.RandomnessSource;

/**
 * Created by Tommy Ettinger on 12/25/2017.
 */
public class PositionRNG implements RandomnessSource
{
    public long x;
    public long y;
    public long state = 1L;

    public static long hash3(final long x, final long y, long state)
    {
        state *= 0x352E9CF570932BDDL;
        return (((state = ((state += 0x6C8E9CD570932BD5L ^ x) ^ (state >>> 25)) * ((y ^ state) | 0xA529L)) ^ (state >>> 22)) ^
                ((state = ((state += 0x6C8E9CD570932BD5L ^ y) ^ (state >>> 25)) * ((x ^ state) | 0xA529L)) ^ (state >>> 22)));
    }

    public PositionRNG()
    {
        this(1L);
    }

    public PositionRNG(long seed)
    {
        this(seed, 0L, 0L);
    }
    public PositionRNG(long seed, long x, long y)
    {
        this.x = x;
        this.y = y;
        state = seed;
    }
    public PositionRNG(long x, long y)
    {
        this.x = x;
        this.y = y;
        state = (state = ((x += 0x6C8E9CF570932BD5L) ^ (x >>> 25)) * (y + 0x9E3779B97F4A7BB5L | 1L)) ^ (state >>> 28);
    }
    public PositionRNG move(long x, long y)
    {
        this.x = x;
        this.y = y;
        state = (state = ((x += 0x6C8E9CF570932BD5L) ^ (x >>> 25)) * (y + 0x9E3779B97F4A7BB5L | 1L)) ^ (state >>> 28);
        return this;
    }
    public PositionRNG move(long seed, long x, long y)
    {
        this.x = x;
        this.y = y;
        state = seed;
        return this;
    }

    @Override
    public int next(int bits) {
        long s = (state += 0x352E9CF570932BDDL);
        return (int)(
                (((s = ((s += 0x6C8E9CD570932BD5L ^ x) ^ (s >>> 25)) * ((y ^ s) | 0xA529L)) ^ (s >>> 22)) ^
                        ((s = ((s += 0x6C8E9CD570932BD5L ^ y) ^ (s >>> 25)) * ((x ^ s) | 0xA529L)) ^ (s >>> 22)))
                        >>> (64 - bits));
    }

    @Override
    public long nextLong() {
        long s = (state += 0x352E9CF570932BDDL);
        return (((s = ((s += 0x6C8E9CD570932BD5L ^ x) ^ (s >>> 25)) * ((y ^ s) | 0xA529L)) ^ (s >>> 22)) ^
                ((s = ((s += 0x6C8E9CD570932BD5L ^ y) ^ (s >>> 25)) * ((x ^ s) | 0xA529L)) ^ (s >>> 22)));
    }

    @Override
    public RandomnessSource copy() {
        return new PositionRNG(state, x, y);
    }

    public float nextFloat() {
        return NumberTools.formFloat((int)nextLong());
    }
    public float nextFloat(float bound) {
        return bound * nextFloat();
    }
}
