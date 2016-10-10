package squidpony.epigon.data.generic;

import java.util.LinkedList;
import java.util.Queue;
import squidpony.epigon.actions.Action;

/**
 *
 * @author SquidPony
 */
public class Effect {

    private Queue<Effect> followUpOnSuccess = new LinkedList<>();
    private Queue<Effect> followUpOnFailure = new LinkedList<>();
    public Action action;

    public Effect getNextSuccessFollowUp() {
        return followUpOnSuccess.poll();
    }

    public Effect getNextFailureFollowUp() {
        return followUpOnFailure.poll();
    }
}
