package squidpony.epigon.mapping;

import java.util.Queue;

import squidpony.epigon.actions.Action;
import squidpony.epigon.actions.MovementAction;

import squidpony.squidmath.Coord;

/**
 * This represents a single explorable map level.
 *
 * Each cell is considered to be 1 meter by 1 meter square.
 *
 * A null tile represents open space with no special properties or resistances to things passing
 * through. They should not be considered a vacuum, but rather normal air.
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

    /**
     * Returns true if the provided action is valid.
     *
     * @param action
     * @return
     */
    public boolean actionValid(Action action) {
        if (action instanceof MovementAction) {
            MovementAction move = (MovementAction) action;
            String key = move.key;
            Queue<Coord> points = move.moveList;
            for (Coord p : points) {
                if (!inBounds(p) || !contents[p.x][p.y].isPassable(key) || (move.mover.creatureData != null && contents[p.x][p.y].creature != null)) {
                    return false;//found a blocking area
                }
            }
            return true;//no blocking areas found
        }

        return false;//action type not dealt with so default to not valid
    }

    public String doAction(Action action) {
        if (actionValid(action)) {
            if (action instanceof MovementAction) {
                MovementAction move = (MovementAction) action;
                Coord p;
                do {
                    p = move.moveList.poll();
                } while (!move.moveList.isEmpty());
                contents[move.start.x][move.start.y].remove(move.mover);
                contents[p.x][p.y].add(move.mover);
                move.mover.location = p;
                return "Move completed.";
            }
        }

        return "Invalid action.";
    }

    public boolean inBounds(Coord p) {
        return p.x >= 0 && p.x < width && p.y >= 0 && p.y < height;
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
}
