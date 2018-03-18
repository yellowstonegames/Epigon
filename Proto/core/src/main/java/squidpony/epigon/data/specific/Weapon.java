package squidpony.epigon.data.specific;

import squidpony.Maker;
import squidpony.epigon.GauntRNG;
import squidpony.epigon.data.WeightedTableWrapper;
import squidpony.epigon.data.blueprint.*;
import squidpony.epigon.data.generic.ChangeTable;
import squidpony.epigon.data.raw.RawWeapon;
import squidpony.epigon.universe.CalcStat;
import squidpony.epigon.universe.Element;
import squidpony.epigon.universe.Rating;
import squidpony.squidmath.OrderedMap;
import squidpony.squidmath.OrderedSet;
import squidpony.squidmath.ThrustAltRNG;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static squidpony.epigon.data.specific.Physical.basePhysical;

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
    public ChangeTable calcStats;
    public List<String> groups = new ArrayList<>(2), maneuvers = new ArrayList<>(4), statuses = new ArrayList<>(4);
    public String[] qualities = new String[4];
    public String[] materialTypes, training;
    public WeightedTableWrapper<Element> elements;
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
    public static OrderedMap<String, Weapon> weapons = new OrderedMap<>(RawWeapon.ENTRIES.length),
            physicalWeapons = new OrderedMap<>(RawWeapon.ENTRIES.length),
            unarmedWeapons = new OrderedMap<>(RawWeapon.ENTRIES.length);
    public static OrderedMap<String, List<Weapon>> categories = new OrderedMap<>(RawWeapon.ENTRIES.length >> 2),
    cultures = new OrderedMap<>(24);
    private static boolean initialized = false;
    public static void init() {
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
        initialized = true;
    }
    public static OrderedMap<String, Weapon> getWeapons()
    {
        if(!initialized) init();
        return weapons;
    }

    /**
     * Call with {@code ++state}.
     * @param state must be called with {@code ++state}.
     * @return a random weapon, which may be tangible or unarmed
     */
    public static Weapon randomWeapon(long state)
    {
        if(!initialized) init();
        return weapons.getAt(ThrustAltRNG.determineBounded(state, weapons.size()));
    }
    public static OrderedMap<String, Weapon> getPhysicalWeapons()
    {
        if(!initialized) init();
        return physicalWeapons;
    }
    /**
     * Call with {@code ++state}.
     * @param state must be called with {@code ++state}.
     * @return a random tangible weapon
     */
    public static Weapon randomPhysicalWeapon(long state)
    {
        if(!initialized) init();
        return physicalWeapons.getAt(ThrustAltRNG.determineBounded(state, physicalWeapons.size()));
    }
    public static OrderedMap<String, Weapon> getUnarmedWeapons()
    {
        if(!initialized) init();
        return unarmedWeapons;
    }
    /**
     * Call with {@code ++state}.
     * @param state must be called with {@code ++state}.
     * @return a random unarmed weapon
     */
    public static Weapon randomUnarmedWeapon(long state)
    {
        if(!initialized) init();
        return unarmedWeapons.getAt(ThrustAltRNG.determineBounded(state, unarmedWeapons.size()));
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
        calcStats = ChangeTable.makeCT(
                CalcStat.PRECISION, '+', raw.precision,
                CalcStat.DAMAGE, '+', raw.damage,
                CalcStat.CRIT, '+', raw.crit,
                CalcStat.INFLUENCE, '+', raw.influence,
                CalcStat.EVASION, '+', raw.evasion,
                CalcStat.DEFENSE, '+', raw.defense,
                CalcStat.LUCK, '+', raw.luck,
                CalcStat.STEALTH, '+', raw.stealth,
                CalcStat.RANGE, '+', raw.range,
                CalcStat.AREA, '+', raw.area,
                CalcStat.PREPARE, '+', raw.prepare);
//        calcStats[PRECISION] = raw.precision;
//        calcStats[DAMAGE] = raw.damage;
//        calcStats[CRIT] = raw.crit;
//        calcStats[INFLUENCE] = raw.influence;
//        calcStats[EVASION] = raw.evasion;
//        calcStats[DEFENSE] = raw.defense;
//        calcStats[LUCK] = raw.luck;
//        calcStats[STEALTH] = raw.stealth;
//        calcStats[RANGE] = raw.range;
//        calcStats[AREA] = raw.area;
//        calcStats[PREPARE] = raw.prepare;
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
        elements = new WeightedTableWrapper<>(blueprint.chaos, new Element[]{Element.valueOf(raw.type1), Element.valueOf(raw.type2)}, new double[]{3, 2});
        materialTypes = raw.materials;
        training = raw.training;
        blueprint.weaponData = this;
        blueprint.rarity = Rating.values()[GauntRNG.between(++blueprint.chaos, 1, 8)];
        recipeBlueprint = new RecipeBlueprint();
        recipeBlueprint.requiredCatalyst.put(basePhysical,1);
        recipeBlueprint.result.put(blueprint,1);
    }
    public Weapon(Weapon toCopy)
    {
        blueprint = Physical.makeBasic(toCopy.rawWeapon.name, toCopy.rawWeapon.glyph, -0x1.81818p126F);
        calcStats = new ChangeTable(toCopy.calcStats);
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
            blueprint.rarity = GauntRNG.getRandomElement(++blueprint.chaos, Rating.values());
        }
        recipeBlueprint = new RecipeBlueprint();
        recipeBlueprint.requiredCatalyst.put(basePhysical,1);
        recipeBlueprint.result.put(blueprint,1);
    }

    public Weapon copy()
    {
        return new Weapon(this);
    }
}
