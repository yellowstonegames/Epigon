package squidpony.epigon.actions;

import squidpony.epigon.data.specific.Condition;
import squidpony.epigon.data.mixin.Creature;

/**
 * Attempts to add a condition to the target source.
 *
 * @author Eben Howard - http://squidpony.com
 */
public class ConditionAddAction implements Action {

    private Creature target;
    private Condition condition;
    private boolean succeeded = false;

    public ConditionAddAction(Creature target, Condition condition) {
        this.target = target;
        this.condition = condition;
    }

    public boolean wouldSucceed() {
//        return !target.hasCondition(condition.parent) && !target.immune(condition.parent);
        return true;
    }

    public boolean isValid() {
        return wouldSucceed();//nothing prevents conditions except immunities
    }

    public void apply() {
        if (wouldSucceed()) {
//            target.applyCondition(condition);
            succeeded = true;
        }
    }

    public boolean wasSuccessful() {
        return succeeded;
    }

    public long time() {
        return 0;//should be immediate
    }
}
