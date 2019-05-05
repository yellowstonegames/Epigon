package squidpony.epigon.mapping;

import squidpony.epigon.data.Physical;
import squidpony.epigon.data.control.RecipeMixer;
import squidpony.epigon.data.WeightedTableWrapper;
import squidpony.epigon.data.quality.Inclusion;
import squidpony.epigon.data.quality.Stone;
import squidpony.epigon.data.quality.Tree;
import squidpony.epigon.data.quality.Vegetable;
import squidpony.epigon.data.trait.Grouping;
import squidpony.epigon.data.control.DataStarter;
import squidpony.squidgrid.Direction;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidgrid.mapping.DenseRoomMapGenerator;
import squidpony.squidgrid.mapping.DungeonGenerator;
import squidpony.squidgrid.mapping.FlowingCaveGenerator;
import squidpony.squidgrid.mapping.SerpentMapGenerator;
import squidpony.squidgrid.mapping.styled.TilesetType;
import squidpony.squidmath.*;

import squidpony.epigon.data.control.DataPool;

/**
 * Creates a world.
 *
 * @author Eben Howard - http://squidpony.com
 */
public class LocalAreaGenerator {
    private EpiMap[] world;
    private int width, height, depth;
    private DataStarter dataStarter;
    private MapDecorator decorator;
    private StatefulRNG rng;

    public LocalAreaGenerator(MapDecorator decorator) {
        this.decorator = decorator;
        dataStarter = decorator.handBuilt;
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
                        map.contents[x][y] = new EpiTile(dataStarter.emptySpace);
                        break;
                    case '$':
                        map.contents[x][y] = new EpiTile(dataStarter.emptySpace);
                        map.contents[x][y].add(dataStarter.money);
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
        long seed1 = dataStarter.rng.nextLong() + System.nanoTime(),
            seed2 = dataStarter.rng.nextLong() + seed1,
            seed3 = dataStarter.rng.nextLong() + seed2 ^ seed1;
        final double portionGapSize = 0.08 * width, offGapSize = 0.12 * width,
            halfWidth = 0.5 * width, centerOff = 0.135 * width, extraWiggle = 0.02 * width;
        for (int level = World.DIVE_HEADER.length; level < height; level++) {
            for (int x = centerGap - gapSize; x < centerGap + gapSize; x++) {
                map.contents[x][level].floor = dataStarter.emptySpace;
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
        rng = new StatefulRNG(new DiverRNG(dataStarter.rng.nextLong() ^ seed3));
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
        contents[inclusions.length] = dataStarter.money;
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
        init(width, height, depth);

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
                            decorator.placeWall(tile);
                            break;
                        case '+':
                        case '/':
                            decorator.placeDoor(tile);
                            break;
                        case '~': // TODO - distinguish deep water
                        case ',':
                            decorator.placeWater(tile);
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
                                eMap.contents[x + dirs[i].deltaX][y + dirs[i].deltaY].floor = RecipeMixer.buildPhysical(dataStarter.shadedGrass);
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
                decorator.placeStairs(eMap, nextMap, c);
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
                decorator.placeStairs(prevMap, eMap, c);
            }
            floorWorld[e].andNot(tmp);
            floorWorld[e - 1].andNot(tmp);
        }

        return world;
    }

    private void init(int width, int height, int depth) {
        this.width = width;
        this.height = height;
        this.depth = depth;
        rng = dataStarter.rng.copy();
        rng.setState(1000L);
        world = new EpiMap[depth];

        for (int d = 0; d < depth; d++) {
            world[d] = new EpiMap(width, height);
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

}
