package squidpony.epigon;

import squidpony.squidmath.NumberTools;
import squidpony.squidmath.RandomnessSource;

/**
 * Created by Tommy Ettinger on 12/25/2017.
 */
public class PositionRNG implements RandomnessSource
{
    public long stream;
    public long state = 1L;

    private static long hash2(long x, long y)
    {
        x += 0x6C8E9CD570932BD5L; // increase weight, usually
        y -= 0x6C8E9CD570932BD5L;
        x ^= x << 13;
        y ^= y << 33;
        x ^= x >>> 7;
        y ^= y << 14;
        return ((x ^ x << 17) + (y ^ y >>> 23)) * 0x352E9CF570932BDDL; // multiply to reduce binary rank issue
    }

    /**
     * PCG-Random's RXS-M-XS output function on 64 bits of state with 64 bits of output
     * @param state a state long produced by an LCG
     * @return a higher-quality pseudo-random long
     */
    public static long pcg(final long state)
    {
        final long result = ((state >>> ((state >>> 59) + 5)) ^ state)
                    * 0xAEF17502108EF2D9L;
        return (result >>> 43) ^ result;
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
        stream = hash2(x, y) | 1L;
        state = seed;
    }
    public PositionRNG(long state, long stream)
    {
        this.stream = stream | 1L;
        this.state = state;
    }
    public void move(long x, long y)
    {
        stream = hash2(x, y) | 1L;
        state = hash2(stream * x + y, stream * y + x) ^ stream;
    }
    public void move(long seed, long x, long y)
    {
        stream = hash2(x, y) | 1L;
        state = seed;
    }

    @Override
    public int next(int bits) {
        long z = (state = state * 0x5851F42D4C957F2DL + stream);
        final long result = ((z >>> ((z >>> 59) + 5)) ^ z) * 0xAEF17502108EF2D9L;
        return (int)(((result >>> 43) ^ result) >>> (64 - bits));
    }

    @Override
    public long nextLong() {
        long z = (state = state * 0x5851F42D4C957F2DL + stream);
        final long result = ((z >>> ((z >>> 59) + 5)) ^ z) * 0xAEF17502108EF2D9L;
        return ((result >>> 43) ^ result);
    }

    @Override
    public PositionRNG copy() {
        return new PositionRNG(state, stream);
    }

    public float nextFloat() {
        return NumberTools.formFloat((int)nextLong());
    }
    public float nextFloat(float bound) {
        return bound * NumberTools.formFloat((int)nextLong());
    }
}
