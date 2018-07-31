package squidpony.epigon.data;

import squidpony.epigon.data.quality.Element;
import squidpony.squidmath.OrderedMap;

/**
 * Created by Tommy Ettinger on 7/20/2018.
 */
public class Maneuver {
    /** A Maneuver applies these changes to its user for 1 round when selected */
    public ChangeTable changes;
    /** If non-null, changes the weapon's Element to this for 1 round */
    public Element elementReplacement;
    /** If non-null, changes the weapon's condition it can deliver to this for 1 round */
    public String conditionReplacement;
    /** If 0 or greater, changes the weapon's shape to the given constant (e.g. {@link Weapon#SWEEP}) for 1 round */
    public int shapeReplacement;
    public Maneuver()
    {
        changes = new ChangeTable();
        elementReplacement = null;
        conditionReplacement = null;
        shapeReplacement = -1;
    }
    public Maneuver(Element element, String condition, int shape, Object... ct)
    {
        changes = ChangeTable.makeCT(ct);
        elementReplacement = element;
        conditionReplacement = condition;
        shapeReplacement = shape;
    }
    public static final OrderedMap<String, Maneuver> ALL = OrderedMap.makeMap(
            "Attack", new Maneuver(),
            "Yank", new Maneuver(null, "Disarm", -1, CalcStat.DAMAGE, (int)'-', 3.0, CalcStat.INFLUENCE, (int)'+', 3.0),
            "Lunge", new Maneuver(null, null, -1, CalcStat.RANGE, (int)'+', 1.0, CalcStat.DAMAGE, (int)'+', 2.0, CalcStat.DEFENSE, (int)'-', 4.0));
}
