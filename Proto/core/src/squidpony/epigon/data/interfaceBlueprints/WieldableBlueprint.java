package squidpony.epigon.data.interfaceBlueprints;

import java.util.List;
import java.util.Set;
import squidpony.epigon.data.blueprints.ConditionBlueprint;
import squidpony.epigon.data.generic.Ability;

/**
 * Information on what happens when the item is used as the focus of an action.
 */
public class WieldableBlueprint {

    public Set<Ability> relevantAbilities; // Which abilities can be used by this
    public List<ConditionBlueprint> causes;//conditions imparted by a successful hit
    public int hitChance;
    public int damage;
    public int distance;
}
