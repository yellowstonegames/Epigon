package squidpony.epigon.data.raw;

import java.util.Map;

import static squidpony.squidmath.OrderedMap.makeMap;

public class MeleeWeapon {
  public static final MeleeWeapon[] ENTRIES = new MeleeWeapon[] {
    new MeleeWeapon("longsword", "Longswords have good accuracy, damage, and parrying ability, helping their bearer excel in every battle.", '†', "Melee", "Repeat", 1, "Slashing", "Slashing", "Blade", "Blade", "Parry", "Thrust", 3, 5, 1, 1, 5, 3, 0, 0, 1, new String[] {"Metal"}),
    new MeleeWeapon("katana", "A curved blade attributed mystical power that can make effortless quick slices with impressive accuracy.", '†', "Melee", "Repeat", 1, "Slashing", "Slashing", "Blade", "Dueling", "Sweep", "Blur", 4, 3, 2, 3, 1, 3, 0, 0, 2, new String[] {"Metal"}),
    new MeleeWeapon("broadsword", "A wide-bladed two-handed sword that mixes the savagery of an axe with the precision of a sword.", '†', "Melee", "Repeat", 2, "Slashing", "Slashing", "Blade", "Blade", "Sweep", "Chop", 2, 8, 3, 0, 2, 3, 0, 0, 1, new String[] {"Metal"}),
    new MeleeWeapon("rapier", "An elegant dueling weapon that is sharp all along its blade; ideal for thrusting attacks and parries.", '†', "Melee", "Repeat", 1, "Piercing", "Piercing", "Blade", "Dueling", "Parry", "Lunge", 5, 1, 2, 6, 0, 3, 1, 1, 0, new String[] {"Metal"}),
    new MeleeWeapon("shortsword", "Not quite a knife or a sword, Shortswords are heavier than they look, but can still be swung quickly.", '▶', "Melee", "Repeat", 1, "Slashing", "Piercing", "Blade", "Dueling", "Parry", "Chop", 4, 3, 2, 5, 1, 2, 3, 0, 0, new String[] {"Metal"}),
    new MeleeWeapon("carving knife", "This sharpened chef's implement is just as good at slicing up a steak as it is slashing living flesh.", '▶', "Melee", "Repeat", 1, "Piercing", "Slashing", "Dueling", "Dueling", "Parry", "Hurl", 4, 1, 3, 5, 0, 2, 5, 0, 0, new String[] {"Metal"}),
    new MeleeWeapon("handaxe", "Some call them tomahawks, others hunga munga; this is a throwing axe by any name.", '⚑', "Melee", "Repeat", 1, "Slashing", "Slashing", "Axe", "Axe", "Chop", "Hurl", 3, 5, 7, 3, 1, 0, 1, 0, 0, new String[] {"Metal", "Wood", "Stone"}),
    new MeleeWeapon("hatchet", "A medium-sized axe balanced for carving wood rather than throwing, it can carve flesh just as well.", '⚑', "Melee", "Repeat", 1, "Slashing", "Slashing", "Axe", "Axe", "Chop", "Heft", 1, 7, 8, 2, 2, 0, 0, 0, 0, new String[] {"Metal", "Wood", "Stone"}),
    new MeleeWeapon("greataxe", "It's hard to beat this two-handed axe when it comes to overkill; the destruction these wreak is legendary.", '⚑', "Melee", "Repeat", 2, "Slashing", "Slashing", "Axe", "Axe", "Chop", "Heft", 1, 9, 6, 0, 4, 0, 0, 0, 0, new String[] {"Metal", "Wood"}),
    new MeleeWeapon("halberd", "It's an axe on a pole, the pole ends in a spear, and there's also a hook on the pole; clearly this is versatile.", '↟', "Melee", "Repeat", 2, "Slashing", "Piercing", "Polearm", "Axe", "Chop", "Brace", 4, 6, 4, 0, 4, 0, 0, 1, 0, new String[] {"Metal", "Wood"}),
    new MeleeWeapon("glaive", "Anything blade-like attached to the end of a pole, for sweeping slices at a comfortable distance.", '↑', "Melee", "Repeat", 2, "Slashing", "Piercing", "Polearm", "Blade", "Sweep", "Brace", 5, 6, 2, 0, 3, 0, 0, 1, 1, new String[] {"Metal", "Wood"}),
    new MeleeWeapon("cape", "Among the least effective ways to kill someone, but great for confusing or disarming foes while keeping your hands free.", '⍝', "Melee", "Repeat", 0, "Blunt", "Blunt", "Flexible", "Flexible", "Blur", "Yank", 4, 0, 0, 6, 0, 3, 3, 0, 2, new String[] {"Cloth"}),
    new MeleeWeapon("hammer", "A heavy one-handed tool converted into a weapon of war that is especially strong against armor.", '⊤', "Melee", "Repeat", 1, "Blunt", "Blunt", "Bludgeon", "Bludgeon", "Pound", "Heft", 4, 7, 0, 0, 8, 0, 1, 0, 0, new String[] {"Metal", "Stone"}),
    new MeleeWeapon("maul", "A brutal two-handed sledgehammer that seems impractical against all but the slowest foes -- or monsters.", '⊤', "Melee", "Repeat", 2, "Blunt", "Blunt", "Bludgeon", "Bludgeon", "Pound", "Heft", 2, 9, 0, 0, 9, 0, 0, 0, 0, new String[] {"Metal", "Stone"}),
    new MeleeWeapon("club", "A plain-old heavy stick that can be swung with surprising speed, and can be passed off as a normal twig.", '╿', "Melee", "Repeat", 1, "Blunt", "Blunt", "Bludgeon", "Bludgeon", "Pound", "Hurl", 3, 4, 0, 0, 4, 5, 4, 0, 0, new String[] {"Wood"}),
    new MeleeWeapon("mace", "Not just any stick, this one's made of solid metal, and can pulverize armor with a good wallop.", '╿', "Melee", "Repeat", 1, "Blunt", "Blunt", "Bludgeon", "Bludgeon", "Parry", "Pound", 4, 5, 0, 0, 9, 0, 2, 0, 0, new String[] {"Metal", "Stone"}),
    new MeleeWeapon("pole", "Whether an collapsible pole or just a piece of bamboo, a Pole is excellent at whacking multiple foes.", '|', "Melee", "Repeat", 2, "Blunt", "Blunt", "Polearm", "Bludgeon", "Sweep", "Lunge", 4, 3, 0, 1, 4, 2, 0, 1, 2, new String[] {"Wood"}),
    new MeleeWeapon("taiaha", "An unusual mix of a spear's pointed tip with a shield-shaped haft, usually made entirely from hard wood.", '⌽', "Melee", "Repeat", 2, "Blunt", "Piercing", "Polearm", "Dueling", "Parry", "Pound", 3, 3, 2, 4, 4, 4, 0, 0, 0, new String[] {"Wood"}),
    new MeleeWeapon("shortspear", "Lighter than a Lance and heavier than a Javelin, a Shortspear can be effectively wielded at close to medium range.", '↑', "Melee", "Repeat", 1, "Piercing", "Piercing", "Polearm", "Polearm", "Brace", "Hurl", 5, 3, 2, 6, 2, 0, 0, 1, 0, new String[] {"Wood", "Metal"}),
    new MeleeWeapon("lance", "An uncommon weapon, the Lance can't attack a close-by enemy but can annihilate further-away foes.", '↑', "Melee", "Repeat", 2, "Piercing", "Piercing", "Polearm", "Polearm", "Brace", "Lunge", 2, 6, 1, 3, 4, 0, 0, 2, 0, new String[] {"Wood", "Metal"}),
    new MeleeWeapon("leaf spear", "A heavier mid-length spear with a broad leaf-shaped blade at the end; it can be used for slicing and stabbing alike.", '⍋', "Melee", "Repeat", 2, "Piercing", "Slashing", "Polearm", "Polearm", "Thrust", "Sweep", 2, 6, 4, 1, 3, 0, 0, 1, 1, new String[] {"Wood", "Metal"}),
    new MeleeWeapon("nunchaku", "A martial-arts weapon consisting of two club-like bars connected with a short chain; excellent for defensive use.", '⋀', "Melee", "Repeat", 1, "Blunt", "Blunt", "Bludgeon", "Flexible", "Parry", "Blur", 5, 1, 0, 5, 0, 2, 5, 0, 1, new String[] {"Wood"}),
    new MeleeWeapon("section staff", "A series of three or more wooden rods connected with chains; originally meant for threshing rice but deadly when swung.", '⋀', "Melee", "Repeat", 2, "Blunt", "Blunt", "Bludgeon", "Flexible", "Parry", "Sweep", 4, 2, 0, 6, 2, 2, 0, 0, 2, new String[] {"Wood"}),
    new MeleeWeapon("hand flail", "A nasty spiked ball whipped around on a heavy chain; it's simply brutal against armored foes.", '!', "Melee", "Repeat", 1, "Blunt", "Blunt", "Bludgeon", "Flexible", "Sweep", "Yank", 3, 7, 0, 0, 6, 2, 0, 0, 1, new String[] {"Wood", "Metal"}),
    new MeleeWeapon("meteor flail", "A small, heavy weight on the end of a long vine or rope; making one is easy, but fighting with one is very hard.", '!', "Melee", "Repeat", 2, "Blunt", "Blunt", "Bludgeon", "Flexible", "Heft", "Yank", 3, 4, 0, 3, 0, 4, 0, 3, 0, new String[] {"Leather", "Stone"}),
    new MeleeWeapon("whip", "A leather bullwhip that's flexible enough to knock a weapon from the hands of nearby opponents.", '∫', "Melee", "Repeat", 1, "Blunt", "Slashing", "Flexible", "Flexible", "Lunge", "Yank", 5, 0, 4, 3, 0, 4, 0, 2, 0, new String[] {"Leather"}),
    new MeleeWeapon("chain", "A metal chain that can be spun to deliver painful strikes, and can wrap around an enemy's weapon to relieve him of it.", '∫', "Melee", "Repeat", 1, "Blunt", "Blunt", "Flexible", "Flexible", "Parry", "Yank", 4, 2, 0, 5, 1, 4, 0, 0, 2, new String[] {"Metal"}),
    new MeleeWeapon("sai", "A martial-arts weapon with three curving spikes that provide ample ability to disarm foes.", '⍦', "Melee", "Repeat", 1, "Piercing", "Piercing", "Dueling", "Dueling", "Thrust", "Yank", 4, 1, 2, 6, 2, 1, 4, 0, 0, new String[] {"Metal"}),
    new MeleeWeapon("trident", "A fishing spear adapted for gladiatorial and maritime combat, the trident has three barbed prongs on a mid-size pole.", '⍦', "Melee", "Repeat", 2, "Piercing", "Piercing", "Polearm", "Dueling", "Brace", "Hurl", 3, 4, 2, 3, 4, 2, 0, 1, 0, new String[] {"Metal", "Wood"}),
    new MeleeWeapon("sickle", "A lightweight, deeply-curved blade that can be used for severing plant roots, or a foe's limbs, as well as disarming.", 'ʕ', "Melee", "Repeat", 1, "Slashing", "Slashing", "Blade", "Axe", "Chop", "Yank", 3, 4, 7, 0, 1, 3, 2, 0, 0, new String[] {"Metal"}),
    new MeleeWeapon("khopesh", "A sword-like weapon with a heavy blade that goes straight and then curves; challenging to wield and to confront.", 'ʕ', "Melee", "Repeat", 1, "Slashing", "Slashing", "Blade", "Axe", "Heft", "Yank", 2, 6, 6, 0, 3, 1, 0, 0, 1, new String[] {"Metal"}),
    new MeleeWeapon("scythe", "A hook-bladed farming tool meant for slicing wide swaths of grain, adapted to slicing wide swaths in necks.", 'ʕ', "Melee", "Repeat", 2, "Slashing", "Slashing", "Blade", "Axe", "Chop", "Sweep", 1, 6, 9, 0, 0, 0, 0, 0, 2, new String[] {"Metal", "Wood"}),
    new MeleeWeapon("light shield", "A shield that's meant for actively parrying and blocking enemy attacks, mixing in quick jabs with the edge of the shield.", '⍟', "Melee", "Repeat", 1, "Blunt", "Blunt", "Bludgeon", "Dueling", "Parry", "Blur", 3, 2, 0, 7, 7, 1, 0, 0, 0, new String[] {"Wood", "Metal", "Leather"}),
    new MeleeWeapon("heavy shield", "A large shield that's effective at defending even while barely moving, but it's hard to precisely wield one.", '⍟', "Melee", "Repeat", 1, "Blunt", "Blunt", "Bludgeon", "Bludgeon", "Parry", "Heft", 1, 5, 0, 5, 9, 0, 0, 0, 0, new String[] {"Wood", "Metal"}),
  };

