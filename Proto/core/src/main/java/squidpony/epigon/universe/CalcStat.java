package squidpony.epigon.universe;

import squidpony.epigon.Utilities;

/**
 * Created by Tommy Ettinger on 12/10/2017.
 */
public enum CalcStat {
    PRECISION("PRECISION", ""), DAMAGE("DAMAGE", ""), CRIT("CRIT", ""), INFLUENCE("INFLUENCE", ""),
    EVASION("EVASION", ""), DEFENSE("DEFENSE", ""), STEALTH("STEALTH", ""), LUCK("LUCK", ""),
    RANGE("RANGE", ""), AREA("AREA", ""), PREPARE("PREPARE", "");

    private final String nick;
    private final String description;
    private final String prettyName;

    CalcStat(String nick, String description) {
        this.nick = nick;
        this.description = description;
        prettyName = Utilities.capitalize(name());
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
}
