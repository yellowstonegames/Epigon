package squidpony.epigon.mapping;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import squidpony.epigon.data.Physical;
import squidpony.epigon.data.RecipeMixer;
import squidpony.epigon.data.quality.Stone;
import squidpony.epigon.data.control.HandBuilt;
import squidpony.squidgrid.Direction;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidgrid.mapping.DungeonGenerator;
import squidpony.squidgrid.mapping.styled.DungeonBoneGen;
import squidpony.squidgrid.mapping.styled.TilesetType;
import squidpony.squidmath.Coord;
import squidpony.squidmath.FastNoise;
import squidpony.squidmath.GreasedRegion;

import static squidpony.epigon.Utilities.*;
import squidpony.epigon.data.control.DataPool;
import squidpony.epigon.data.quality.Tree;
import squidpony.epigon.data.quality.Vegetable;

public class CastleGenerator {

    private int width, height, depth, sky;
    private final HandBuilt handBuilt;

    public CastleGenerator(HandBuilt handbuilt) {
        this.handBuilt = handbuilt;
    }

    /**
     * Builds a castle that is only a single surface level deep.
     *
     * @param width x axis
     * @param height y axis
     * @return
     */
    public EpiMap[] buildCastle(int width, int height) {
        return buildCastle(width, height, 0, 1);
    }

    /**
     * Builds a castle with no basement and the desired distance into the sky.
     *
     * The ground level counts as 1 sky, so minimum sky can be is 1. (First floor is the ground floor)
     *
     * @param width x axis
     * @param height y axis
     * @param sky z level above ground, including the first castle level
     * @return
     */
    public EpiMap[] buildCastle(int width, int height, int sky) {
        return buildCastle(width, height, 0, sky);
    }

