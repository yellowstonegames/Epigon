package squidpony.epigon.playground.tests;

import com.badlogic.gdx.*;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import squidpony.squidgrid.gui.gdx.*;
import squidpony.squidgrid.mapping.WorldMapGenerator;
import squidpony.squidmath.*;

/**
 * World map generator for a shuttlecock-shaped world.
 * Port of Zachary Carter's world generation technique, https://github.com/zacharycarter/mapgen
 * It seems to mostly work now, though it only generates one view of the map that it renders (but biome, moisture, heat,
 * and height maps can all be requested from it).
 */
public class ShapedWorldMapDemo extends ApplicationAdapter {
    public static class ShapedMap extends WorldMapGenerator {
        protected static final double terrainFreq = 1.65, terrainRidgedFreq = 1.8, heatFreq = 2.1, moistureFreq = 2.125, otherFreq = 3.375, riverRidgedFreq = 21.7;
        private double minHeat0 = Double.POSITIVE_INFINITY, maxHeat0 = Double.NEGATIVE_INFINITY,
                minHeat1 = Double.POSITIVE_INFINITY, maxHeat1 = Double.NEGATIVE_INFINITY,
                minWet0 = Double.POSITIVE_INFINITY, maxWet0 = Double.NEGATIVE_INFINITY;

        public final Noise.Noise3D terrain, terrainRidged, heat, moisture, otherRidged, riverRidged;
        public final double[][] xPositions,
                yPositions,
                zPositions;


        /**
         * Constructs a concrete WorldMapGenerator for a map that can be used to wrap a sphere (as with a texture on a
         * 3D model), with seamless east-west wrapping, no north-south wrapping, and distortion that causes the poles to
         * have significantly-exaggerated-in-size features while the equator is not distorted.
         * Always makes a 256x256 map.
         * Uses SeededNoise as its noise generator, with 1.0 as the octave multiplier affecting detail.
         * If you were using {@link SphereMap#SphereMap(long, int, int, Noise.Noise3D, double)}, then this would be the
         * same as passing the parameters {@code 0x1337BABE1337D00DL, 256, 256, SeededNoise.instance, 1.0}.
         */
        public ShapedMap() {
            this(0x1337BABE1337D00DL, 256, 256, SeededNoise.instance, 1.0);
        }

        /**
         * Constructs a concrete WorldMapGenerator for a map that can be used to wrap a sphere (as with a texture on a
         * 3D model), with seamless east-west wrapping, no north-south wrapping, and distortion that causes the poles to
         * have significantly-exaggerated-in-size features while the equator is not distorted.
         * Takes only the width/height of the map. The initial seed is set to the same large long
         * every time, and it's likely that you would set the seed when you call {@link #generate(long)}. The width and
         * height of the map cannot be changed after the fact, but you can zoom in.
         * Uses SeededNoise as its noise generator, with 1.0 as the octave multiplier affecting detail.
         *
         * @param mapWidth  the width of the map(s) to generate; cannot be changed later
         * @param mapHeight the height of the map(s) to generate; cannot be changed later
         */
        public ShapedMap(int mapWidth, int mapHeight) {
            this(0x1337BABE1337D00DL, mapWidth, mapHeight, SeededNoise.instance, 1.0);
        }

        /**
         * Constructs a concrete WorldMapGenerator for a map that can be used to wrap a sphere (as with a texture on a
         * 3D model), with seamless east-west wrapping, no north-south wrapping, and distortion that causes the poles to
         * have significantly-exaggerated-in-size features while the equator is not distorted.
         * Takes an initial seed and the width/height of the map. The {@code initialSeed}
         * parameter may or may not be used, since you can specify the seed to use when you call {@link #generate(long)}.
         * The width and height of the map cannot be changed after the fact, but you can zoom in.
         * Uses SeededNoise as its noise generator, with 1.0 as the octave multiplier affecting detail.
         *
         * @param initialSeed the seed for the StatefulRNG this uses; this may also be set per-call to generate
         * @param mapWidth    the width of the map(s) to generate; cannot be changed later
         * @param mapHeight   the height of the map(s) to generate; cannot be changed later
         */
        public ShapedMap(long initialSeed, int mapWidth, int mapHeight) {
            this(initialSeed, mapWidth, mapHeight, SeededNoise.instance, 1.0);
        }

        /**
         * Constructs a concrete WorldMapGenerator for a map that can be used to wrap a sphere (as with a texture on a
         * 3D model), with seamless east-west wrapping, no north-south wrapping, and distortion that causes the poles to
         * have significantly-exaggerated-in-size features while the equator is not distorted.
         * Takes an initial seed, the width/height of the map, and a noise generator (a
         * {@link Noise.Noise3D} implementation, which is usually {@link SeededNoise#instance}. The {@code initialSeed}
         * parameter may or may not be used, since you can specify the seed to use when you call
         * {@link #generate(long)}. The width and height of the map cannot be changed after the fact, but you can zoom
         * in. Currently only SeededNoise makes sense to use as the value for {@code noiseGenerator}, and the seed it's
         * constructed with doesn't matter because it will change the seed several times at different scales of noise
         * (it's fine to use the static {@link SeededNoise#instance} because it has no changing state between runs of
         * the program; it's effectively a constant). The detail level, which is the {@code octaveMultiplier} parameter
         * that can be passed to another constructor, is always 1.0 with this constructor.
         *
         * @param initialSeed      the seed for the StatefulRNG this uses; this may also be set per-call to generate
         * @param mapWidth         the width of the map(s) to generate; cannot be changed later
         * @param mapHeight        the height of the map(s) to generate; cannot be changed later
         * @param noiseGenerator   an instance of a noise generator capable of 3D noise, almost always {@link SeededNoise}
         */
        public ShapedMap(long initialSeed, int mapWidth, int mapHeight, final Noise.Noise3D noiseGenerator) {
            this(initialSeed, mapWidth, mapHeight, noiseGenerator, 1.0);
        }

