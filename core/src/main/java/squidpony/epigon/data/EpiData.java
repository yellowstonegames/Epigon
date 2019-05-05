package squidpony.epigon.data;

import squidpony.StringKit;
import squidpony.epigon.Epigon;
import squidpony.squidmath.*;

import java.io.Serializable;

/**
 * Data class for information shared by all data objects.
 * <br>
 * Every EpiData has a unique int in a field called {@link #idHash}. Unless more than 2 to the 32 EpiData are created,
 * which is basically impossible due to memory constraints unless some EpiData values are constantly created and
 * destroyed, this number will never repeat. This may be useful for some purposes; it is used as a more-controlled
 * identity hash in the {@link #hashCode()} implementation here. Subclasses that want to compare EpiData by value should
 * not call {@code super.equals()} or {@code super.hashCode()} but may compare and hash {@link #name} and/or
 * {@link #description}.
 * <br>
 * Acts as an IRNG that stores its own state for its own random number generation, allowing the game to avoid relying so
 * heavily on the order in which a static RNG generates numbers for various purposes. The random number generation
 * algorithm this uses is MiniMover64RNG from Sarong. It is very, very fast and passes 32TB of PractRand.
 * About 33 million seeds are possible for this variant of MiniMover64RNG, which have a chance of being on different
 * subcycles with periods from 1,048,575 to 18,446,744,073,709,551,615. By restricting the seeds to a range that is
 * known to have no cycles shorter than a million states, seeding gets to be much faster.
 */
public abstract class EpiData extends AbstractRNG implements Serializable, StatefulRandomness {
    protected static final long serialVersionUID = 0L;

    public String name;
    public String description;
    //public String notes; // NOTE - don't need to copy this into created objects <-- no way to eliminate the field

    private String id;
    private final int idHash;
    private long state;
//    public static int count = 0;
    private static int uniqueIntGen = Epigon.rootChaos.nextInt();
    
    public final void setState(final int s) {
        // 33,554,432 possible seeds should be enough for Epigon.
        // some may be in shorter cycles, but none may have periods of less than 1048576.
        state = (s & 0x1FFFFFF) + 1L;
        state = (state << 29 | state >>> 35) * 0xAC564B05L;
    }
    
    public EpiData() {
        idHash = (uniqueIntGen += 0x632BE5AB);
        setState(idHash);
        //count++;
    }
    public EpiData(final String name)
    {
        this();
        this.name = name;
    }

    /**
     * Get the current internal state of the StatefulRandomness as a long.
     *
     * @return the current internal state of this object.
     */
    @Override
    public final long getState() {
        return state;
    }

    /**
     * Set the current internal state of this StatefulRandomness with a long, which must not be 0.
     *
     * @param state a 64-bit long. You must not pass 0 here.
     */
    @Override
    public final void setState(long state) {
        this.state = state;
    }

