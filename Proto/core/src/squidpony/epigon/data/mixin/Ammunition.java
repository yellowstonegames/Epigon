package squidpony.epigon.data.mixin;

import java.util.List;
import java.util.Set;

import squidpony.epigon.data.blueprint.ConditionBlueprint;
import squidpony.epigon.data.blueprint.PhysicalBlueprint;

/**
 * A single unit of ammunition.
 *
 * @author Eben Howard - http://squidpony.com
 */
public class Ammunition {

    public Ammunition parent;
    public List<ConditionBlueprint> causes;//conditions imparted by a successful hit
    public Set<PhysicalBlueprint> launchers;//what weapons can use this ammo
    public boolean throwable;
    public double hitChance;
    public double damage;
    public double distance;
}
