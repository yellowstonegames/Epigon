package squidpony.epigon.data.generic;

import squidpony.epigon.data.specific.Physical;
import squidpony.epigon.universe.LiveValue;
import squidpony.epigon.universe.Rating;
import squidpony.epigon.universe.Stat;
import squidpony.squidmath.ThrustAltRNG;

/**
 * A way to describe what calculations should be performed at run time.
 *
 * @author Eben Howard
 */
public class Formula {

    private static int[] chances = new int[]{1, 4, 22, 38, 54, 70, 78, 85, 87, 92, 94, 97, 98, 99}; // -5 -> +8
    private static int inflection = 5; // how far into the array is 0 stat difference

    private Formula() {
    }

    public static int randomizedStartingStatLevel(long seed){
        int n = ThrustAltRNG.determineBounded(seed, 100);
        if (n < 45){
            return 1;
        } else if (n < 65){
            return 2;
        } else if (n < 80){
            return 3;
        } else if (n < 88){
            return 4;
        } else if (n < 94){
            return 5;
        } else if (n < 98){
            return 6;
        } else {
            return 7;
        }
    }

    public static int healthForLevel(int level, Rating rating) {
        int base;
        double multiplier;
        switch (rating) {
            case NONE:
                base = 0;
                multiplier = 0;
                break;
            case SLIGHT:
                base = 2;
                multiplier = 1.26;
                break;
            case TYPICAL:
                base = 4;
                multiplier = 1.25;
                break;
            case GOOD:
                base = 6;
                multiplier = 1.24;
                break;
            case HIGH:
                base = 9;
                multiplier = 1.23;
                break;
            case SUPERB:
                base = 13;
                multiplier = 1.22;
                break;
            case AMAZING:
                base = 19;
                multiplier = 1.21;
                break;
            case ULTIMATE:
                base = 29;
                multiplier = 1.20;
                break;
            default:
                base = 0;
                multiplier = 0;
        }

        return (int) Math.floor(base + base * Math.pow(multiplier, level - 1));
    }

    public static int needForLevel(int level, Rating rating) {
        int base;
        double multiplier;
        switch (rating) {
            case NONE:
                base = 0;
                multiplier = 0;
                break;
            case SLIGHT:
                base = 100;
                multiplier = 1.026;
                break;
            case TYPICAL:
                base = 200;
                multiplier = 1.025;
                break;
            case GOOD:
                base = 450;
                multiplier = 1.024;
                break;
            case HIGH:
                base = 700;
                multiplier = 1.023;
                break;
            case SUPERB:
                base = 1200;
                multiplier = 1.022;
                break;
            case AMAZING:
                base = 2100;
                multiplier = 1.021;
                break;
            case ULTIMATE:
                base = 3200;
                multiplier = 1.020;
                break;
            default:
                base = 0;
                multiplier = 0;
        }

        return (int) Math.floor(base + base * Math.pow(multiplier, level - 1));
    }

    public static int senseForLevel(int level, Rating rating) {
        int base;
        double multiplier;
        switch (rating) {
            case NONE:
                base = 0;
                multiplier = 0;
                break;
            case SLIGHT:
                base = 1;
                multiplier = 1.5;
                break;
            case TYPICAL:
                base = 2;
                multiplier = 1.049;
                break;
            case GOOD:
                base = 4;
                multiplier = 1.048;
                break;
            case HIGH:
                base = 7;
                multiplier = 1.047;
                break;
            case SUPERB:
                base = 11;
                multiplier = 1.046;
                break;
            case AMAZING:
                base = 23;
                multiplier = 1.045;
                break;
            case ULTIMATE:
                base = 37;
                multiplier = 1.044;
                break;
            default:
                base = 0;
                multiplier = 0;
        }

        return (int) Math.floor(base + base * Math.pow(multiplier, level - 1));
    }

    public static double opposedRoll(double source, double target) {

        double difference = source - target + inflection;
        if (difference < 0) {
            return chances[0];
        }
        if (difference >= chances.length) {
            return chances[chances.length - 1];
        }

        int under = (int) Math.floor(difference);
        int over = (int) Math.ceil(difference);
        under = chances[under];
        over = chances[over];

        return under + (over - under) * (difference % 1);
    }

    public static double baseHitChance(Physical source, Physical target) {
        LiveValue sourceAim = source.stats.get(Stat.AIM);
        if (sourceAim == null || sourceAim.actual() <= 0.0) {
            return 0.0;
        }
        LiveValue targetDodge = target.stats.get(Stat.DODGE);
        if (targetDodge == null || targetDodge.actual() <= 0) {
            return 1.0;
        }

        return opposedRoll(sourceAim.actual(), targetDodge.actual());
    }

    public static double baseDamageDealt(Physical source, Physical target) {

        LiveValue sourceImpact = source.stats.get(Stat.IMPACT);
        if (sourceImpact == null || sourceImpact.actual() <= 0.0) {
            return 0.0;
        }
        LiveValue targetToughness = target.stats.get(Stat.TOUGHNESS);
        if (targetToughness == null || targetToughness.actual() <= 0) {
            return 1.0;
        }

        double difference = sourceImpact.actual() - targetToughness.actual();
        if (difference < -3) {
            return 1.0;
        }
        if (difference < -2) {
            return 2.0;
        }

        // Xn^1.2 + Xn^(0.2 + 5)
        // Xn starts at 5 for difference of -2
        return Math.pow(difference, 1.2) + Math.pow(difference, 0.2) + 5;
    }

    public static double berserkDamage(Physical source) {
        LiveValue lv = source.stats.get(Stat.IMPACT);
        if (lv == null) {
            return 0.0;
        }
        double val = lv.actual();
        lv = source.stats.get(Stat.VIGOR);
        if (lv == null) {
            return val;
        }
        if (lv.actual() / lv.base() < 0.2) {
            val *= 3;
        }
        return val;
    }
}
