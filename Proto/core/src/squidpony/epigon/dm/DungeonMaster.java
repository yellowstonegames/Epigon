package squidpony.epigon.dm;

import java.util.List;
import squidpony.epigon.actions.Action;
import squidpony.epigon.data.specific.Condition;
import squidpony.epigon.data.specific.Physical;

/**
 * Controls the overall flow of the game world.
 *
 * @author Eben Howard - http://squidpony.com
 */
public class DungeonMaster {

    private TimeLine timeline = new TimeLine();
    private double gameTime;

    private void runTurn() {
        Action action = timeline.next();
//        if (action.time() >= 0) {
//            gameTime = action.time();//update to latest time if action wasn't instantaneous
//        }
//        action.apply();
    }

    /**
     * Removes the provided condition. This may cause other conditions to become unsuppressed.
     *
     * @param condition
     * @param canceled true if it's being removed due to cancellation rather than expiration
     */
    private void detachCondition(Physical thing, Condition condition, boolean cancelled) {
        thing.conditions.remove(condition);
        for (Condition c : thing.conditions) {
            c.suppresedBys.remove(condition);//remove it from the list of suppressing conditions if it's there
        }
        if (cancelled) {
            applyActions(condition.parent.cancelledActions);
        } else {
            applyActions(condition.parent.wearsOffActions);
        }
    }

    private void applyActions(List<Action> actions) {

    }
}
