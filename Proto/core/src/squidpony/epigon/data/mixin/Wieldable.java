package squidpony.epigon.data.mixin;

import java.util.List;
import java.util.Set;
import squidpony.epigon.data.blueprint.ConditionBlueprint;
import squidpony.epigon.data.generic.Ability;
import squidpony.epigon.data.generic.Modification;

/**
 * The specific information for a wieldable object.
 *
 * @author Eben
 */
public class Wieldable {

    public Wieldable parent;
    public Set<Ability> relevantAbilities; // Which abilities can be used by this
    public List<ConditionBlueprint> causes; // conditions imparted by a successful hit

    public List<Modification> changes;

    public int hitChance;
    public int damage;
    public int reachDistance;
}
