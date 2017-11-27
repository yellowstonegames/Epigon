package squidpony.epigon.data.raw;

import static squidpony.squidmath.OrderedMap.makeMap;

import java.util.Map;

public class RangedWeapon {
  public static final RangedWeapon[] ENTRIES = new RangedWeapon[] {
    new RangedWeapon("shortbow", "A smaller bow that isn't as unwieldy in close-quarters combat as a Longbow, and isn't as conspicuous when carried.", '(', "Ranged", "Projectile", 2, "Piercing", "Piercing", "Forceful", "Arc", "Pin", "Arrows", 4, 4, 0, 1, 0, 1, 1, 4, 5, new String[] {"Wood", "Hide"}),
    new RangedWeapon("longbow", "A very large bow, usually taller than its wielder, that boasts tremendous range and hefty power.", '(', "Ranged", "Projectile", 2, "Piercing", "Piercing", "Forceful", "Arc", "Heave", "Arrows", 3, 5, 1, 0, 0, 1, 0, 6, 4, new String[] {"Wood", "Hide"}),
    new RangedWeapon("light crosssbow", "A small machine that launches a bolt with the pull of a trigger; slow to reload, but capable of piercing thick armor.", '⊢', "Ranged", "Projectile", 1, "Piercing", "Piercing", "Precise", "Straight", "Weave", "Bolts", 6, 5, 0, 0, 1, 0, 1, 5, 2, new String[] {"Wood"}),
    new RangedWeapon("heavy crossbow", "A weighty machine that can launch a bolt in an instant, but takes a long time to reload; makes up for it with huge power.", '⊢', "Ranged", "Projectile", 2, "Piercing", "Piercing", "Precise", "Straight", "Weave", "Bolts", 5, 7, 0, 0, 1, 0, 0, 6, 1, new String[] {"Wood"}),
    new RangedWeapon("pistol", "This hand-held firearm is very imprecise, but that doesn't always hinder it because its bullet keeps bouncing after missing.", 'ᵣ', "Ranged", "Projectile", 1, "Piercing", "Piercing", "Precise", "Straight", "Ricochet", "Powder", 1, 8, 4, 0, 0, 0, 3, 4, 0, new String[] {"Metal"}),
    new RangedWeapon("musket", "A heavy two-handed firearm with better accuracy than a Pistol and longer range, but more lengthy reload times.", 'ɼ', "Ranged", "Projectile", 2, "Piercing", "Piercing", "Precise", "Straight", "Ricochet", "Powder", 2, 9, 4, 0, 0, 0, 0, 5, 0, new String[] {"Metal"}),
    new RangedWeapon("sling", "An early stone-hurling weapon made from cord and hide, it has considerably better range than throwing a rock by hand.", '⍩', "Ranged", "Projectile", 1, "Blunt", "Blunt", "Forceful", "Arc", "Yank", "Pellets", 3, 2, 3, 0, 0, 2, 4, 3, 3, new String[] {"Hide"}),
    new RangedWeapon("staff sling", "A curved staff with cords to hold a projectile at one end, this weapon improves a Sling's range even further.", 'Ḯ', "Ranged", "Projectile", 2, "Blunt", "Blunt", "Forceful", "Arc", "Pound", "Pellets", 2, 4, 2, 0, 2, 1, 1, 5, 3, new String[] {"Hide"}),
    new RangedWeapon("chakram", "A bladed hoop with just as much use at medium range as in melee combat, it can be drawn quickly and thrown far.", '°', "Ranged", "Thrown", 1, "Slashing", "Slashing", "Blade", "Straight", "Chop", "Blur", 4, 3, 3, 0, 4, 0, 0, 2, 4, new String[] {"Metal"}),
    new RangedWeapon("boomerang", "A chunk of hard wood, carefully fashioned into a hunting weapon that spins all around the target when it misses.", '❮', "Ranged", "Thrown", 1, "Blunt", "Blunt", "Bludgeon", "Arc", "Pound", "Ricochet", 3, 2, 0, 1, 3, 4, 1, 3, 3, new String[] {"Wood"}),
    new RangedWeapon("javelin", "A very light spear that's primarily meant for throwing, but can be used as a melee weapon in desperate situations.", '↗', "Ranged", "Thrown", 1, "Piercing", "Piercing", "Polearm", "Arc", "Pin", "Thrust", 4, 4, 1, 1, 0, 2, 1, 4, 3, new String[] {"Wood"}),
    new RangedWeapon("atlatl", "A simple-looking device that enables a spear to be thrown much further, and with greater precision, than it can by hand.", '⋁', "Ranged", "Projectile", 2, "Piercing", "Piercing", "Forceful", "Arc", "Heave", "Darts", 4, 7, 1, 0, 0, 1, 0, 5, 2, new String[] {"Wood"}),
    new RangedWeapon("shuriken", "A throwing weapon, shaped like a star or tiny blade, that deals pitiful damage but can be laced with poison.", '★', "Ranged", "Thrown", 1, "Piercing", "Piercing", "Dueling", "Straight", "Pin", "Blur", 3, 1, 1, 1, 0, 0, 6, 2, 6, new String[] {"Metal"}),
    new RangedWeapon("blowgun", "A simple projectile weapon that allows a quick exhalation to propel a needle at the target; usually the needle is poisoned.", '⁻', "Ranged", "Projectile", 1, "Piercing", "Piercing", "Precise", "Straight", "Weave", "Needles", 4, 1, 0, 0, 0, 5, 6, 2, 2, new String[] {"Wood"}),
    new RangedWeapon("bolas", "A pair of weights linked with a cord that can be thrown with a spinning motion to entangle a target's legs or arms.", 'ʊ', "Ranged", "Thrown", 1, "Blunt", "Blunt", "Flexible", "Arc", "Yank", "Pin", 3, 2, 0, 3, 0, 4, 3, 2, 3, new String[] {"Stone", "Hide"}),
    new RangedWeapon("net", "A heavy mesh with barbed weights on the edge that could be used for fishing or ensnaring fugitives.", '⍯', "Ranged", "Thrown", 2, "Blunt", "Blunt", "Flexible", "Arc", "Pound", "Pin", 4, 0, 0, 6, 3, 4, 0, 2, 1, new String[] {"Cloth"}),
    new RangedWeapon("grenade", "A precursor to other firearms, this is a container of shrapnel and blast powder that causes brutal damage when it works.", '¤', "Ranged", "Projectile", 2, "Piercing", "Slashing", "Forceful", "Arc", "Heave", "Powder", 0, 9, 5, 3, 0, 0, 0, 3, 0, new String[] {"Metal"}),
    new RangedWeapon("handcannon", "A simpler model of firearm that has advanced in parallel to the Musket, this weapon fires a shell in a very long arc.", 'ɼ', "Ranged", "Projectile", 2, "Piercing", "Slashing", "Precise", "Arc", "Ricochet", "Powder", 0, 9, 5, 0, 0, 0, 0, 6, 0, new String[] {"Metal"}),
    new RangedWeapon("throwing dagger", "A well-balanced, light-weight knife that is primarily meant to be thrown, but is also usable in close combat in a pinch.", '¹', "Ranged", "Thrown", 1, "Piercing", "Piercing", "Dueling", "Straight", "Blur", "Thrust", 4, 2, 2, 2, 0, 0, 3, 3, 4, new String[] {"Metal"}),
  };

