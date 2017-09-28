package squidpony.epigon.mapping;

import squidpony.epigon.data.blueprint.Stone;
import squidpony.epigon.data.mixin.Terrain;
import squidpony.epigon.data.specific.Physical;
import squidpony.squidgrid.mapping.FlowingCaveGenerator;

import java.util.*;
import java.util.stream.Collectors;

import static squidpony.epigon.Epigon.*;

/**
 * Creates and populates a world.
 *
 * @author Eben Howard - http://squidpony.com
 */
public class WorldGenerator {
    private static final int maxRecurse = 10;
    private EpiMap[] world;
    private int width, height, depth;

    private Map<Stone, Physical> walls = new HashMap<>();

    public EpiMap[] buildWorld(int width, int height, int depth) {
        this.width = width;
        this.height = height;
        this.depth = depth;

        world = new EpiMap[depth];
        for (int d = 0; d < depth; d++) {
            world[d] = new EpiMap(width, height);
        }

        mineralPlacement();
        bubbleMap(false);
        //intrudeMap();

        makeSolid();

//        DungeonGenerator sdg = new DungeonGenerator(width, height, rng);
//        //sdg.addGrass(15);
//        sdg.addWater(15);
//        sdg.addDoors(20, true);
//        sdg.addGrass(10);
//        char[][] simpleChars = DungeonUtility.closeDoors(sdg.generate());

//        OrganicMapGenerator omg = new OrganicMapGenerator(width, height, rng);
//        char[][] simpleChars = omg.generate();
//        char[][] simpleChars = new char[width][height];
//
//        for (int x = 0; x < width; x++) {
//            for (int y = 0; y < height; y++) {
//                if (x <= 0 || y <= 0 || x >= width -1 || y >= height -1){
//                    simpleChars[x][y] = '#';
//                } else {
//                    simpleChars[x][y] = '.';
//                }
//            }
//        }
        FlowingCaveGenerator flow = new FlowingCaveGenerator(width, height, null, rng);
        char[][] simpleChars = flow.generate();

        EpiTile tile;
        Physical adding;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                char c = simpleChars[x][y];
                tile = world[0].contents[x][y];
                if (tile.getLargeObject() != null) {
                    tile.remove(tile.getLargeObject());
                }
                switch (c) {
                    case '.':
                        break;
                    case '#':
                        adding = getWall(tile.floor.terrainData.stone);
                        tile.add(adding);
                        break;
                    case '+':
                        Stone stone = tile.floor.terrainData.stone;
                        adding = mixer.getPhysical(stone);
                        List<Physical> adds = mixer.mix(handBuilt.doorRecipe, Collections.singletonList(adding), Collections.emptyList());
                        Physical door = adds.get(0);
                        mixer.applyModification(door, rng.nextBoolean() ? handBuilt.closeDoor : handBuilt.openDoor);
                        tile.add(door);
                        break;
                    default:
                        tile.floor = mixer.buildPhysical(tile.floor); // Copy out the old floor before modifying it
                        tile.floor.symbol = EpiMap.altSymbolOf(c);
                        tile.floor.color = EpiMap.colorOf(c);
                        tile.floor.name = "modified " + c;
                        break;
                }
            }
        }

        return world;
    }

    private Physical getWall(Stone stone){
        Physical wall = walls.get(stone);
        if (wall != null){
            return wall;
        }

        wall = mixer.buildPhysical(mixer.getPhysical(stone));
        mixer.applyModification(wall, handBuilt.makeWall);
        walls.put(stone, wall);
        return wall;
    }

    /**
     * Randomly places minerals in the provided map.
     */
    private void mineralPlacement() {
        Physical floor = mixer.getPhysical(rng.getRandomElement(Stone.values()));
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < depth; z++) {
                    EpiTile tile = world[z].contents[x][y];
                    if (tile == null) {
                        tile = new EpiTile();
                        world[z].contents[x][y] = tile;
                    }

                    tile.floor = floor;
                }
            }
        }
    }

    /**
     * Makes every block contain a full wall. Should be called after floor manipulations are done to
     * have walls match the floor under them.
     */
    private void makeSolid(){
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < depth; z++) {
                    EpiTile tile = world[z].contents[x][y];
                    Physical p = mixer.buildPhysical(mixer.getPhysical(tile.floor.terrainData.stone));
                    mixer.applyModification(p, handBuilt.makeWall);
                    tile.add(p);
                }
            }
        }
    }

    private void bubbleMap(boolean useExistingFloor) {
        int quantity = (width * height) / 10;
        int sizeX = 16;
        int sizeY = 15;
        int sizeZ = 2;
        int centerX = 0;
        int centerY = 0;
        int centerZ = 0;
        int counter = 0;
        Physical blueprint = null;
        for (int growStep = 0; growStep < quantity; growStep++) {
            if (counter <= 0) {
                counter = rng.nextInt(3);
                centerX = rng.between(1, width);
                centerY = rng.between(1, height);
                centerZ = rng.nextInt(depth);

                if (useExistingFloor) {
                    blueprint = world[centerZ].contents[centerX][centerY].floor;
                } else {
                    blueprint = mixer.getPhysical(rng.getRandomElement(Stone.values()));
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
        Terrain terrain;
        int x;
        int y;//y = mx+b
        int x2;
        int y2;
        double m = 1;

        do {
            x = rng.nextInt(width);
            y = rng.nextInt(height);
            do {
                x2 = x - rng.nextInt(width);
                y2 = y - rng.nextInt(height);
            } while ((x2 == 0) || (y2 == 0));
            m = (y2) / (x2);//y - y1/x - x1
        } while (((int) m == 0) || ((int) m == 1) || ((int) m == -1));
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
                        world[z].contents[x][y].floor = mixer.buildPhysical(mixer.getPhysical(intruder)); // TODO - get from cache
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
                blueprint = world[testZ].contents[extrudeX][extrudeY].floor;

                if ((blueprint.terrainData.extrusive) || (blueprint.terrainData.intrusive)) {
                    extrudeZ = testZ;
                    break test_for_igneous;
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
                                world[z].contents[x][y].floor = mixer.buildPhysical(mixer.getPhysical(extruder)); // TODO - cache
                            }
                        }
                    }
                }
            }
        }
    }

    private void metamorphoseMap() {
        Physical[][][] near = new Physical[3][3][3];
        Stone changer;
        int changetrack = 0;
        boolean changing, igneous, sedimentary;
        changer = rng.getRandomElement(Arrays
            .stream(Stone.values())
            .filter(s -> s.metamorphic)
            .collect(Collectors.toList()));
        for (int j = 0; j < depth; j++) {
            changetrack++;
            if (changetrack > 4) {
                changer = rng.getRandomElement(Arrays
                    .stream(Stone.values())
                    .filter(s -> s.metamorphic)
                    .collect(Collectors.toList()));
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
                    for (Physical testing1[][] : near) {
                        for (Physical testing2[] : testing1) {
                            for (Physical test : testing2) {
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
                                world[j].contents[i][k].floor = mixer.buildPhysical(mixer.getPhysical(changer)); // TODO - cache
                            }
                        }
                    }
                }
            }
        }

        for (int j = depth; j > 0; j--) {
            changetrack++;
            if (changetrack > 4) {
                changer = rng.getRandomElement(Arrays
                    .stream(Stone.values())
                    .filter(s -> s.metamorphic)
                    .collect(Collectors.toList()));
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
                    for (Physical testing1[][] : near) {
                        for (Physical testing2[] : testing1) {
                            for (Physical test : testing2) {
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
                                world[j].contents[i][k].floor = mixer.buildPhysical(mixer.getPhysical(changer)); // TODO - cache
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean pointInBounds(int x, int y, int z) {
        return x >= 0 && x < width && y >= 0 && y < height && z >= 0 && z < depth;
    }
}
