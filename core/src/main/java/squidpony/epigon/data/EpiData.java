package squidpony.epigon.data;

import squidpony.StringKit;
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
 * algorithm this uses is OverdriveRNG from Sarong. It is slightly faster than TangleRNG or LinnormRNG and has very
 * strong quality, but takes some time to initialize (instead of almost none for Linnorm). Overdrive is a combination of
 * two subcycle generators, so some states will be on a low-period cycle and others will be on a higher-period cycle;
 * the longer initialization ensures a high-enough period (minimum 2 to the 47, probably much higher). The time to
 * initialize should take no more than the time it takes to make 512 calls to {@link #nextLong()}, and almost always
 * less time than that.
 */
public abstract class EpiData extends AbstractRNG implements Serializable, StatefulRandomness {
    protected static final long serialVersionUID = 0L;

    public String name;
    public String description;
    //public String notes; // NOTE - don't need to copy this into created objects <-- no way to eliminate the field

    private String id;
    private final int idHash;
    private long state;
    
    private static int uniqueIntGen = (int) LinnormRNG.determine(System.nanoTime());

//    private static final long[] startingA = {
//            0x0000000000000001L, 0x770391C6587202CDL, 0x0148D0D6B055DE19L, 0x2CFFDEE4C4C8654FL, 0x3669291DE218F372L, 0x5B744ACB07F3D380L, 0x103F93C86BDF21D0L, 0x9A1D980831BCF2ABL,
//            0x92D56961736A4B50L, 0x71A9832527530EADL, 0x4C524342889BCFE1L, 0xF39CFA3D37AB4038L, 0xA3E9A70AD8EEF84DL, 0x65C7AFEFFC4DA898L, 0x4D455E304CDC7741L, 0xA6EDACBD6740B1A7L,
//            0xAA7F8E77C41AF5EBL, 0x96B50AD6E4AA2B18L, 0x77432395B55EDFD9L, 0x2748C2DD4565F1F0L, 0x3CAC2CDB2F8318D0L, 0x7D983C0295175158L, 0xDCFC33F629C3D00FL, 0x1EF0C5B47164F981L,
//            0x3AB9A3877956251EL, 0xBA230F415A833533L, 0xA489CC2EF532A6BEL, 0xB212F25D09BFC366L, 0xB9014F210A77310DL, 0x8590EF3967A8C6E0L, 0x1011FD4E97B1F81AL, 0x1C57F18A80F4C131L,
//            0x4AA90F013DB975E3L, 0xB3FAAC7A9374BD99L, 0xA15B9AA709431B2DL, 0xD3201A4C3953FFA2L, 0x0B34634F0B74BAB5L, 0x501389102E6E08EEL, 0xFCAC8A7CFCEB534DL, 0xA6A1A2C7CB5CEE8FL,
//            0x5F461436431B3D6DL, 0x1F3DE41F1E991A39L, 0x96A5BD1D16EDC265L, 0xAEC3F8C461FA0749L, 0x4445933104846C0BL, 0xAD088B25A4AA0E59L, 0x862FCA81FF8B1BE5L, 0x12E5A795C17DA902L,
//            0x5CA3CDC494DF2B93L, 0xCF612FCBDD25B41EL, 0xAD0CC4406EC6FCC3L, 0x352336C92FA965EAL, 0x675AB684694EE4A8L, 0x811A5D47EE8B3568L, 0x4937A92A07C372A4L, 0xE1483C680A24BEA4L,
//            0x1B3E829B910E343CL, 0x0F5F8EF159F931C0L, 0x7F5DDFDA98EFE7EAL, 0xA2FA4A6C79F5C6EFL, 0xEA416C98A2A0945CL, 0x29CC34E89FCC5D02L, 0x157FC5094CCC1795L, 0x27252C1165C6E255L,
//            0xAB963445C144A9BCL, 0x601530DECC304F69L, 0xC92D8F3257316572L, 0xC348074025724519L, 0x0F8305789523701EL, 0xD288EFE7BDDABF47L, 0xC428DA0AD18149BAL, 0xBA1D19D35E61A11EL,
//            0x6D81979DC0110FA2L, 0x3C144A6DC2C2982BL, 0x7593425EA77652A8L, 0xBA416F84332EFD0AL, 0x691EAA02B1351B41L, 0xF1B15F5AD69A16BAL, 0x026D58B160B39D4CL, 0x813B48A15DA161E6L,
//            0xCC92B59765EF4C5FL, 0x46B6C1ED44BF6877L, 0xA679D47C27EA4A03L, 0x393BEF21C904261CL, 0xE40A734EFE039992L, 0xD114E560A35EC443L, 0x85A46B901B80F546L, 0xCC8C80C6AB27F53CL,
//            0xC9B5FCE7C3EE4A83L, 0x64D4B2A2A91ADC11L, 0x7157576E65940314L, 0x75BF0B0737304143L, 0x4A11300B7F32C8C5L, 0x4B4FB70D7701DD60L, 0xE877F97BEC9E8FACL, 0x151E431374EF9D79L,
//            0x636214B0856DF427L, 0x088F1774DE7730CFL, 0x9E3B5CD7FF590F81L, 0x4DA157EA25850BC1L, 0xE9C7C31744E062F4L, 0x4767FCAF076B9508L, 0xC5C767D939AC8425L, 0x1ABAF0D4EC698A8FL,
//            0x5035DC94FA971B81L, 0x718EE38E931713E2L, 0x497DB43133CEF0F2L, 0xF01BE721B0145805L, 0x9D6239853FF80744L, 0x256B893D4DD0689AL, 0x256647CAA07563D6L, 0xCE4087F877A6D24FL,
//            0x68A0537869364FE2L, 0x32BA732DEC14AE42L, 0x3AAF6CDE0CE8DB48L, 0x552C1D9594CE212AL, 0x8BC1A33AE250B2E9L, 0xC02FCB678B465D00L, 0x496F580658AFD50AL, 0x6D0D982E45AD15A9L,
//            0xC8E87307F336E8D0L, 0x257E726598418548L, 0xFADF2ED10B13D148L, 0x46FA6CC74F293535L, 0xF03227995C268856L, 0x46087E39622EA4CDL, 0x17EE09D3D2181207L, 0x9C7518A1E5AD4544L,
//    }, startingB = {
//            0x0000000000000001L, 0xE400A8A8DFFCDBB8L, 0xD48F52BDA014F338L, 0x1B348EF2FB81A072L, 0x52953C3D77622AFCL, 0x22CEA1428F6FAAAAL, 0x11C4D45E3F37ACCDL, 0x709F5A366A542F61L,
//            0xD093E219F7E32762L, 0xC2986214F61E4935L, 0x9841F301821A420DL, 0x8BC8F2F2B2D421DAL, 0x8819E1ED2169B636L, 0x019D281DB10747F3L, 0x018E1EA12FD10EFFL, 0xB5EDD9A1AF9852C8L,
//            0x606393B52FB4FE4BL, 0xDD1AC582299E7B24L, 0xD6AAB23FC69E967AL, 0x594E61948FAB7E27L, 0x474EF8B0F7AB6E45L, 0x494D82A15FB1A8F5L, 0xFE4DC58A2A26636DL, 0x1CE41B69F88A436CL,
//            0x1CDBAD85DF9D8DC8L, 0x268187ADA0F9843EL, 0xA6A9FD24B5F20971L, 0x43BD3DE92518DE85L, 0x08693D533C9E0253L, 0x39294885073F9B25L, 0x34694C33DC7B5E4DL, 0x2A9B79A87C7058FFL,
//            0x8C8D0152100D5921L, 0xF19FAB4D1E387EE0L, 0xDBC2D53FDEC850E2L, 0x5BCA216BC9B1C99AL, 0xC409048CF9E1CC8BL, 0x025105B820B1CDBFL, 0xE0C57B5A27453EF5L, 0xE0E91F20508585E9L,
//            0x928C7D3A4FD7C7A7L, 0xA562A6BDF1015D80L, 0x016294BA8883E09AL, 0xBFAB35783CC3DA97L, 0xBA4305AEBE03D91BL, 0xC6E306B12006310CL, 0x2F8305B416C912CEL, 0x1EC26EB303D9BDF2L,
//            0x2EBF2DB4B3DAE292L, 0xDA532E18FA229293L, 0x053F869333A352B7L, 0x653FD7FF33D3171EL, 0x85A9E9FFDD180311L, 0xCB43C7B03489D26CL, 0x9F10A148350AD2CFL, 0xA71FF57039ABD697L,
//            0x62A6809DC83FEE89L, 0x3BFE4C425850BA89L, 0x62D2B0C0AC1424A6L, 0x720F1FF7AD159B39L, 0x620FC06702156F39L, 0x69175E436E54D339L, 0x6A535CF4A6645739L, 0x272DAE2591C17FEFL,
//            0xB5E32375F56F1FF7L, 0x15B3265CD7A0EE6EL, 0x426D4A9BADB7BC05L, 0xCF169B92AD877067L, 0xB212C6FE7575A4EDL, 0xCE42C49D2D7C7AEEL, 0x8492C339B59939DFL, 0x04DA5E053365C9B3L,
//            0x6D2241057E3E7383L, 0x00C1286E2E3B37EFL, 0x00CB78687D54DF4BL, 0xFF29D9EAB6771277L, 0xF4B489E676CF73DFL, 0x1FC3C899124F900EL, 0x42A30B587EDD30D3L, 0xA6CC47B8042A6DF5L,
//            0x6230D76E4D065860L, 0x1B6C922EA7A2583AL, 0xFFD35FAA261D09F9L, 0xD608602F4DC9A277L, 0x140C2A804A58DE38L, 0xB5EC5505D402DE8AL, 0x15B309B8FFAA896FL, 0x9A87391F605087B3L,
//            0xA9CBB441389237B4L, 0x484BDC4B4E9CA56AL, 0xFE31344AA267873CL, 0x14F8333096E7A879L, 0x09F7505F5A67C77FL, 0x4CF79EE399E5073FL, 0x611EC9D57F153234L, 0xFC37D35F7CEC1FA1L,
//            0xF54EBD813E05D901L, 0xA97E15B924E7500DL, 0x1581AA01C467A6AEL, 0xBDADD62D68E7A1C8L, 0x45AF8A8FAAC02002L, 0x5A517A8AAAC6A6ACL, 0x4E710A9437BA1F46L, 0x5541D562BF1294D9L,
//            0x269D548125D1B97AL, 0xD056D48B6F668605L, 0x45E64E1AA85D7B43L, 0x354FD09228C67B24L, 0x5E8FC612828FB3BAL, 0x69CFCB52C50BC09AL, 0xBA2DD0CD530A2A8FL, 0x0854F25414696819L,
//            0x9F4578D0D43735CCL, 0x5F5FE60CFA3EE2F4L, 0x0B4BE5B17A413AF7L, 0x5B3FE34D820740A7L, 0x8D977D1830B404A6L, 0xD1FCA3BAD54E3953L, 0x0F69F02CE2902FA4L, 0x0849A89D71201C15L,
//            0x44F39E5C979007F3L, 0x7E8D24FF09FE44F3L, 0x4160C07D0F111A35L, 0x76718DD168041ACFL, 0xAF5D3F916FE3CC23L, 0x24B3DDF4EFBF834AL, 0x5E1689A56F9F58D0L, 0x3251CFCB89D7BF0AL,
//    };

    public final void setState(final int s) {
//        stateA = startingA[s >>> 9 & 0x7F];
//        for (int i = s & 0x1FF; i > 0; i--) {
//            stateA *= 0x41C64E6BL;
//            stateA = (stateA << 28 | stateA >>> 36);
//        }
//        stateB = startingB[s >>> 25];
//        for (int i = s >>> 16 & 0x1FF; i > 0; i--) {
//            stateB = 0xC6BC279692B5CC8BL - (stateB << 35 | stateB >>> 29);
//        }
        state = s ^ (long)s << 32;
    }
    
    public EpiData() {
        idHash = (uniqueIntGen += 0x632BE5AB);
        setState(idHash);
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
    public long getState() {
        return state;
    }

    /**
     * Set the current internal state of this StatefulRandomness with a long.
     *
     * @param state a 64-bit long. You can safely pass 0 here, or any long.
     */
    @Override
    public void setState(long state) {
        this.state = state;
    }

    @Override
    public final int nextInt()
    {
        final long s = (state += 0x6C8E9CF570932BD5L);
        final long z = (s ^ s >>> 25) * (s | 0xA529L);
        return (int)(z ^ z >>> 22);

//        final long a = stateA * 0x41C64E6BL;
//        return (int)((stateB = 0xC6BC279692B5CC8BL - (stateB << 35 | stateB >>> 29)) ^ (stateA = (a << 28 | a >>> 36)));
    }
    @Override
    public final int next(final int bits)
    {
//        final long a = stateA * 0x41C64E6BL;
//        return (int)((stateB = 0xC6BC279692B5CC8BL - (stateB << 35 | stateB >>> 29)) ^ (stateA = (a << 28 | a >>> 36))) >>> (32 - bits);
        final long s = (state += 0x6C8E9CF570932BD5L);
        final long z = (s ^ s >>> 25) * (s | 0xA529L);
        return (int)(z ^ z >>> 22) >>> (32 - bits);
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
        final long s = (state += 0x6C8E9CF570932BD5L);
        final long z = (s ^ s >>> 25) * (s | 0xA529L);
        return (z ^ z >>> 22);
//        final long a = stateA * 0x41C64E6BL;
//        return (stateB = 0xC6BC279692B5CC8BL - (stateB << 35 | stateB >>> 29)) ^ (stateA = (a << 28 | a >>> 36));
    }

    /**
     * Get a random bit of state, interpreted as true or false with approximately equal likelihood.
     * @return a random boolean.
     */
    @Override
    public final boolean nextBoolean() {
        final long s = (state += 0x6C8E9CF570932BD5L);
        final long z = (s ^ s >>> 25) * (s | 0xA529L);
        return z < 0L;

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
        final long s = (state += 0x6C8E9CF570932BD5L);
        final long z = (s ^ s >>> 25) * (s | 0xA529L);
        return ((z ^ z >>> 22) & 0x1FFFFFFFFFFFFFL) * 0x1p-53;

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
        final long s = (state += 0x6C8E9CF570932BD5L);
        final long z = (s ^ s >>> 25) * (s | 0xA529L);
        return ((z ^ z >>> 22) & 0xFFFFFFL) * 0x1p-24f;

//        final long a = stateA * 0x41C64E6BL;
//        return (((stateB = 0xC6BC279692B5CC8BL - (stateB << 35 | stateB >>> 29)) ^ (stateA = (a << 28 | a >>> 36)))
//                & 0xFFFFFFL) * 0x1p-24f;
//        long z = (chaos = chaos * 0x41C64E6DL + 1L);
//        return ((z ^ z >>> 27) * 0xAEF17502108EF2D9L >>> 40) * 0x1p-24f;
    }
    
    public final double nextCurvedDouble()
    {
        final long s = (state += 0x6C8E9CF570932BD5L);
        final long z = (s ^ s >>> 25) * (s | 0xA529L);
        return NumberTools.formCurvedDouble(z ^ z >>> 22);
//        final long a = stateA * 0x41C64E6BL;
//        return NumberTools.formCurvedDouble((stateB = 0xC6BC279692B5CC8BL - (stateB << 35 | stateB >>> 29)) ^ (stateA = (a << 28 | a >>> 36)));
//        long z = (chaos = chaos * 0x41C64E6DL + 1L);
//        z = (z ^ z >>> 27) * 0xAEF17502108EF2D9L;
//        return NumberTools.formCurvedDouble(z ^ z >>> 25);
    }
    
    /**
     * This can't copy itself because EpiData is abstract, so it returns an RNG using a new TangleRNG as its
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
