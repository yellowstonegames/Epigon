package squidpony.epigon.mapping;

import java.util.Arrays;
import java.util.stream.Collectors;
import squidpony.epigon.data.Physical;
import squidpony.epigon.data.control.DataPool;
import squidpony.epigon.data.quality.Stone;
import squidpony.epigon.util.Bounds;

import static squidpony.epigon.util.Utilities.rng;

/**
 * Applies geological changes.
 */
public class GeologyGenerator {

    private static final int maxRecurse = 10;

    private Bounds bounds;
    private EpiMap[] world;
    private int width, height, depth;

    public GeologyGenerator(EpiMap[] world) {
        this.world = world;
        width = world[0].width;
        height = world[0].height;
        depth = world.length;
        bounds = new Bounds(width, height, depth);
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
                        if (bounds.pointInBounds(bubbleGrowX, bubbleGrowY, bubbleGrowZ)) {
                            world[bubbleGrowZ].contents[bubbleGrowX][bubbleGrowY].floor = blueprint;
                        }
                        /* mini-stagger walk here */
                        for (int m = 0; m < rng.nextInt(3); m++) {
                            int newX = bubbleGrowX - rng.between(-1, 2);//rng.nextInt(2) - 1);
                            int newY = bubbleGrowY - rng.between(-1, 2);//rng.nextInt(2) - 1);
                            int newZ = bubbleGrowZ - rng.between(-1, 2);//rng.nextInt(2) - 1);
                            if (bounds.pointInBounds(newX, newY, newZ)) {
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
                    if (bounds.pointInBounds(x, y, z)) {
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
                if (bounds.pointInBounds(extrudeX, extrudeY, testZ)) {
                    blueprint = world[testZ].contents[extrudeX][extrudeY].floor;

                    if ((blueprint.terrainData.extrusive) || (blueprint.terrainData.intrusive)) {
                        extrudeZ = testZ;
                        break test_for_igneous;
                    }
                }
            }
        }

        if (bounds.pointInBounds(extrudeX, extrudeY, extrudeZ)) {
            int sizeX, sizeY;
            double n;
            for (int z = extrudeZ; z >= 0; z--) {
                sizeX = rng.nextInt(5) + 10;
                sizeY = rng.nextInt(3) + 10;
                for (int x = extrudeX - sizeX; x < extrudeX + sizeX; x++) {
                    for (int y = extrudeY - sizeY; y < extrudeY + sizeY; y++) {
                        n = (Math.pow((double) (extrudeX - x) / sizeX, 1) + Math.pow((double) (extrudeY - y) / sizeX, 1));
                        if (n < 1) {//code for oval shape
                            if (bounds.pointInBounds(x, y, z)) {
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
                                near[a][b][c] = bounds.pointInBounds(i + a - 1, k + b - 1, j + c - 1) ? world[j + c - 1].contents[i + a - 1][k + b - 1].floor : null;
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
                        if (bounds.pointInBounds(i, k, j)) {
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
                                near[a][b][c] = bounds.pointInBounds(i + a - 1, k + b - 1, j + c - 1) ? world[j + c - 1].contents[i + a - 1][k + b - 1].floor : null;
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
                        if (bounds.pointInBounds(i, k, j)) {
                            if (rng.nextInt(100) < 25) {
                                world[j].contents[i][k].floor = changer;
                            }
                        }
                    }
                }
            }
        }
    }

}