    @Override
    public final int nextInt()
    {
//        final long s = (state += 0x6C8E9CF570932BD5L);
//        final long z = (s ^ s >>> 25) * (s | 0xA529L);
//        return (int)(z ^ z >>> 22);
        return (int)((state = (state << 29 | state >>> 35) * 0xAC564B05L) * 0x818102004182A025L);

//        final long a = stateA * 0x41C64E6BL;
//        return (int)((stateB = 0xC6BC279692B5CC8BL - (stateB << 35 | stateB >>> 29)) ^ (stateA = (a << 28 | a >>> 36)));
    }
    @Override
    public final int next(final int bits)
    {
//        final long a = stateA * 0x41C64E6BL;
//        return (int)((stateB = 0xC6BC279692B5CC8BL - (stateB << 35 | stateB >>> 29)) ^ (stateA = (a << 28 | a >>> 36))) >>> (32 - bits);
//        final long s = (state += 0x6C8E9CF570932BD5L);
//        final long z = (s ^ s >>> 25) * (s | 0xA529L);
//        return (int)(z ^ z >>> 22) >>> (32 - bits);
        return (int)((state = (state << 29 | state >>> 35) * 0xAC564B05L) * 0x818102004182A025L) >>> (32 - bits);
    }

//    /**
//     * Get up to 32 bits (inclusive) of random output; the int this produces
//     * will not require more than {@code bits} bits to represent.
//     *
//     * @param bits an int between 1 and 32, both inclusive
//     * @return a random number that fits in the specified number of bits
//     */
//    @Override
//    public final int next(int bits) {
//        long z = (chaos = chaos * 0x41C64E6DL + 1L);
//        z = (z ^ z >>> 27) * 0xAEF17502108EF2D9L;
//        return (int)(z ^ z >>> 25) >>> (32 - bits);
////        long z = (chaos = chaos * 0x41C64E6DL + 1L);
////        z = (z ^ z >>> 27) * 0xAEF17502108EF2D9L;
////        return (int)(z ^ z >>> 25) >>> (32 - bits);
//    }
//
//    /**
//     * Get a random integer between Integer.MIN_VALUE to Integer.MAX_VALUE (both inclusive).
//     *
//     * @return a 32-bit random int.
//     */
//    @Override
//    public final int nextInt() {
//        long z = (chaos = chaos * 0x41C64E6DL + 1L);
//        z = (z ^ z >>> 27) * 0xAEF17502108EF2D9L;
//        return (int)(z ^ z >>> 25);
//    }

    /**
     * Get a random long between Long.MIN_VALUE to Long.MAX_VALUE (both inclusive).
     *
     * @return a 64-bit random long.
     */
    @Override
    public final long nextLong() {
//        long z = (chaos = chaos * 0x41C64E6DL + 1L);
//        z = (z ^ z >>> 27) * 0xAEF17502108EF2D9L;
//        return (z ^ z >>> 25);
//        final long s = (state += 0x6C8E9CF570932BD5L);
//        final long z = (s ^ s >>> 25) * (s | 0xA529L);
//        return (z ^ z >>> 22);
        // return (state = (state << 21 | state >>> 43) * 0x9E3779B9L) * 0x41C64E6DL;
        return ((state = (state << 29 | state >>> 35) * 0xAC564B05L) * 0x818102004182A025L);
//        final long a = stateA * 0x41C64E6BL;
//        return (stateB = 0xC6BC279692B5CC8BL - (stateB << 35 | stateB >>> 29)) ^ (stateA = (a << 28 | a >>> 36));
    }

    /**
     * Get a random bit of state, interpreted as true or false with approximately equal likelihood.
     * @return a random boolean.
     */
    @Override
    public final boolean nextBoolean() {
//        final long s = (state += 0x6C8E9CF570932BD5L);
//        final long z = (s ^ s >>> 25) * (s | 0xA529L);
//        return z < 0L;
        return ((state = (state << 29 | state >>> 35) * 0xAC564B05L) * 0x818102004182A025L) < 0L;

//        final long a = stateA * 0x41C64E6BL;
//        return ((stateB = 0xC6BC279692B5CC8BL - (stateB << 35 | stateB >>> 29)) ^ (stateA = (a << 28 | a >>> 36))) < 0L;
        //        return  (chaos = chaos * 0x41C64E6DL + 1L) < 0;
    }

