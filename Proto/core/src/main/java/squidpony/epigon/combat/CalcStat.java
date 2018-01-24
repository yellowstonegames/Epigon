package squidpony.epigon.combat;

import squidpony.epigon.ImmutableKey;

/**
 * Created by Tommy Ettinger on 11/26/2017.
 */
public enum CalcStat implements ImmutableKey {
    PRECISION, DAMAGE, CRIT, INFLUENCE,
    EVASION, DEFENSE, STEALTH, LUCK, RANGE, AREA, PREPARE;

    CalcStat() {
        hash = ImmutableKey.precomputeHash("combat.CalcStat", ordinal());
    }
    public long hash;
    @Override
    public long hash64() {
        return hash;
    }
    @Override
    public int hash32() {
        return (int)(hash & 0xFFFFFFFFL);
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
