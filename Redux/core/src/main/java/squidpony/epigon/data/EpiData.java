package squidpony.epigon.data;

import squidpony.StringKit;
import squidpony.squidmath.AbstractRNG;
import squidpony.squidmath.NumberTools;
import squidpony.squidmath.StatefulRNG;

import java.io.Serializable;

import static squidpony.epigon.Epigon.rootChaos;

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
 * algorithm this uses is LinnormRNG, which is the default in SquidLib currently. It has very high quality and is tied
 * with another (slightly less-certain quality) generator for the fastest that can produce all possible results and pass
 * quality tests.
 */
public abstract class EpiData extends AbstractRNG implements Serializable {

    public String name;
    public String description;
    //public String notes; // NOTE - don't need to copy this into created objects <-- no way to eliminate the field

    private String id;
    public final int idHash;
    public long chaos;
    
    private static int uniqueIntGen = 123456789;

    public EpiData() {
        idHash = (uniqueIntGen += 0x632BE5AB);
        chaos = rootChaos.nextLong();
    }
    public EpiData(final String name)
    {
        this();
        this.name = name;
    }

    /**
     * Get up to 32 bits (inclusive) of random output; the int this produces
     * will not require more than {@code bits} bits to represent.
     *
     * @param bits an int between 1 and 32, both inclusive
     * @return a random number that fits in the specified number of bits
     */
    @Override
    public final int next(int bits) {
        long z = (chaos = chaos * 0x41C64E6DL + 1L);
        z = (z ^ z >>> 27) * 0xAEF17502108EF2D9L;
        return (int)(z ^ z >>> 25) >>> (32 - bits);
    }

    /**
     * Get a random integer between Integer.MIN_VALUE to Integer.MAX_VALUE (both inclusive).
     *
     * @return a 32-bit random int.
     */
    @Override
    public final int nextInt() {
        long z = (chaos = chaos * 0x41C64E6DL + 1L);
        z = (z ^ z >>> 27) * 0xAEF17502108EF2D9L;
        return (int)(z ^ z >>> 25);
    }

    /**
     * Get a random long between Long.MIN_VALUE to Long.MAX_VALUE (both inclusive).
     *
     * @return a 64-bit random long.
     */
    @Override
    public final long nextLong() {
        long z = (chaos = chaos * 0x41C64E6DL + 1L);
        z = (z ^ z >>> 27) * 0xAEF17502108EF2D9L;
        return (z ^ z >>> 25);
    }

    /**
     * Get a random bit of state, interpreted as true or false with approximately equal likelihood.
     * @return a random boolean.
     */
    @Override
    public final boolean nextBoolean() {
        return  (chaos = chaos * 0x41C64E6DL + 1L) < 0;
    }

    /**
     * Gets a random double between 0.0 inclusive and 1.0 exclusive.
     * This returns a maximum of 0.9999999999999999 because that is the largest double value that is less than 1.0 .
     *
     * @return a double between 0.0 (inclusive) and 0.9999999999999999 (inclusive)
     */
    @Override
    public final double nextDouble() {
        long z = (chaos = chaos * 0x41C64E6DL + 1L);
        z = (z ^ z >>> 27) * 0xAEF17502108EF2D9L;
        return ((z ^ z >>> 25) & 0x1FFFFFFFFFFFFFL) * 0x1p-53;
    }

    /**
     * Gets a random float between 0.0f inclusive and 1.0f exclusive.
     * This returns a maximum of 0.99999994 because that is the largest float value that is less than 1.0f .
     * @return a float between 0f (inclusive) and 0.99999994f (inclusive)
     */
    @Override
    public final float nextFloat() {
        long z = (chaos = chaos * 0x41C64E6DL + 1L);
        return ((z ^ z >>> 27) * 0xAEF17502108EF2D9L >>> 40) * 0x1p-24f;
    }
    
    public final double nextCurvedDouble()
    {
        long z = (chaos = chaos * 0x41C64E6DL + 1L);
        z = (z ^ z >>> 27) * 0xAEF17502108EF2D9L;
        return NumberTools.formCurvedDouble(z ^ z >>> 25);
    }
    
    /**
     * This can't copy itself because EpiData is abstract, so it returns a StatefulRNG with {@link #chaos} as its seed
     * instead. This makes it stay an IRNG, but not an EpiData, and it won't use the same random number generation
     * algorithm, either.
     */
    @Override
    public StatefulRNG copy() {
        return new StatefulRNG(chaos);
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