        /**
         * Constructs a concrete WorldMapGenerator for a map that can be used to wrap a sphere (as with a texture on a
         * 3D model), with seamless east-west wrapping, no north-south wrapping, and distortion that causes the poles to
         * have significantly-exaggerated-in-size features while the equator is not distorted.
         * Takes an initial seed, the width/height of the map, and parameters for noise
         * generation (a {@link Noise.Noise3D} implementation, which is usually {@link SeededNoise#instance}, and a
         * multiplier on how many octaves of noise to use, with 1.0 being normal (high) detail and higher multipliers
         * producing even more detailed noise when zoomed-in). The {@code initialSeed} parameter may or may not be used,
         * since you can specify the seed to use when you call {@link #generate(long)}. The width and height of the map
         * cannot be changed after the fact, but you can zoom in. Currently only SeededNoise makes sense to use as the
         * value for {@code noiseGenerator}, and the seed it's constructed with doesn't matter because it will change the
         * seed several times at different scales of noise (it's fine to use the static {@link SeededNoise#instance} because
         * it has no changing state between runs of the program; it's effectively a constant). The {@code octaveMultiplier}
         * parameter should probably be no lower than 0.5, but can be arbitrarily high if you're willing to spend much more
         * time on generating detail only noticeable at very high zoom; normally 1.0 is fine and may even be too high for
         * maps that don't require zooming.
         * @param initialSeed the seed for the StatefulRNG this uses; this may also be set per-call to generate
         * @param mapWidth the width of the map(s) to generate; cannot be changed later
         * @param mapHeight the height of the map(s) to generate; cannot be changed later
         * @param noiseGenerator an instance of a noise generator capable of 3D noise, almost always {@link SeededNoise}
         * @param octaveMultiplier used to adjust the level of detail, with 0.5 at the bare-minimum detail and 1.0 normal
         */
        public ShapedMap(long initialSeed, int mapWidth, int mapHeight, final Noise.Noise3D noiseGenerator, double octaveMultiplier) {
            super(initialSeed, mapWidth, mapHeight);
            xPositions = new double[width][height];
            yPositions = new double[width][height];
            zPositions = new double[width][height];
            terrain = new Noise.InverseLayered3D(noiseGenerator, (int) (0.5 + octaveMultiplier * 8), terrainFreq, 0.55);
            terrainRidged = new Noise.Ridged3D(noiseGenerator, (int) (0.5 + octaveMultiplier * 10), terrainRidgedFreq);
            heat = new Noise.InverseLayered3D(noiseGenerator, (int) (0.5 + octaveMultiplier * 3), heatFreq, 0.75);
            moisture = new Noise.InverseLayered3D(noiseGenerator, (int) (0.5 + octaveMultiplier * 4), moistureFreq, 0.55);
            otherRidged = new Noise.Ridged3D(noiseGenerator, (int) (0.5 + octaveMultiplier * 6), otherFreq);
            riverRidged = new Noise.Ridged3D(noiseGenerator, (int)(0.5 + octaveMultiplier * 4), riverRidgedFreq);
        }
        protected int wrapY(final int y)  {
            return Math.max(0, Math.min(y, height - 1));
        }



