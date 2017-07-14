package squidpony.epigon.data.generic;

import java.util.ArrayList;
import java.util.List;

import squidpony.epigon.data.specific.Physical;
import squidpony.epigon.universe.LiveValue;
import squidpony.epigon.universe.Stat;

/**
 * A way to describe what calculations should be performed at run time.
 *
 * Contains all the kinds of values that can be used to perform calculations.
 *
 * @author Eben Howard
 */
public class Formula {

    // example to support: physical attack that does extra critical damage when health is low
    public static Formula berserkAttack;

    static {
        berserkAttack = new Formula();
        berserkAttack.formulas.add(new DirectValueMultiplierAdditive(Stat.IMPACT, 1.0));
        berserkAttack.formulas.add(new UnderPercentConstantMultiplier(Stat.LIFE_FORCE, 3.0, 0.2));
    }

    public List<DetailFormula> formulas = new ArrayList<>();

    public double result(Physical physical) {
        double value = 0;
        for (DetailFormula formula : formulas) {
            if (formula instanceof ConstantFormula) {
                value = ((ConstantFormula) formula).result(value);
            } else if (formula instanceof PhysicalFormula) {
                value = ((PhysicalFormula) formula).result(value, physical);
            } else {
                System.err.println("Unknown formula type " + formula.getClass());
            }
        }
        return value;
    }

    public static interface DetailFormula {
    }

    public static abstract class ConstantFormula implements DetailFormula {

        public final double value;

        public ConstantFormula(double value) {
            this.value = value;
        }

        public abstract double result(double prior);
    }

    public static class ConstantAdditive extends ConstantFormula {

        public ConstantAdditive(double value) {
            super(value);
        }

        @Override
        public double result(double prior) {
            return prior + value;
        }
    }

    public static class ConstantMultiplier extends ConstantFormula {

        public ConstantMultiplier(double value) {
            super(value);
        }

        @Override
        public double result(double prior) {
            return prior * value;
        }
    }

    public static abstract class PhysicalFormula implements DetailFormula {

        public final Stat stat;
        public final double value;

        PhysicalFormula(Stat stat, double value) {
            this.stat = stat;
            this.value = value;
        }

        public abstract double result(double prior, Physical physical);
    }

    public static class DirectValueMultiplierAdditive extends PhysicalFormula {

        public DirectValueMultiplierAdditive(Stat stat, double value) {
            super(stat, value);
        }

        @Override
        public double result(double prior, Physical physical) {
            LiveValue lv = physical.stats.get(stat);
            if (lv == null) {
                return prior;
            }
            return prior + lv.actual() * value;
        }
    }

    public static class DirectValueMultiplierMultiplier extends PhysicalFormula {

        public DirectValueMultiplierMultiplier(Stat stat, double value) {
            super(stat, value);
        }

        @Override
        public double result(double prior, Physical physical) {
            LiveValue lv = physical.stats.get(stat);
            if (lv == null) {
                return prior;
            }
            return prior * lv.actual() * value;
        }
    }

    public static abstract class StatComparison extends PhysicalFormula {

        public final double checkValue;

        public StatComparison(Stat stat, double value, double checkValue) {
            super(stat, value);
            this.checkValue = checkValue;
        }
    }

    public static class OverPercentActualMultiplierAdditive extends StatComparison {

        public OverPercentActualMultiplierAdditive(Stat stat, double value, double checkValue) {
            super(stat, value, checkValue);
        }

        @Override
        public double result(double prior, Physical physical) {
            LiveValue lv = physical.stats.get(stat);
            if (lv == null) {
                return prior;
            }
            boolean over = lv.actual() / lv.base() > checkValue;
            double adding = over ? lv.actual() * value : 0.0;
            return prior + adding;
        }
    }

    public static class UnderPercentActualMultiplierAdditive extends StatComparison {

        public UnderPercentActualMultiplierAdditive(Stat stat, double value, double checkValue) {
            super(stat, value, checkValue);
        }

        @Override
        public double result(double prior, Physical physical) {
            LiveValue lv = physical.stats.get(stat);
            if (lv == null) {
                return prior;
            }
            boolean under = lv.actual() / lv.base() < checkValue;
            double adding = under ? lv.actual() * value : 0.0;
            return prior + adding;
        }
    }

    public static class OverPercentBaseMultiplierAdditive extends StatComparison {

        public OverPercentBaseMultiplierAdditive(Stat stat, double value, double checkValue) {
            super(stat, value, checkValue);
        }

        @Override
        public double result(double prior, Physical physical) {
            LiveValue lv = physical.stats.get(stat);
            if (lv == null) {
                return prior;
            }
            boolean over = lv.actual() / lv.base() > checkValue;
            double adding = over ? lv.base() * value : 0.0;
            return prior + adding;
        }
    }

