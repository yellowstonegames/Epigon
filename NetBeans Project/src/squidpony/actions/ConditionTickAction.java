package squidpony.actions;

import squidpony.data.specific.Condition;

/**
 * This Action is a wrapper for what happens when an applied Condition reaches
 * its timer.
 *
 * Includes a callback to the Condition to allow for replacement in the
 * timeline.
 *
 * @author Eben Howard - http://squidpony.com - howard@squidpony.com
 */
public class ConditionTickAction implements Action<Condition> {

    private Condition condition;
    private Action action;
    private long time;

    public ConditionTickAction(Condition condition, Action action, long time) {
        this.condition = condition;
        this.action = action;
        this.time = time;
    }

    @Override
    public boolean wouldSucceed() {
        return action.wouldSucceed();
    }

    @Override
    public boolean isValid() {
        return action.isValid();
    }

    @Override
    public void apply() {
        action.apply();
        condition.firedConditionTick(condition, action.wasSuccessful());
    }

    @Override
    public boolean wasSuccessful() {
        return action.wasSuccessful();
    }

    @Override
    public long time() {
        return time;
    }

    @Override
    public Condition source() {
        return condition;
    }
}