  public static final Map<String, MeleeWeapon> MAPPING = makeMap(
  "longsword", ENTRIES[0], "katana", ENTRIES[1], "broadsword", ENTRIES[2], "rapier",
  ENTRIES[3], "shortsword", ENTRIES[4], "carving knife", ENTRIES[5], "handaxe",
  ENTRIES[6], "hatchet", ENTRIES[7], "greataxe", ENTRIES[8], "halberd",
  ENTRIES[9], "glaive", ENTRIES[10], "cape", ENTRIES[11], "hammer", ENTRIES[12],
  "maul", ENTRIES[13], "club", ENTRIES[14], "mace", ENTRIES[15], "pole",
  ENTRIES[16], "taiaha", ENTRIES[17], "shortspear", ENTRIES[18], "lance",
  ENTRIES[19], "leaf spear", ENTRIES[20], "nunchaku", ENTRIES[21], "section staff",
  ENTRIES[22], "hand flail", ENTRIES[23], "meteor flail", ENTRIES[24],
  "whip", ENTRIES[25], "chain", ENTRIES[26], "sai", ENTRIES[27], "trident",
  ENTRIES[28], "sickle", ENTRIES[29], "khopesh", ENTRIES[30], "scythe",
  ENTRIES[31], "light shield", ENTRIES[32], "heavy shield", ENTRIES[33]);

