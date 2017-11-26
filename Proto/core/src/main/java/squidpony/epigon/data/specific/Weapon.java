package squidpony.epigon.data.specific;

import squidpony.epigon.data.blueprint.Inclusion;
import squidpony.epigon.data.blueprint.RecipeBlueprint;
import squidpony.epigon.data.blueprint.Stone;
import squidpony.epigon.data.mixin.Wieldable;
import squidpony.epigon.data.raw.MeleeWeapon;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidmath.OrderedMap;

import static squidpony.epigon.Epigon.mixer;
import static squidpony.epigon.Epigon.rng;
import static squidpony.epigon.data.specific.Physical.basePhysical;

/**
 * Created by Tommy Ettinger on 11/25/2017.
 */
public class Weapon {
    public Physical blueprint;
    public RecipeBlueprint recipeBlueprint;
    public Recipe recipe;

    public Weapon()
    {
        // the float color used is SColor.SILVER
        blueprint = Physical.makeBasic(MeleeWeapon.ENTRIES[0].name, MeleeWeapon.ENTRIES[0].glyph, -0x1.81818p126F);
        recipeBlueprint = new RecipeBlueprint();
        recipeBlueprint.requiredCatalyst.put(basePhysical,1);
        recipeBlueprint.result.put(blueprint,1);
        recipe = mixer.createRecipe(recipeBlueprint);
    }

    public Weapon(MeleeWeapon raw)
    {
        blueprint = Physical.makeBasic(raw.name, raw.glyph, -0x1.81818p126F);
        switch (rng.getRandomElement(raw.materials))
        {
            case "Wood": blueprint.color = SColor.BRUSHWOOD_DYED.toFloatBits();
            break;
            case "Inclusion": blueprint.color = rng.getRandomElement(Inclusion.values()).front.toFloatBits();
            break;
            case "Stone": blueprint.color = rng.getRandomElement(Stone.values()).front.toFloatBits();
            break;
            case "Leather": blueprint.color = SColor.CW_FLUSH_BROWN.toFloatBits();
            break;
            case "Paper": blueprint.color = SColor.OLD_LACE.toFloatBits();
            break;
        }
        blueprint.wieldableData = new Wieldable();
        blueprint.wieldableData.damage = raw.damage;
        blueprint.wieldableData.hitChance = 35 + 10 * raw.precision;
        blueprint.wieldableData.reachDistance = raw.reach;
        recipeBlueprint = new RecipeBlueprint();
        recipeBlueprint.requiredCatalyst.put(basePhysical,1);
        recipeBlueprint.result.put(blueprint,1);
        recipe = mixer.createRecipe(recipeBlueprint);
    }
    public static OrderedMap<String, Weapon> meleeWeapons = new OrderedMap<>(MeleeWeapon.ENTRIES.length);
    static {
        for(MeleeWeapon mw : MeleeWeapon.ENTRIES)
        {
            meleeWeapons.put(mw.name, new Weapon(mw));
        }
    }

}
