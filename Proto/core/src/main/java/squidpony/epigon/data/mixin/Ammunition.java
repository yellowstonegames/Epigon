package squidpony.epigon.data.mixin;

import squidpony.epigon.data.blueprint.ConditionBlueprint;
import squidpony.epigon.data.specific.Physical;

import java.util.List;
import java.util.Set;

/**
 * A single unit of ammunition.
 *
 * @author Eben Howard - http://squidpony.com
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
