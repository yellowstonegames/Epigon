package squidpony.epigon.dm;

/**
 * The world clock timeline.
 *
 * All actions and ticking conditions should register with this class in order
 * to be activated on schedule.
 *
 * Times are in long integers, with each integer being one millisecond of game
 * world time.
 *
 * In the case of a tie in the schedule, the event that was added first will
 * activate first.
 *
 * The time does not necessarily advance, simply the lowest value item is
 * considered the next actor. Classes using this class should adhere to their
 * own timing standards if this behavior is not desired.
 *
 * @author Eben Howard - http://squidpony.com
 */
public class TimeLine { // TODO - rebuild this class

}
