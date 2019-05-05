package squidpony.epigon.mapping;

import squidpony.epigon.data.Physical;
import squidpony.epigon.data.RecipeMixer;
import squidpony.epigon.data.WeightedTableWrapper;
import squidpony.epigon.data.quality.Inclusion;
import squidpony.epigon.data.quality.Stone;
import squidpony.epigon.data.quality.Tree;
import squidpony.epigon.data.quality.Vegetable;
import squidpony.epigon.data.trait.Grouping;
import squidpony.epigon.data.control.HandBuilt;
import squidpony.squidgrid.Direction;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidgrid.mapping.DenseRoomMapGenerator;
import squidpony.squidgrid.mapping.DungeonGenerator;
import squidpony.squidgrid.mapping.FlowingCaveGenerator;
import squidpony.squidgrid.mapping.SerpentMapGenerator;
import squidpony.squidgrid.mapping.styled.TilesetType;
import squidpony.squidmath.*;

import java.util.Arrays;
import java.util.stream.Collectors;
import squidpony.epigon.data.control.DataPool;

/**
 * Creates a world.
 *
 * @author Eben Howard - http://squidpony.com
 */
public class WorldGenerator {

    private static final int maxRecurse = 10;
    private EpiMap[] world;
    private int width, height, depth;
    private HandBuilt handBuilt;
    private StatefulRNG rng;

    public WorldGenerator(HandBuilt handBuilt) {
        this.handBuilt = handBuilt;
    }

