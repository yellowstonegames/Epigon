package squidpony.epigon.data.mixin;

import java.util.List;
import java.util.Set;
import squidpony.epigon.data.blueprint.ConditionBlueprint;
import squidpony.epigon.data.blueprint.PhysicalBlueprint;
import squidpony.epigon.data.generic.Ability;

/**
 * A single unit of ammunition.
 *
 * @author Eben Howard - http://squidpony.com
 */
public class Ammunition {

    public Ammunition parent;
    public Set<Ability> relevantAbilities;
    public List<ConditionBlueprint> causes;//conditions imparted by a successful hit
    public Set<PhysicalBlueprint> launchers;//what weapons can use this ammo
    public boolean throwable;
    public int hitChance;
    public int damage;
    public int distance;
}
