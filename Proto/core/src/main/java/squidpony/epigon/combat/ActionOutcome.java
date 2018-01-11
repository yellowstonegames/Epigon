package squidpony.epigon.combat;

import squidpony.epigon.data.generic.Modification;
import squidpony.epigon.data.specific.Physical;

/**
 * Handles the specific result of one event in a combat, such as whether a sword swing hit, how much damage it did, if
 * a crit occurred (which makes a hit more likely and damage higher, but does not guarantee either), and if conditions
 * were changed, but does not apply to more than one pairing of actor and target. This means an area-of-effect action
 * has multiple ActionOutcomes associated with it, one per target (and a target does not have to be a creature).
 * <br>
 * Created by Tommy Ettinger on 1/10/2018.
 */
public class ActionOutcome {
    public Modification actorChange, targetChange;
    public ActionOutcome()
    {
        actorChange = new Modification();
        targetChange = new Modification();
    }
    // no-op currently
    public static ActionOutcome attack(Physical actor, Physical target)
    {
        ActionOutcome ao = new ActionOutcome();
        return ao;
    }
}
