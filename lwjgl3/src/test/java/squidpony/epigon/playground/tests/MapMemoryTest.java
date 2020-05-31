package squidpony.epigon.playground.tests;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.TimeUtils;
import com.github.tommyettinger.merry.ObjectSet;

//import com.badlogic.gdx.math.GridPoint2;
//import com.badlogic.gdx.utils.ObjectMap;
//import com.badlogic.gdx.utils.ObjectSet;

public class MapMemoryTest extends ApplicationAdapter {
    private static final int width = 600, height = 600;
    // the initial bug was reported on ObjectMap
    // 44x160:
    //17318774ns taken, about 10 to the 7.2385171449630645 power. (default libGDX hashCode())
    // 2673270ns taken, about 10 to the 6.427042824670609 power.  (custom hashCode(), long Rosenberg-Strong-based)
    // 600x600:
    //49127010ns taken, about 10 to the 7.691320332626245 power.  (custom hashCode(), long Rosenberg-Strong-based)
    
    // libGDX ObjectSet
    // 100x100:
    // 2553571ns taken, about 10 to the 6.407147937570271 power. (custom hashCode(), long Rosenberg-Strong-based)
    // 3524591ns taken, about 10 to the 6.547108727923517 power. (default libGDX hashCode()
    // 600x600:
    //42106665ns taken, about 10 to the 7.624350845090325 power. (custom hashCode(), long Rosenberg-Strong-based)
    // OUT OF MEMORY ERROR (default libGDX hashCode())
    
    // merry
    // 100x100:
    // 1991027ns taken, about 10 to the 6.299077149465633 power. (custom hashCode(), long Rosenberg-Strong-based)
    // 2825757ns taken, about 10 to the 6.451134812113873 power. (default libGDX hashCode())
    // 600x600:
    //49160589ns taken, about 10 to the 7.691617077589466 power. (custom hashCode(), long Rosenberg-Strong-based)
    //39207429ns taken, about 10 to the 7.593368364674056 power. (default libGDX hashCode())
//    private ObjectSet<GridPoint2> theMap;
    //258847822730ns taken, about 10 to the 11.413044516312956 power. (Boom's horrible always-0 hashCode())
    private ObjectSet<Boom> theMap;
    
    //99835817310ns taken, about 10 to the 10.999286377654473 power. (default libGDX hashCode())
    //private UnorderedSet<GridPoint2> theMap;
    // 600x600:
    //158299104788ns taken, about 10 to the 11.199478458850157 power. (Boom's horrible always-0 hashCode())
//    private ObjectMap<Boom, Integer> theMap;
    // 2413330ns taken, about 10 to the 6.382616711622814 power. (default libGDX hashCode())
    // 1974040ns taken, about 10 to the 6.29535594853791 power.  (custom hashCode(), long Rosenberg-Strong-based)
//    private HashMap<GridPoint2, Integer> theMap;
    // 600x600:
    //4661515873501ns taken, about 10 to the 12.668527167428932 power. (Boom's horrible always-0 hashCode())
    // above, that's 77 minutes!
//    private HashMap<Boom, Integer> theMap;
    
//    private ObjectSet<Boom> theMap;
//    private ObjectSet<GridPoint2> theMap;
    // 600x600:
    //175800568ns taken, about 10 to the 8.245020273916747 power. (default hashCode())
    //28242954ns taken, about 10 to the 7.450910118683097 power. (Rosenberg-Strong long hashCode())
//    private HashSet<GridPoint2> theMap;
    // 600x600:
    //260426720ns taken, about 10 to the 8.415685541160471 power. (default hashCode())
    //121823986ns taken, about 10 to the 8.085732805225199 power. (Rosenberg-Strong long hashCode())
//    private RobinHood<GridPoint2> theMap;
    // 600x600:
    //78650700ns taken, about 10 to the 7.895702592245821 power. (Rosenberg-Strong long hashCode(), not much different with default)
//    private RobinHood2<GridPoint2> theMap;
    // 600x600:
    //384595009317ns taken, about 10 to the 11.585003644332646 power. (Boom's horrible always-0 hashCode())
//    private RobinHood2<Boom> theMap;
    // 600x600:
    // a lot... about 10 to the 11 power.
//    private DoubleHashing<GridPoint2> theMap;
//    private com.badlogic.gdx.utils.ObjectMap<GridPoint2, Integer> theMap; //5806446897ns taken, about 10 to the 9.763910458542151 power.
//    private UnorderedMap<GridPoint2, Integer> theMap; //5368669452ns taken, about 10 to the 9.729866665354251 power.
//    private com.badlogic.gdx.utils.ObjectMap<com.badlogic.gdx.math.GridPoint2, Integer> theMap; //CRASH, out of memory
//    private ObjectMap<Vector2, Integer> theMap;
//    private ObjectMap<com.badlogic.gdx.math.Vector2, Integer> theMap;
//    private com.badlogic.gdx.utils.ObjectMap<Vector2, Integer> theMap;
//    private OrderedMap<com.badlogic.gdx.math.Vector2, Integer> theMap;
//    private HashMap<GridPoint2, Integer> theMap; //8018047237ns taken, about 10 to the 9.904068610496196 power.
//    private HashMap<com.badlogic.gdx.math.GridPoint2, Integer> theMap; //15204398669ns taken, about 10 to the 10.1819692485578 power.
//    private HashMap<com.badlogic.gdx.math.Vector2, Integer> theMap;
//    private com.badlogic.gdx.utils.ObjectMap<com.badlogic.gdx.math.Vector2, Integer> theMap;
//    private HashMap<com.badlogic.gdx.math.GridPoint2, Integer> theMap;

