package squidpony.epigon.data.interfaceBlueprints;

import java.util.ArrayList;
import squidpony.epigon.data.generic.Ability;

/**
 * Information on what happens when the item is used as the focus of an action.
 */
public class WieldableBlueprint {
    public ArrayList<Ability> causes = new ArrayList<>();//conditions imparted by a successful hit
    public int hitChance;
    public int damage;
    public int distance;
}
