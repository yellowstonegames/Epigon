package squidpony.epigon.mapping;

import squidpony.epigon.data.Physical;
import squidpony.squidgrid.Radius;
import squidpony.squidgrid.gui.gdx.LightingHandler;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidgrid.mapping.DungeonUtility;
import squidpony.squidmath.*;

import static squidpony.epigon.Epigon.rootChaos;

/**
 * This represents a single explorable map level.
 *
 * Each cell is considered to be 1 meter by 1 meter square.
 *
 * A null tile represents open space with no special properties or opacities to things passing through. They should not
 * be considered a vacuum, but rather normal air.
 */
public class EpiMap {

    public int width, height;
    public EpiTile[][] contents;
    public RememberedTile[][] remembered;
    public GreasedRegion seen, tempSeen;
    public char[][] simple;
    public char[][] line;
    public LightingHandler lighting;
    public GreasedRegion downStairPositions, upStairPositions;
    public StatefulRNG chaos;
    public OrderedMap<Coord, Physical> creatures;
    public boolean populated;

    public EpiMap(int width, int height) {
        this.width = width;
        this.height = height;
        lighting = new LightingHandler(new double[width][height]);
        lighting.radiusStrategy = Radius.CIRCLE;
        lighting.backgroundColor = RememberedTile.memoryColorFloat;
        lighting.viewerRange = 0.0;
        chaos = new StatefulRNG(new DiverRNG(rootChaos.nextLong()));
        contents = new EpiTile[width][height];
        remembered = new RememberedTile[width][height];
        downStairPositions = new GreasedRegion(width, height);
        upStairPositions = new GreasedRegion(width, height);
        seen = new GreasedRegion(width, height);
        tempSeen = new GreasedRegion(width, height);
        creatures = new OrderedMap<>();
    }

    public EpiMap() {
        this(3, 3);
    }

    public boolean inBounds(Coord p) {
        return inBounds(p.x, p.y);
    }

    public boolean inBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    public double[][] opacities() {
        double[][] resistances = lighting.resistances;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                resistances[x][y] = contents[x][y].opacity();
            }
        }
        return resistances;
    }

    public char[][] simpleChars() {
        if (simple == null || simple.length != width || simple[0].length != height) {
            simple = new char[width][height];
        }
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                simple[x][y] = contents[x][y].getSymbol();
            }
        }
        line = DungeonUtility.hashesToLines(simple, true);
        return simple;
    }

    public char altSymbolOf(char symbol) {

        switch (symbol) {
            case '¸'://grass
            case '"':
                return '¸';
            case '~':
                return '≈';
            case 'ø':
                return ' ';
            default://opaque items
                return symbol;
        }
    }

    public float colorOf(char symbol) {
        float color;
        switch (symbol) {
            case '.'://stone ground
                color = SColor.SLATE_GRAY.toRandomizedFloat(chaos, 0.05f, 0f, 0.15f);
                break;
            case '"':
            case '¸'://grass
                color = SColor.GREEN.toRandomizedFloat(chaos, 0.08f, 0.05f, 0.18f);
                break;
            case ','://pathway
                color = SColor.STOREROOM_BROWN.toRandomizedFloat(chaos, 0.04f, 0.05f, 0.1f);
                break;
            case 'c':
                color = SColor.SEPIA.toRandomizedFloat(chaos, 0.05f, 0f, 0.15f);
                break;
            case '/':
                color = SColor.BROWNER.toRandomizedFloat(chaos, 0.05f, 0f, 0.15f);
                break;
            case '~':
                color = SColor.AZUL.toRandomizedFloat(chaos, 0.1f, 0f, 0.25f);
                break;
            case '≈':
                color = SColor.CW_FLUSH_BLUE.toRandomizedFloat(chaos, 0.1f, 0f, 0.2f);
                break;
            case '<':
            case '>':
                color = SColor.SLATE_GRAY.toRandomizedFloat(chaos, 0.05f, 0f, 0.15f);
                break;
            case 't':
                color = SColor.BROWNER.toRandomizedFloat(chaos, 0.05f, 0f, 0.15f);
                break;
            case 'm':
                color = SColor.BAIKO_BROWN.toRandomizedFloat(chaos, 0.05f, 0f, 0.15f);
                break;
            case 'u':
                color = SColor.TAN.toRandomizedFloat(chaos, 0.05f, 0f, 0.15f);
                break;
            case 'T':
            case '₤':
                color = SColor.FOREST_GREEN.toRandomizedFloat(chaos, 0.1f, 0f, 0.2f);
                break;
            case 'E':
                color = SColor.SILVER.toRandomizedFloat(chaos, 0.05f, 0f, 0.15f);
                break;
            case 'S':
                color = SColor.BREWED_MUSTARD_BROWN.toRandomizedFloat(chaos, 0.05f, 0f, 0.15f);
                break;
            case '#':
                color = SColor.SLATE_GRAY.toRandomizedFloat(chaos, 0.05f, 0f, 0.15f);
                break;
            case '+':
                color = SColor.BROWNER.toRandomizedFloat(chaos, 0.05f, 0f, 0.15f);
                break;
            case 'A':
                color = SColor.ALICE_BLUE.toRandomizedFloat(chaos, 0.05f, 0f, 0.15f);
                break;
            case 'ø':
            case ' ':
                color = SColor.DB_INK.toRandomizedFloat(chaos, 0.05f, 0f, 0.15f);
                break;
            default://opaque items
                color = SColor.DEEP_PINK.toRandomizedFloat(chaos, 0.05f, 0f, 0.15f);
        }
        return color;
    }
}
