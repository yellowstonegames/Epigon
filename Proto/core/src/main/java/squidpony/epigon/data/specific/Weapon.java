package squidpony.epigon.data.specific;

import squidpony.epigon.data.blueprint.*;
import squidpony.epigon.data.mixin.Wieldable;
import squidpony.epigon.data.raw.MeleeWeapon;
import squidpony.squidmath.OrderedMap;
import squidpony.squidmath.OrderedSet;

import static squidpony.epigon.Epigon.mixer;
import static squidpony.epigon.data.specific.Physical.basePhysical;

/**
 * Created by Tommy Ettinger on 11/25/2017.
 */
public class Weapon {
    public Physical blueprint;
    public RecipeBlueprint recipeBlueprint;
    public Recipe recipe;
    public String[] materialTypes;
    public static final OrderedMap<String, OrderedSet<Material>> makes = OrderedMap.makeMap(
            "Stone", new OrderedSet<>(Stone.values()),
            "Inclusion", new OrderedSet<>(Inclusion.values()),
            "Metal", new OrderedSet<>(Metal.values()),
            "Paper", new OrderedSet<>(Paper.values()),
            "Wood", new OrderedSet<>(Wood.values()),
            "Hide", new OrderedSet<>(Hide.values()),
            "Cloth", new OrderedSet<>(Cloth.values()),
            "Metal|Wood", new OrderedSet<>(Metal.values()),
            "Metal|Stone", new OrderedSet<>(Metal.values()),
            "Hide|Metal|Wood", new OrderedSet<>(Hide.values())
    );
    public static OrderedMap<String, Weapon> meleeWeapons = new OrderedMap<>(MeleeWeapon.ENTRIES.length);
    static {
        makes.get("Metal|Wood").addAll(Wood.values());
        makes.get("Metal|Stone").addAll(Stone.values());
        makes.get("Hide|Metal|Wood").addAll(makes.get("Metal|Wood"));
        for(MeleeWeapon mw : MeleeWeapon.ENTRIES)
        {
            meleeWeapons.put(mw.name, new Weapon(mw));
        }
    }
    public Weapon()
    {
        this(MeleeWeapon.ENTRIES[0]);
    }

    public Weapon(MeleeWeapon raw)
    {
        blueprint = Physical.makeBasic(raw.name, raw.glyph, -0x1.81818p126F);
        blueprint.wieldableData = new Wieldable();
        blueprint.wieldableData.damage = raw.damage;
        blueprint.wieldableData.hitChance = 35 + 10 * raw.precision;
        blueprint.wieldableData.reachDistance = raw.reach;
        recipeBlueprint = new RecipeBlueprint();
        recipeBlueprint.requiredCatalyst.put(basePhysical,1);
        recipeBlueprint.result.put(blueprint,1);
        recipe = mixer.createRecipe(recipeBlueprint);
        materialTypes = raw.materials;
    }

}
