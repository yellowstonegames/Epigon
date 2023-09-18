package squidpony.epigon.data.trait;

import squidpony.epigon.data.ConditionBlueprint;
import squidpony.epigon.data.Physical;

import java.util.List;
import java.util.Set;

/**
 * A single unit of ammunition. Because it's the ammo that does the damage and not the weapon (when fired), the damage
 * and results are attached to the ammo instead of the weapon.
 */
public class Ammunition {

    public Ammunition parent;
    public Set<Physical> launchers; //what weapons can use this ammo
    public double hitChanceMultiplier = 1.0; // 1.0 is normal, 0.5 is half as likely to hit, 2.0 is twice as likely to hit
    public double range;
}