    // HashSet from the JDK
    //200x200
    //Initial allocated space for Set: Not supported
    //21198700ns taken, about 10 to the 7.326309228846793 power.
    //Post-assign allocated space for Set: Not supported    
    //1000x1000
    //Initial allocated space for Set: Not supported
    //913632622ns taken, about 10 to the 8.960771598018294 power.
    //Post-assign allocated space for Set: Not supported
    //1000x1000 Strings
    //Initial allocated space for Set: Not supported
    //893019736ns taken, about 10 to the 8.950861057030966 power.
    //Post-assign allocated space for Set: Not supported
//    private HashSet<Object> theMap;
    // Linear probing UnorderedSet, from SquidLib
    //200x200
    //Initial allocated space for Set: 2049
    //2383177240ns taken, about 10 to the 9.377156342594699 power.
    //Post-assign allocated space for Set: 131073
    //1000x1000
    //Initial allocated space for Set: 2049
    //43338435400ns taken, about 10 to the 10.636873228409462 power.
    //Post-assign allocated space for Set: 2097153
    //1000x1000 Strings
    //Initial allocated space for Set: 2049
    //868829554ns taken, about 10 to the 8.938934585404091 power.
    //Post-assign allocated space for Set: 2097153
//    private UnorderedSet<Object> theMap;
    // DoubleHashing mostly as-is, some adjustments to use a mask
    //200x200
    //Initial allocated space for Set: 2048
    //33255870ns taken, about 10 to the 7.521868313807727 power.
    //Post-assign allocated space for Set: 131072
    //1000x1000
    //Initial allocated space for Set: 2048
    //791335494ns taken, about 10 to the 8.898360645700507 power.
    //Post-assign allocated space for Set: 2097152
    // DoubleHashing with no modulus used
    //200x200
    //Initial allocated space for Set: 2048
    //21927116ns taken, about 10 to the 7.340981514137573 power.
    //Post-assign allocated space for Set: 131072 
    //1000x1000
    //Initial allocated space for Set: 2048
    //719916826ns taken, about 10 to the 8.857282324075996 power.
    //Post-assign allocated space for Set: 2097152
    //1000x1000 Strings
    //Initial allocated space for Set: 2048
    //895451080ns taken, about 10 to the 8.952041864594444 power.
    //Post-assign allocated space for Set: 2097152
//    private DoubleHashing<Object> theMap;
    // RobinHood WITHOUT better hash mixing:
    //200x200
    //Initial allocated space for Set: 2048
    //9836116046ns taken, about 10 to the 9.992823643881254 power.
    //Post-assign allocated space for Set: 131072
    //1000x1000
    //Initial allocated space for Set: 2048
    //133757218480ns taken, about 10 to the 11.126317228909627 power.
    //Post-assign allocated space for Set: 2097152
    
