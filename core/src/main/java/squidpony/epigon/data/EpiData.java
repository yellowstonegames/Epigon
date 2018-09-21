package squidpony.epigon.data;

import squidpony.StringKit;
import squidpony.squidmath.AbstractRNG;
import squidpony.squidmath.LinnormRNG;
import squidpony.squidmath.NumberTools;
import squidpony.squidmath.RNG;

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
 * algorithm this uses is LinnormRNG from SquidLib. It is slightly slower than TangleRNG and does not pass
 * birthday-problem tests (whether Tangle passes them is debatable), but has stronger quality and uses less state.
 */
public abstract class EpiData extends AbstractRNG implements Serializable {

    public String name;
    public String description;
    //public String notes; // NOTE - don't need to copy this into created objects <-- no way to eliminate the field

    private String id;
    private final int idHash;
    private long stateA, stateB;
    
    private static int uniqueIntGen = (int)System.nanoTime();

    private static final long[] startingA = {
            0x0000000000000001L, 0x770391C6587202CDL, 0x0148D0D6B055DE19L, 0x2CFFDEE4C4C8654FL, 0x3669291DE218F372L, 0x5B744ACB07F3D380L, 0x103F93C86BDF21D0L, 0x9A1D980831BCF2ABL,
            0x92D56961736A4B50L, 0x71A9832527530EADL, 0x4C524342889BCFE1L, 0xF39CFA3D37AB4038L, 0xA3E9A70AD8EEF84DL, 0x65C7AFEFFC4DA898L, 0x4D455E304CDC7741L, 0xA6EDACBD6740B1A7L,
            0xAA7F8E77C41AF5EBL, 0x96B50AD6E4AA2B18L, 0x77432395B55EDFD9L, 0x2748C2DD4565F1F0L, 0x3CAC2CDB2F8318D0L, 0x7D983C0295175158L, 0xDCFC33F629C3D00FL, 0x1EF0C5B47164F981L,
            0x3AB9A3877956251EL, 0xBA230F415A833533L, 0xA489CC2EF532A6BEL, 0xB212F25D09BFC366L, 0xB9014F210A77310DL, 0x8590EF3967A8C6E0L, 0x1011FD4E97B1F81AL, 0x1C57F18A80F4C131L,
            0x4AA90F013DB975E3L, 0xB3FAAC7A9374BD99L, 0xA15B9AA709431B2DL, 0xD3201A4C3953FFA2L, 0x0B34634F0B74BAB5L, 0x501389102E6E08EEL, 0xFCAC8A7CFCEB534DL, 0xA6A1A2C7CB5CEE8FL,
            0x5F461436431B3D6DL, 0x1F3DE41F1E991A39L, 0x96A5BD1D16EDC265L, 0xAEC3F8C461FA0749L, 0x4445933104846C0BL, 0xAD088B25A4AA0E59L, 0x862FCA81FF8B1BE5L, 0x12E5A795C17DA902L,
            0x5CA3CDC494DF2B93L, 0xCF612FCBDD25B41EL, 0xAD0CC4406EC6FCC3L, 0x352336C92FA965EAL, 0x675AB684694EE4A8L, 0x811A5D47EE8B3568L, 0x4937A92A07C372A4L, 0xE1483C680A24BEA4L,
            0x1B3E829B910E343CL, 0x0F5F8EF159F931C0L, 0x7F5DDFDA98EFE7EAL, 0xA2FA4A6C79F5C6EFL, 0xEA416C98A2A0945CL, 0x29CC34E89FCC5D02L, 0x157FC5094CCC1795L, 0x27252C1165C6E255L,
            0xAB963445C144A9BCL, 0x601530DECC304F69L, 0xC92D8F3257316572L, 0xC348074025724519L, 0x0F8305789523701EL, 0xD288EFE7BDDABF47L, 0xC428DA0AD18149BAL, 0xBA1D19D35E61A11EL,
            0x6D81979DC0110FA2L, 0x3C144A6DC2C2982BL, 0x7593425EA77652A8L, 0xBA416F84332EFD0AL, 0x691EAA02B1351B41L, 0xF1B15F5AD69A16BAL, 0x026D58B160B39D4CL, 0x813B48A15DA161E6L,
            0xCC92B59765EF4C5FL, 0x46B6C1ED44BF6877L, 0xA679D47C27EA4A03L, 0x393BEF21C904261CL, 0xE40A734EFE039992L, 0xD114E560A35EC443L, 0x85A46B901B80F546L, 0xCC8C80C6AB27F53CL,
            0xC9B5FCE7C3EE4A83L, 0x64D4B2A2A91ADC11L, 0x7157576E65940314L, 0x75BF0B0737304143L, 0x4A11300B7F32C8C5L, 0x4B4FB70D7701DD60L, 0xE877F97BEC9E8FACL, 0x151E431374EF9D79L,
            0x636214B0856DF427L, 0x088F1774DE7730CFL, 0x9E3B5CD7FF590F81L, 0x4DA157EA25850BC1L, 0xE9C7C31744E062F4L, 0x4767FCAF076B9508L, 0xC5C767D939AC8425L, 0x1ABAF0D4EC698A8FL,
            0x5035DC94FA971B81L, 0x718EE38E931713E2L, 0x497DB43133CEF0F2L, 0xF01BE721B0145805L, 0x9D6239853FF80744L, 0x256B893D4DD0689AL, 0x256647CAA07563D6L, 0xCE4087F877A6D24FL,
            0x68A0537869364FE2L, 0x32BA732DEC14AE42L, 0x3AAF6CDE0CE8DB48L, 0x552C1D9594CE212AL, 0x8BC1A33AE250B2E9L, 0xC02FCB678B465D00L, 0x496F580658AFD50AL, 0x6D0D982E45AD15A9L,
            0xC8E87307F336E8D0L, 0x257E726598418548L, 0xFADF2ED10B13D148L, 0x46FA6CC74F293535L, 0xF03227995C268856L, 0x46087E39622EA4CDL, 0x17EE09D3D2181207L, 0x9C7518A1E5AD4544L,
    }, startingB = {
            0x0000000000000001L, 0x07293E6E09EC6368L, 0x96D969CADB4CD368L, 0x3FF86768F89EAEB3L, 0x2F9FAC39CC8E5CB7L, 0xF0ACF2D0542EE141L, 0x7BF403A079DCD087L, 0xDA68703F5EAB9409L,
            0xF887EDE8E8AD388BL, 0xB93108A12DD8DC5CL, 0x98676A8BE90BB48EL, 0x3C66E22B602A7007L, 0x69A56A92BAD39B5BL, 0x58857B966DDF07BCL, 0x3B6890E3EDB96D6EL, 0xF0363B595221C86DL,
            0x62EE3C3A7A528614L, 0xB0175247E00B4935L, 0xE70D810777ED42ACL, 0x275CE4F27473631AL, 0xB5DF57C4502967E9L, 0xB8EB0B9EC111C7FEL, 0xC28F3B422CA03689L, 0xD09DD3A8FEAB2DD1L,
            0x4E2C713B5A7A0FFCL, 0x9AFC4BE99ED5F1AAL, 0xA89BFD2F6C2E97AEL, 0xF8735B9A6DF5F258L, 0xB2F89E533D9B9897L, 0xD89711EA7777E671L, 0x9658217AF4F448CAL, 0xEE3F474204385F6BL,
            0x2B20D085EAB7ECC0L, 0xDF4FBDB5877EC70AL, 0xA27D970C88F1246BL, 0x88D0B336E63ADE23L, 0xC06AF42B0855C181L, 0x00E8B464987358DBL, 0xDA1DF8BA1E45586EL, 0x4C12347AB35D2F03L,
            0x752C4942F1095640L, 0x608BD5FC9E04FA0AL, 0xB253E48775CDD5E1L, 0x643E8401460AAA59L, 0xE248C00B3A622F06L, 0xB01AD54DFE588BF2L, 0x1D486285F47A99D0L, 0x4ACE70E9A24E7B42L,
            0xE498314246C2E894L, 0x67BB0785AEA67873L, 0xAB50922AD5171ABDL, 0x4BFA6DEE10549DC3L, 0x889BB7C03B745D65L, 0x705D68BC7379AEECL, 0x08BC6282C82C8B72L, 0xB967A84918604EDCL,
            0x17F2AE6E78487967L, 0x038874F2D394FB80L, 0x7F7A2F1A581C66D3L, 0x99977A67381F6F7FL, 0x6B62915A4927F8D3L, 0x4DE18BB59A3C182DL, 0x94E508A682455109L, 0x986BE18241462557L,
            0x0578DFAE00F8A0D6L, 0x29B22988B2264886L, 0xD552345E6E2A3125L, 0x5DB9E3195164C051L, 0x0E43BA334827D573L, 0x3AFAF8799E87209CL, 0xBC0E249E28B42DE9L, 0x022A07577137E25FL,
            0x7DEB553C69DAA1FFL, 0x4A69C3A72EF45E41L, 0xBEBAC3CF3B608398L, 0xEB5771FF214E2487L, 0x9FB5E8C5B36B4CC9L, 0xC09F95341A44B518L, 0x668BEA20B4AE0875L, 0x633E56557743D5CBL,
            0x60F91113C85EAAAAL, 0xB7FD377C14A36222L, 0xFCF5360544E39E14L, 0xC8201F79E019A016L, 0x9298BE81EFD5200FL, 0xBEB6A71A91068F67L, 0xB48125BFEFE20180L, 0xC470152566C3E1A0L,
            0x46646F5388059BA1L, 0x6B2EFA0363CEB524L, 0xC60186015E2573E1L, 0x514BF9772FA2ACCEL, 0x1C44DACDE62A44EDL, 0x0CC4356D150B5469L, 0xDF21F9DAE98D5C86L, 0xA22573A5D741ACECL,
            0x722CB87504029D8AL, 0x5727EA9D310F90F7L, 0x06D1E7DC6CF5C689L, 0x735BAEB75FDD9F85L, 0xEF96C3AF03785BEAL, 0xBE453FC733BEFA00L, 0xE27E2672BFCC1C44L, 0x541C5523E0FBB038L,
            0xA04B840944E17E54L, 0x313CA18B6537B063L, 0xC7B93061D18C2FFEL, 0xE1D991D2E4A8CD20L, 0x5BB21B4ED59FAE91L, 0x7DB82C96F57D18C5L, 0x9EEBA39CBD611F6EL, 0x093E9402ABCC23F7L,
            0x9A7637252A4475F7L, 0x0C8A522F0B70DB19L, 0x3532D24B07A4D08BL, 0x633C908FA64BB58AL, 0x16A3123AD6B3DD79L, 0x1169BB0D6BD6DEC5L, 0xDABFB787CED62E83L, 0x8F17A15C52A3B9BDL,
            0xA2F3FA0F0F5F6FDFL, 0x95DA83EA34697FEFL, 0xFE1541E512CBAC77L, 0xE68287CDEB9302A5L, 0xB928A0223B695207L, 0x3F9D05B291DE5A8AL, 0x5E28B275895A2C79L, 0x8E9BD22FBFD57A6CL,
    };

