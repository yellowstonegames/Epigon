package squidpony.epigon.data.mixin;

import squidpony.epigon.data.blueprint.ConditionBlueprint;
import squidpony.epigon.data.generic.Modification;

import java.util.List;

/**
 * The specific information for a wieldable object.
 *
 * @author Eben
 */
public class Wieldable {

    public Wieldable parent;
    public List<ConditionBlueprint> causes; // conditions imparted by a successful hit

    public List<Modification> changes;

    public int hitChance;
    public int damage;
    public int reachDistance;
    public static final Wieldable UNARMED = new Wieldable();
    static {
        UNARMED.reachDistance = 0;
        UNARMED.hitChance = 66;
        UNARMED.damage = 1;
    }
}
