package squidpony.data.interfaces;

import java.util.ArrayList;
import squidpony.data.EpiData;
import squidpony.data.blueprints.ItemBlueprint;
import squidpony.data.generic.Ability;
import squidpony.data.interfaceBlueprints.AmmunitionBlueprint;

/**
 * A single unit of ammunition.
 *
 * @author Eben Howard - http://squidpony.com - howard@squidpony.com
 */
public class Ammunition extends EpiData {

    public AmmunitionBlueprint parent;
    public ArrayList<Ability> causes = new ArrayList<>();//conditions imparted by a successful hit
    public ArrayList<ItemBlueprint> launchers = new ArrayList<>();//what weapons can use this ammo
    public boolean throwable;
    public int hitChance, damage, distance;
}
