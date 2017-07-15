package squidpony.epigon.data.generic;

import squidpony.epigon.data.specific.Physical;
import squidpony.epigon.universe.LiveValue;
import squidpony.epigon.universe.Stat;

/**
 * A way to describe what calculations should be performed at run time.
 *
 * @author Eben Howard
 */
public class Formula {

    public static double baseHitChance(Physical source, Physical target) {
        LiveValue sourceAim = source.stats.get(Stat.AIM);
        if (sourceAim == null || sourceAim.actual() <= 0.0) {
            return 0.0;
        }
        double aim = sourceAim.actual();
        double chance = aim;
        LiveValue targetDodge = target.stats.get(Stat.DODGE);
        if (targetDodge == null || targetDodge.actual() <= 0) {
            return chance;
        }

        double dodge = targetDodge.actual();
        double difference = chance - dodge;
        int magnitude = 1;
        while (difference > 10){
            magnitude++;
            difference /= 10;
        }
        double percent = 1 - (dodge / aim) % 1;

        for (int i = 1; i < dodge / aim; i++) {
            chance *= 0.5;
        }
        chance = 0.5 * (chance + chance * percent);

        return chance / magnitude;
    }

    public static double beserkDamage(Physical source) {
        LiveValue lv = source.stats.get(Stat.IMPACT);
        if (lv == null) {
            return 0.0;
        }
        double val = lv.actual();
        lv = source.stats.get(Stat.LIFE_FORCE);
        if (lv == null) {
            return val;
        }
        if (lv.actual() / lv.base() < 0.2) {
            val *= 3;
        }
        return val;
    }
}
