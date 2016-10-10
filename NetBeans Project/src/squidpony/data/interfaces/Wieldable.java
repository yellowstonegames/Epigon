package squidpony.data.interfaces;

import squidpony.data.interfaceBlueprints.WieldableBlueprint;

/**
 * The specific information for a wieldable object.
 *
 * @author Eben
 */
public class Wieldable {

    public WieldableBlueprint parent;
    public int hitChance, damage, distance;
    public boolean wielded;
}
