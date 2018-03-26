package squidpony.epigon.mapping;

import squidpony.squidmath.NumberTools;
import squidpony.squidmath.RandomnessSource;

/**
 * Created by Tommy Ettinger on 12/25/2017.
 */
public class PositionRNG implements RandomnessSource
{
    public long stream;
    public long state = 1L;

    public static long hash3(final long x, final long y, long state)
    {
        state *= 0x352E9CF570932BDDL;
        return (((state = ((state += 0x6C8E9CD570932BD5L ^ x) ^ (state >>> 25)) * ((y ^ state) | 0xA529L)) ^ (state >>> 22)) ^
                ((state = ((state += 0x6C8E9CD570932BD5L ^ y) ^ (state >>> 25)) * ((x ^ state) | 0xA529L)) ^ (state >>> 22)));
    }
    public static long hash2(long x, final long y)
    {
        return (x = ((x *= 0x6C8E9CF570932BD5L) ^ (x >>> 25)) * (y * 0x9E3779B97F4A7BB5L | 1L)) ^ (x >>> 28);
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
        stream = hash2(x, y);
        state = seed;
    }
    public PositionRNG(long state, long stream)
    {
        this.stream = stream;
        this.state = state;
    }
    public PositionRNG move(long x, long y)
    {
        stream = hash2(x, y);
        state = (stream + x) * (stream * y + x) + y;
        return this;
    }
    public PositionRNG move(long seed, long x, long y)
    {
        stream = hash2(x, y);
        state = seed;
        return this;
    }

    @Override
    public int next(int bits) {
        final long s = (state += 0x6C8E9CF570932BD5L);
        final long z = (stream - (s ^ (s >>> 25))) * (s | 0xA529L);
        return (int)(z ^ (z >>> 22)) >>> (32 - bits);
    }

    @Override
    public long nextLong() {
        final long s = (state += 0x6C8E9CF570932BD5L);
        final long z = (stream - (s ^ (s >>> 25))) * (s | 0xA529L);
        return z ^ (z >>> 22);
    }

    @Override
    public RandomnessSource copy() {
        return new PositionRNG(state, stream);
    }

    public float nextFloat() {
        return NumberTools.formFloat((int)nextLong());
    }
    public float nextFloat(float bound) {
        return bound * nextFloat();
    }
}