    // RobinHood WITH better hash mixing:
    //200x200
    //Initial allocated space for Set: 2048
    //34606440ns taken, about 10 to the 7.539156925272842 power.
    //Post-assign allocated space for Set: 131072
    //1000x1000
    //Initial allocated space for Set: 2048
    //953714426ns taken, about 10 to the 8.97941835187509 power.
    //Post-assign allocated space for Set: 2097152
    //1000x1000 Strings
    //Initial allocated space for Set: 2048
    //990291616ns taken, about 10 to the 8.995763102244613 power.
    //Post-assign allocated space for Set: 2097152
    //private RobinHood<Object> theMap;
    
//    private ObjectSet<Object> theMap;
    @Override
    public void create() {
//        theMap = new UnorderedSet<>(1024, 0.5f);
//        theMap = new DoubleHashing<>(2048);
//        theMap = new RobinHood<>(2048);
//        theMap = new HashSet<>(2048, 0.5f);
//        theMap = new ObjectSet<>(2048, 0.5f);
//        theMap = new HashMap<>(1000000, 0.5f);
//        theMap = new ObjectMap<>(1000000, 0.5f);
//        theMap = new OrderedMap<>(width * height, 0.5f);
//        theMap = new OrderedMap<>(width * height, 0.5f);
//        theMap = new HashMap<>(width * height, 0.5f);
//        theMap = new ObjectMap<>(width * height, 0.5f);
//        theMap = new DoubleHashing<>(width * height);
//        theMap = new RobinHood<>(width * height);
//        theMap = new RobinHood2<>(width * height);
//        theMap = new HashSet<>(width * height, 0.5f);
        theMap = new ObjectSet<>(width * height, 0.5f);
//        theMap = new UnorderedSet<>(width * height, 0.5f);
//        theMap = new com.badlogic.gdx.utils.ObjectMap<>(width * height, 0.5f);
//        theMap = new com.badlogic.gdx.utils.ObjectMap<>(1000000, 0.5f);
//        theMap = new ObjectMap<>(1000000, 1f - 0.015625f);
        generate();
    }

    private static long szudzik(long x, long y) {
        x = (x << 1) ^ (x >> 63);
        y = (y << 1) ^ (y >> 63);
        return (x >= y ? x * x + x + y : x + y * y);
    }

    private static void unSzudzik(long[] output, long z) {
        final long low = (long)Math.sqrt(z), lessSquare = z - low * low, x, y;
        if(lessSquare < low) { 
            x = lessSquare;
            y = low;
        }
        else {
            x = low;
            y = lessSquare - low;
        }
        output[0] = x >> 1 ^ -(x & 1L);
        output[1] = y >> 1 ^ -(y & 1L);
    }


