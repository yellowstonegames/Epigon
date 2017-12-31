package squidpony.epigon.data.specific;

import squidpony.Maker;
import squidpony.epigon.data.blueprint.*;
import squidpony.epigon.data.raw.RawWeapon;
import squidpony.epigon.universe.Element;
import squidpony.epigon.universe.Rating;
import squidpony.squidmath.OrderedMap;
import squidpony.squidmath.OrderedSet;
import squidpony.squidmath.ProbabilityTable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static squidpony.epigon.Epigon.*;
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
            "Acid", Element.ACID,
            "Air", Element.AIR,
            "Blunt", Element.BLUNT,
            "Contract", Element.CONTRACTUAL,
            "Crystal", Element.CRYSTAL,
            "Death", Element.DEATH,
            "Divine", Element.DIVINE,
            "Earth", Element.EARTH,
            "Fate", Element.FATEFUL,
            "Fire", Element.FIRE,
            "Ice", Element.ICE,
            "Light", Element.SHINING,
            "Piercing", Element.PIERCING,
            "Poison", Element.POISON,
            "Pure", Element.PURE,
            "Radiation", Element.RADIATION,
            "Shadow", Element.SHADOW,
            "Sinister", Element.SINISTER,
            "Slashing", Element.SLASHING,
            "Storm", Element.LIGHTNING,
            "Time", Element.TEMPORAL,
            "Water", Element.WATER);
    public static OrderedMap<String, Weapon> weapons = new OrderedMap<>(RawWeapon.ENTRIES.length),
            physicalWeapons = new OrderedMap<>(RawWeapon.ENTRIES.length),
            unarmedWeapons = new OrderedMap<>(RawWeapon.ENTRIES.length);
    public static OrderedMap<String, List<Weapon>> categories = new OrderedMap<>(RawWeapon.ENTRIES.length >> 2),
    cultures = new OrderedMap<>(24);
    static {
        makes.get("Metal|Wood").addAll(Wood.values());
        makes.get("Metal|Stone").addAll(Stone.values());
        makes.get("Hide|Metal|Wood").addAll(makes.get("Metal|Wood"));
        List<Weapon> cat;
        Weapon wpn;
        for(RawWeapon rw : RawWeapon.ENTRIES)
        {
            weapons.put(rw.name, (wpn = new Weapon(rw)));
            if(rw.materials.length > 0)
            {
                physicalWeapons.put(rw.name, wpn);
                for(String culture : rw.culture)
                {
                    if((cat = cultures.get(culture)) != null)
                        cat.add(wpn);
                    else
                        cultures.put(culture, Maker.makeList(wpn));
                }

            }
            else
                unarmedWeapons.put(rw.name, wpn);
            for(String training : rw.training)
            {
                if((cat = categories.get(training)) != null)
                    cat.add(wpn);
                else
                    categories.put(training, Maker.makeList(wpn));
            }
        }
    }
    //public static final Weapon UNARMED = weapons.getAt(0);
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
        elements.add(Element.valueOf(raw.type1), 3);
        elements.add(Element.valueOf(raw.type2), 2);
        materialTypes = raw.materials;
        training = raw.training;
        blueprint.weaponData = this;
        blueprint.rarity = Rating.values()[rng.between(1, 8)];
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
        rawWeapon = toCopy.rawWeapon;
        blueprint.weaponData = this;
        while (blueprint.rarity == null || blueprint.rarity == Rating.NONE) {
            blueprint.rarity = rng.getRandomElement(Rating.values());
        }
        recipeBlueprint = new RecipeBlueprint();
        recipeBlueprint.requiredCatalyst.put(basePhysical,1);
        recipeBlueprint.result.put(blueprint,1);
        recipe = mixer.createRecipe(recipeBlueprint);
    }

    public Weapon copy()
    {
        return new Weapon(this);
    }
}
