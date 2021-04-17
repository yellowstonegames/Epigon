package squidpony.epigon.data.trait;

import squidpony.epigon.data.ConditionBlueprint;
import squidpony.epigon.data.Physical;

import java.util.List;
import java.util.Set;

/**
 * A single unit of ammunition.
 */
public class Ammunition {

    public Ammunition parent;
    public List<ConditionBlueprint> causes;//conditions imparted by a successful hit
    public Set<Physical> launchers;//what weapons can use this ammo // seems odd, maybe backwards?
    public boolean throwable;
    public double hitChance;
    public double damage;
    public double distance;
}
