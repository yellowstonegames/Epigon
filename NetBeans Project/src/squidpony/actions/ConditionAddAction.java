package squidpony.actions;

import squidpony.data.specific.Condition;
import squidpony.data.specific.Creature;

/**
 * Attempts to add a condition to the target source.
 *
 * @author Eben Howard - http://squidpony.com - howard@squidpony.com
 */
public class ConditionAddAction implements Action {

    private Creature target;
    private Condition condition;
    private boolean succeeded = false;

    public ConditionAddAction(Creature target, Condition condition) {
        this.target = target;
        this.condition = condition;
    }

    @Override
    public boolean wouldSucceed() {
        return !target.hasCondition(condition.parent) && !target.immune(condition.parent);
    }

    @Override
    public boolean isValid() {
        return wouldSucceed();//nothing prevents conditions except immunities
    }

    @Override
    public void apply() {
        if (wouldSucceed()) {
            target.applyCondition(condition);
            succeeded = true;
        }
    }

    @Override
    public boolean wasSuccessful() {
        return succeeded;
    }

    @Override
    public long time() {
        return 0;//should be immediate
    }
}
