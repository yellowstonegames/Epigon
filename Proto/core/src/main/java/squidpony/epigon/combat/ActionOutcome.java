package squidpony.epigon.combat;

import com.badlogic.gdx.math.MathUtils;
import squidpony.epigon.GauntRNG;
import squidpony.epigon.data.specific.Physical;
import squidpony.squidmath.NumberTools;

import static squidpony.epigon.data.specific.Physical.*;
import static squidpony.squidmath.ThrustAltRNG.determine;

/**
 * Handles the specific result of one event in a combat, such as whether a sword swing hit, how much damage it did, if
 * a crit occurred (which makes a hit more likely and damage higher, but does not guarantee either), and if conditions
 * were changed, but does not apply to more than one pairing of actor and target. This means an area-of-effect action
 * has multiple ActionOutcomes associated with it, one per target (and a target does not have to be a creature).
 * <br>
 * Created by Tommy Ettinger on 1/10/2018.
 */
public class ActionOutcome {
    public boolean crit, hit, targetConditioned, actorConditioned;
    public int attemptedDamage, actualDamage, actorDamage;
    public ActionOutcome()
    {
    }

    public static ActionOutcome attack(Physical actor, Physical target)
    {
        ActionOutcome ao = new ActionOutcome();
        long r = determine(++actor.chaos);
        ao.crit = (20 + 4 * (actor.calcStats[CRIT] + actor.weaponData.calcStats[CRIT] -
                target.calcStats[STEALTH] - target.weaponData.calcStats[STEALTH])) >= GauntRNG.next(r, 9);
        ao.hit = (67 + 5 * ((ao.crit ? 2 : 0) + actor.calcStats[PRECISION] + actor.weaponData.calcStats[PRECISION] -
                target.calcStats[EVASION] - target.weaponData.calcStats[EVASION])) >= GauntRNG.next(r + 1, 7);
        if(ao.hit)
        {
            ao.attemptedDamage = Math.min(0, MathUtils.floor((NumberTools.randomFloatCurved(r + 2) * 0.4f - 0.45f) * ((ao.crit ? 2 : 1) +
                    actor.calcStats[DAMAGE] + actor.weaponData.calcStats[DAMAGE])));
            ao.actualDamage = Math.min(0, ao.attemptedDamage -
                    MathUtils.floor(NumberTools.randomFloatCurved(r + 3) * 0.3f + 0.35f) * (target.calcStats[DEFENSE] + target.weaponData.calcStats[DEFENSE]));
            ao.targetConditioned = (35 + 5 * ((ao.crit ? 1 : 0) + actor.calcStats[INFLUENCE] + actor.weaponData.calcStats[INFLUENCE] -
                    target.calcStats[LUCK] - target.weaponData.calcStats[LUCK])) >= GauntRNG.next(r + 4, 8);
        }
        return ao;
    }
}
