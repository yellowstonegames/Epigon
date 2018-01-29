package squidpony.epigon.universe;

import squidpony.epigon.ImmutableKey;
import squidpony.epigon.Utilities;

/**
 * Created by Tommy Ettinger on 12/10/2017.
 */
public enum CalcStat implements ImmutableKey {
    PRECISION("PRECISION", ""), DAMAGE("DAMAGE", ""), CRIT("CRIT", ""), INFLUENCE("INFLUENCE", ""),
    EVASION("EVASION", ""), DEFENSE("DEFENSE", ""), STEALTH("STEALTH", ""), LUCK("LUCK", ""),
    RANGE("RANGE", ""), AREA("AREA", ""), PREPARE("PREPARE", "");

    private final String nick;
    private final String description;
    private final String prettyName;

    CalcStat(String nick, String description) {
        this.nick = nick;
        this.description = description;
        prettyName = Utilities.caps(name(), "_");
        hash = ImmutableKey.precomputeHash("creature.CalcStat", ordinal());
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

    public String nick(){
        return nick;
    }

    public String description() {
        return description;
    }

    @Override
    public String toString() {
        return prettyName;
    }
    public static final CalcStat[] all = values();
}