    /**
     * Builds the castle levels, including basement if needed. The length of the array will be the sky plus the depth
     * values.
     *
     * @param width x axis
     * @param height y axis
     * @param depth z axis quantity below the ground floor
     * @param sky z level above ground, including the first castle level.
     * @return
     */
    private EpiMap[] buildCastle(int width, int height, int depth, int sky) { // NOTE - made private until depth actually builds a basement to prevent using this overload
        System.out.println("Building castle.");
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.sky = sky;

        EpiMap[] underground = buildBasement();
        EpiMap[] aboveground = new EpiMap[sky + 1]; // first layer above ground is floor zero

        for (int i = 0; i <= sky; i++) {
            aboveground[i] = new EpiMap(width, height);
        }

        Physical walkway = RecipeMixer.buildPhysical(Physical.makeBasic("stone walkway", '.', SColor.LINEN));
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                aboveground[sky].contents[x][y] = new EpiTile(walkway);
            }
        }

        generateCastle(aboveground);

        return Stream.of(aboveground, underground).flatMap(Stream::of).toArray(EpiMap[]::new);
    }

    private EpiMap[] buildBasement() {
        EpiMap[] basement = new EpiMap[depth];
        Arrays.fill(basement, new EpiMap());
        return basement;
    }

    private void generateCastle(EpiMap[] buildZone) {
        Castle castle = new Castle(buildZone);
        buildGroundLevelCastle(castle);
    }

    private void buildGroundLevelCastle(Castle castle) {
        buildMoat(castle);

        List<Coord> corners = findInternalPolygonCorners(castle.insideMoat, 4, 4);
        Coord courtyardCentroid = findCentroid(corners);

        buildOuterWall(courtyardCentroid, castle);
        layBricks(courtyardCentroid, castle);
        tearDownWalls(castle);
        buildKeep(courtyardCentroid, castle);
        buildGardens(castle);
        addPlants(castle);
    }

    private void buildMoat(Castle castle) {
        EpiMap map = castle.buildZone[castle.ground];
        int distance = 8; // space between points
        castle.moat.surface8way(8);
        List<Coord> corners = findInternalPolygonCorners(castle.moat, distance, 7);
        castle.moat.clear();
        connectPoints(castle.moat, corners);

        castle.moat.expand8way();
        castle.moatBank = castle.moat.copy();
        GreasedRegion nonMoat = castle.region.copy().andNot(castle.moat);
        for (Coord c : nonMoat) {
            map.contents[c.x][c.y].floor = DataPool.instance().getFloor(Stone.ARGILLITE);
        }

        castle.moat.fray(0.3).fray(0.2);
        for (Coord c : castle.moat) {
            handBuilt.placeWater(map.contents[c.x][c.y]);
        }

        castle.moatBank.andNot(castle.moat);
        for (Coord c : castle.moatBank) {
            handBuilt.placeMud(map.contents[c.x][c.y]);
        }

        castle.insideMoat = new GreasedRegion(findCentroid(corners), castle.region.width, castle.region.height)
            .flood8way(nonMoat, castle.region.width * castle.region.height);

        castle.insideMoat.andNot(castle.moat).andNot(castle.moatBank);
        for (Coord c : castle.insideMoat) {
            map.contents[c.x][c.y].floor = DataPool.instance().getFloor(Stone.OBSIDIAN);
        }
    }

    private void buildOuterWall(Coord centroid, Castle castle) {
        castle.outerWall = new GreasedRegion(centroid, castle.insideMoat.width, castle.insideMoat.height);
        while (!castle.outerWall.intersects(castle.moat)) {
            castle.outerWall.expand8way();
        }
        castle.outerWall.surface8way(3);
//        castle.outerWall = connectPoints(castle.outerWall, corners);
        castle.holes = castle.outerWall.copy().randomScatter(rng, 8).expand(); // find the holes before the expansion so that they're in the middle of the wall
        castle.outerWall.expand();

        for (Coord c : castle.outerWall) {
            for (int z = castle.ground; z > Integer.max(castle.ground - 3, castle.ground - castle.height); z--) {
                EpiTile tile = castle.tileAt(c, z);
                if (tile == null) {
                    tile = new EpiTile();
                    castle.setTileAt(c, castle.ground - z, tile);
                }

                tile.add(DataPool.instance().getWall(Stone.GNEISS));
                tile.floor = DataPool.instance().getFloor(Stone.GNEISS);
            }
        }
    }

    private void layBricks(Coord centroid, Castle castle) {
        EpiMap map = castle.buildZone[castle.ground];
        castle.courtyard = new GreasedRegion(centroid, castle.region.width, castle.region.height)
            .flood8way(castle.insideMoat.copy().andNot(castle.outerWall), castle.region.width * castle.region.height);

        Physical brick = RecipeMixer.buildPhysical(Physical.makeBasic("brick", '≡', SColor.PERSIAN_RED));
        for (Coord c : castle.courtyard) {
            map.contents[c.x][c.y].floor = brick;
        }
    }

    private void tearDownWalls(Castle castle) {
        EpiMap map = castle.buildZone[castle.ground];
        castle.holes.expand(2);
        castle.holes.fray(0.2);
        castle.holes.fray(0.2);
        Physical rubble = RecipeMixer.buildPhysical(Physical.makeBasic("rubble", ';', SColor.GREYISH_DARK_GREEN));
        for (Coord c : castle.holes) {
            map.contents[c.x][c.y].blockage = null;
            map.contents[c.x][c.y].add(rubble);
            handBuilt.placeMud(map.contents[c.x][c.y]);
        }
    }

    private void buildGardens(Castle castle) {
        EpiMap map = castle.buildZone[castle.ground];
        castle.garden = castle.courtyard.copy().andNot(castle.keepWall).andNot(castle.insideKeep);
        Physical pondWater = RecipeMixer.buildPhysical(Physical.makeBasic("pond water", '~', SColor.SEA_GREEN));

        castle.pond = castle.garden.copy();
        Coord pondCenter = castle.pond.singleRandom(rng);
        castle.pond.and(castle.pond.copy().fill(false).insertCircle(pondCenter, 2));
        castle.pond.fray(0.4);
        for (Coord c : castle.pond) {
            map.contents[c.x][c.y].blockage = null;
            map.contents[c.x][c.y].floor = pondWater;
        }
        castle.pondBank = castle.pond.copy().fringe().andNot(castle.keepWall).andNot(castle.insideKeep).andNot(castle.outerWall);
        for (Coord c : castle.pondBank) {
            handBuilt.placeMud(map.contents[c.x][c.y]);
        }
    }

    private void addPlants(Castle castle) {
        EpiMap map = castle.buildZone[castle.ground];
        GreasedRegion outside = castle.region.copy().not().flood(castle.insideMoat.copy().not(), castle.width * castle.height);
        for (GreasedRegion area : new GreasedRegion[]{castle.garden, castle.pond, castle.pondBank, outside}) {
            Direction[] dirs = new Direction[8];
            EpiTile tile;
            Vegetable veggie;
            Tree treeBase;
            for (Coord c : area) {
                float noise = FastNoise.instance.getSimplex(c.x * 1.6f, c.y * 1.6f);
                if (noise > -0.1f && noise < 0.35f) {
                    tile = map.contents[c.x][c.y];
                    if (tile.floor.symbol == '.') {
                        tile.floor = RecipeMixer.buildPhysical(handBuilt.grass);
                    }

                    if (tile.floor.next(3) == 0 && (veggie = DataPool.instance().getVegetable(tile.floor.symbol, tile.floor)) != null) {
                        tile.contents.add(RecipeMixer.buildVegetable(veggie));
                    } else if (tile.floor.next(8) < 9 && (treeBase = DataPool.instance().getTree(tile.floor.symbol, tile.floor)) != null) {    // 9 in 256 chance
                        Physical tree = RecipeMixer.buildTree(treeBase);
                        tree.shuffle(Direction.OUTWARDS, dirs);
                        for (int i = 0; i < dirs.length && !tree.inventory.isEmpty(); i++) {
                            if (map.inBounds(c.x + dirs[i].deltaX, c.y + dirs[i].deltaY)
                                && (map.contents[c.x + dirs[i].deltaX][c.y + dirs[i].deltaY].floor.symbol == '.' || map.contents[c.x + dirs[i].deltaX][c.y + dirs[i].deltaY].floor.symbol == '¸')) {
                                map.contents[c.x + dirs[i].deltaX][c.y + dirs[i].deltaY].floor = RecipeMixer.buildPhysical(handBuilt.shadedGrass);
                                map.contents[c.x + dirs[i].deltaX][c.y + dirs[i].deltaY].contents.add(tree.inventory.remove(0));
                            }
                        }
                        tile.contents.add(tree);
                    }

                }
            }

        }
    }

    private void buildKeep(Coord courtyardCentroid, Castle castle) {
        // sketch out a keep's borders

//        int top = courtyardCentroid.y;
//        int bottom = courtyardCentroid.y;
//        int left = courtyardCentroid.x;
//        int right = courtyardCentroid.x;
//        int lastChoice = 0;
//        expandKeep:
//        while (true) {
//            lastChoice = rng.nextInt(4);
//            switch (lastChoice) {
//                case 0:
//                    top--;
//                    break;
//                case 1:
//                    right++;
//                    break;
//                case 2:
//                    left--;
//                    break;
//                case 3:
//                default:
//                    bottom++;
//                    break;
//            }
//            for (int x = left; x <= right; x++) {
//                if (!castle.courtyard.contains(x, top) || !castle.courtyard.contains(x, bottom)) {
//                    break expandKeep;
//                }
//            }
//            for (int y = top; y <= bottom; y++) {
//                if (!castle.courtyard.contains(left, y) || !castle.courtyard.contains(right, y)) {
//                    break expandKeep;
//                }
//            }
//        }
//        switch (lastChoice) {
//            case 0:
//                top++;
//                break;
//            case 1:
//                right--;
//                break;
//            case 2:
//                left++;
//                break;
//            case 3:
//            default:
//                bottom--;
//                break;
//        }
        castle.insideKeep = new GreasedRegion(courtyardCentroid, castle.courtyard.width, castle.courtyard.height);
        while (!castle.insideKeep.intersects(castle.outerWall)) {
            castle.insideKeep.expand8way();
        }
        castle.insideKeep.retract(6);

        castle.keepWall = connectPoints(new GreasedRegion(castle.courtyard.width, castle.courtyard.height),
            castle.insideKeep.copy().andNot(castle.insideKeep.copy().removeCorners()).asCoords());
        for (Coord c : castle.keepWall) {
            for (int z = 0; z <= 4; z++) {
                EpiTile tile = castle.tileAt(c, castle.ground - z);
                if (tile == null) {
                    tile = new EpiTile();
                    castle.setTileAt(c, castle.ground - z, tile);
                }
                tile.floor = DataPool.instance().getFloor(Stone.MARBLE);
                //tile.add(getWall(Stone.MARBLE));
            }

            if ((c.x + c.y & 1) == 0) {
                EpiTile tile = castle.tileAt(c, castle.ground - 5);
                if (tile == null) {
                    tile = new EpiTile();
                    castle.setTileAt(c, castle.ground - 5, tile);
                }
                tile.floor = DataPool.instance().getFloor(Stone.MARBLE);
                tile.add(DataPool.instance().getWall(Stone.MARBLE));
            }
        }

//        castle.insideKeep = new GreasedRegion(courtyardCentroid, castle.region.width, castle.region.height)
//            .flood8way(castle.courtyard.copy().andNot(castle.keepWall), castle.region.width * castle.region.height);
        Physical carpet = RecipeMixer.buildPhysical(Physical.makeBasic("plush carpet", 'ˬ', SColor.ROYAL_PURPLE));
        Physical insideFloor = DataPool.instance().getFloor(Stone.GRANITE), insideWall = DataPool.instance().getWall(Stone.GRANITE);
        DungeonBoneGen dbg = new DungeonBoneGen(rng);
        DungeonGenerator dungeonGenerator = new DungeonGenerator(width, height, rng);
        dungeonGenerator.addDoors(30, true);
        dbg.generate(TilesetType.LIMITED_CONNECTIVITY, width, height);
        GreasedRegion gr = dbg.region.copy();
        dbg.generate(TilesetType.LIMITED_CONNECTIVITY, width + 5, height + 5);
        gr.insert(-5, -5, dbg.region);
        dbg.generate(TilesetType.LIMITED_CONNECTIVITY, width + 3, height + 3);
        gr.not().insert(-2, -2, dbg.region);
        char[][] interior = dungeonGenerator.generate(gr.toChars());
        for (Coord c : castle.insideKeep) {
            EpiTile tile = castle.tileAt(c, castle.ground);
            if (tile == null) {
                castle.setTileAt(c, castle.ground, tile = new EpiTile(insideFloor));
            } else {
                tile.floor = insideFloor;
            }
            if (interior[c.x][c.y] == '+' || interior[c.x][c.y] == '/') {
                tile.blockage = null;
                handBuilt.placeDoor(tile);
            } else if (interior[c.x][c.y] != '.') {
                tile.add(insideWall);
            }
        }
//        for (Coord c : castle.insideKeep) {
//            for (int z = 0; z <= 4; z++) {
//                EpiTile tile = castle.tileAt(c, castle.ground - z);
//                if (tile == null) {
//                    castle.setTileAt(c, castle.ground - z, new EpiTile(carpet));
//                } else {
//                    tile.floor = carpet;
//                }
//            }
//        }
//        GreasedRegion doors = castle.keepWall.copy().randomScatter(rng, 8, 5);
//        for (Coord c : doors) {
//            EpiTile tile = castle.tileAt(c, castle.ground);
//            tile.floor = getFloor(Stone.MARBLE);
//            tile.blockage = null;
//            placeDoor(tile);
//        }
    }

}
