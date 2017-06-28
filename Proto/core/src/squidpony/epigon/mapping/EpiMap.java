package squidpony.epigon.mapping;

import java.util.Queue;

import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidmath.Coord;

import squidpony.epigon.actions.Action;
import squidpony.epigon.actions.MovementAction;
import squidpony.epigon.data.specific.Physical;

/**
 * This represents a single explorable map level.
 *
 * Each cell is considered to be 1 meter by 1 meter square.

 A null tile represents open space with no special properties or opacities to things passing
 through. They should not be considered a vacuum, but rather normal air.
 *
 * @author Eben Howard - http://squidpony.com
 */
public class EpiMap {

    public int width, height;
    public EpiTile[][] contents;
    public boolean[][] hasBeenSeen;

    public EpiMap(int width, int height) {
        this.width = width;
        this.height = height;
        contents = new EpiTile[width][height];
        hasBeenSeen = new boolean[width][height];
    }

    public EpiMap(EpiTile[][] contents) {
        this.contents = contents;
        this.width = contents.length;
        this.height = contents[0].length;
    }

    public EpiMap() {
        this(80, 30);
    }

    public EpiMap(char[][] map) {
        width = map.length;
        height = map[0].length;
        contents = new EpiTile[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                char c = altSymbolOf(map[x][y]);
                SColor color = colorOf(c);
                EpiTile tile = new EpiTile();
                Physical floor = new Physical();
                floor.color = color;
                floor.symbol = c;
                tile.floor = floor;
                contents[x][y] = tile;
            }
        }

    }

    /**
     * Returns true if the provided action is valid.
     *
     * @param action
     * @return
     */
    public boolean actionValid(Action action) {
        if (action instanceof MovementAction) {
            MovementAction move = (MovementAction) action;
            Queue<Coord> points = move.moveList;
            return points.stream().parallel().noneMatch(p -> (
                !inBounds(p)
                || contents[p.x][p.y].getLargeObject() != null
                || (move.mover.creatureData != null && contents[p.x][p.y].getCreature() != null)));
        }

        return false;//action type not dealt with so default to not valid
    }

    public String doAction(Action action) {
        if (actionValid(action)) {
            if (action instanceof MovementAction) {
                MovementAction move = (MovementAction) action;
                Coord p;
                Coord s = move.mover.location;
                do {
                    p = move.moveList.poll();
                    contents[s.x][s.y].remove(move.mover);
                    contents[p.x][p.y].add(move.mover);
                    move.mover.location = p;
                    s = p;
                } while (!move.moveList.isEmpty());
                return "Move completed.";
            }
        }

        return "Invalid action.";
    }

    public boolean inBounds(Coord p) {
        return inBounds(p.x, p.y);
    }

    public boolean inBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    public double[][] opacities(){
        double[][] resistances = new double[width][height];
        for (int x = 0; x < width; x++){
            for(int y = 0; y < height; y++){
                resistances[x][y] = contents[x][y].opeacity();
            }
        }
        return resistances;
    }

    public char[][] simpleChars() {
        char[][] ret = new char[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                ret[x][y] = contents[x][y].getSymbol();
            }
        }
        return ret;
    }

    public static char altSymbolOf(char symbol) {

        switch (symbol) {
            case '"':
                return '¸';
            case '¸'://grass
            case '~':
                return '≈';
            case 'ø':
                return ' ';
            default://opaque items
                return symbol;
        }
    }

    public static SColor colorOf(char symbol) {
        SColor color;
        switch (symbol) {
            case '.'://stone ground
                color = SColor.SLATE_GRAY;
                break;
            case '"':
                color = SColor.GREEN;
                break;
            case '¸'://grass
                color = SColor.GREEN;
                break;
            case ','://pathway
                color = SColor.STOREROOM_BROWN;
                break;
            case 'c':
                color = SColor.SEPIA;
                break;
            case '/':
                color = SColor.BROWNER;
                break;
            case '~':
                color = SColor.AZUL;
                break;
            case '≈':
                color = SColor.AZUL;
                break;
            case '<':
            case '>':
                color = SColor.SLATE_GRAY;
                break;
            case 't':
                color = SColor.BROWNER;
                break;
            case 'm':
                color = SColor.BAIKO_BROWN;
                break;
            case 'u':
                color = SColor.TAN;
                break;
            case 'T':
            case '₤':
                color = SColor.FOREST_GREEN;
                break;
            case 'E':
                color = SColor.SILVER;
                break;
            case 'S':
                color = SColor.BREWED_MUSTARD_BROWN;
                break;
            case '#':
                color = SColor.SLATE_GRAY;
                break;
            case '+':
                color = SColor.BROWNER;
                break;
            case 'A':
                color = SColor.ALICE_BLUE;
                break;
            case 'ø':
            case ' ':
                color = SColor.DB_INK;
                break;
            default://opaque items
                color = SColor.DEEP_PINK;
        }
        return color;
    }
}