  public static final Map<String, RangedWeapon> MAPPING = makeMap(
  "shortbow", ENTRIES[0], "longbow", ENTRIES[1], "light crosssbow", ENTRIES[2], "heavy crossbow",
  ENTRIES[3], "pistol", ENTRIES[4], "musket", ENTRIES[5], "sling",
  ENTRIES[6], "staff sling", ENTRIES[7], "chakram", ENTRIES[8], "boomerang",
  ENTRIES[9], "javelin", ENTRIES[10], "atlatl", ENTRIES[11], "shuriken",
  ENTRIES[12], "blowgun", ENTRIES[13], "bolas", ENTRIES[14], "net", ENTRIES[15],
  "grenade", ENTRIES[16], "handcannon", ENTRIES[17], "throwing dagger",
  ENTRIES[18]);

  public String name;

  public String description;

  public char glyph;

  public String kind;

  public String usage;

  public int hands;

  public String type1;

  public String type2;

  public String group1;

  public String path;

  public String maneuver1;

  public String maneuver2;

  public int precision;

  public int damage;

  public int crit;

  public int evasion;

  public int defense;

  public int luck;

  public int stealth;

  public int range;

  public int prepare;

  public String[] materials;

  public RangedWeapon(String name, String description, char glyph, String kind, String usage,
      int hands, String type1, String type2, String group1, String path, String maneuver1,
      String maneuver2, int precision, int damage, int crit, int evasion, int defense, int luck,
      int stealth, int range, int prepare, String[] materials) {
    this.name = name;
    this.description = description;
    this.glyph = glyph;
    this.kind = kind;
    this.usage = usage;
    this.hands = hands;
    this.type1 = type1;
    this.type2 = type2;
    this.group1 = group1;
    this.path = path;
    this.maneuver1 = maneuver1;
    this.maneuver2 = maneuver2;
    this.precision = precision;
    this.damage = damage;
    this.crit = crit;
    this.evasion = evasion;
    this.defense = defense;
    this.luck = luck;
    this.stealth = stealth;
    this.range = range;
    this.prepare = prepare;
    this.materials = materials;
  }

  public static RangedWeapon get(String item) {
    return MAPPING.get(item);
  }
}