    public EpiMap buildDive(int width, int depth) {
        world = buildWorld(width, 6, depth);

        this.width = width;
        this.height = depth + World.DIVE_HEADER.length;
        this.depth = 1;
        EpiMap map = new EpiMap(width, height);
        GreasedRegion safeSpots = new GreasedRegion(width, height);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < depth; y++) {
                map.contents[x][y + World.DIVE_HEADER.length] = world[y].contents[x][0];
            }
        }

        // Add in dive header
        for (int x = 0; x < World.DIVE_HEADER[0].length(); x++) {
            for (int y = 0; y < World.DIVE_HEADER.length; y++) {
                char c = World.DIVE_HEADER[y].charAt(x);
                switch (c) {
                    case ' ':
                        map.contents[x][y] = new EpiTile(handBuilt.emptySpace);
                        break;
                    case '$':
                        map.contents[x][y] = new EpiTile(handBuilt.emptySpace);
                        map.contents[x][y].add(handBuilt.money);
                        break;
                    default:
                        Physical p = new Physical();
                        p.symbol = c;
                        p.color = SColor.SCARLET.toFloatBits();
                        p.blocking = true;
                        map.contents[x][y] = new EpiTile(p);
                        break;
                }
            }
        }

        int centerGap = width / 2;
        int gapSize = (int) (width * 0.4);
        long seed1 = handBuilt.rng.nextLong() + System.nanoTime(),
            seed2 = handBuilt.rng.nextLong() + seed1,
            seed3 = handBuilt.rng.nextLong() + seed2 ^ seed1;
        final double portionGapSize = 0.08 * width, offGapSize = 0.12 * width,
            halfWidth = 0.5 * width, centerOff = 0.135 * width, extraWiggle = 0.02 * width;
        for (int level = World.DIVE_HEADER.length; level < height; level++) {
            for (int x = centerGap - gapSize; x < centerGap + gapSize; x++) {
                map.contents[x][level].floor = handBuilt.emptySpace;
                map.contents[x][level].blockage = null;
                safeSpots.insert(x, level);
            }
            // Basic1D noise is more wobbly, with small changes frequently and frequent (cyclical) major changes
            gapSize = (int) (Noise.Basic1D.noise(level * 0.17, seed1) * portionGapSize + offGapSize
                + NumberTools.randomFloatCurved(seed3 * (level + seed2)) * extraWiggle);
            // swayRandomized spends a little more time at extremes before shifting suddenly to a very different value
            centerGap = (int) ((NumberTools.swayRandomized(seed2, level * 0.08) + NumberTools.swayRandomized(seed3, level * 0.135)) * centerOff + halfWidth);
            centerGap = Math.max(centerGap, gapSize / 2 + 1); // make sure it's not off the left side
            centerGap = Math.min(centerGap, width - gapSize / 2 - 1); // make sure it's not off the right side
        }
        rng = new StatefulRNG(new DiverRNG(handBuilt.rng.nextLong() ^ seed3));
        safeSpots.retract(2).randomScatter(rng, 8);

        Inclusion[] inclusions = Inclusion.values();
        Physical[] contents = new Physical[inclusions.length + 1];
        double[] weights = new double[inclusions.length + 1];
        for (int i = 0; i < inclusions.length; i++) {
            Physical gem = RecipeMixer.buildPhysical(inclusions[i]);
            gem.symbol = '♦';
            gem.groupingData = new Grouping(1);
            contents[i] = gem;
            weights[i] = rng.between(1.0, 3.0);
        }
        contents[inclusions.length] = handBuilt.money;
        weights[inclusions.length] = inclusions.length * 3.25;
        WeightedTableWrapper<Physical> table = new WeightedTableWrapper<>(rng.nextLong(), contents, weights);

        for (Coord cash : safeSpots) {
            if (map.contents[cash.x][cash.y].blockage == null) {
                map.contents[cash.x][cash.y].add(table.random());
            }
        }

        // Close off bottom with "goal"
        Physical goal = new Physical();
        goal.color = SColor.GOLDEN.toFloatBits();
        goal.symbol = '♥';
        goal.blocking = false;
        goal.unique = true; // misusing this intentionally to mark special "objects"
        for (int x = 0; x < width; x++) {
            map.contents[x][height - 2].floor = goal;
            map.contents[x][height - 2].add(RecipeMixer.buildPhysical(goal));
            map.contents[x][height - 2].blockage = null;
            map.contents[x][height - 1].floor = goal;
            map.contents[x][height - 1].add(RecipeMixer.buildPhysical(goal));
            map.contents[x][height - 1].blockage = null;
        }

        return map;
    }

    public EpiMap[] buildWorld(int width, int height, int depth) {
        init(width, height, depth, handBuilt);
//        placeMinerals();
//        faultMap();
//        bubbleMap(false);
//        extrudeMap();
//        faultMap();
//        bubbleMap(false);
//        intrudeMap();
//        metamorphoseMap();

        noiseMap();

        makeSolid();

        EpiTile tile;
        GreasedRegion[] floorWorld = new GreasedRegion[depth];
        GreasedRegion tmp = new GreasedRegion(width, height);

        for (int e = 0; e < depth; e++) {
            EpiMap eMap = world[e];
            DungeonGenerator gen = new DungeonGenerator(width, height, rng);
            // create vertical "zones" for types of generation
            if (e < 2) {
                DenseRoomMapGenerator dense = new DenseRoomMapGenerator(width, height, rng);
                gen.addDoors(80, true);
                gen.generate(dense.generate());
            } else if (e < 4) {
                FlowingCaveGenerator flowing = new FlowingCaveGenerator(width, height, TilesetType.DEFAULT_DUNGEON, rng);
                gen.addBoulders(8);
                gen.addWater(14, 4);
                gen.addGrass(17);
                gen.generate(flowing.generate());
            } else {
                SerpentMapGenerator serpent = new SerpentMapGenerator(width, height, rng, 0.2);
                serpent.putWalledBoxRoomCarvers(4);
                serpent.putWalledRoundRoomCarvers(2);
                serpent.putCaveCarvers(1);
                gen.generate(serpent.generate());
            }

            char[][] dungeonChars = gen.getDungeon();
            floorWorld[e] = new GreasedRegion(gen.getBareDungeon(), '.');
            Direction[] dirs = new Direction[8];
            Vegetable veggie;
            Tree treeBase;
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    char c = dungeonChars[x][y];
                    tile = eMap.contents[x][y];
                    tile.blockage = null;
                    switch (c) {
                        case '.':
                            break;
                        case '#':
                            handBuilt.placeWall(tile);
                            break;
                        case '+':
                        case '/':
                            handBuilt.placeDoor(tile);
                            break;
                        case '~': // TODO - distinguish deep water
                        case ',':
                            handBuilt.placeWater(tile);
                            break;
                        case '&': // should never occur naturally
                            break;
                        default:
                            tile.floor = RecipeMixer.buildPhysical(tile.floor); // Copy out the old floor before modifying it
                            tile.floor.symbol = eMap.altSymbolOf(c);
                            tile.floor.color = eMap.colorOf(c);
                            tile.floor.name = "modified " + c;
                            break;
                    }

                    if (tile.floor.next(5) == 0 && (veggie = DataPool.instance().getVegetable(tile.floor.symbol, tile.floor)) != null) {// 1 in 32 chance
                        tile.contents.add(RecipeMixer.buildVegetable(veggie));
                    } else if (tile.floor.next(8) < 3 && (treeBase = DataPool.instance().getTree(tile.floor.symbol, tile.floor)) != null) {    // 3 in 256 chance
                        Physical tree = RecipeMixer.buildTree(treeBase);
                        tree.shuffle(Direction.OUTWARDS, dirs);
                        for (int i = 0; i < dirs.length && !tree.inventory.isEmpty(); i++) {
                            if (eMap.inBounds(x + dirs[i].deltaX, y + dirs[i].deltaY)
                                && (dungeonChars[x + dirs[i].deltaX][y + dirs[i].deltaY] == '.' || dungeonChars[x + dirs[i].deltaX][y + dirs[i].deltaY] == '"')) {
                                dungeonChars[x + dirs[i].deltaX][y + dirs[i].deltaY] = '&';
                                eMap.contents[x + dirs[i].deltaX][y + dirs[i].deltaY].floor = RecipeMixer.buildPhysical(handBuilt.shadedGrass);
                                eMap.contents[x + dirs[i].deltaX][y + dirs[i].deltaY].contents.add(tree.inventory.remove(0));
                            }
                        }
                        tile.contents.add(tree);
                    }
                }
            }
        }

        for (int e = 0; e < depth - 1; e++) {
            EpiMap eMap = world[e];
            EpiMap nextMap = world[e + 1];
            tmp.remake(floorWorld[e]).and(floorWorld[e + 1]).randomScatter(rng, 21, 4);
            eMap.downStairPositions.or(tmp);
            nextMap.upStairPositions.or(tmp);
            for (Coord c : tmp) {
                handBuilt.placeStairs(eMap, nextMap, c);
            }
            floorWorld[e].andNot(tmp);
            floorWorld[e + 1].andNot(tmp);
        }
        for (int e = 1; e < depth; e++) {
            EpiMap eMap = world[e];
            EpiMap prevMap = world[e - 1];
            tmp.remake(floorWorld[e]).and(floorWorld[e - 1]).randomScatter(rng, 21, 4);
            eMap.upStairPositions.or(tmp);
            prevMap.downStairPositions.or(tmp);
            for (Coord c : tmp) {
                handBuilt.placeStairs(prevMap, eMap, c);
            }
            floorWorld[e].andNot(tmp);
            floorWorld[e - 1].andNot(tmp);
        }

        return world;
    }

    private void init(int width, int height, int depth, HandBuilt handBuilt) {
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.handBuilt = handBuilt;
        rng = handBuilt.rng.copy();
        rng.setState(1000L);
        world = new EpiMap[depth];

        for (int d = 0; d < depth; d++) {
            world[d] = new EpiMap(width, height);
        }
    }

    /**
     * Randomly places minerals in the provided map.
     */
    private void placeMinerals() {
        int z = 0;
        int thickness = rng.between(12, 18);
        Physical floor = DataPool.instance().getFloor(rng.getRandomElement(Stone.values()));
        while (z < depth) {
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    if (world[z].contents[x][y] == null) {
                        world[z].contents[x][y] = new EpiTile(floor);
                    }
                }
            }
            z++;
            thickness--;
            if (thickness <= 0) {
                thickness = rng.between(2, 10);
                floor = DataPool.instance().getFloor(rng.getRandomElement(Stone.values()));
            }
        }
    }

    /**
     * Makes every block contain a full wall. Should be called after floor manipulations are done to have walls match
     * the floor under them.
     */
    private void makeSolid() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < depth; z++) {
                    EpiTile tile = world[z].contents[x][y];
                    tile.add(DataPool.instance().getWall(tile.floor.terrainData.stone));
                }
            }
        }
    }

    private void bubbleMap(boolean useExistingFloor) {
        int quantity = (width * Integer.max(depth, height)) / 10;
        int sizeX = 16;
        int sizeY = 15;
        int sizeZ = 8;
        int centerX = 0;
        int centerY = 0;
        int centerZ = 0;
        int counter = 0;
        Physical blueprint = null;
        for (int growStep = 0; growStep < quantity; growStep++) {
            if (counter <= 0) {
                counter = rng.nextInt(3);
                centerX = rng.nextInt(width);
                centerY = rng.nextInt(height);
                centerZ = rng.nextInt(depth);

                if (useExistingFloor) {
                    blueprint = world[centerZ].contents[centerX][centerY].floor;
                } else {
                    blueprint = DataPool.instance().getFloor(rng.getRandomElement(Stone.values()));
                }
            }

            counter--;
            int bubbleSizeX = rng.nextInt(sizeX) + 1;//this will be the approximate size of the bubbles
            int bubbleSizeY = rng.nextInt(sizeY) + 1;//this will be the approximate size of the bubbles
            int bubbleSizeZ = rng.nextInt(sizeZ) + 2;//this will be the approximate size of the bubbles
            int bubbleGrowXStart = centerX - bubbleSizeX - rng.nextInt(2);
            int bubbleGrowXEnd = centerX + bubbleSizeX + rng.nextInt(2);
            for (int bubbleGrowZ = centerZ - bubbleSizeZ - rng.nextInt(1); bubbleGrowZ < (centerZ + bubbleSizeZ + rng.nextInt(1)); bubbleGrowZ++) {
                for (int bubbleGrowX = bubbleGrowXStart; bubbleGrowX < bubbleGrowXEnd; bubbleGrowX++) {
                    for (int bubbleGrowY = centerY - bubbleSizeY - rng.nextInt(2); bubbleGrowY < (centerY + bubbleSizeY + rng.nextInt(2)); bubbleGrowY++) {
                        if (pointInBounds(bubbleGrowX, bubbleGrowY, bubbleGrowZ)) {
                            world[bubbleGrowZ].contents[bubbleGrowX][bubbleGrowY].floor = blueprint;
                        }
                        /* mini-stagger walk here */
                        for (int m = 0; m < rng.nextInt(3); m++) {
                            int newX = bubbleGrowX - rng.between(-1, 2);//rng.nextInt(2) - 1);
                            int newY = bubbleGrowY - rng.between(-1, 2);//rng.nextInt(2) - 1);
                            int newZ = bubbleGrowZ - rng.between(-1, 2);//rng.nextInt(2) - 1);
                            if (pointInBounds(newX, newY, newZ)) {
                                world[newZ].contents[newX][newY].floor = blueprint;
                            }
                        }
                    }
                }
            }
        }
    }

    private void faultMap() {
        int x;
        int y;//y = mx+b
        int x2;
        int y2;
        double m = 1;

//        do { // single thickness does not play nice with checks against single thickness :)
        x = rng.nextInt(width);
        y = rng.nextInt(height);
        do {
            x2 = x - rng.nextInt(width);
            y2 = y - rng.nextInt(height);
        } while ((x2 == 0) || (y2 == 0));
        m = (y2) / (double) (x2);//y - y1/x - x1
//        } while (((int) m == 0) || ((int) m == 1) || ((int) m == -1));

        int b = (int) (y - m * x);//y-mx

        for (int z = 0; z < (depth - 1); z++) {
            for (x = 1; x < (width - 1); x++) {
                for (y = 1; y < (height - 1); y++) {
                    if (y < (m * x + b)) {
                        world[z].contents[x][y].floor = world[z + 1].contents[x][y].floor;
                    }
                }
            }
        }
    }

    private void intrudeMap() {
        Stone intruder = rng.getRandomElement(Arrays
            .stream(Stone.values())
            .filter(s -> s.intrusive)
            .collect(Collectors.toList()));
        int startX = rng.nextInt(width - 2) + 1;
        int startY = rng.nextInt(height - 2) + 1;
        int startZ = rng.nextInt(depth) + depth / 2;
        startZ = Math.min(startZ, depth - 1);

        int forceZ = 10;
        int n = rng.nextInt(3);
        int n2 = rng.nextInt(2);
        switch (n) {
            case 0:
                switch (n2) {
                    case 0:
                        startX = 1;
                        break;
                    case 1:
                        startX = width - 1;
                        break;
                }
                break;
            case 1:
                switch (n2) {
                    case 0:
                        startY = 1;
                        break;
                    case 1:
                        startY = height - 1;
                        break;
                }
                break;
            case 2:
                startZ = depth - 1;
                forceZ = depth * 4 / 5;
                break;
        }

        int currentX = startX;
        int currentY = startY;
        for (int z = startZ; ((forceZ > 0) && (z >= 0)); z--) {
            int forceX = 10;
            int forceY = 5;
            for (int x = currentX - forceX; x < currentX + forceX; x++) {
                for (int y = currentY - forceY; y < currentY + forceY; y++) {
                    if (pointInBounds(x, y, z)) {
                        world[z].contents[x][y].floor = DataPool.instance().getFloor(intruder);
                    }
                    forceY += rng.nextInt(3) - 1;
                    forceX += rng.nextInt(3) - 1;
                }
            }

            forceZ -= 1;
        }
    }

    private void extrudeMap() {
        Stone extruder = rng.getRandomElement(Arrays
            .stream(Stone.values())
            .filter(s -> s.extrusive)
            .collect(Collectors.toList()));
        Physical blueprint;
        int extrudeX = -1;
        int extrudeY = -1;
        int extrudeZ = -1;

        test_for_igneous:
        for (int testZ = 0; testZ < depth; testZ++) {
            for (int n = 0; n < maxRecurse; n++) {
                extrudeX = rng.nextInt(width - 2) + 1;
                extrudeY = rng.nextInt(height - 2) + 1;
                if (pointInBounds(extrudeX, extrudeY, testZ)) {
                    blueprint = world[testZ].contents[extrudeX][extrudeY].floor;

                    if ((blueprint.terrainData.extrusive) || (blueprint.terrainData.intrusive)) {
                        extrudeZ = testZ;
                        break test_for_igneous;
                    }
                }
            }
        }

        if (pointInBounds(extrudeX, extrudeY, extrudeZ)) {
            int sizeX, sizeY;
            double n;
            for (int z = extrudeZ; z >= 0; z--) {
                sizeX = rng.nextInt(5) + 10;
                sizeY = rng.nextInt(3) + 10;
                for (int x = extrudeX - sizeX; x < extrudeX + sizeX; x++) {
                    for (int y = extrudeY - sizeY; y < extrudeY + sizeY; y++) {
                        n = (Math.pow((double) (extrudeX - x) / sizeX, 1) + Math.pow((double) (extrudeY - y) / sizeX, 1));
                        if (n < 1) {//code for oval shape
                            if (pointInBounds(x, y, z)) {
                                world[z].contents[x][y].floor = DataPool.instance().getFloor(extruder);
                            }
                        }
                    }
                }
            }
        }
    }

    private void metamorphoseMap() {
        Physical[][][] near = new Physical[3][3][3];
        Physical changer;
        int changetrack = 0;
        boolean changing, igneous, sedimentary;
        changer = DataPool.instance().getFloor(rng.getRandomElement(Arrays
            .stream(Stone.values())
            .filter(s -> s.metamorphic)
            .collect(Collectors.toList())));
        for (int j = 0; j < depth; j++) {
            changetrack++;
            if (changetrack > 4) {
                changer = DataPool.instance().getFloor(rng.getRandomElement(Arrays
                    .stream(Stone.values())
                    .filter(s -> s.metamorphic)
                    .collect(Collectors.toList())));
            }
            for (int i = 1; i < width - 1; i++) {
                for (int k = 1; k < height - 1; k++) {
                    changing = false;
                    igneous = false;
                    sedimentary = false;
                    for (int a = 0; a < 3; a++) {//build array of near objects
                        for (int b = 0; b < 3; b++) {
                            for (int c = 0; c < 3; c++) {
                                near[a][b][c] = pointInBounds(i + a - 1, k + b - 1, j + c - 1) ? world[j + c - 1].contents[i + a - 1][k + b - 1].floor : null;
                            }
                        }
                    }

                    test_for_change:
                    for (Physical[][] testing1 : near) {
                        for (Physical[] testing2 : testing1) {
                            for (Physical test : testing2) {
                                if (test == null || test.terrainData == null) {
                                    continue;
                                }
                                if (test.terrainData.sedimentary) {
                                    sedimentary = true;
                                }
                                if (test.terrainData.extrusive || test.terrainData.intrusive) {
                                    igneous = true;
                                }
                                if (sedimentary && igneous) {
                                    changing = true;
                                    break test_for_change;
                                }
                            }
                        }
                    }

                    if (changing) {
                        if (pointInBounds(i, k, j)) {
                            if (rng.nextInt(100) < 45) {
                                world[j].contents[i][k].floor = changer;
                            }
                        }
                    }
                }
            }
        }

        for (int j = depth; j > 0; j--) {
            changetrack++;
            if (changetrack > 4) {
                changer = DataPool.instance().getFloor(rng.getRandomElement(Arrays
                    .stream(Stone.values())
                    .filter(s -> s.metamorphic)
                    .collect(Collectors.toList())));
            }
            for (int i = width - 1; i > 1; i--) {
                for (int k = height - 1; k > 1; k--) {
                    changing = false;
                    igneous = false;
                    sedimentary = false;
                    for (int a = 0; a < 3; a++) {//build array of near objects
                        for (int b = 0; b < 3; b++) {
                            for (int c = 0; c < 3; c++) {
                                near[a][b][c] = pointInBounds(i + a - 1, k + b - 1, j + c - 1) ? world[j + c - 1].contents[i + a - 1][k + b - 1].floor : null;
                            }
                        }
                    }

                    test_for_change:
                    for (Physical[][] testing1 : near) {
                        for (Physical[] testing2 : testing1) {
                            for (Physical test : testing2) {
                                if (test == null || test.terrainData == null) {
                                    continue;
                                }
                                if (test.terrainData.sedimentary) {
                                    sedimentary = true;
                                }
                                if (test.terrainData.extrusive || test.terrainData.intrusive) {
                                    igneous = true;
                                }
                                if (sedimentary && igneous) {
                                    changing = true;
                                    break test_for_change;
                                }
                            }
                        }
                    }

                    if (changing) {
                        if (pointInBounds(i, k, j)) {
                            if (rng.nextInt(100) < 25) {
                                world[j].contents[i][k].floor = changer;
                            }
                        }
                    }
                }
            }
        }
    }

    private void noiseMap() {
        FastNoise noise = new FastNoise(rng.nextInt(), 0.025f, FastNoise.SIMPLEX_FRACTAL, 2),
            ridge = new FastNoise(rng.nextInt(), 0.035f, FastNoise.SIMPLEX_FRACTAL, 3);
        ridge.setFractalType(FastNoise.RIDGED_MULTI);
        EpiMap map;
        Stone[] stones = rng.shuffleInPlace(Stone.values());
        Physical[] floors = new Physical[stones.length];
        for (int i = 0; i < stones.length; i++) {
            floors[i] = DataPool.instance().getFloor(stones[i]);
        }
        float diversity = stones.length * 0.04f + rng.nextFloat(stones.length * 0.16f), halfway = stones.length * 0.5f;
        for (int z = 0; z < depth; z++) {
            map = world[z];
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    map.contents[x][y] = new EpiTile(floors[Math.round(halfway + (diversity * noise.getConfiguredNoise(x, y, z + ridge.getConfiguredNoise(x, y, z) * 0.5f)))]);
                }
            }
        }
    }

    private boolean pointInBounds(int x, int y, int z) {
        return x >= 0 && x < width && y >= 0 && y < height && z >= 0 && z < depth;
    }
}