    /**
     * Gets a random double between 0.0 inclusive and 1.0 exclusive.
     * This returns a maximum of 0.9999999999999999 because that is the largest double value that is less than 1.0 .
     *
     * @return a double between 0.0 (inclusive) and 0.9999999999999999 (inclusive)
     */
    @Override
    public final double nextDouble() {
//        final long s = (state += 0x6C8E9CF570932BD5L);
//        final long z = (s ^ s >>> 25) * (s | 0xA529L);
//        return ((z ^ z >>> 22) & 0x1FFFFFFFFFFFFFL) * 0x1p-53;
        return (((state = (state << 29 | state >>> 35) * 0xAC564B05L) * 0x818102004182A025L) & 0x1FFFFFFFFFFFFFL) * 0x1p-53;

//        final long a = stateA * 0x41C64E6BL;
//        return (((stateB = 0xC6BC279692B5CC8BL - (stateB << 35 | stateB >>> 29)) ^ (stateA = (a << 28 | a >>> 36))) 
//                & 0x1FFFFFFFFFFFFFL) * 0x1p-53;
//        long z = (chaos = chaos * 0x41C64E6DL + 1L);
//        z = (z ^ z >>> 27) * 0xAEF17502108EF2D9L;
//        return ((z ^ z >>> 25) & 0x1FFFFFFFFFFFFFL) * 0x1p-53;
//        long z = (chaos = chaos * 0x41C64E6DL + 1L);
//        z = (z ^ z >>> 27) * 0xAEF17502108EF2D9L;
//        return ((z ^ z >>> 25) & 0x1FFFFFFFFFFFFFL) * 0x1p-53;
    }

    /**
     * Gets a random float between 0.0f inclusive and 1.0f exclusive.
     * This returns a maximum of 0.99999994 because that is the largest float value that is less than 1.0f .
     * @return a float between 0f (inclusive) and 0.99999994f (inclusive)
     */
    @Override
    public final float nextFloat() {
//        final long s = (state += 0x6C8E9CF570932BD5L);
//        final long z = (s ^ s >>> 25) * (s | 0xA529L);
//        return ((z ^ z >>> 22) & 0xFFFFFFL) * 0x1p-24f;
        return (((state = (state << 29 | state >>> 35) * 0xAC564B05L) * 0x818102004182A025L) & 0xFFFFFFL) * 0x1p-24f;

//        final long a = stateA * 0x41C64E6BL;
//        return (((stateB = 0xC6BC279692B5CC8BL - (stateB << 35 | stateB >>> 29)) ^ (stateA = (a << 28 | a >>> 36)))
//                & 0xFFFFFFL) * 0x1p-24f;
//        long z = (chaos = chaos * 0x41C64E6DL + 1L);
//        return ((z ^ z >>> 27) * 0xAEF17502108EF2D9L >>> 40) * 0x1p-24f;
    }
    
    public final double nextCurvedDouble()
    {
//        final long s = (state += 0x6C8E9CF570932BD5L);
//        final long z = (s ^ s >>> 25) * (s | 0xA529L);
//        return NumberTools.formCurvedDouble(z ^ z >>> 22);
        return NumberTools.formCurvedDouble((state = (state << 29 | state >>> 35) * 0xAC564B05L) * 0x818102004182A025L);
//        final long a = stateA * 0x41C64E6BL;
//        return NumberTools.formCurvedDouble((stateB = 0xC6BC279692B5CC8BL - (stateB << 35 | stateB >>> 29)) ^ (stateA = (a << 28 | a >>> 36)));
//        long z = (chaos = chaos * 0x41C64E6DL + 1L);
//        z = (z ^ z >>> 27) * 0xAEF17502108EF2D9L;
//        return NumberTools.formCurvedDouble(z ^ z >>> 25);
    }
    
    /**
     * This can't copy itself because EpiData is abstract, so it returns a StatefulRNG using a new ThrustAltRNG as its
     * RandomnessSource, seeded with this EpiData's state. This makes it stay an IRNG, but not an EpiData.
     */
    @Override
    public StatefulRNG copy() {
        return new StatefulRNG(new ThrustAltRNG(state));
    }

    /**
     * Gets a view of this IRNG in a way that implements {@link Serializable}, which may simply be this IRNG if it
     * implements Serializable as well as IRNG.
     *
     * @return a {@link Serializable} view of this IRNG or a similar one; here, {@code this}
     */
    @Override
    public Serializable toSerializable() {
        return this;
    }

    @Override
    public String toString() {
        return name == null ? id == null ? (id = "EpiData_" + StringKit.hex(idHash)) : id : name;
    }

    @Override
    public int hashCode() {
        return idHash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final EpiData other = (EpiData) obj;
        return idHash == other.idHash;
    }

}
