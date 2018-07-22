package squidpony.epigon.data;

import squidpony.epigon.ConstantKey;
import squidpony.epigon.Utilities;

/**
 * Created by Tommy Ettinger on 12/10/2017.
 */
public enum CalcStat implements ConstantKey {
    PRECISION("PRECISION", ""), DAMAGE("DAMAGE", ""), CRIT("CRIT", ""), INFLUENCE("INFLUENCE", ""),
    EVASION("EVASION", ""), DEFENSE("DEFENSE", ""), STEALTH("STEALTH", ""), LUCK("LUCK", ""),
    RANGE("RANGE", ""), AREA("AREA", ""), QUICKNESS("QUICKNESS", "");

    private final String nick;
    private final String description;
    private final String prettyName;

    CalcStat(String nick, String description) {
        this.nick = nick;
        this.description = description;
        prettyName = Utilities.caps(name(), "_");
        hash = ConstantKey.precomputeHash("creature.CalcStat", ordinal());
        hashSmall = (int)(hash & 0xFFFFFFFFL);
    }
    public final long hash;
    public final int hashSmall;
    @Override
    public long hash64() {
        return hash;
    }
    @Override
    public int hash32() {
        return hashSmall;
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