        protected void regenerate(int startX, int startY, int usedWidth, int usedHeight,
                                  double waterMod, double coolMod, long state)
        {
            boolean fresh = false;
            if(cachedState != state || waterMod != waterModifier || coolMod != coolingModifier)
            {
                minHeight = Double.POSITIVE_INFINITY;
                maxHeight = Double.NEGATIVE_INFINITY;
                minHeat0 = Double.POSITIVE_INFINITY;
                maxHeat0 = Double.NEGATIVE_INFINITY;
                minHeat1 = Double.POSITIVE_INFINITY;
                maxHeat1 = Double.NEGATIVE_INFINITY;
                minHeat = Double.POSITIVE_INFINITY;
                maxHeat = Double.NEGATIVE_INFINITY;
                minWet0 = Double.POSITIVE_INFINITY;
                maxWet0 = Double.NEGATIVE_INFINITY;
                minWet = Double.POSITIVE_INFINITY;
                maxWet = Double.NEGATIVE_INFINITY;
                cachedState = state;
                fresh = true;
            }
            rng.setState(state);
            long seedA = rng.nextLong(), seedB = rng.nextLong(), seedC = rng.nextLong();
            int t;

            waterModifier = (waterMod <= 0) ? rng.nextDouble(0.29) + 0.91 : waterMod;
            coolingModifier = (coolMod <= 0) ? rng.nextDouble(0.45) * (rng.nextDouble()-0.5) + 1.1 : coolMod;

            double p,
                    ps, pc,
                    qs, qc,
                    h, temp,
                    i_w = 6.283185307179586 / width, i_h = (3.141592653589793) / (height+2.0),
                    xPos = startX, yPos, i_uw = usedWidth / (double)width, i_uh = usedHeight / (height+2.0);
            final double[] trigTable = new double[width << 1];
            for (int x = 0; x < width; x++, xPos += i_uw) {
                p = xPos * i_w;
                trigTable[x<<1]   = Math.sin(p);
                trigTable[x<<1|1] = Math.cos(p);
            }
            yPos = startY + i_uh;
            for (int y = 0; y < height; y++, yPos += i_uh) {
                qs = yPos * i_h; //- 1.5707963267948966
//                qc = Math.cos(qs);
//                qs = Math.sin(qs);
                qc = Math.sin(qs);
                qs = Math.cos(qs);
                qs = (3.375 * qs * (0.625 + qs)) / (3 + qc * qc);
                //qs = Math.sin(qs);
                for (int x = 0, xt = 0; x < width; x++) {
                    ps = trigTable[xt++] * qc;//Math.sin(p);
                    pc = trigTable[xt++] * qc;//Math.cos(p);
                    xPositions[x][y] = pc;
                    yPositions[x][y] = ps;
                    zPositions[x][y] = qs;
                    h = terrain.getNoiseWithSeed(pc +
                                    terrainRidged.getNoiseWithSeed(pc, ps, qs,seedA + seedB),
                            ps, qs, seedA);
                    h *= waterModifier;
                    heightData[x][y] = h;
                    heatData[x][y] = (p = heat.getNoiseWithSeed(pc, ps
                                    + otherRidged.getNoiseWithSeed(pc, ps, qs,seedB + seedC)
                            , qs, seedB));
                    moistureData[x][y] = (temp = moisture.getNoiseWithSeed(pc, ps, qs
                                    + otherRidged.getNoiseWithSeed(pc, ps, qs, seedC + seedA)
                            , seedC));
                    freshwaterData[x][y] = (ps = Math.min(
                            NumberTools.sway(riverRidged.getNoiseWithSeed(pc * 0.46, ps * 0.46, qs * 0.46, seedC - seedA - seedB) + 0.38),
                            NumberTools.sway( riverRidged.getNoiseWithSeed(pc, ps, qs, seedC - seedA - seedB) + 0.5))) * ps * ps * 45.42;
                    minHeightActual = Math.min(minHeightActual, h);
                    maxHeightActual = Math.max(maxHeightActual, h);
                    if(fresh) {
                        minHeight = Math.min(minHeight, h);
                        maxHeight = Math.max(maxHeight, h);

                        minHeat0 = Math.min(minHeat0, p);
                        maxHeat0 = Math.max(maxHeat0, p);

                        minWet0 = Math.min(minWet0, temp);
                        maxWet0 = Math.max(maxWet0, temp);
                    }
                }
                minHeightActual = Math.min(minHeightActual, minHeight);
                maxHeightActual = Math.max(maxHeightActual, maxHeight);

            }
            double heightDiff = 2.0 / (maxHeightActual - minHeightActual),
                    heatDiff = 0.8 / (maxHeat0 - minHeat0),
                    wetDiff = 1.0 / (maxWet0 - minWet0),
                    hMod,
                    halfHeight = (height - 1) * 0.5, i_half = 1.0 / halfHeight;
            double minHeightActual0 = minHeightActual;
            double maxHeightActual0 = maxHeightActual;
            yPos = startY + i_uh;
            ps = Double.POSITIVE_INFINITY;
            pc = Double.NEGATIVE_INFINITY;

            for (int y = 0; y < height; y++, yPos += i_uh) {
                temp = Math.abs(yPos - halfHeight) * i_half;
                temp *= (2.4 - temp);
                temp = 2.2 - temp;
                for (int x = 0; x < width; x++) {
                    heightData[x][y] = (h = (heightData[x][y] - minHeightActual) * heightDiff - 1.0);
                    minHeightActual0 = Math.min(minHeightActual0, h);
                    maxHeightActual0 = Math.max(maxHeightActual0, h);
                    heightCodeData[x][y] = (t = codeHeight(h));
                    hMod = 1.0;
                    switch (t) {
                        case 0:
                        case 1:
                        case 2:
                        case 3:
                            h = 0.4;
                            hMod = 0.2;
                            break;
                        case 6:
                            h = -0.1 * (h - forestLower - 0.08);
                            break;
                        case 7:
                            h *= -0.25;
                            break;
                        case 8:
                            h *= -0.4;
                            break;
                        default:
                            h *= 0.05;
                    }
                    heatData[x][y] = (h = (((heatData[x][y] - minHeat0) * heatDiff * hMod) + h + 0.6) * temp);
                    if (fresh) {
                        ps = Math.min(ps, h); //minHeat0
                        pc = Math.max(pc, h); //maxHeat0
                    }
                }
            }
            if(fresh)
            {
                minHeat1 = ps;
                maxHeat1 = pc;
            }
            heatDiff = coolingModifier / (maxHeat1 - minHeat1);
            qs = Double.POSITIVE_INFINITY;
            qc = Double.NEGATIVE_INFINITY;
            ps = Double.POSITIVE_INFINITY;
            pc = Double.NEGATIVE_INFINITY;


            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    heatData[x][y] = (h = ((heatData[x][y] - minHeat1) * heatDiff));
                    moistureData[x][y] = (temp = (moistureData[x][y] - minWet0) * wetDiff);
                    if (fresh) {
                        qs = Math.min(qs, h);
                        qc = Math.max(qc, h);
                        ps = Math.min(ps, temp);
                        pc = Math.max(pc, temp);
                    }
                }
            }
            if(fresh)
            {
                minHeat = qs;
                maxHeat = qc;
                minWet = ps;
                maxWet = pc;
            }
            landData.refill(heightCodeData, 4, 999);
        }
    }



    public static final int
        Desert                 = 0 ,
        Savanna                = 1 ,
        TropicalRainforest     = 2 ,
        Grassland              = 3 ,
        Woodland               = 4 ,
        SeasonalForest         = 5 ,
        TemperateRainforest    = 6 ,
        BorealForest           = 7 ,
        Tundra                 = 8 ,
        Ice                    = 9 ,
        Beach                  = 10,
        Rocky                  = 11,
        River                  = 12;

    private static final int width = 314 * 5, height = 500;
    //private static final int width = 650, height = 650;

    private SpriteBatch batch;
    private SquidPanel display;//, overlay;
    private static final int cellWidth = 1, cellHeight = 1;
    private SquidInput input;
    private Stage stage;
    private Viewport view;
    private StatefulRNG rng;
    private long seed;
    private ShapedMap world;
    private final double[][] shadingData = new double[width][height];
    private final int[][]
            heatCodeData = new int[width][height],
            moistureCodeData = new int[width][height],
            biomeUpperCodeData = new int[width][height],
            biomeLowerCodeData = new int[width][height];
    private Noise.Noise4D cloudNoise;
    //private final float[][][] cloudData = new float[128][128][128];
    private long counter = 0;
    private boolean cloudy = false;
    private long ttg = 0; // time to generate
    public static final double
            coldestValueLower = 0.0,   coldestValueUpper = 0.15, // 0
            colderValueLower = 0.15,   colderValueUpper = 0.31,  // 1
            coldValueLower = 0.31,     coldValueUpper = 0.5,     // 2
            warmValueLower = 0.5,      warmValueUpper = 0.69,     // 3
            warmerValueLower = 0.69,    warmerValueUpper = 0.85,   // 4
            warmestValueLower = 0.85,   warmestValueUpper = 1.0,  // 5

            driestValueLower = 0.0,    driestValueUpper  = 0.27, // 0
            drierValueLower = 0.27,    drierValueUpper   = 0.4,  // 1
            dryValueLower = 0.4,       dryValueUpper     = 0.6,  // 2
            wetValueLower = 0.6,       wetValueUpper     = 0.8,  // 3
            wetterValueLower = 0.8,    wetterValueUpper  = 0.9,  // 4
            wettestValueLower = 0.9,   wettestValueUpper = 1.0;  // 5

    private static float black = SColor.floatGetI(0, 0, 0),
            white = SColor.floatGet(0xffffffff);
    // Biome map colors

    private static float ice = SColor.ALICE_BLUE.toFloatBits();
    private static float darkIce = SColor.lerpFloatColors(ice, black, 0.15f);
    private static float lightIce = white;

    private static float desert = SColor.floatGetI(248, 229, 180);
    private static float darkDesert = SColor.lerpFloatColors(desert, black, 0.15f);

    private static float savanna = SColor.floatGetI(181, 200, 100);
    private static float darkSavanna = SColor.lerpFloatColors(savanna, black, 0.15f);

    private static float tropicalRainforest = SColor.floatGetI(66, 123, 25);
    private static float darkTropicalRainforest = SColor.lerpFloatColors(tropicalRainforest, black, 0.15f);

    private static float tundra = SColor.floatGetI(151, 175, 159);
    private static float darkTundra = SColor.lerpFloatColors(tundra, black, 0.15f);

    private static float temperateRainforest = SColor.floatGetI(54, 113, 60);
    private static float darkTemperateRainforest = SColor.lerpFloatColors(temperateRainforest, black, 0.15f);

    private static float grassland = SColor.floatGetI(169, 185, 105);
    private static float darkGrassland = SColor.lerpFloatColors(grassland, black, 0.15f);

    private static float seasonalForest = SColor.floatGetI(100, 158, 75);
    private static float darkSeasonalForest = SColor.lerpFloatColors(seasonalForest, black, 0.15f);

    private static float borealForest = SColor.floatGetI(75, 105, 45);
    private static float darkBorealForest = SColor.lerpFloatColors(borealForest, black, 0.15f);

    private static float woodland = SColor.floatGetI(122, 170, 90);
    private static float darkWoodland = SColor.lerpFloatColors(woodland, black, 0.15f);

    private static float rocky = SColor.floatGetI(171, 175, 145);
    private static float darkRocky = SColor.lerpFloatColors(rocky, black, 0.15f);

    private static float beach = SColor.floatGetI(255, 235, 180);
    private static float darkBeach = SColor.lerpFloatColors(beach, black, 0.15f);

    // water colors
    private static float deepColor = SColor.floatGetI(0, 68, 128);
    private static float darkDeepColor = SColor.lerpFloatColors(deepColor, black, 0.15f);
    private static float mediumColor = SColor.floatGetI(0, 89, 159);
    private static float darkMediumColor = SColor.lerpFloatColors(mediumColor, black, 0.15f);
    private static float shallowColor = SColor.floatGetI(0, 123, 167);
    private static float darkShallowColor = SColor.lerpFloatColors(shallowColor, black, 0.15f);
    private static float coastalColor = SColor.lerpFloatColors(shallowColor, white, 0.3f);
    private static float darkCoastalColor = SColor.lerpFloatColors(coastalColor, black, 0.15f);
    private static float foamColor = SColor.floatGetI(61,  162, 215);
    private static float darkFoamColor = SColor.lerpFloatColors(foamColor, black, 0.15f);

    private static float iceWater = SColor.floatGetI(210, 255, 252);
    private static float coldWater = mediumColor;
    private static float riverWater = shallowColor;

    private static float riverColor = SColor.floatGetI(30, 120, 200);
    private static float sandColor = SColor.floatGetI(240, 240, 64);
    private static float grassColor = SColor.floatGetI(50, 220, 20);
    private static float forestColor = SColor.floatGetI(16, 160, 0);
    private static float rockColor = SColor.floatGetI(177, 167, 157);
    private static float snowColor = SColor.floatGetI(255, 255, 255);

    // Heat map colors
    private static float coldest = SColor.floatGetI(0, 255, 255);
    private static float colder = SColor.floatGetI(170, 255, 255);
    private static float cold = SColor.floatGetI(0, 229, 133);
    private static float warm = SColor.floatGetI(255, 255, 100);
    private static float warmer = SColor.floatGetI(255, 100, 0);
    private static float warmest = SColor.floatGetI(241, 12, 0);

    // Moisture map colors
    private static float dryest = SColor.floatGetI(255, 139, 17);
    private static float dryer = SColor.floatGetI(245, 245, 23);
    private static float dry = SColor.floatGetI(80, 255, 0);
    private static float wet = SColor.floatGetI(85, 255, 255);
    private static float wetter = SColor.floatGetI(20, 70, 255);
    private static float wettest = SColor.floatGetI(0, 0, 100);

    private static float cloudFull = SColor.floatGet(0xffffffff);

    private static float[] biomeColors = {
            desert,
            savanna,
            tropicalRainforest,
            grassland,
            woodland,
            seasonalForest,
            temperateRainforest,
            borealForest,
            tundra,
            ice,
            beach,
            rocky,
            foamColor//SColor.floatGetI(255, 40, 80)
    }, biomeDarkColors = {
            darkDesert,
            darkSavanna,
            darkTropicalRainforest,
            darkGrassland,
            darkWoodland,
            darkSeasonalForest,
            darkTemperateRainforest,
            darkBorealForest,
            darkTundra,
            darkIce,
            darkBeach,
            darkRocky,
            darkFoamColor//SColor.floatGetI(225, 10, 20)
    };

    protected final static float[] BIOME_TABLE = {
        //COLDEST   //COLDER      //COLD               //HOT                     //HOTTER                 //HOTTEST
        Ice+0.7f,   Ice+0.65f,    Grassland+0.9f,      Desert+0.75f,             Desert+0.8f,             Desert+0.85f,            //DRYEST
        Ice+0.6f,   Tundra+0.9f,  Grassland+0.6f,      Grassland+0.3f,           Desert+0.65f,            Desert+0.7f,             //DRYER
        Ice+0.5f,   Tundra+0.7f,  Woodland+0.4f,       Woodland+0.6f,            Savanna+0.8f,           Desert+0.6f,              //DRY
        Ice+0.4f,   Tundra+0.5f,  SeasonalForest+0.3f, SeasonalForest+0.5f,      Savanna+0.6f,            Savanna+0.4f,            //WET
        Ice+0.2f,   Tundra+0.3f,  BorealForest+0.35f,  TemperateRainforest+0.4f, TropicalRainforest+0.6f, Savanna+0.2f,            //WETTER
        Ice+0.0f,   BorealForest, BorealForest+0.15f,  TemperateRainforest+0.2f, TropicalRainforest+0.4f, TropicalRainforest+0.2f, //WETTEST
        Rocky+0.9f, Rocky+0.6f,   Beach+0.4f,          Beach+0.55f,              Beach+0.75f,             Beach+0.9f,              //COASTS
        Ice+0.3f,   River+0.8f,   River+0.7f,          River+0.6f,               River+0.5f,              River+0.4f,              //RIVERS
        Ice+0.2f,   River+0.7f,   River+0.6f,          River+0.5f,               River+0.4f,              River+0.3f,              //LAKES
    }, BIOME_COLOR_TABLE = new float[54], BIOME_DARK_COLOR_TABLE = new float[54];

    static {
        float b, diff;
        for (int i = 0; i < 54; i++) {
            b = BIOME_TABLE[i];
            diff = ((b % 1.0f) - 0.48f) * 0.27f;
            BIOME_COLOR_TABLE[i] = (b = (diff >= 0)
                    ? SColor.lerpFloatColors(biomeColors[(int)b], white, diff)
                    : SColor.lerpFloatColors(biomeColors[(int)b], black, -diff));
            BIOME_DARK_COLOR_TABLE[i] = SColor.lerpFloatColors(b, black, 0.08f);
        }
    }

    protected void makeBiomes() {
        final WorldMapGenerator world = this.world;
        final int[][] heightCodeData = world.heightCodeData;
        final double[][] heatData = world.heatData, moistureData = world.moistureData, heightData = world.heightData;
        int hc, mc, heightCode;
        double hot, moist, high, i_hot = 1.0 / this.world.maxHeat, fresh;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {

                heightCode = heightCodeData[x][y];
                hot = heatData[x][y];
                moist = moistureData[x][y];
                high = heightData[x][y];
                fresh = world.freshwaterData[x][y];
                boolean isLake = world.generateRivers && heightCode >= 4 && fresh > 0.65 && fresh + moist * 2.35 > 2.75,//world.partialLakeData.contains(x, y) && heightCode >= 4,
                        isRiver = world.generateRivers && !isLake && heightCode >= 4 && fresh > 0.55 && fresh + moist * 2.2 > 2.15;//world.partialRiverData.contains(x, y) && heightCode >= 4;
                if (moist >= (wettestValueUpper - (wetterValueUpper - wetterValueLower) * 0.2)) {
                    mc = 5;
                } else if (moist >= (wetterValueUpper - (wetValueUpper - wetValueLower) * 0.2)) {
                    mc = 4;
                } else if (moist >= (wetValueUpper - (dryValueUpper - dryValueLower) * 0.2)) {
                    mc = 3;
                } else if (moist >= (dryValueUpper - (drierValueUpper - drierValueLower) * 0.2)) {
                    mc = 2;
                } else if (moist >= (drierValueUpper - (driestValueUpper) * 0.2)) {
                    mc = 1;
                } else {
                    mc = 0;
                }

                if (hot >= (warmestValueUpper - (warmerValueUpper - warmerValueLower) * 0.2) * i_hot) {
                    hc = 5;
                } else if (hot >= (warmerValueUpper - (warmValueUpper - warmValueLower) * 0.2) * i_hot) {
                    hc = 4;
                } else if (hot >= (warmValueUpper - (coldValueUpper - coldValueLower) * 0.2) * i_hot) {
                    hc = 3;
                } else if (hot >= (coldValueUpper - (colderValueUpper - colderValueLower) * 0.2) * i_hot) {
                    hc = 2;
                } else if (hot >= (colderValueUpper - (coldestValueUpper) * 0.2) * i_hot) {
                    hc = 1;
                } else {
                    hc = 0;
                }

                heatCodeData[x][y] = hc;
                moistureCodeData[x][y] = mc;
                biomeUpperCodeData[x][y] = isLake ? hc + 48 : (isRiver ? hc + 42 : ((heightCode == 4) ? hc + 36 : hc + mc * 6));

                if (moist >= (wetterValueUpper + (wettestValueUpper - wettestValueLower) * 0.2)) {
                    mc = 5;
                } else if (moist >= (wetValueUpper + (wetterValueUpper - wetterValueLower) * 0.2)) {
                    mc = 4;
                } else if (moist >= (dryValueUpper + (wetValueUpper - wetValueLower) * 0.2)) {
                    mc = 3;
                } else if (moist >= (drierValueUpper + (dryValueUpper - dryValueLower) * 0.2)) {
                    mc = 2;
                } else if (moist >= (driestValueUpper + (drierValueUpper - drierValueLower) * 0.2)) {
                    mc = 1;
                } else {
                    mc = 0;
                }

                if (hot >= (warmerValueUpper + (warmestValueUpper - warmestValueLower) * 0.2) * i_hot) {
                    hc = 5;
                } else if (hot >= (warmValueUpper + (warmerValueUpper - warmerValueLower) * 0.2) * i_hot) {
                    hc = 4;
                } else if (hot >= (coldValueUpper + (warmValueUpper - warmValueLower) * 0.2) * i_hot) {
                    hc = 3;
                } else if (hot >= (colderValueUpper + (coldValueUpper - coldValueLower) * 0.2) * i_hot) {
                    hc = 2;
                } else if (hot >= (coldestValueUpper + (colderValueUpper - colderValueLower) * 0.2) * i_hot) {
                    hc = 1;
                } else {
                    hc = 0;
                }

                biomeLowerCodeData[x][y] = hc + mc * 6;

                if (isRiver || isLake)
                    shadingData[x][y] = //((moist - minWet) / (maxWet - minWet)) * 0.45 + 0.15 - 0.14 * ((hot - minHeat) / (maxHeat - minHeat))
                            (moist * 0.35 + 0.6);
                else
                    shadingData[x][y] = //(upperProximityH + upperProximityM - lowerProximityH - lowerProximityM) * 0.1 + 0.2
                            (heightCode == 4) ? (0.18 - high) / (0.08) :
                                    NumberTools.bounce((high + moist) * (4.1 + high - hot)) * 0.5 + 0.5; // * (7.5 + moist * 1.9 - hot * 0.9)
            }
        }
        long seedA = LightRNG.determine(seed),
                seedB = LightRNG.determine(seed + seedA),
                seedC = LightRNG.determine(seed + seedA + seedB);
        counter = LightRNG.determine(seed + seedA + seedB + seedC) >>> 48;
        //Noise.seamless3D(cloudData, seedC, 3);
    }


    @Override
    public void create() {
        batch = new SpriteBatch();
        display = new SquidPanel(width, height, new TextCellFactory().includedFont().width(cellWidth).height(cellHeight).initBySize());
        view = new StretchViewport(width*cellWidth, height*cellHeight);
        stage = new Stage(view, batch);
        seed = 0xDEBACL;
        rng = new StatefulRNG(seed);
        world = new ShapedMap(seed, width, height, WhirlingNoise.instance, 0.7);
        //cloudNoise = new Noise.Turbulent4D(WhirlingNoise.instance, new Noise.Ridged4D(SeededNoise.instance, 2, 3.7), 3, 5.9);
        cloudNoise = new Noise.Layered4D(WhirlingNoise.instance, 2, 3.2);
        //cloudNoise2 = new Noise.Ridged4D(SeededNoise.instance, 3, 6.5);
        //world = new WorldMapGenerator.TilingMap(seed, width, height, WhirlingNoise.instance, 0.9);
        input = new SquidInput(new SquidInput.KeyHandler() {
            @Override
            public void handle(char key, boolean alt, boolean ctrl, boolean shift) {
                switch (key) {
                    case SquidInput.ENTER:
                        seed = rng.nextLong();
                        generate(seed);
                        rng.setState(seed);
                        break;
                    case '=':
                    case '+':
                        zoomIn();
                        break;
                    case '-':
                    case '_':
                        zoomOut();
                        break;
                    case 'C':
                    case 'c':
                        cloudy = !cloudy;
                        break;
                    case 'W':
                    case 'w':
                        world = new ShapedMap(seed, width, height, WhirlingNoise.instance, 0.9);
                        seed = rng.nextLong();
                        generate(seed);
                        rng.setState(seed);
                        break;
                    case 'S':
                    case 's':
                        world = new ShapedMap(seed, width, height, SeededNoise.instance, 1.0);
                        seed = rng.nextLong();
                        generate(seed);
                        rng.setState(seed);
                        break;
                    case 'Q':
                    case 'q':
                    case SquidInput.ESCAPE: {
                        Gdx.app.exit();
                    }
                }
                Gdx.graphics.requestRendering();
            }
        }, new SquidMouse(1, 1, width, height, 0, 0, new InputAdapter()
        {
            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                if(button == Input.Buttons.RIGHT)
                {
                    zoomOut(screenX, screenY);
                    Gdx.graphics.requestRendering();
                }
                else
                {
                    zoomIn(screenX, screenY);
                    Gdx.graphics.requestRendering();
                }
                return true;
            }
        }));
        generate(seed);
        rng.setState(seed);
        Gdx.input.setInputProcessor(input);
        display.setPosition(0, 0);
        stage.addActor(display);
        Gdx.graphics.setContinuousRendering(true);
        Gdx.graphics.requestRendering();
    }

    public void zoomIn() {
        zoomIn(width >> 1, height >> 1);
    }
    public void zoomIn(int zoomX, int zoomY)
    {
        long startTime = System.currentTimeMillis();
        world.zoomIn(1, zoomX, zoomY);
        makeBiomes();
        ttg = System.currentTimeMillis() - startTime;
    }
    public void zoomOut()
    {
        zoomOut(width>>1, height>>1);
    }
    public void zoomOut(int zoomX, int zoomY)
    {
        long startTime = System.currentTimeMillis();
        world.zoomOut(1, zoomX, zoomY);
        makeBiomes();
        ttg = System.currentTimeMillis() - startTime;
    }
    public void generate(final long seed)
    {
        long startTime = System.currentTimeMillis();
        world.generate(seed);
        makeBiomes();
        ttg = System.currentTimeMillis() - startTime;
    }

    public void putMap() {
        // uncomment next line to generate maps as quickly as possible
        //generate(rng.nextLong());
        int hc, tc;
        int[][] heightCodeData = world.heightCodeData;
        double[][] heightData = world.heightData;
        double xp, yp, zp;
        float cloud = 0f, shown, cloudLight = 1f;
        for (int y = 0; y < height; y++) {
            PER_CELL:
            for (int x = 0; x < width; x++) {
                hc = heightCodeData[x][y];
                tc = heatCodeData[x][y];
                //cloud = (float) cloudNoise2.getNoiseWithSeed(xp = world.xPositions[x][y], yp = world.yPositions[x][y],
                //        zp = world.zPositions[x][y], counter * 0.04, (int) seed) * 0.06f;
                if(cloudy) {
                    cloud = (float) Math.min(1f, (cloudNoise.getNoiseWithSeed(world.xPositions[x][y], world.yPositions[x][y], world.zPositions[x][y], counter * 0.0125, seed) * (0.75 + world.moistureData[x][y]) - 0.07));
                    cloudLight = 0.65f + NumberTools.swayTight(cloud * 1.4f + 0.55f) * 0.35f;
                    cloudLight = SColor.floatGet(cloudLight, cloudLight, cloudLight, 1f);
                }
                /*
                cloud = Math.min(1f,
                        cloudData[(int) (world.xPositions[x][y] * 109 + counter * 1.7) & 127]
                        [(int) (world.yPositions[x][y] * 109) & 127]
                        [(int) (world.zPositions[x][y] * 109) & 127] * 1.1f +
                        cloudData[(int) (world.xPositions[x][y] * 119) & 127]
                                [(int) (world.yPositions[x][y] * 119 + counter * 1.7) & 127]
                                [(int) (world.zPositions[x][y] * 119) & 127] * 1.4f);
                cloudLight = Math.min(1f, 1f +
                        cloudData[(int) (world.xPositions[x][y] * 233) & 127]
                                [(int) (world.yPositions[x][y] * 233) & 127]
                                [(int) (world.zPositions[x][y] * 233 + counter * 2.3) & 127] * 0.64f);
                cloudLight = SColor.floatGet(cloudLight, cloudLight, cloudLight, 1f);
                                */

                if(tc == 0)
                {
                    switch (hc)
                    {
                        case 0:
                        case 1:
                        case 2:
                        case 3:
                            shown = SColor.lerpFloatColors(shallowColor, ice,
                                    (float) ((heightData[x][y] - -1.0) / (0.1 - -1.0)));
                            if(cloud > 0.0)
                                shown = SColor.lerpFloatColors(shown, cloudLight, cloud);
                            display.put(x, y, shown);
                            continue PER_CELL;
                        case 4:
                            shown = SColor.lerpFloatColors(lightIce, ice,
                                    (float) ((heightData[x][y] - 0.1) / (0.18 - 0.1)));
                            if(cloud > 0.0)
                                shown = SColor.lerpFloatColors(shown, cloudLight, cloud);
                            display.put(x, y, shown);
                            continue PER_CELL;
                    }
                }
                switch (hc) {
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                        shown = SColor.lerpFloatColors(deepColor, coastalColor,
                                (float) ((heightData[x][y] - -1.0) / (0.1 - -1.0)));
                        if(cloud > 0.0)
                            shown = SColor.lerpFloatColors(shown, cloudLight, cloud);
                        display.put(x, y, shown);
                        break;
                    default:
                        /*
                        if(partialLakeData.contains(x, y))
                            System.out.println("LAKE  x=" + x + ",y=" + y + ':' + (((heightData[x][y] - lowers[hc]) / (differences[hc])) * 19
                                    + shadingData[x][y] * 13) * 0.03125f);
                        else if(partialRiverData.contains(x, y))
                            System.out.println("RIVER x=" + x + ",y=" + y + ':' + (((heightData[x][y] - lowers[hc]) / (differences[hc])) * 19
                                    + shadingData[x][y] * 13) * 0.03125f);
                        */
                        shown = SColor.lerpFloatColors(BIOME_COLOR_TABLE[biomeLowerCodeData[x][y]],
                                BIOME_DARK_COLOR_TABLE[biomeUpperCodeData[x][y]],
                                (float) shadingData[x][y]);
                        if(cloud > 0.0)
                            shown = SColor.lerpFloatColors(shown, cloudLight, cloud);
                        display.put(x, y, shown);

                        //display.put(x, y, SColor.lerpFloatColors(darkTropicalRainforest, desert, (float) (heightData[x][y])));
                }
            }
        }
    }
    @Override
    public void render() {
        // standard clear the background routine for libGDX
        //Gdx.gl.glClearColor(0f, 0f, 0f, 1.0f);
        //Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glDisable(GL20.GL_BLEND);
        // need to display the map every frame, since we clear the screen to avoid artifacts.
        putMap();
        ++counter;
        Gdx.graphics.setTitle("Map! Took " + ttg + " ms to generate");

        // if we are waiting for the player's input and get input, process it.
        if (input.hasNext()) {
            input.next();
        }
        // stage has its own batch and must be explicitly told to draw().
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        view.update(width, height, true);
        view.apply(true);
    }

    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "SquidLib Demo: Detailed World Map";
        config.width = width * cellWidth;
        config.height = height * cellHeight;
        config.foregroundFPS = 60;
        //config.fullscreen = true;
        config.backgroundFPS = 5;
        config.addIcon("libgdx16.png", Files.FileType.Internal);
        config.addIcon("libgdx32.png", Files.FileType.Internal);
        config.addIcon("libgdx64.png", Files.FileType.Internal);
        config.addIcon("libgdx128.png", Files.FileType.Internal);
        new LwjglApplication(new ShapedWorldMapDemo(), config);
    }
}