    public void generate()
    {
//        long[] pair = new long[2];
        System.out.println("Initial allocated space for Set: Not supported");
//        System.out.println("Initial allocated space for Set: " + theMap.capacity);
        final long startTime = TimeUtils.nanoTime();
//        Mnemonic m = new Mnemonic(123456789L);
//        GridPoint2 gp = new GridPoint2(0, 0);
//        RandomXS128 random = new RandomXS128(1);
//        for (int i = 0; i < 10000000; i++) {
//            theMap.put(new com.badlogic.gdx.math.Vector2(random.nextFloat() - 0.5f, random.nextFloat() - 0.5f), i);
//        }
        for (int x = -width >> 1; x < width >> 1; x++) {
            for (int y = -height >> 1; y < height >> 1; y++) {
//                for (int z = -height; z < height; z++) {

//                long z = (x & 0xFFFFFFFFL) << 32 | (y & 0xFFFFFFFFL);
//                z =        ((z & 0x00000000ffff0000L) << 16) | ((z >>> 16) & 0x00000000ffff0000L) | (z & 0xffff00000000ffffL);
//                z =        ((z & 0x0000ff000000ff00L) << 8 ) | ((z >>> 8 ) & 0x0000ff000000ff00L) | (z & 0xff0000ffff0000ffL);
//                z =        ((z & 0x00f000f000f000f0L) << 4 ) | ((z >>> 4 ) & 0x00f000f000f000f0L) | (z & 0xf00ff00ff00ff00fL);
//                z =        ((z & 0x0c0c0c0c0c0c0c0cL) << 2 ) | ((z >>> 2 ) & 0x0c0c0c0c0c0c0c0cL) | (z & 0xc3c3c3c3c3c3c3c3L);
//                z =        ((z & 0x2222222222222222L) << 1 ) | ((z >>> 1 ) & 0x2222222222222222L) | (z & 0x9999999999999999L);
//                theMap.put(z, null);                                                   // uses 23312536 bytes of heap
//                long z = szudzik(x, y);
//                theMap.put(z, null);                                                   // uses 18331216 bytes of heap?
//                unSzudzik(pair, z);
//                theMap.put(0xC13FA9A902A6328FL * x ^ 0x91E10DA5C79E7B1DL * y, null); // uses 23312576 bytes of heap
//                theMap.put((x & 0xFFFFFFFFL) << 32 | (y & 0xFFFFFFFFL), null);       // uses 28555456 bytes of heap
//                theMap.add(new Vector2(x - width * 0.5f, y - height * 0.5f));
//                theMap.put(new Vector2((x * 0xC13FA9A9 >> 8), (y * 0x91E10DA5 >> 8)), x); // sub-random point sequence
//                theMap.put(new com.badlogic.gdx.math.Vector2(x - 2500, y - 2500), x);
//                theMap.put(new com.badlogic.gdx.math.Vector2(x, y), x);  // when using old hashCode with HashMap: 7,481,873 ns
                theMap.add(new Boom(x, y));  // when using new hashCode with HashMap: 4,799,871 ns
//                909940988ns for 1M with original hashCode
//                182242216ns for 1M with R2 hashCode, roughly 5x faster due to HashMap not having to handle collisions
//                theMap.put(new com.badlogic.gdx.math.Vector2(x - 25, y - 25), x);  // crashes out of heap with 50x50 Vector2

//                theMap.put(new GridPoint2(x - 1500, y - 1500), x); // crashes out of heap with 720 Vector2
//                    gp.set(x, y);
//                    theMap.add(gp.hashCode());
//                    long r, s;
//                    r = (x ^ 0xa0761d65L) * (y ^ 0x8ebc6af1L);
//                    s = 0xa0761d65L * (z ^ 0x589965cdL);
//                    r -= r >> 32;
//                    s -= s >> 32;
//                    r = ((r ^ s) + 0xeb44accbL) * 0xeb44acc8L;
//                    theMap.add((int)(r - (r >> 32)));

//                theMap.add(m.toMnemonic(szudzik(x, y)));
                }
            }
//        }
//                final GridPoint2 gp = new GridPoint2(x, y);
//                final int gpHash = gp.hashCode(); // uses the updated GridPoint2 hashCode(), not the current GDX code
//                theMap.put(gp, gpHash | 0xFF000000); //value doesn't matter; this was supposed to test ObjectMap
                //theMap.put(gp, (53 * 53 + x + 53 * y) | 0xFF000000); //this is what the hashCodes would look like for the current code
                
                //final int gpHash = x * 0xC13F + y * 0x91E1; // updated hashCode()
                //// In the updated hashCode(), numbers are based on the plastic constant, which is
                //// like the golden ratio but with better properties for 2D spaces. These don't need to be prime.
                
                //final int gpHash = 53 * 53 + x + 53 * y; // equivalent to current hashCode()
        long taken = TimeUtils.timeSinceNanos(startTime);
        System.out.println(taken + "ns taken, about 10 to the " + Math.log10(taken) + " power.");
//        System.out.println("Post-assign allocated space for Set: " + theMap.capacity);
        System.out.println("Post-assign allocated space for Set: Not supported");
    }

    @Override
    public void render() {
        // standard clear the background routine for libGDX
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }


    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("LibGDX Test: ObjectMap<GridPoint2, Integer> memory usage");
        config.setWindowedMode(500, 100);
        config.setIdleFPS(1);
        new Lwjgl3Application(new MapMemoryTest(), config);
    }
}