package squidpony.mapping;

import java.util.ArrayList;
import squidpony.data.blueprints.Inclusion;
import squidpony.data.blueprints.Stone;
import squidpony.data.specific.Terrain;
import squidpony.squidmath.RNG;

/**
 * Creates and populates a world.
 *
 * @author Eben Howard - http://squidpony.com - howard@squidpony.com
 */
public class WorldGenerator {

    private static ArrayList<Stone> wallList = new ArrayList<>(),
            sedimentaryList = new ArrayList<>(),
            intrusiveList = new ArrayList<>(),
            extrusiveList = new ArrayList<>(),
            metamorphicList = new ArrayList<>();
    private static ArrayList<Inclusion> gemList = new ArrayList<>(),
            sedimentaryGemList = new ArrayList<>(),
            intrusiveGemList = new ArrayList<>(),
            extrusiveGemList = new ArrayList<>(),
            metamorphicGemList = new ArrayList<>();
    private static boolean initialized = false;
    private RNG rng = new RNG();

    public WorldGenerator() {
        if (!initialized) {
            initWallLists();
            initialized = true;
        }
    }

    private static void initWallLists() {
        for (Stone stone : Stone.values()) {
            wallList.add(stone);
            if (stone.sedimentary) {
                sedimentaryList.add(stone);
            }
            if (stone.intrusive) {
                intrusiveList.add(stone);
            }
            if (stone.extrusive) {
                extrusiveList.add(stone);
            }
            if (stone.metamorphic) {
                metamorphicList.add(stone);
            }
        }

        for (Inclusion inclusion : Inclusion.values()) {
            gemList.add(inclusion);
            if (inclusion.sedimentary) {
                sedimentaryGemList.add(inclusion);
            }
            if (inclusion.intrusive) {
                intrusiveGemList.add(inclusion);
            }
            if (inclusion.extrusive) {
                extrusiveGemList.add(inclusion);
            }
            if (inclusion.metamorphic) {
                metamorphicGemList.add(inclusion);
            }
        }
    }

