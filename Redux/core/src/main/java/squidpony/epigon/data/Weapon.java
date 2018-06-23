package squidpony.epigon.data;

import squidpony.Maker;
import squidpony.epigon.GauntRNG;
import squidpony.epigon.data.quality.*;
import squidpony.epigon.data.raw.RawWeapon;
import squidpony.squidmath.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static squidpony.epigon.data.Physical.basePhysical;

/**
 * Created by Tommy Ettinger on 11/25/2017.
 */
public class Weapon {
    public static final int HANDS = 11;
    public RawWeapon rawWeapon;
    public int hands = 1;
    public Physical blueprint;
    public RecipeBlueprint recipeBlueprint;
    public ChangeTable calcStats;
    public List<String> groups = new ArrayList<>(2), maneuvers = new ArrayList<>(4), statuses = new ArrayList<>(4);
    //public String[] qualities = new String[4];
    public int kind, usage, shape, path;
    public String[] materialTypes, training;
    public Skill[] skills;
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
    
    // various constants to make sense of kinds, usages, shapes, and paths instead of using Strings (compile-checked)
    public static final int
            MELEE = 0,     // + 2 DAMAGE
            RANGED = 1,    // + 1 RANGE
            IMPLEMENT = 2, // + 2 INFLUENCE
            UNARMED = 3,   // + 1 STEALTH
            MAGIC = 4,     // + 1 PRECISION
    
            REPEAT = 0, 
            PROJECTILE = 1, 
            THROWN = 2, 
    
            MULTI = 0,     // gives a 1/16 chance to double an attack per AREA
            BEAM = 1,      // affects 1 cell beyond the initial target per AREA
            SWEEP = 2,     // affects 1 cell clockwise and counterclockwise from the target per AREA 
            WAVE = 3,      // affects a 60 degree cone with length = AREA + RANGE, accuracy penalty
            BURST = 4,     // affects a (AREA * 2 + 1) side-length square at up to RANGE distance, accuracy penalty 
            THROUGH = 5,   // can attempt to target even ignoring AREA obstacles in the way 
    
            STRAIGHT = 0,  // aims at the first thing in a line
            ARC = 1;       // aims at the target unless it is adjacent, then it cannot target
    // for if you need to get a string from one of the above constants, and also to read the TSV contents
    public static final Arrangement<String> kinds = Maker.makeArrange("Melee", "Ranged", "Implement", "Unarmed", "Magic"), 
            usages = Maker.makeArrange("Repeat", "Projectile", "Thrown"),
            shapes = Maker.makeArrange("Multi", "Beam", "Sweep", "Wave", "Burst", "Through"),
            paths = Maker.makeArrange("Straight", "Arc");
    public static OrderedMap<String, Weapon> weapons = new OrderedMap<>(RawWeapon.ENTRIES.length),
            physicalWeapons = new OrderedMap<>(RawWeapon.ENTRIES.length),
            unarmedWeapons = new OrderedMap<>(RawWeapon.ENTRIES.length),
            naturalWeapons = new OrderedMap<>(RawWeapon.ENTRIES.length);
    public static OrderedMap<String, List<Weapon>> categories = new OrderedMap<>(RawWeapon.ENTRIES.length >> 2, Hashers.caseInsensitiveStringHasher),
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
            else if("Natural".equals(rw.kind))
                naturalWeapons.put(rw.name, wpn);
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
     * Gets a random weapon with the given IRNG.
     * @param rng an IRNG, which is expected to be an EpiData much of the time
     * @return a random weapon, which may be tangible or unarmed
     */
    public static Weapon randomWeapon(IRNG rng)
    {
        if(!initialized) init();
        return weapons.randomValue(rng);
    }
    
