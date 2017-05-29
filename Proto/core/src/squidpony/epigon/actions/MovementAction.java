package squidpony.epigon.actions;

import java.util.LinkedList;
import java.util.Queue;

import squidpony.squidgrid.Direction;
import squidpony.squidmath.Coord;

import squidpony.epigon.data.specific.Physical;
import squidpony.epigon.universe.Element;

/**
 * Represents an action attempting to move the target.
 *
 * @author Eben Howard
 */
public class MovementAction implements Action {

    public Coord start;
    public Physical mover;
    public Element element;
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
        initialize(start, mover, null, false, moveList);
    }

    /**
     * Constructor which sets the end point as provided.
     *
     * @param start
     * @param mover
     * @param element
     * @param forced
     * @param end
     */
    public MovementAction(Coord start, Physical mover, Element element, boolean forced, Coord end) {
        moveList = new LinkedList<>();
        moveList.add(end);
        initialize(start, mover, element, forced, moveList);
    }

    /**
     * Constructor which sets the point in the given direction as the end point.
     *
     * @param start
     * @param mover
     * element key
     * @param forced
     * @param dir
     */
    public MovementAction(Coord start, Physical mover, Element element, boolean forced, Direction dir) {
        moveList = new LinkedList<>();
        moveList.add(Coord.get(start.x + dir.deltaX, start.y + dir.deltaY));
        initialize(start, mover, element, forced, moveList);
    }

    /**
     * Constructor which takes a complete queue of points for the movement path. The starting
     * location should not be in the queue, but the end location must be.
     *
     * @param start
     * @param mover
     * @param element
     * @param forced
     * @param moveList
     */
    public MovementAction(Coord start, Physical mover, Element element, boolean forced, Queue<Coord> moveList) {
        initialize(start, mover, element, forced, moveList);
    }

    private void initialize(Coord start, Physical mover, Element element, boolean forced, Queue<Coord> moveList) {
        this.start = start;
        this.mover = mover;
        this.element = element;
        this.forced = forced;
        this.moveList = moveList;
    }
}