    /**
     * Randomly places minerals in the provided map.
     */
    private void mineralPlacement(EpiMap map) {
        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                for (int z = 0; z < map.getDepth(); z++) {
                    EpiTile tile = map.contents[x][y][z];
                    if (tile == null) {
                        tile = new EpiTile();
                        map.contents[x][y][z] = tile;
                    }
                    tile.floor = new Terrain((Stone.values()[rng.nextInt(Stone.values().length)]));
                }
            }
        }
    }

    private void bubbleMap() {
        int x = 0, y = 0, z = 0, nx = 0, ny = 0, nz = 0, counter = 0;
        TerrainObject terrain;
        DiggableWall tempWall = new DiggableWall(Stone.GRANITE);
        for (int j = 0; j < (15); j++) {
            if (counter <= 0) {
                counter = rng.nextInt(3);
                x = rng.nextInt(mapSizeX - 2) + 1;
                y = rng.nextInt(mapSizeY - 2) + 1;
                z = rng.nextInt(mapSizeZ);
                terrain = mapContents[x][y][z].getFlooring();
                if (!(terrain instanceof DiggableWall)) {
                    return;
                } else {
                    tempWall = (DiggableWall) terrain;
                }
            }
            counter--;
            nx = rng.nextInt(6) + 1;//this will be the approximate size of the bubbles
            ny = rng.nextInt(4) + 1;//this will be the approximate size of the bubbles
            nz = rng.nextInt(2) + 1;//this will be the approximate size of the bubbles
            for (int l = z - nz - rng.nextInt(1); l < (z + nz + rng.nextInt(1)); l++) {
                for (int i = x - nx - rng.nextInt(2); i < (x + nx + rng.nextInt(2)); i++) {
                    for (int k = y - ny - rng.nextInt(2); k < (y + ny + rng.nextInt(2)); k++) {
                        if (pointInBounds(i, k, l)) {
                            mapContents[i][k][l].setFlooring(new DiggableWall(tempWall));
                        }
                        /* mini-stagger walk here */
                        for (int m = 0; m < rng.nextInt(3); m++) {
                            int newX, newY, newZ;
                            newX = i - (rng.nextInt(2) - 1);
                            newY = k - (rng.nextInt(2) - 1);
                            newZ = l - (rng.nextInt(2) - 1);
                            if (pointInBounds(newX, newY, newZ)) {
                                mapContents[newX][newY][newZ].setFlooring(new DiggableWall(tempWall));

                            }
                        }
                    }
                }
            }
        }
    }

    private void faultMap() {
        TerrainObject terrain;
        DiggableWall tempWall = new DiggableWall(Stone.GRANITE);
        int b = mapSizeY / 2, x, y, x2, y2;//y = mx+b
        double m = 1;
        do {
            x = rng.nextInt(mapSizeX);
            y = rng.nextInt(mapSizeY);
            do {
                x2 = x - rng.nextInt(mapSizeX);
                y2 = y - rng.nextInt(mapSizeY);
            } while ((x2 == 0) || (y2 == 0));
            m = (y2) / (x2);//y - y1/x - x1
        } while (((int) m == 0) || ((int) m == 1) || ((int) m == -1));
        b = (int) (y - m * x);//y-mx

        for (int l = 0; l < (mapSizeZ - 1); l++) {
            for (int i = 1; i < (mapSizeX - 1); i++) {
                for (int k = 1; k < (mapSizeY - 1); k++) {
                    if (k < (m * i + b)) {
                        terrain = mapContents[i][k][l + 1].getFlooring();
                        if (terrain instanceof DiggableWall) {
                            tempWall = (DiggableWall) terrain;
                            mapContents[i][k][l].setFlooring(new DiggableWall(tempWall));
                        }
                    }
                }
            }
        }
    }

    private void intrudeMap() {
        int startX, startY, startZ, forceX, forceY, forceZ, n, n2;
        boolean xGrow = rng.nextBoolean(), yGrow = rng.nextBoolean();
        DiggableWall intruder = intrusiveList.get(rng.nextInt(intrusiveList.size()));
        startX = rng.nextInt(mapSizeX - 2) + 1;
        startY = rng.nextInt(mapSizeY - 2) + 1;
        startZ = rng.nextInt(mapSizeZ) + mapSizeZ / 2;
        startZ = Math.min(startZ, mapSizeZ - 1);
        forceX = 10;
        forceY = 5;
        forceZ = 10;
        n = rng.nextInt(3);
        n2 = rng.nextInt(2);
        switch (n) {
            case 0:
                switch (n2) {
                    case 0:
                        startX = 1;
                        break;
                    case 1:
                        startX = mapSizeX - 1;
                        break;
                }
                break;
            case 1:
                switch (n2) {
                    case 0:
                        startY = 1;
                        break;
                    case 1:
                        startY = mapSizeY - 1;
                        break;
                }
                break;
            case 2:
                startZ = mapSizeZ - 1;
                forceZ = mapSizeZ * 4 / 5;
                break;
        }

        if (startX < mapSizeX / 5) {
            xGrow = true;
        }
        if (startY < mapSizeY / 5) {
            yGrow = true;
        }
        if (startX > (mapSizeX * 4 / 5)) {
            xGrow = false;
        }
        if (startY > (mapSizeY * 4 / 5)) {
            yGrow = false;
        }

        int currentX = startX;
        int currentY = startY;
        for (int l = startZ; ((forceZ > 0) && (l >= 0)); l--) {
            forceX = 10;
            forceY = 5;
            for (int i = currentX - forceX; i < currentX + forceX; i++) {
                for (int k = currentY - forceY; k < currentY + forceY; k++) {
                    if (pointInBounds(i, k, l)) {
                        mapContents[i][k][l].setFlooring(new DiggableWall(intruder));
                    }
                    forceY += rng.nextInt(3) - 1;
                    forceX += rng.nextInt(3) - 1;
                }
            }

//            int currentX = startX;
//            int currentY = startY;
            forceZ -= 1;
        }
    }

    private void extrudeMap() {
        TerrainObject terrain;
        DiggableWall tempWall = new DiggableWall(Stone.GRANITE);
        DiggableWall extruder = extrusiveList.get(rng.nextInt(extrusiveList.size()));
        int x = -1, y = -1, z = -1;

        test_for_igneous:
        for (int l = 0; l < mapSizeZ; l++) {
            for (int n = 0; n < maxRecurse; n++) {
                x = rng.nextInt(mapSizeX - 2) + 1;
                y = rng.nextInt(mapSizeY - 2) + 1;
                terrain = mapContents[x][y][l].getFlooring();
                if (terrain instanceof DiggableWall) {
                    tempWall = (DiggableWall) terrain;
                    if ((tempWall.extrusive) || (tempWall.intrusive)) {
                        z = l;
                        break test_for_igneous;
                    }
                }
            }
        }
        if (pointInBounds(x, y, z)) {
            int sizeX, sizeY;
            double n;
            for (int l = z; l >= 0; l--) {
//                do {
                sizeX = rng.nextInt(5) + 10;
                sizeY = rng.nextInt(3) + 10;
//                } while ((sizeX == 0) || (sizeY == 0));
                for (int i = x - sizeX; i < x + sizeX; i++) {
                    for (int k = y - sizeY; k < y + sizeY; k++) {
                        n = (Math.pow((double) (x - i) / sizeX, 1) + Math.pow((double) (y - k) / sizeX, 1));
                        if (n < 1) {//code for oval shape
                            if (pointInBounds(i, k, l)) {
                                mapContents[i][k][l].setFlooring(new DiggableWall(extruder));

                            }
                        }
                    }
                }
            }
        }
    }

    private void metamorphoseMap() {
        TerrainObject[][][] near = new TerrainObject[3][3][3];
        DiggableWall wall;
        DiggableWall changer;
        int changetrack = 0;
        boolean changing, igneous, sedimentary;
        changer = metamorphicList.get(rng.nextInt(metamorphicList.size()));
        for (int j = 0; j < mapSizeZ; j++) {
            changetrack++;
            if (changetrack > 4) {
                changer = metamorphicList.get(rng.nextInt(metamorphicList.size()));
            }
            for (int i = 1; i < mapSizeX - 1; i++) {
                for (int k = 1; k < mapSizeY - 1; k++) {
                    changing = false;
                    igneous = false;
                    sedimentary = false;
                    for (int a = 0; a < 3; a++) {//build array of near objects
                        for (int b = 0; b < 3; b++) {
                            for (int c = 0; c < 3; c++) {
                                near[a][b][c] = pointInBounds(i + a - 1, k + b - 1, j + c - 1) ? mapContents[i + a - 1][k + b - 1][j + c - 1].getFlooring() : null;
                            }
                        }
                    }

                    test_for_change:
                    for (TerrainObject testing1[][] : near) {
                        for (TerrainObject testing2[] : testing1) {
                            for (TerrainObject test : testing2) {
                                if (test instanceof DiggableWall) {
                                    wall = (DiggableWall) test;
                                    if (wall.sedimentary) {
                                        sedimentary = true;
                                    }
                                    if (wall.extrusive || wall.intrusive) {
                                        igneous = true;
                                    }
                                    if (sedimentary && igneous) {
                                        changing = true;
                                        break test_for_change;
                                    }
                                }
                            }
                        }
                    }

                    if (changing) {
                        if (pointInBounds(i, k, j)) {
                            if (rng.nextInt(100) < 45) {
                                mapContents[i][k][j].setFlooring(new DiggableWall(changer));
                            }
                        }
                    }
                }
            }
        }
        for (int j = mapSizeZ; j > 0; j--) {
            changetrack++;
            if (changetrack > 4) {
                changer = metamorphicList.get(rng.nextInt(metamorphicList.size()));
            }
            for (int i = mapSizeX - 1; i > 1; i--) {
                for (int k = mapSizeY - 1; k > 1; k--) {
                    changing = false;
                    igneous = false;
                    sedimentary = false;
                    for (int a = 0; a < 3; a++) {//build array of near objects
                        for (int b = 0; b < 3; b++) {
                            for (int c = 0; c < 3; c++) {
                                near[a][b][c] = pointInBounds(i + a - 1, k + b - 1, j + c - 1) ? mapContents[i + a - 1][k + b - 1][j + c - 1].getFlooring() : null;
                            }
                        }
                    }

                    test_for_change:
                    for (TerrainObject testing1[][] : near) {
                        for (TerrainObject testing2[] : testing1) {
                            for (TerrainObject test : testing2) {
                                if (test instanceof DiggableWall) {
                                    wall = (DiggableWall) test;
                                    if (wall.sedimentary) {
                                        sedimentary = true;
                                    }
                                    if (wall.extrusive || wall.intrusive) {
                                        igneous = true;
                                    }
                                    if (sedimentary && igneous) {
                                        changing = true;
                                        break test_for_change;
                                    }
                                }
                            }
                        }
                    }

                    if (changing) {
                        if (pointInBounds(i, k, j)) {
                            if (rng.nextInt(100) < 25) {
                                mapContents[i][k][j].setFlooring(new DiggableWall(changer));
                            }
                        }
                    }
                }
            }
        }
    }
}
