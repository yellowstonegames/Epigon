package squidpony.epigon.data.trait;

import squidpony.epigon.data.ConditionBlueprint;
import squidpony.epigon.data.Physical;

import java.util.List;
import java.util.Set;

/**
 * Can be thrown to cause direct damage to a target.
 */
public class Thrown {

    public Thrown parent;
    public List<ConditionBlueprint> causes; // conditions imparted by a successful hit
    public double hitChance;
    public double damage;
    public double distance;
}
