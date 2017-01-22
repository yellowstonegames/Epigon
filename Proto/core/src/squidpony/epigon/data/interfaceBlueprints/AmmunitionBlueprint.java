package squidpony.epigon.data.interfaceBlueprints;

import java.util.List;
import java.util.Set;
import squidpony.epigon.data.blueprints.ConditionBlueprint;

import squidpony.epigon.data.blueprints.PhysicalBlueprint;
import squidpony.epigon.data.generic.Ability;

/**
 * Holds data for an item that can be used as ammunition.
 *
 * @author Eben Howard - http://squidpony.com
 */
public class AmmunitionBlueprint {

    public AmmunitionBlueprint parent;
    public Set<Ability> relevantAbilities;
    public List<ConditionBlueprint> causes;//conditions imparted by a successful hit
    public Set<PhysicalBlueprint> launchers;//what weapons can use this ammo
    public boolean throwable;
    public int hitChance;
    public int damage;
    public int distance;
}
