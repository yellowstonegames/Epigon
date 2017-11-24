package squidpony.epigon.data.raw;

import java.util.Map;

import static squidpony.squidmath.OrderedMap.makeMap;

public class RangedWeapons {
  public static final RangedWeapons[] ENTRIES = new RangedWeapons[] {
    new RangedWeapons("Shortbow", "A smaller bow that isn't as unwieldy in close-quarters combat as a Longbow, and isn't as conspicuous when carried.", "Ranged", "Projectile", 2, "Piercing", "Piercing", "Forceful", "Arc", "Pin", "Arrows"),
    new RangedWeapons("Longbow", "A very large bow, usually taller than its wielder, that boasts tremendous range and hefty power.", "Ranged", "Projectile", 2, "Piercing", "Piercing", "Forceful", "Arc", "Heave", "Arrows"),
    new RangedWeapons("Light Crosssbow", "A small machine that launches a bolt with the pull of a trigger; slow to reload, but capable of piercing thick armor.", "Ranged", "Projectile", 1, "Piercing", "Piercing", "Precise", "Straight", "Weave", "Bolts"),
    new RangedWeapons("Heavy Crossbow", "A weighty machine that can launch a bolt in an instant, but takes a long time to reload; makes up for it with huge power.", "Ranged", "Projectile", 2, "Piercing", "Piercing", "Precise", "Straight", "Weave", "Bolts"),
    new RangedWeapons("Pistol", "This hand-held firearm is very imprecise, but that doesn't always hinder it because its bullet keeps bouncing after missing.", "Ranged", "Projectile", 1, "Piercing", "Piercing", "Precise", "Straight", "Ricochet", "Powder"),
    new RangedWeapons("Musket", "A heavy two-handed firearm with better accuracy than a Pistol and longer range, but more lengthy reload times.", "Ranged", "Projectile", 2, "Piercing", "Piercing", "Precise", "Straight", "Ricochet", "Powder"),
    new RangedWeapons("Sling", "An early stone-hurling weapon made from cord and hide, it has considerably better range than throwing a rock by hand.", "Ranged", "Projectile", 1, "Blunt", "Blunt", "Forceful", "Arc", "Yank", "Pellets"),
    new RangedWeapons("Staff Sling", "A curved staff with cords to hold a projectile at one end, this weapon improves a Sling's range even further.", "Ranged", "Projectile", 2, "Blunt", "Blunt", "Forceful", "Arc", "Pound", "Pellets"),
    new RangedWeapons("Chakram", "A bladed hoop with just as much use at medium range as in melee combat, it can be drawn quickly and thrown far.", "Ranged", "Thrown", 1, "Slashing", "Slashing", "Blade", "Straight", "Chop", "Blur"),
    new RangedWeapons("Boomerang", "A chunk of hard wood, carefully fashioned into a hunting weapon that spins all around the target when it misses.", "Ranged", "Thrown", 1, "Blunt", "Blunt", "Bludgeon", "Arc", "Pound", "Ricochet"),
    new RangedWeapons("Javelin", "A very light spear that's primarily meant for throwing, but can be used as a melee weapon in desperate situations.", "Ranged", "Thrown", 1, "Piercing", "Piercing", "Polearm", "Arc", "Pin", "Thrust"),
    new RangedWeapons("Atlatl", "A simple-looking device that enables a spear to be thrown much further, and with greater precision, than it can by hand.", "Ranged", "Projectile", 2, "Piercing", "Piercing", "Forceful", "Arc", "Heave", "Darts"),
    new RangedWeapons("Shuriken", "A throwing weapon, shaped like a star or tiny blade, that deals pitiful damage but can be laced with poison.", "Ranged", "Thrown", 1, "Piercing", "Piercing", "Dueling", "Straight", "Pin", "Blur"),
    new RangedWeapons("Blowgun", "A simple projectile weapon that allows a quick exhalation to propel a needle at the target; usually the needle is poisoned.", "Ranged", "Projectile", 1, "Piercing", "Piercing", "Precise", "Straight", "Weave", "Needles"),
    new RangedWeapons("Bolas", "A pair of weights linked with a cord that can be thrown with a spinning motion to entangle a target's legs or arms.", "Ranged", "Thrown", 1, "Blunt", "Blunt", "Flexible", "Arc", "Yank", "Pin"),
    new RangedWeapons("Net", "A heavy mesh with barbed weights on the edge that could be used for fishing or ensnaring fugitives.", "Ranged", "Thrown", 2, "Blunt", "Blunt", "Flexible", "Arc", "Pound", "Pin"),
    new RangedWeapons("Grenade", "A precursor to other firearms, this is a container of shrapnel and blast powder that causes brutal damage when it works.", "Ranged", "Projectile", 2, "Piercing", "Slashing", "Forceful", "Arc", "Heave", "Powder"),
    new RangedWeapons("Handcannon", "A simpler model of firearm that has advanced in parallel to the Musket, this weapon fires a shell in a very long arc.", "Ranged", "Projectile", 2, "Piercing", "Slashing", "Precise", "Arc", "Ricochet", "Powder"),
    new RangedWeapons("Throwing Dagger", "A well-balanced, light-weight knife that is primarily meant to be thrown, but is also usable in close combat in a pinch.", "Ranged", "Thrown", 1, "Piercing", "Piercing", "Dueling", "Straight", "Blur", "Thrust"),
  };

  public static final Map<String, RangedWeapons> MAPPING = makeMap(
  "Shortbow", ENTRIES[0], "Longbow", ENTRIES[1], "Light Crosssbow", ENTRIES[2], "Heavy Crossbow",
  ENTRIES[3], "Pistol", ENTRIES[4], "Musket", ENTRIES[5], "Sling",
  ENTRIES[6], "Staff Sling", ENTRIES[7], "Chakram", ENTRIES[8], "Boomerang",
  ENTRIES[9], "Javelin", ENTRIES[10], "Atlatl", ENTRIES[11], "Shuriken",
  ENTRIES[12], "Blowgun", ENTRIES[13], "Bolas", ENTRIES[14], "Net", ENTRIES[15],
  "Grenade", ENTRIES[16], "Handcannon", ENTRIES[17], "Throwing Dagger",
  ENTRIES[18]);

  public String name;

  public String description;

  public String kind;

  public String usage;

  public int hands;

  public String type1;

  public String type2;

  public String group1;

  public String path;

  public String maneuver1;

  public String maneuver2;

  public RangedWeapons(String name, String description, String kind, String usage, int hands,
      String type1, String type2, String group1, String path, String maneuver1, String maneuver2) {
    this.name = name;
    this.description = description;
    this.kind = kind;
    this.usage = usage;
    this.hands = hands;
    this.type1 = type1;
    this.type2 = type2;
    this.group1 = group1;
    this.path = path;
    this.maneuver1 = maneuver1;
    this.maneuver2 = maneuver2;
  }

  public static RangedWeapons get(String item) {
    return MAPPING.get(item);
  }
}