    public static class UnderPercentBaseMultiplierAdditive extends StatComparison {

        public UnderPercentBaseMultiplierAdditive(Stat stat, double value, double checkValue) {
            super(stat, value, checkValue);
        }

        @Override
        public double result(double prior, Physical physical) {
            LiveValue lv = physical.stats.get(stat);
            if (lv == null) {
                return prior;
            }
            boolean under = lv.actual() / lv.base() < checkValue;
            double adding = under ? lv.base() * value : 0.0;
            return prior + adding;
        }
    }

    public static class OverPercentConstantAdditive extends StatComparison {

        public OverPercentConstantAdditive(Stat stat, double value, double checkValue) {
            super(stat, value, checkValue);
        }

        @Override
        public double result(double prior, Physical physical) {
            LiveValue lv = physical.stats.get(stat);
            if (lv == null) {
                return prior;
            }
            boolean over = lv.actual() / lv.base() > checkValue;
            double adding = over ? value : 0.0;
            return prior + adding;
        }
    }

    public static class UnderPercentConstantAdditive extends StatComparison {

        public UnderPercentConstantAdditive(Stat stat, double value, double checkValue) {
            super(stat, value, checkValue);
        }

        @Override
        public double result(double prior, Physical physical) {
            LiveValue lv = physical.stats.get(stat);
            if (lv == null) {
                return prior;
            }
            boolean under = lv.actual() / lv.base() < checkValue;
            double adding = under ? value : 0.0;
            return prior + adding;
        }
    }

    public static class OverPercentActualMultiplierMultiplier extends StatComparison {

        public OverPercentActualMultiplierMultiplier(Stat stat, double value, double checkValue) {
            super(stat, value, checkValue);
        }

        @Override
        public double result(double prior, Physical physical) {
            LiveValue lv = physical.stats.get(stat);
            if (lv == null) {
                return prior;
            }
            boolean over = lv.actual() / lv.base() > checkValue;
            double adding = over ? lv.actual() * value : 1.0;
            return prior * adding;
        }
    }

    public static class UnderPercentActualMultiplierMultiplier extends StatComparison {

        public UnderPercentActualMultiplierMultiplier(Stat stat, double value, double checkValue) {
            super(stat, value, checkValue);
        }

        @Override
        public double result(double prior, Physical physical) {
            LiveValue lv = physical.stats.get(stat);
            if (lv == null) {
                return prior;
            }
            boolean under = lv.actual() / lv.base() < checkValue;
            double adding = under ? lv.actual() * value : 1.0;
            return prior * adding;
        }
    }

    public static class OverPercentBaseMultiplierMultiplier extends StatComparison {

        public OverPercentBaseMultiplierMultiplier(Stat stat, double value, double checkValue) {
            super(stat, value, checkValue);
        }

        @Override
        public double result(double prior, Physical physical) {
            LiveValue lv = physical.stats.get(stat);
            if (lv == null) {
                return prior;
            }
            boolean over = lv.actual() / lv.base() > checkValue;
            double adding = over ? lv.base() * value : 1.0;
            return prior * adding;
        }
    }

    public static class UnderPercentBaseMultiplierMultiplier extends StatComparison {

        public UnderPercentBaseMultiplierMultiplier(Stat stat, double value, double checkValue) {
            super(stat, value, checkValue);
        }

        @Override
        public double result(double prior, Physical physical) {
            LiveValue lv = physical.stats.get(stat);
            if (lv == null) {
                return prior;
            }
            boolean under = lv.actual() / lv.base() < checkValue;
            double adding = under ? lv.base() * value : 1.0;
            return prior * adding;
        }
    }

    public static class OverPercentConstantMultiplier extends StatComparison {

        public OverPercentConstantMultiplier(Stat stat, double value, double checkValue) {
            super(stat, value, checkValue);
        }

        @Override
        public double result(double prior, Physical physical) {
            LiveValue lv = physical.stats.get(stat);
            if (lv == null) {
                return prior;
            }
            boolean over = lv.actual() / lv.base() > checkValue;
            double adding = over ? value : 1.0;
            return prior * adding;
        }
    }

    public static class UnderPercentConstantMultiplier extends StatComparison {

        public UnderPercentConstantMultiplier(Stat stat, double value, double checkValue) {
            super(stat, value, checkValue);
        }

        @Override
        public double result(double prior, Physical physical) {
            LiveValue lv = physical.stats.get(stat);
            if (lv == null) {
                return prior;
            }
            boolean under = lv.actual() / lv.base() < checkValue;
            double adding = under ? value : 1.0;
            return prior * adding;
        }
    }
}
