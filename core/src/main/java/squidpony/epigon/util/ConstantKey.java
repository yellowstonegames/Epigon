package squidpony.epigon.util;

import squidpony.epigon.GauntRNG;
import squidpony.squidmath.CrossHash;

import java.io.Serializable;
import java.util.Objects;

/**
 * Created by Tommy Ettinger on 1/22/2018.
 */
public interface ConstantKey {
    /**
     * Gets the (normally) pre-calculated 64-bit hash of this value, usually produced by {@link #precomputeHash(String, long)}.
     * @return a 64-bit hash code of this value
     */
    long hash64();
    /**
     * Gets the (normally) pre-calculated 32-bit hash of this value, usually produced by {@link #precomputeHash(String, long)}.
     * This is normally equivalent to the bottom 32 bits of {@link #hash64()}.
     * @return a 32-bit hash code of this value
     */
    int hash32();

    /**
     * When constructing an ConstantKey (typically from an enum), you should get the 64-bit hash code by calling this
     * method with the fully-qualified name of the class and a unique number for the instance (usually, this is the
     * {@link Enum#ordinal()} of an enum field). This should generally be stored instead of being calculated repeatedly.
     * @param namespace usually the fully-qualified class name; may differ for compatibility with package moves
     * @param unique a long that must be unique per ConstantKey with this namespace; usually the ordinal of an enum
     * @return a 64-bit hashCode that should be stored for this value
     */
    static long precomputeHash(String namespace, long unique)
    {
        return GauntRNG.nextLong(CrossHash.hash64(namespace) + unique);
    }

    /**
     * An IHasher to be used with OrderedMap, OrderedSet, Arrangement, etc. that allows any ConstantKey value.
     */
    class ConstantKeyHasher implements CrossHash.IHasher, Serializable {
        private static final long serialVersionUID = 1L;

        ConstantKeyHasher() {
        }
        public static final ConstantKeyHasher instance = new ConstantKeyHasher();

        @Override
        public int hash(final Object data) {
            return data == null ? 0 : (data instanceof ConstantKey) ? ((ConstantKey) data).hash32() : data.hashCode();
        }

        @Override
        public boolean areEqual(Object left, Object right) {
            return Objects.equals(left, right);
        }
    }
}
