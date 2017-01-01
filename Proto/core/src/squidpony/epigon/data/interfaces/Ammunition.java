package squidpony.epigon.data.interfaces;

import java.util.ArrayList;

import squidpony.epigon.data.EpiData;
import squidpony.epigon.data.blueprints.PhysicalBlueprint;
import squidpony.epigon.data.generic.Ability;
import squidpony.epigon.data.interfaceBlueprints.AmmunitionBlueprint;

/**
 * A single unit of ammunition.
 *
 * @author Eben Howard - http://squidpony.com
 */
public class Ammunition extends EpiData {

    public AmmunitionBlueprint parent;
    public ArrayList<Ability> causes = new ArrayList<>();//conditions imparted by a successful hit
    public ArrayList<PhysicalBlueprint> launchers = new ArrayList<>();//what weapons can use this ammo
    public boolean throwable;
    public int hitChance, damage, distance;
}