  public String name;

  public String description;

  public char glyph;

  public String kind;

  public String usage;

  public int hands;

  public String type1;

  public String type2;

  public String group1;

  public String group2;

  public String maneuver1;

  public String maneuver2;

  public int precision;

  public int damage;

  public int crit;

  public int evasion;

  public int defense;

  public int luck;

  public int stealth;

  public int reach;

  public int span;

  public String[] materials;

  public MeleeWeapon(String name, String description, char glyph, String kind, String usage,
      int hands, String type1, String type2, String group1, String group2, String maneuver1,
      String maneuver2, int precision, int damage, int crit, int evasion, int defense, int luck,
      int stealth, int reach, int span, String[] materials) {
    this.name = name;
    this.description = description;
    this.glyph = glyph;
    this.kind = kind;
    this.usage = usage;
    this.hands = hands;
    this.type1 = type1;
    this.type2 = type2;
    this.group1 = group1;
    this.group2 = group2;
    this.maneuver1 = maneuver1;
    this.maneuver2 = maneuver2;
    this.precision = precision;
    this.damage = damage;
    this.crit = crit;
    this.evasion = evasion;
    this.defense = defense;
    this.luck = luck;
    this.stealth = stealth;
    this.reach = reach;
    this.span = span;
    this.materials = materials;
  }

  public static MeleeWeapon get(String item) {
    return MAPPING.get(item);
  }
}
