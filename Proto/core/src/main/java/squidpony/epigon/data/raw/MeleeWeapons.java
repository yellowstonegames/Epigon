package squidpony.epigon.data.raw;

import java.util.Map;

import static squidpony.squidmath.OrderedMap.makeMap;

public class MeleeWeapons {
  public static final MeleeWeapons[] ENTRIES = new MeleeWeapons[] {
    new MeleeWeapons("Longsword", "Longswords have good accuracy, damage, and parrying ability, helping their bearer excel in every battle.", '†', "Melee", "Repeat", 1, "Slashing", "Slashing", "Blade", "Blade", "Parry", "Thrust", 3, 5, 1, 1, 5, 3, 0, 0, 1),
    new MeleeWeapons("Katana", "A curved blade attributed mystical power that can make effortless quick slices with impressive accuracy.", '†', "Melee", "Repeat", 1, "Slashing", "Slashing", "Blade", "Dueling", "Sweep", "Blur", 4, 3, 2, 3, 1, 3, 0, 0, 2),
    new MeleeWeapons("Broadsword", "A wide-bladed two-handed sword that mixes the savagery of an axe with the precision of a sword.", '†', "Melee", "Repeat", 2, "Slashing", "Slashing", "Blade", "Blade", "Sweep", "Chop", 2, 8, 3, 0, 2, 3, 0, 0, 1),
    new MeleeWeapons("Rapier", "An elegant dueling weapon that is sharp all along its blade; ideal for thrusting attacks and parries.", '†', "Melee", "Repeat", 1, "Piercing", "Piercing", "Blade", "Dueling", "Parry", "Lunge", 5, 1, 2, 6, 0, 3, 1, 1, 0),
    new MeleeWeapons("Shortsword", "Not quite a knife or a sword, Shortswords are heavier than they look, but can still be swung quickly.", '▶', "Melee", "Repeat", 1, "Slashing", "Piercing", "Blade", "Dueling", "Parry", "Chop", 4, 3, 2, 5, 1, 2, 3, 0, 0),
    new MeleeWeapons("Carving Knife", "This sharpened chef's implement is just as good at slicing up a steak as it is slashing living flesh.", '▶', "Melee", "Repeat", 1, "Piercing", "Slashing", "Dueling", "Dueling", "Parry", "Hurl", 4, 1, 3, 5, 0, 2, 5, 0, 0),
    new MeleeWeapons("Handaxe", "Some call them tomahawks, others hunga munga; this is a throwing axe by any name.", '⚑', "Melee", "Repeat", 1, "Slashing", "Slashing", "Axe", "Axe", "Chop", "Hurl", 3, 5, 7, 3, 1, 0, 1, 0, 0),
    new MeleeWeapons("Hatchet", "A medium-sized axe balanced for carving wood rather than throwing, it can carve flesh just as well.", '⚑', "Melee", "Repeat", 1, "Slashing", "Slashing", "Axe", "Axe", "Chop", "Heft", 1, 7, 8, 2, 2, 0, 0, 0, 0),
    new MeleeWeapons("Greataxe", "It's hard to beat this two-handed axe when it comes to overkill; the destruction these wreak is legendary.", '⚑', "Melee", "Repeat", 2, "Slashing", "Slashing", "Axe", "Axe", "Chop", "Heft", 1, 9, 6, 0, 4, 0, 0, 0, 0),
    new MeleeWeapons("Halberd", "It's an axe on a pole, the pole ends in a spear, and there's also a hook on the pole; clearly this is versatile.", '↟', "Melee", "Repeat", 2, "Slashing", "Piercing", "Polearm", "Axe", "Chop", "Brace", 4, 6, 4, 0, 4, 0, 0, 1, 0),
    new MeleeWeapons("Glaive", "Anything blade-like attached to the end of a pole, for sweeping slices at a comfortable distance.", '↑', "Melee", "Repeat", 2, "Slashing", "Piercing", "Polearm", "Blade", "Sweep", "Brace", 5, 6, 2, 0, 3, 0, 0, 1, 1),
    new MeleeWeapons("Cape", "Among the least effective ways to kill someone, but great for confusing or disarming foes while keeping your hands free.", '⍝', "Melee", "Repeat", 0, "Blunt", "Blunt", "Flexible", "Flexible", "Blur", "Yank", 4, 0, 0, 6, 0, 3, 3, 0, 2),
    new MeleeWeapons("Hammer", "A heavy one-handed tool converted into a weapon of war that is especially strong against armor.", '┯', "Melee", "Repeat", 1, "Blunt", "Blunt", "Bludgeon", "Bludgeon", "Pound", "Heft", 4, 7, 0, 0, 8, 0, 1, 0, 0),
    new MeleeWeapons("Maul", "A brutal two-handed sledgehammer that seems impractical against all but the slowest foes -- or monsters.", '┯', "Melee", "Repeat", 2, "Blunt", "Blunt", "Bludgeon", "Bludgeon", "Pound", "Heft", 2, 9, 0, 0, 9, 0, 0, 0, 0),
    new MeleeWeapons("Club", "A plain-old heavy stick that can be swung with surprising speed, and can be passed off as a normal twig.", '╿', "Melee", "Repeat", 1, "Blunt", "Blunt", "Bludgeon", "Bludgeon", "Pound", "Hurl", 3, 4, 0, 0, 4, 5, 4, 0, 0),
    new MeleeWeapons("Mace", "Not just any stick, this one's made of solid metal, and can pulverize armor with a good wallop.", '╿', "Melee", "Repeat", 1, "Blunt", "Blunt", "Bludgeon", "Bludgeon", "Parry", "Pound", 4, 5, 0, 0, 9, 0, 2, 0, 0),
    new MeleeWeapons("Pole", "Whether an collapsible pole or just a piece of bamboo, a Pole is excellent at whacking multiple foes.", '|', "Melee", "Repeat", 2, "Blunt", "Blunt", "Polearm", "Bludgeon", "Sweep", "Lunge", 4, 3, 0, 1, 4, 2, 0, 1, 2),
    new MeleeWeapons("Taiaha", "An unusual mix of a spear's pointed tip with a shield-shaped haft, usually made entirely from hard wood.", '⌽', "Melee", "Repeat", 2, "Blunt", "Piercing", "Polearm", "Dueling", "Parry", "Pound", 3, 3, 2, 4, 4, 4, 0, 0, 0),
    new MeleeWeapons("Shortspear", "Lighter than a Lance and heavier than a Javelin, a Shortspear can be effectively wielded at close to medium range.", '↑', "Melee", "Repeat", 1, "Piercing", "Piercing", "Polearm", "Polearm", "Brace", "Hurl", 5, 3, 2, 6, 2, 0, 0, 1, 0),
    new MeleeWeapons("Lance", "An uncommon weapon, the Lance can't attack a close-by enemy but can annihilate further-away foes.", '↑', "Melee", "Repeat", 2, "Piercing", "Piercing", "Polearm", "Polearm", "Brace", "Lunge", 2, 6, 1, 3, 4, 0, 0, 2, 0),
    new MeleeWeapons("Leaf Spear", "A heavier mid-length spear with a broad leaf-shaped blade at the end; it can be used for slicing and stabbing alike.", '⍋', "Melee", "Repeat", 2, "Piercing", "Slashing", "Polearm", "Polearm", "Thrust", "Sweep", 2, 6, 4, 1, 3, 0, 0, 1, 1),
    new MeleeWeapons("Nunchaku", "A martial-arts weapon consisting of two club-like bars connected with a short chain; excellent for defensive use.", '⋀', "Melee", "Repeat", 1, "Blunt", "Blunt", "Bludgeon", "Flexible", "Parry", "Blur", 5, 1, 0, 5, 0, 2, 5, 0, 1),
    new MeleeWeapons("Section Staff", "A series of three or more wooden rods connected with chains; originally meant for threshing rice but deadly when swung.", '⋀', "Melee", "Repeat", 2, "Blunt", "Blunt", "Bludgeon", "Flexible", "Parry", "Sweep", 4, 2, 0, 6, 2, 2, 0, 0, 2),
    new MeleeWeapons("Hand Flail", "A nasty spiked ball whipped around on a heavy chain; it's simply brutal against armored foes.", '!', "Melee", "Repeat", 1, "Blunt", "Blunt", "Bludgeon", "Flexible", "Sweep", "Yank", 3, 7, 0, 0, 6, 2, 0, 0, 1),
    new MeleeWeapons("Meteor Flail", "A small, heavy weight on the end of a long vine or rope; making one is easy, but fighting with one is very hard.", '!', "Melee", "Repeat", 2, "Blunt", "Blunt", "Bludgeon", "Flexible", "Heft", "Yank", 3, 4, 0, 3, 0, 4, 0, 3, 0),
    new MeleeWeapons("Whip", "A leather bullwhip that's flexible enough to knock a weapon from the hands of nearby opponents.", '∫', "Melee", "Repeat", 1, "Blunt", "Slashing", "Flexible", "Flexible", "Lunge", "Yank", 5, 0, 4, 3, 0, 4, 0, 2, 0),
    new MeleeWeapons("Chain", "A metal chain that can be spun to deliver painful strikes, and can wrap around an enemy's weapon to relieve him of it.", '∫', "Melee", "Repeat", 1, "Blunt", "Blunt", "Flexible", "Flexible", "Parry", "Yank", 4, 2, 0, 5, 1, 4, 0, 0, 2),
    new MeleeWeapons("Sai", "A martial-arts weapon with three curving spikes that provide ample ability to disarm foes.", '⍦', "Melee", "Repeat", 1, "Piercing", "Piercing", "Dueling", "Dueling", "Thrust", "Yank", 4, 1, 2, 6, 2, 1, 4, 0, 0),
    new MeleeWeapons("Trident", "A fishing spear adapted for gladiatorial and maritime combat, the trident has three barbed prongs on a mid-size pole.", '⍦', "Melee", "Repeat", 2, "Piercing", "Piercing", "Polearm", "Dueling", "Brace", "Hurl", 3, 4, 2, 3, 4, 2, 0, 1, 0),
    new MeleeWeapons("Sickle", "A lightweight, deeply-curved blade that can be used for severing plant roots, or a foe's limbs, as well as disarming.", 'ʕ', "Melee", "Repeat", 1, "Slashing", "Slashing", "Blade", "Axe", "Chop", "Yank", 3, 4, 7, 0, 1, 3, 2, 0, 0),
    new MeleeWeapons("Khopesh", "A sword-like weapon with a heavy blade that goes straight and then curves; challenging to wield and to confront.", 'ʕ', "Melee", "Repeat", 1, "Slashing", "Slashing", "Blade", "Axe", "Heft", "Yank", 2, 6, 6, 0, 3, 1, 0, 0, 1),
    new MeleeWeapons("Scythe", "A hook-bladed farming tool meant for slicing wide swaths of grain, adapted to slicing wide swaths in necks.", 'ʕ', "Melee", "Repeat", 2, "Slashing", "Slashing", "Blade", "Axe", "Chop", "Sweep", 1, 6, 9, 0, 0, 0, 0, 0, 2),
    new MeleeWeapons("Light Shield", "A shield that's meant for actively parrying and blocking enemy attacks, mixing in quick jabs with the edge of the shield.", '⍟', "Melee", "Repeat", 1, "Blunt", "Blunt", "Bludgeon", "Dueling", "Parry", "Blur", 3, 2, 0, 7, 7, 1, 0, 0, 0),
    new MeleeWeapons("Heavy Shield", "A large shield that's effective at defending even while barely moving, but it's hard to precisely wield one.", '⍟', "Melee", "Repeat", 1, "Blunt", "Blunt", "Bludgeon", "Bludgeon", "Parry", "Heft", 1, 5, 0, 5, 9, 0, 0, 0, 0),
  };

