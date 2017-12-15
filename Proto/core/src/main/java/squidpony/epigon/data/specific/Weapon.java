package squidpony.epigon.data.specific;

import squidpony.epigon.data.blueprint.*;
import squidpony.epigon.data.raw.RawWeapon;
import squidpony.epigon.universe.Element;
import squidpony.squidmath.OrderedMap;
import squidpony.squidmath.OrderedSet;
import squidpony.squidmath.ProbabilityTable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static squidpony.epigon.Epigon.chaos;
import static squidpony.epigon.Epigon.mixer;
import static squidpony.epigon.data.specific.Physical.*;

/**
 * Created by Tommy Ettinger on 11/25/2017.
 */
public class Weapon {
    public static final int HANDS = 11;
    public static final int KIND = 0, USAGE = 1, SHAPE = 2, PATH = 3;
    public RawWeapon rawWeapon;
    public int hands = 1;
    public Physical blueprint;
    public RecipeBlueprint recipeBlueprint;
    public Recipe recipe;
    public int[] calcStats = new int[11];
    public List<String> groups = new ArrayList<>(2), maneuvers = new ArrayList<>(4), statuses = new ArrayList<>(4);
    public String[] qualities = new String[4];
    public String[] materialTypes, training;
    public ProbabilityTable<Element> elements;
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
    public static final OrderedMap<String, Element> elementRename = OrderedMap.makeMap(
            "Blunt", Element.BLUNT,
            "Death", Element.DEATH,
            "Divine", Element.DIVINE,
            "Earth", Element.EARTH,
            "Fate", Element.FATEFUL,
            "Fire", Element.FIRE,
            "Light", Element.SHINING,
            "Piercing", Element.PIERCING,
            "Pure", Element.PURE,
            "Shadow", Element.SHADOW,
            "Slashing", Element.SLASHING,
            "Storm", Element.LIGHTNING);
    public static OrderedMap<String, Weapon> weapons = new OrderedMap<>(RawWeapon.ENTRIES.length);
    static {
        makes.get("Metal|Wood").addAll(Wood.values());
        makes.get("Metal|Stone").addAll(Stone.values());
        makes.get("Hide|Metal|Wood").addAll(makes.get("Metal|Wood"));
        for(RawWeapon mw : RawWeapon.ENTRIES)
        {
            weapons.put(mw.name, new Weapon(mw));
        }
    }
    public static final Weapon UNARMED = weapons.getAt(0);
    public Weapon()
    {
        this(RawWeapon.ENTRIES[0]);
    }

    public Weapon(RawWeapon raw)
    {
        rawWeapon = raw;
        blueprint = Physical.makeBasic(raw.name, raw.glyph, -0x1.81818p126F);
        calcStats[PRECISION] = raw.precision;
        calcStats[DAMAGE] = raw.damage;
        calcStats[CRIT] = raw.crit;
        calcStats[INFLUENCE] = raw.influence;
        calcStats[EVASION] = raw.evasion;
        calcStats[DEFENSE] = raw.defense;
        calcStats[LUCK] = raw.luck;
        calcStats[STEALTH] = raw.stealth;
        calcStats[RANGE] = raw.range;
        calcStats[AREA] = raw.area;
        calcStats[PREPARE] = raw.prepare;
        qualities[KIND] = raw.kind;
        qualities[USAGE] = raw.usage;
        qualities[SHAPE] = raw.shape;
        qualities[PATH] = raw.path;
        hands = raw.hands;
        groups.add(raw.group1);
        groups.add(raw.group2);
        maneuvers.add(raw.maneuver1);
        maneuvers.add(raw.maneuver2);
        statuses.add(raw.status1);
        statuses.add(raw.status2);
        elements = new ProbabilityTable<>(chaos);
        elements.add(elementRename.getOrDefault(raw.type1, Element.BLUNT), 3);
        elements.add(elementRename.getOrDefault(raw.type2, Element.BLUNT), 2);
        materialTypes = raw.materials;
        training = raw.training;
        blueprint.weaponData = this;
        recipeBlueprint = new RecipeBlueprint();
        recipeBlueprint.requiredCatalyst.put(basePhysical,1);
        recipeBlueprint.result.put(blueprint,1);
        recipe = mixer.createRecipe(recipeBlueprint);
    }
    public Weapon(Weapon toCopy)
    {
        blueprint = Physical.makeBasic(toCopy.rawWeapon.name, toCopy.rawWeapon.glyph, -0x1.81818p126F);
        System.arraycopy(toCopy.calcStats, 0, calcStats, 0, 11);
        System.arraycopy(toCopy.qualities, 0, qualities, 0, 4);
        materialTypes = Arrays.copyOf(toCopy.materialTypes, toCopy.materialTypes.length);
        training = Arrays.copyOf(toCopy.training, toCopy.training.length);
        hands = toCopy.hands;
        groups.addAll(toCopy.groups);
        maneuvers.addAll(toCopy.maneuvers);
        statuses.addAll(toCopy.statuses);
        elements = toCopy.elements.copy();
        blueprint.weaponData = this;
        recipeBlueprint = new RecipeBlueprint();
        recipeBlueprint.requiredCatalyst.put(basePhysical,1);
        recipeBlueprint.result.put(blueprint,1);
        recipe = mixer.createRecipe(recipeBlueprint);
    }

    public Weapon copy()
    {
        return new Weapon(this);
    }

    public static double calculateHitChance(int raw)
    {
        return (67 + 5 * raw) * 0x1p-7;
    }
    public static int calculateDamage(int raw)
    {
        return raw + 1;
    }
}
