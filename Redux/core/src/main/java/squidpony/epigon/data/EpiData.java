package squidpony.epigon.data;

import squidpony.StringKit;
import squidpony.squidmath.AbstractRNG;
import squidpony.squidmath.LinnormRNG;
import squidpony.squidmath.StatefulRNG;

import java.io.Serializable;

import static squidpony.epigon.Epigon.rootChaos;

/**
 * Data class for information shared by all data objects.
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
        final long z = (chaos = chaos * 0x41C64E6DL + 1L);
        return ((z ^ z >>> 27) * 0xAEF17502108EF2D9L) < 0;
    }

    /**
     * Gets a random double between 0.0 inclusive and 1.0 exclusive.
     * This returns a maximum of 0.9999999999999999 because that is the largest double value that is less than 1.0 .
     * <br>
     * This is abstract because some generators may natively work with double or float values, but others may need to
     * convert a long to a double as with {@code (nextLong() & 0x1fffffffffffffL) * 0x1p-53}, which is recommended if
     * longs are fast to produce.
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
        final long z = (chaos = chaos * 0x41C64E6DL + 1L);
        return ((z ^ z >>> 27) * 0xAEF17502108EF2D9L >>> 40) * 0x1p-24f;
    }

    /**
     * Creates a copy of this IRNG; it will generate the same random numbers, given the same calls in order, as this
     * IRNG at the point copy() is called. The copy will not share references with this IRNG. If this IRNG does not
     * permit copying itself, it is suggested to either throw an {@link UnsupportedOperationException} or return a new
     * IRNG of the same type but with a random seed, with the latter meant as a partial defense against cheating.
     *
     * @return a copy of this IRNG
     */
    @Override
    public StatefulRNG copy() {
        return new StatefulRNG(new LinnormRNG(chaos));
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