    public final void setState(final int s) {
        stateA = startingA[s >>> 9 & 0x7F];
        for (int i = s & 0x1FF; i > 0; i--) {
            stateA *= 0x41C64E6BL;
            stateA = (stateA << 28 | stateA >>> 36);
        }
        stateB = startingB[s >>> 25];
        for (int i = s >>> 16 & 0x1FF; i > 0; i--) {
            stateB *= 0x9E3779B9L;
            stateB = (stateB << 37 | stateB >>> 27);
        }
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
    @Override
    public final int nextInt()
    {
        final long a = stateA * 0x41C64E6BL;
        final long b = stateB * 0x9E3779B9L;
        return (int)((stateA = (a << 28 | a >>> 36)) ^ (stateB = (b << 37 | b >>> 27)));
    }
    @Override
    public final int next(final int bits)
    {
        final long a = stateA * 0x41C64E6BL;
        final long b = stateB * 0x9E3779B9L;
        return (int)((stateA = (a << 28 | a >>> 36)) ^ (stateB = (b << 37 | b >>> 27))) >>> (32 - bits);
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
        final long a = stateA * 0x41C64E6BL;
        final long b = stateB * 0x9E3779B9L;
        return (stateA = (a << 28 | a >>> 36)) ^ (stateB = (b << 37 | b >>> 27));
    }

    /**
     * Get a random bit of state, interpreted as true or false with approximately equal likelihood.
     * @return a random boolean.
     */
    @Override
    public final boolean nextBoolean() {
        final long a = stateA * 0x41C64E6BL;
        final long b = stateB * 0x9E3779B9L;
        return ((stateA = (a << 28 | a >>> 36)) ^ (stateB = (b << 37 | b >>> 27))) < 0L;
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

        final long a = stateA * 0x41C64E6BL;
        final long b = stateB * 0x9E3779B9L;
        return (((stateA = (a << 28 | a >>> 36)) ^ (stateB = (b << 37 | b >>> 27))) & 0x1FFFFFFFFFFFFFL) * 0x1p-53;
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
        final long a = stateA * 0x41C64E6BL;
        final long b = stateB * 0x9E3779B9L;
        return (((stateA = (a << 28 | a >>> 36)) ^ (stateB = (b << 37 | b >>> 27))) & 0xFFFFFFL) * 0x1p-24f;
//        long z = (chaos = chaos * 0x41C64E6DL + 1L);
//        return ((z ^ z >>> 27) * 0xAEF17502108EF2D9L >>> 40) * 0x1p-24f;
    }
    
    public final double nextCurvedDouble()
    {

        final long a = stateA * 0x41C64E6BL;
        final long b = stateB * 0x9E3779B9L;
        return NumberTools.formCurvedDouble((stateA = (a << 28 | a >>> 36)) ^ (stateB = (b << 37 | b >>> 27)));
//        long z = (chaos = chaos * 0x41C64E6DL + 1L);
//        z = (z ^ z >>> 27) * 0xAEF17502108EF2D9L;
//        return NumberTools.formCurvedDouble(z ^ z >>> 25);
    }
    
    /**
     * This can't copy itself because EpiData is abstract, so it returns an RNG using a new LinnormRNG as its
     * RandomnessSource, seeded with this EpiData's chaos and jumble. This makes it stay an IRNG, but not an EpiData.
     */
    @Override
    public RNG copy() {
        return new RNG(new LinnormRNG(stateA ^ stateB));
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
