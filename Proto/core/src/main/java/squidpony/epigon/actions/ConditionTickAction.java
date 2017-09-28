package squidpony.epigon.actions;

import squidpony.epigon.data.specific.Condition;


/**
 * This Action is a wrapper for what happens when an applied Condition reaches
 * its timer.
 *
 * Includes a callback to the Condition to allow for replacement in the
 * timeline.
 *
 * @author Eben Howard - http://squidpony.com
 */
public class ConditionTickAction implements Action {

    private Condition condition;
    private Action action;
    private long time;
}