    public static Weapon randomWeapon(long state)
    {
        if(!initialized) init();
        return weapons.getAt(GauntRNG.nextInt(state, weapons.size()));
    }
    public static OrderedMap<String, Weapon> getPhysicalWeapons()
    {
        if(!initialized) init();
        return physicalWeapons;
    }
    /**
     * Gets a random tangible weapon with the given IRNG.
     * @param rng an IRNG, which is expected to be an EpiData much of the time
     * @return a random tangible weapon
     */
    public static Weapon randomPhysicalWeapon(IRNG rng)
    {
        if(!initialized) init();
        return physicalWeapons.randomValue(rng);
    }
    public static OrderedMap<String, Weapon> getUnarmedWeapons()
    {
        if(!initialized) init();
        return unarmedWeapons;
    }
    /**
     * Gets a random unarmed weapon with the given IRNG.
     * @param rng an IRNG, which is expected to be an EpiData much of the time
     * @return a random unarmed weapon
     */
    public static Weapon randomUnarmedWeapon(IRNG rng)
    {
        if(!initialized) init();
        return unarmedWeapons.randomValue(rng);
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
        final Integer plus = (int)'+';
        calcStats = ChangeTable.makeCT(
                CalcStat.PRECISION, plus, raw.precision,
                CalcStat.DAMAGE, plus, raw.damage,
                CalcStat.CRIT, plus, raw.crit,
                CalcStat.INFLUENCE, plus, raw.influence,
                CalcStat.EVASION, plus, raw.evasion,
                CalcStat.DEFENSE, plus, raw.defense,
                CalcStat.LUCK, plus, raw.luck,
                CalcStat.STEALTH, plus, raw.stealth,
                CalcStat.RANGE, plus, raw.range,
                CalcStat.AREA, plus, raw.area,
                CalcStat.QUICKNESS, plus, raw.quickness);
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
//        calcStats[QUICKNESS] = raw.quickness;
        kind = kinds.getInt(raw.kind);
        usage = usages.getInt(raw.usage);
        shape = shapes.getInt(raw.shape);
        path = paths.getInt(raw.path);
        hands = raw.hands;
        groups.add(raw.group1);
        groups.add(raw.group2);
        maneuvers.add(raw.maneuver1);
        maneuvers.add(raw.maneuver2);
        statuses.add(raw.status1);
        statuses.add(raw.status2);
        elements = new WeightedTableWrapper<>(blueprint.nextLong(), new Element[]{Element.valueOf(raw.type1), Element.valueOf(raw.type2)}, new double[]{3, 2});
        materialTypes = raw.materials;
        training = raw.training;
        if(training != null) {
            skills = new Skill[training.length];
            for (int i = 0; i < training.length; i++) {
                skills[i] = Skill.skillsByName.get(training[i]);
            }
        }
        blueprint.weaponData = this;
        blueprint.rarity = Rating.values()[blueprint.between(1, 8)];
        recipeBlueprint = new RecipeBlueprint();
        recipeBlueprint.requiredCatalyst.put(basePhysical,1);
        recipeBlueprint.result.put(blueprint,1);
    }
    public Weapon(Weapon toCopy)
    {
        blueprint = Physical.makeBasic(toCopy.rawWeapon.name, toCopy.rawWeapon.glyph, -0x1.81818p126F);
        calcStats = new ChangeTable(toCopy.calcStats);
        kind = toCopy.kind;
        usage = toCopy.usage;
        shape = toCopy.shape;
        path = toCopy.path;
        materialTypes = Arrays.copyOf(toCopy.materialTypes, toCopy.materialTypes.length);
        training = Arrays.copyOf(toCopy.training, toCopy.training.length);
        skills = Arrays.copyOf(toCopy.skills, toCopy.skills.length);
        hands = toCopy.hands;
        groups.addAll(toCopy.groups);
        maneuvers.addAll(toCopy.maneuvers);
        statuses.addAll(toCopy.statuses);
        elements = toCopy.elements.copy();
        rawWeapon = toCopy.rawWeapon;
        blueprint.weaponData = this;
        while (blueprint.rarity == null || blueprint.rarity == Rating.NONE) {
            blueprint.rarity = blueprint.getRandomElement(Rating.values());
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
