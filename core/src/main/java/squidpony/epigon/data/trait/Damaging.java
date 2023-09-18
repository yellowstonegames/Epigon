package squidpony.epigon.data.trait;

import squidpony.epigon.data.ConditionBlueprint;
import squidpony.epigon.data.quality.Element;
import squidpony.squidmath.ProbabilityTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Can be used to cause direct damage to a target.
 */
public class Damaging {
    public double hitChance;
    public double damage;
    public double range;
    public ProbabilityTable<Element> elements = new ProbabilityTable<>();
    public List<ConditionBlueprint> causes = new ArrayList<>(); // conditions imparted by a successful hit

}
