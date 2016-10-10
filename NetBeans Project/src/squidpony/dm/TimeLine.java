package squidpony.dm;

import java.util.Comparator;
import java.util.Iterator;
import java.util.concurrent.PriorityBlockingQueue;
import squidpony.actions.Action;

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
 * This class is thread-safe and can have specific items removed while iterating
 * through it. Note that if objects are being removed by multiple threads, the
 * order of removal is undefined. This is done through the remove(Action action)
 * method as the Iterator.remove() does not work with a specific object.
 *
 * @author Eben Howard - http://squidpony.com - howard@squidpony.com
 */
public class TimeLine implements Iterable<Action>, Iterator<Action> {

    private PriorityBlockingQueue<Action> queue = new PriorityBlockingQueue<>(50, new ActionTimeComparator());

    public void addAction(Action action) {
        queue.add(action);
    }

    @Override
    public Iterator<Action> iterator() {
        return this;
    }

    @Override
    public boolean hasNext() {
        return !queue.isEmpty();
    }

    @Override
    public Action next() {
        return queue.poll();
    }

    /**
     * Returns the next item in the timeline. If there is no next item then will
     * wait for an object to be added before returning if blocking is true or
     * return null if blocking is false or when blocking is true but an
     * InterruptedException occurs.
     *
     * @param blocking
     * @return
     */
    public Action next(boolean blocking) {
        if (blocking) {
            try {
                return queue.take();
            } catch (InterruptedException ex) {//allow cancelling the wait
                return null;
            }
        } else {
            return next();
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    public void remove(Action action) {
        queue.remove(action);
    }

    private class ActionTimeComparator implements Comparator<Action> {

        @Override
        public int compare(Action a1, Action a2) {
            return (int) Math.signum(a1.time() - a2.time());
        }
    }
}
