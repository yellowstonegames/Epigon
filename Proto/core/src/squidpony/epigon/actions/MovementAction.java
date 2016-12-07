package squidpony.epigon.actions;

import squidpony.epigon.data.specific.Physical;
import squidpony.squidgrid.Direction;
import squidpony.squidmath.Coord;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Represents an action attempting to move the target.
 *
 * @author Eben Howard - http://squidpony.com
 */
public class MovementAction implements Action {

    public Coord start;
    public Physical mover;
    public String key;
    public boolean forced;
    public Queue<Coord> moveList;//movements in order of execution

    /**
     * Constructor which sets the movement type to default and forced to false.
     *
     * @param start
     * @param mover
     * @param end
     */
    public MovementAction(Coord start, Physical mover, Coord end) {
        moveList = new LinkedList<>();
        moveList.add(end);
        initialize(start, mover, "", false, moveList);
    }

    /**
     * Constructor which sets the end point as provided.
     *
     * @param start
     * @param mover
     * @param key
     * @param forced
     * @param end
     */
    public MovementAction(Coord start, Physical mover, String key, boolean forced, Coord end) {
        moveList = new LinkedList<>();
        moveList.add(end);
        initialize(start, mover, key, forced, moveList);
    }

    /**
     * Constructor which sets the point in the given direction as the end point.
     *
     * @param start
     * @param mover
     * @param key
     * @param forced
     * @param dir
     */
    public MovementAction(Coord start, Physical mover, String key, boolean forced, Direction dir) {
        moveList = new LinkedList<>();
        moveList.add(Coord.get(start.x + dir.deltaX, start.y + dir.deltaY));
        initialize(start, mover, key, forced, moveList);
    }

    /**
     * Constructor which takes a complete queue of points for the movement path.
     * The starting location should not be in the queue, but the end location
     * must be.
     *
     * @param start
     * @param mover
     * @param key
     * @param forced
     * @param moveList
     */
    public MovementAction(Coord start, Physical mover, String key, boolean forced, Queue<Coord> moveList) {
        initialize(start, mover, key, forced, moveList);
    }

    private void initialize(Coord start, Physical mover, String key, boolean forced, Queue<Coord> moveList) {
        this.start = start;
        this.mover = mover;
        this.key = key;
        this.forced = forced;
        this.moveList = moveList;
    }
}
