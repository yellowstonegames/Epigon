package squidpony.epigon.combat;

import squidpony.epigon.GauntRNG;
import squidpony.epigon.ImmutableKey;
import squidpony.epigon.data.specific.Physical;
import squidpony.epigon.universe.CalcStat;
import squidpony.epigon.universe.LiveValue;
import squidpony.squidmath.Noise;
import squidpony.squidmath.NumberTools;
import squidpony.squidmath.OrderedMap;

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
    public static final OrderedMap<ImmutableKey, LiveValue> tempActorStats = new OrderedMap<>(32, 0.5f, ImmutableKey.ImmutableKeyHasher.instance),
            tempTargetStats = new OrderedMap<>(32, 0.5f, ImmutableKey.ImmutableKeyHasher.instance);
    public static void deepCopyInto(OrderedMap<ImmutableKey, LiveValue> source, OrderedMap<ImmutableKey, LiveValue> toFill)
    {
        final int len = source.size();
        toFill.clear();
        for (int i = 0; i < len; i++) {
            toFill.put(source.keyAt(i), new LiveValue(source.getAt(i)));
        }
    }
    public static ActionOutcome attack(Physical actor, Physical target)
    {
        ActionOutcome ao = new ActionOutcome();
        long r = determine(++actor.chaos);
        deepCopyInto(actor.stats, tempActorStats);
        for (int i = 0; i < actor.statEffects.size(); i++) {
            actor.statEffects.getAt(i).changeLiveValues(tempActorStats);
        }
        //System.out.println("Attacker is " + actor.name + " with base stats " + actor.stats + " and adjusted stats: " + tempActorStats);
        deepCopyInto(target.stats, tempTargetStats);
        for (int i = 0; i < target.statEffects.size(); i++) {
            target.statEffects.getAt(i).changeLiveValues(tempTargetStats);
        }
        //System.out.println("Defender is " + target.name  + " with base stats " + target.stats + " and adjusted stats: " + tempTargetStats);
        ao.crit = (20 + 4 * (tempActorStats.getOrDefault(CalcStat.CRIT, LiveValue.ZERO).actual() -
                tempTargetStats.getOrDefault(CalcStat.STEALTH, LiveValue.ZERO).actual())) >= GauntRNG.next(r, 9);
        ao.hit = (67 + 5 * ((ao.crit ? 2 : 0) + tempActorStats.getOrDefault(CalcStat.PRECISION, LiveValue.ZERO).actual() -
                tempTargetStats.getOrDefault(CalcStat.EVASION, LiveValue.ZERO).actual())) >= GauntRNG.next(r + 1, 7);
        if(ao.hit)
        {
            ao.attemptedDamage = Math.min(0, Noise.fastFloor((NumberTools.randomFloatCurved(r + 2) * 0.4f - 0.45f) * ((ao.crit ? 2 : 1) +
                    tempActorStats.getOrDefault(CalcStat.DAMAGE, LiveValue.ZERO).actual())));
            ao.actualDamage = Math.min(0, ao.attemptedDamage -
                    Noise.fastFloor((NumberTools.randomFloatCurved(r + 3) * 0.3f + 0.35f) * tempTargetStats.getOrDefault(CalcStat.DEFENSE, LiveValue.ZERO).actual()));
            ao.targetConditioned = (35 + 5 * ((ao.crit ? 1 : 0) + tempActorStats.getOrDefault(CalcStat.INFLUENCE, LiveValue.ZERO).actual() -
                    tempTargetStats.getOrDefault(CalcStat.LUCK, LiveValue.ZERO).actual())) >= GauntRNG.next(r + 4, 8);
        }
        return ao;
    }
}
