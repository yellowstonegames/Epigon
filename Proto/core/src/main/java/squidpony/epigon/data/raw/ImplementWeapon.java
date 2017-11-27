package squidpony.epigon.data.raw;

import static squidpony.squidmath.OrderedMap.makeMap;

import java.util.Map;

public class ImplementWeapon {
  public static final ImplementWeapon[] ENTRIES = new ImplementWeapon[] {
    new ImplementWeapon("channeling orb", "A small round crystal that a Mystic wielder can use to disrupt the energy flow of an enemy, or supplant a friend's.", '●', "Implement", "Mystic", 1, "Shadow", "Fate", "Targeted", "Through", "Confound", "Energize", 6, 1, 6, 1, 0, 1, 1, 4, 0, new String[] {"Inclusion"}),
    new ImplementWeapon("mystic deck", "A deck of many fortune-telling cards, each decorated with cryptic imagery; an interpretation can be used as a Mystic curse.", '⍰', "Implement", "Mystic", 2, "Fate", "Light", "Multi", "Straight", "Curse", "Favor", 2, 1, 1, 0, 0, 6, 2, 5, 3, new String[] {"Paper"}),
    new ImplementWeapon("fossil fang", "A gift of the land to those who protect it, this massive stony tooth carries Primal power over rock and soil.", 'ɿ', "Implement", "Primal", 1, "Earth", "Piercing", "Targeted", "Through", "Trip", "Impale", 2, 5, 1, 0, 3, 4, 0, 3, 2, new String[] {"Stone"}),
    new ImplementWeapon("green wreath", "A garland of leaves that lives without soil or water thanks to a Primal blessing; it can grow thorny vines to attack.", '◌', "Implement", "Primal", 0, "Piercing", "Pure", "Multi", "Arc", "Disable", "Regenerate", 2, 1, 2, 0, 0, 4, 6, 4, 1, new String[] {"Wood"}),
    new ImplementWeapon("holy symbol", "When carried by a Blessed wielder, this sacred icon can issue shining judgment even when simply worn or carried.", 'ᵸ', "Implement", "Blessed", 0, "Divine", "Light", "Multi", "Straight", "Judge", "Regenerate", 4, 2, 4, 0, 0, 5, 0, 3, 2, new String[] {"Metal|Wood"}),
    new ImplementWeapon("sacred book", "A ritually-purified holy text that serves to channel Blessed faith into waves of divine wrath against transgressors.", '⍐', "Implement", "Blessed", 2, "Pure", "Divine", "Wave", "Straight", "Awe", "Judge", 3, 5, 3, 0, 0, 5, 0, 3, 1, new String[] {"Paper", "Metal"}),
    new ImplementWeapon("vile grimoire", "An Occult wielder's best (or only) friend, this black book, bound in some kind of skin, can obliterate nearby enemies.", '⍌', "Implement", "Occult", 2, "Storm", "Death", "Wave", "Straight", "Linger", "Wither", 2, 8, 4, 0, 0, 0, 0, 4, 2, new String[] {"Paper", "Hide"}),
    new ImplementWeapon("black lantern", "A rusted antique, this appears to be an ordinary lantern until lit with Occult magic, which makes it project a death ray.", '⍎', "Implement", "Occult", 1, "Death", "Shadow", "Beam", "Straight", "Curse", "Wither", 6, 7, 1, 0, 0, 0, 0, 6, 0, new String[] {"Inclusion", "Metal"}),
    new ImplementWeapon("scholarly tome", "A thick Arcane encyclopedia, laden with enchantments that can drive off enemies who come too close.", '⌺', "Implement", "Arcane", 2, "Earth", "Fire", "Wave", "Straight", "Confound", "Linger", 5, 3, 4, 0, 2, 0, 1, 5, 0, new String[] {"Paper", "Hide"}),
    new ImplementWeapon("magus scepter", "A strong effort by Arcane wielders to one-up firearms, this staff with various gems and sigils on it can fire energy blasts.", '∤', "Implement", "Arcane", 1, "Fire", "Storm", "Beam", "Straight", "Awe", "Energize", 4, 7, 1, 0, 1, 0, 0, 5, 2, new String[] {"Wood", "Inclusion"}),
  };

  public static final Map<String, ImplementWeapon> MAPPING = makeMap(
  "channeling orb", ENTRIES[0], "mystic deck", ENTRIES[1], "fossil fang", ENTRIES[2],
  "green wreath", ENTRIES[3], "holy symbol", ENTRIES[4], "sacred book",
  ENTRIES[5], "vile grimoire", ENTRIES[6], "black lantern", ENTRIES[7],
  "scholarly tome", ENTRIES[8], "magus scepter", ENTRIES[9]);

  public String name;

  public String description;

  public char glyph;

  public String kind;

  public String usage;

  public int hands;

  public String type1;

  public String type2;

  public String shape;

  public String path;

  public String status1;

  public String status2;

  public int precision;

  public int damage;

  public int influence;

  public int evasion;

  public int defense;

  public int luck;

  public int stealth;

  public int range;

  public int prepare;

  public String[] materials;

  public ImplementWeapon(String name, String description, char glyph, String kind, String usage,
      int hands, String type1, String type2, String shape, String path, String status1,
      String status2, int precision, int damage, int influence, int evasion, int defense, int luck,
      int stealth, int range, int prepare, String[] materials) {
    this.name = name;
    this.description = description;
    this.glyph = glyph;
    this.kind = kind;
    this.usage = usage;
    this.hands = hands;
    this.type1 = type1;
    this.type2 = type2;
    this.shape = shape;
    this.path = path;
    this.status1 = status1;
    this.status2 = status2;
    this.precision = precision;
    this.damage = damage;
    this.influence = influence;
    this.evasion = evasion;
    this.defense = defense;
    this.luck = luck;
    this.stealth = stealth;
    this.range = range;
    this.prepare = prepare;
    this.materials = materials;
  }

  public static ImplementWeapon get(String item) {
    return MAPPING.get(item);
  }
}