  public static final Map<String, MeleeWeapons> MAPPING = makeMap(
  "Longsword", ENTRIES[0], "Katana", ENTRIES[1], "Broadsword", ENTRIES[2], "Rapier",
  ENTRIES[3], "Shortsword", ENTRIES[4], "Carving Knife", ENTRIES[5], "Handaxe",
  ENTRIES[6], "Hatchet", ENTRIES[7], "Greataxe", ENTRIES[8], "Halberd",
  ENTRIES[9], "Glaive", ENTRIES[10], "Cape", ENTRIES[11], "Hammer", ENTRIES[12],
  "Maul", ENTRIES[13], "Club", ENTRIES[14], "Mace", ENTRIES[15], "Pole",
  ENTRIES[16], "Taiaha", ENTRIES[17], "Shortspear", ENTRIES[18], "Lance",
  ENTRIES[19], "Leaf Spear", ENTRIES[20], "Nunchaku", ENTRIES[21], "Section Staff",
  ENTRIES[22], "Hand Flail", ENTRIES[23], "Meteor Flail", ENTRIES[24],
  "Whip", ENTRIES[25], "Chain", ENTRIES[26], "Sai", ENTRIES[27], "Trident",
  ENTRIES[28], "Sickle", ENTRIES[29], "Khopesh", ENTRIES[30], "Scythe",
  ENTRIES[31], "Light Shield", ENTRIES[32], "Heavy Shield", ENTRIES[33]);

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

  public MeleeWeapons(String name, String description, char glyph, String kind, String usage,
      int hands, String type1, String type2, String group1, String group2, String maneuver1,
      String maneuver2, int precision, int damage, int crit, int evasion, int defense, int luck,
      int stealth, int reach, int span) {
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
  }

  public static MeleeWeapons get(String item) {
    return MAPPING.get(item);
  }
}
