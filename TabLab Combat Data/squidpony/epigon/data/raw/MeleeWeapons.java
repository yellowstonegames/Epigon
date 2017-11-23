package squidpony.epigon.data.raw;

import static squidpony.squidmath.OrderedMap.makeMap;

import java.util.Map;

public class MeleeWeapons {
  public static final MeleeWeapons[] ENTRIES = new MeleeWeapons[] {
    new MeleeWeapons("Longsword", "Longswords have good accuracy, damage, and parrying ability, helping their bearer excel in every battle.", "Melee", "Repeat", 1, "Slashing", "Slashing", "Blade", "Blade", "Parry", "Thrust"),
    new MeleeWeapons("Katana", "A curved blade attributed mystical power that can make effortless quick slices with impressive accuracy.", "Melee", "Repeat", 1, "Slashing", "Slashing", "Blade", "Dueling", "Sweep", "Blur"),
    new MeleeWeapons("Broadsword", "A wide-bladed two-handed sword that mixes the savagery of an axe with the precision of a sword.", "Melee", "Repeat", 2, "Slashing", "Slashing", "Blade", "Blade", "Sweep", "Chop"),
    new MeleeWeapons("Rapier", "An elegant dueling weapon that is sharp all along its blade; ideal for thrusting attacks and parries.", "Melee", "Repeat", 1, "Piercing", "Piercing", "Blade", "Dueling", "Parry", "Lunge"),
    new MeleeWeapons("Shortsword", "Not quite a knife or a sword, Shortswords are heavier than they look, but can still be swung quickly.", "Melee", "Repeat", 1, "Slashing", "Piercing", "Blade", "Dueling", "Parry", "Chop"),
    new MeleeWeapons("Carving Knife", "This sharpened chef's implement is just as good at slicing up a steak as it is slashing living flesh.", "Melee", "Repeat", 1, "Piercing", "Slashing", "Dueling", "Dueling", "Parry", "Hurl"),
    new MeleeWeapons("Handaxe", "Some call them tomahawks, others hunga munga; this is a throwing axe by any name.", "Melee", "Repeat", 1, "Slashing", "Slashing", "Axe", "Axe", "Chop", "Hurl"),
    new MeleeWeapons("Hatchet", "A medium-sized axe balanced for carving wood rather than throwing, it can carve flesh just as well.", "Melee", "Repeat", 1, "Slashing", "Slashing", "Axe", "Axe", "Chop", "Heft"),
    new MeleeWeapons("Greataxe", "It's hard to beat this two-handed axe when it comes to overkill; the destruction these wreak is legendary.", "Melee", "Repeat", 2, "Slashing", "Slashing", "Axe", "Axe", "Chop", "Heft"),
    new MeleeWeapons("Halberd", "It's an axe on a pole, the pole ends in a spear, and there's also a hook on the pole; clearly this is versatile.", "Melee", "Repeat", 2, "Slashing", "Piercing", "Polearm", "Axe", "Chop", "Brace"),
    new MeleeWeapons("Glaive", "Anything blade-like attached to the end of a pole, for sweeping slices at a comfortable distance.", "Melee", "Repeat", 2, "Slashing", "Piercing", "Polearm", "Blade", "Sweep", "Brace"),
    new MeleeWeapons("Cape", "Among the least effective ways to kill someone, but great for confusing or disarming foes while keeping your hands free.", "Melee", "Repeat", 0, "Blunt", "Blunt", "Flexible", "Flexible", "Blur", "Yank"),
    new MeleeWeapons("Hammer", "A heavy one-handed tool converted into a weapon of war that is especially strong against armor.", "Melee", "Repeat", 1, "Blunt", "Blunt", "Bludgeon", "Bludgeon", "Pound", "Heft"),
    new MeleeWeapons("Maul", "A brutal two-handed sledgehammer that seems impractical against all but the slowest foes -- or monsters.", "Melee", "Repeat", 2, "Blunt", "Blunt", "Bludgeon", "Bludgeon", "Pound", "Heft"),
    new MeleeWeapons("Club", "A plain-old heavy stick that can be swung with surprising speed, and can be passed off as a normal twig.", "Melee", "Repeat", 1, "Blunt", "Blunt", "Bludgeon", "Bludgeon", "Pound", "Hurl"),
    new MeleeWeapons("Mace", "Not just any stick, this one's made of solid metal, and can pulverize armor with a good wallop.", "Melee", "Repeat", 1, "Blunt", "Blunt", "Bludgeon", "Bludgeon", "Parry", "Pound"),
    new MeleeWeapons("Pole", "Whether an collapsible pole or just a piece of bamboo, a Pole is excellent at whacking multiple foes.", "Melee", "Repeat", 2, "Blunt", "Blunt", "Polearm", "Bludgeon", "Sweep", "Lunge"),
    new MeleeWeapons("Taiaha", "An unusual mix of a spear's pointed tip with a shield-shaped haft, usually made entirely from hard wood.", "Melee", "Repeat", 2, "Blunt", "Piercing", "Polearm", "Dueling", "Parry", "Pound"),
    new MeleeWeapons("Shortspear", "Lighter than a Lance and heavier than a Javelin, a Shortspear can be effectively wielded at close to medium range.", "Melee", "Repeat", 1, "Piercing", "Piercing", "Polearm", "Polearm", "Brace", "Hurl"),
    new MeleeWeapons("Lance", "An uncommon weapon, the Lance can't attack a close-by enemy but can annihilate further-away foes.", "Melee", "Repeat", 2, "Piercing", "Piercing", "Polearm", "Polearm", "Brace", "Lunge"),
    new MeleeWeapons("Leaf Spear", "A heavier mid-length spear with a broad leaf-shaped blade at the end; it can be used for slicing and stabbing alike.", "Melee", "Repeat", 2, "Piercing", "Slashing", "Polearm", "Polearm", "Thrust", "Sweep"),
    new MeleeWeapons("Nunchaku", "A martial-arts weapon consisting of two club-like bars connected with a short chain; excellent for defensive use.", "Melee", "Repeat", 1, "Blunt", "Blunt", "Bludgeon", "Flexible", "Parry", "Blur"),
    new MeleeWeapons("Section Staff", "A series of three or more wooden rods connected with chains; originally meant for threshing rice but deadly when swung.", "Melee", "Repeat", 2, "Blunt", "Blunt", "Bludgeon", "Flexible", "Parry", "Sweep"),
    new MeleeWeapons("Hand Flail", "A nasty spiked ball whipped around on a heavy chain; it's simply brutal against armored foes.", "Melee", "Repeat", 1, "Blunt", "Blunt", "Bludgeon", "Flexible", "Sweep", "Yank"),
    new MeleeWeapons("Meteor Flail", "A small, heavy weight on the end of a long vine or rope; making one is easy, but fighting with one is very hard.", "Melee", "Repeat", 2, "Blunt", "Blunt", "Bludgeon", "Flexible", "Heft", "Yank"),
    new MeleeWeapons("Whip", "A leather bullwhip that's flexible enough to knock a weapon from the hands of nearby opponents.", "Melee", "Repeat", 1, "Blunt", "Slashing", "Flexible", "Flexible", "Lunge", "Yank"),
    new MeleeWeapons("Chain", "A metal chain that can be spun to deliver painful strikes, and can wrap around an enemy's weapon to relieve him of it.", "Melee", "Repeat", 1, "Blunt", "Blunt", "Flexible", "Flexible", "Parry", "Yank"),
    new MeleeWeapons("Sai", "A martial-arts weapon with three curving spikes that provide ample ability to disarm foes.", "Melee", "Repeat", 1, "Piercing", "Piercing", "Dueling", "Dueling", "Thrust", "Yank"),
    new MeleeWeapons("Trident", "A fishing spear adapted for gladiatorial and maritime combat, the trident has three barbed prongs on a mid-size pole.", "Melee", "Repeat", 2, "Piercing", "Piercing", "Polearm", "Dueling", "Brace", "Hurl"),
    new MeleeWeapons("Sickle", "A lightweight, deeply-curved blade that can be used for severing plant roots, or a foe's limbs, as well as disarming.", "Melee", "Repeat", 1, "Slashing", "Slashing", "Blade", "Axe", "Chop", "Yank"),
    new MeleeWeapons("Khopesh", "A sword-like weapon with a heavy blade that goes straight and then curves; challenging to wield and to confront.", "Melee", "Repeat", 1, "Slashing", "Slashing", "Blade", "Axe", "Heft", "Yank"),
    new MeleeWeapons("Scythe", "A hook-bladed farming tool meant for slicing wide swaths of grain, adapted to slicing wide swaths in necks.", "Melee", "Repeat", 2, "Slashing", "Slashing", "Blade", "Axe", "Chop", "Sweep"),
    new MeleeWeapons("Light Shield", "A shield that's meant for actively parrying and blocking enemy attacks, mixing in quick jabs with the edge of the shield.", "Melee", "Repeat", 1, "Blunt", "Blunt", "Bludgeon", "Dueling", "Parry", "Blur"),
    new MeleeWeapons("Heavy Shield", "A large shield that's effective at defending even while barely moving, but it's hard to precisely wield one.", "Melee", "Repeat", 1, "Blunt", "Blunt", "Bludgeon", "Bludgeon", "Parry", "Heft"),
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

  public String kind;

  public String usage;

  public int hands;

  public String type1;

  public String type2;

  public String group1;

  public String group2;

  public String maneuver1;

  public String maneuver2;

  public MeleeWeapons(String name, String description, String kind, String usage, int hands,
      String type1, String type2, String group1, String group2, String maneuver1,
      String maneuver2) {
    this.name = name;
    this.description = description;
    this.kind = kind;
    this.usage = usage;
    this.hands = hands;
    this.type1 = type1;
    this.type2 = type2;
    this.group1 = group1;
    this.group2 = group2;
    this.maneuver1 = maneuver1;
    this.maneuver2 = maneuver2;
  }

  public static MeleeWeapons get(String item) {
    return MAPPING.get(item);
  }
}
