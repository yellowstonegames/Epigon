package squidpony.epigon.combat;

import squidpony.epigon.GauntRNG;
import squidpony.epigon.data.specific.Physical;
import squidpony.epigon.universe.CalcStat;
import squidpony.epigon.universe.LiveValue;
import squidpony.squidmath.Noise;
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
        ao.crit = (20 + 4 * (actor.stats.getOrDefault(CalcStat.CRIT, LiveValue.ZERO).actual() + actor.weaponData.calcStats[CRIT] -
                target.stats.getOrDefault(CalcStat.STEALTH, LiveValue.ZERO).actual() - target.weaponData.calcStats[STEALTH])) >= GauntRNG.next(r, 9);
        ao.hit = (67 + 5 * ((ao.crit ? 2 : 0) + actor.stats.getOrDefault(CalcStat.PRECISION, LiveValue.ZERO).actual() + actor.weaponData.calcStats[PRECISION] -
                target.stats.getOrDefault(CalcStat.EVASION, LiveValue.ZERO).actual() - target.weaponData.calcStats[EVASION])) >= GauntRNG.next(r + 1, 7);
        if(ao.hit)
        {
            ao.attemptedDamage = Math.min(0, Noise.fastFloor((NumberTools.randomFloatCurved(r + 2) * 0.4f - 0.45f) * ((ao.crit ? 2 : 1) +
                    actor.stats.getOrDefault(CalcStat.DAMAGE, LiveValue.ZERO).actual() + actor.weaponData.calcStats[DAMAGE])));
            ao.actualDamage = Math.min(0, ao.attemptedDamage -
                    Noise.fastFloor((NumberTools.randomFloatCurved(r + 3) * 0.3f + 0.35f) * (target.stats.getOrDefault(CalcStat.DEFENSE, LiveValue.ZERO).actual() + target.weaponData.calcStats[DEFENSE])));
            ao.targetConditioned = (35 + 5 * ((ao.crit ? 1 : 0) + actor.stats.getOrDefault(CalcStat.INFLUENCE, LiveValue.ZERO).actual() + actor.weaponData.calcStats[INFLUENCE] -
                    actor.stats.getOrDefault(CalcStat.LUCK, LiveValue.ZERO).actual() - target.weaponData.calcStats[LUCK])) >= GauntRNG.next(r + 4, 8);
        }
        return ao;
    }
}
