package squidpony.epigon.combat;

import squidpony.epigon.GauntRNG;
import squidpony.epigon.data.*;
import squidpony.epigon.data.quality.Element;
import squidpony.squidmath.Noise;
import squidpony.squidmath.NumberTools;

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
    public Weapon actorWeapon, targetWeapon;
    public Element element;
    public String actorCondition, targetCondition;
    public ActionOutcome()
    {
    }
    public static ActionOutcome attack(Physical actor, Physical target)
    {
        ActionOutcome ao = new ActionOutcome();
        long r = GauntRNG.nextLong(++actor.chaos);
        ao.actorWeapon = actor.creatureData == null ? Weapon.randomUnarmedWeapon(r - 3L) : actor.creatureData.weaponChoices.random();
        int index = ao.actorWeapon.elements.table.random(r - 1);
        ao.element = ao.actorWeapon.elements.items.get(index);
        ao.targetCondition = index < ao.actorWeapon.statuses.size() ? ao.actorWeapon.statuses.get(index) : GauntRNG.getRandomElement(r - 2, ao.actorWeapon.statuses);
        ao.targetWeapon = target.creatureData == null ? Weapon.randomUnarmedWeapon(r - 4L) : target.creatureData.weaponChoices.random();
        int actorSkill = 1, targetSkill = 1;
        if(actor.creatureData != null)
        {
            for (int i = 0; i < ao.actorWeapon.skills.length; i++) {
                actorSkill += actor.creatureData.skills.getOrDefault(ao.actorWeapon.skills[i], Rating.NONE).ordinal();
            }
        }
        if(target.creatureData != null)
        {
            for (int i = 0; i < ao.targetWeapon.skills.length; i++) {
                targetSkill += target.creatureData.skills.getOrDefault(ao.targetWeapon.skills[i], Rating.NONE).ordinal();
            }
        }
        actor.statEffects.add(ao.actorWeapon.calcStats);
        target.statEffects.add(ao.targetWeapon.calcStats);
        ChangeTable.holdPhysical(actor, actor.statEffects);
        //System.out.println("Attacker is " + actor.name + " with base stats " + actor.stats + " and adjusted stats: " + tempActorStats);
        ChangeTable.holdPhysical(target, target.statEffects);
        //System.out.println("Defender is " + target.name  + " with base stats " + target.stats + " and adjusted stats: " + tempTargetStats);
        
        ao.crit = (5 + (actor.actualStat(CalcStat.CRIT) - target.actualStat(CalcStat.STEALTH))) >= GauntRNG.nextInt(r, 50);
        double actorPrecision = actor.actualStat(CalcStat.PRECISION) + actorSkill,
                targetEvasion = target.actualStat(CalcStat.EVASION) + targetSkill;
        ao.hit = (67 + 5 * ((ao.crit ? 2 : 0) + actorPrecision - targetEvasion)) >= GauntRNG.next(r + 1, 7);
//        ao.hit = (67 + 5 * ((ao.crit ? 2 : 0) + actor.stats.getOrDefault(CalcStat.PRECISION, LiveValue.ZERO).actual() -
//                target.stats.getOrDefault(CalcStat.EVASION, LiveValue.ZERO).actual())) >= GauntRNG.next(r + 1, 7);
        if(ao.hit)
        {
            ao.attemptedDamage = Math.min(0, Noise.fastFloor((NumberTools.randomFloatCurved(r + 2) * 0.4f - 0.45f) * ((ao.crit ? 2 : 1) +
                    actor.actualStat(CalcStat.DAMAGE) + actorSkill)));
            ao.actualDamage = Math.min(0, ao.attemptedDamage -
                    Noise.fastFloor((NumberTools.randomFloatCurved(r + 3) * 0.3f + 0.35f) * (target.actualStat(CalcStat.DEFENSE) +  targetSkill)));
            ao.targetConditioned = (35 + 5 * ((ao.crit ? 1 : 0) + actor.actualStat(CalcStat.INFLUENCE) + actorSkill -
                    target.actualStat(CalcStat.LUCK) - targetSkill)) >= GauntRNG.next(r + 4, 8);
        }
        ChangeTable.releasePhysical(actor, actor.statEffects);
        ChangeTable.releasePhysical(target, target.statEffects);
        actor.statEffects.removeLast();
        target.statEffects.removeLast();
        if(ao.targetConditioned)
        {
            Condition c = new Condition(ConditionBlueprint.CONDITIONS.getOrDefault(ao.targetCondition, ConditionBlueprint.CONDITIONS.getAt(0)), target, ao.element);
            ChangeTable.strikePhysical(target, c.parent.changes);
            target.conditions.add(c);
            
        }
        return ao;
    }
}
