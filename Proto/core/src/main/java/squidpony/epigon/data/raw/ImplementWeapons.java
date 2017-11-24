package squidpony.epigon.data.raw;

import java.util.Map;

import static squidpony.squidmath.OrderedMap.makeMap;

public class ImplementWeapons {
  public static final ImplementWeapons[] ENTRIES = new ImplementWeapons[] {
    new ImplementWeapons("Channeling Orb", "A small round crystal that a Mystic wielder can use to disrupt the energy flow of an enemy, or supplant a friend's.", "Implement", "Mystic", 1, "Shadow", "Fate", "Targeted", "Through", "Confound", "Energize"),
    new ImplementWeapons("Mystic Deck", "A deck of many fortune-telling cards, each decorated with cryptic imagery; an interpretation can be used as a Mystic curse.", "Implement", "Mystic", 2, "Fate", "Light", "Multi", "Straight", "Curse", "Favor"),
    new ImplementWeapons("Fossil Fang", "A gift of the land to those who protect it, this massive stony tooth carries Primal power over rock and soil.", "Implement", "Primal", 1, "Earth", "Piercing", "Targeted", "Through", "Trip", "Impale"),
    new ImplementWeapons("Green Wreath", "A garland of leaves that lives without soil or water thanks to a Primal blessing; it can grow thorny vines to attack.", "Implement", "Primal", 0, "Piercing", "Pure", "Multi", "Arc", "Disable", "Regenerate"),
    new ImplementWeapons("Holy Symbol", "When carried by a Blessed wielder, this sacred icon can issue shining judgment even when simply worn or carried.", "Implement", "Blessed", 0, "Divine", "Light", "Multi", "Straight", "Judge", "Regenerate"),
    new ImplementWeapons("Sacred Book", "A ritually-purified holy text that serves to channel Blessed faith into waves of divine wrath against transgressors.", "Implement", "Blessed", 2, "Pure", "Divine", "Wave", "Straight", "Awe", "Judge"),
    new ImplementWeapons("Vile Grimoire", "An Occult wielder's best (or only) friend, this black book, bound in some kind of skin, can obliterate nearby enemies.", "Implement", "Occult", 2, "Storm", "Death", "Wave", "Straight", "Linger", "Wither"),
    new ImplementWeapons("Black Lantern", "A rusted antique, this appears to be an ordinary lantern until lit with Occult magic, which makes it project a death ray.", "Implement", "Occult", 1, "Death", "Shadow", "Beam", "Straight", "Curse", "Wither"),
    new ImplementWeapons("Scholarly Tome", "A thick Arcane encyclopedia, laden with enchantments that can drive off enemies who come too close.", "Implement", "Arcane", 2, "Earth", "Fire", "Wave", "Straight", "Confound", "Linger"),
    new ImplementWeapons("Magus Scepter", "A strong effort by Arcane wielders to one-up firearms, this staff with various gems and sigils on it can fire energy blasts.", "Implement", "Arcane", 1, "Fire", "Storm", "Beam", "Straight", "Awe", "Energize"),
  };

  public static final Map<String, ImplementWeapons> MAPPING = makeMap(
  "Channeling Orb", ENTRIES[0], "Mystic Deck", ENTRIES[1], "Fossil Fang", ENTRIES[2],
  "Green Wreath", ENTRIES[3], "Holy Symbol", ENTRIES[4], "Sacred Book",
  ENTRIES[5], "Vile Grimoire", ENTRIES[6], "Black Lantern", ENTRIES[7],
  "Scholarly Tome", ENTRIES[8], "Magus Scepter", ENTRIES[9]);

  public String name;

  public String description;

  public String kind;

  public String usage;

  public int hands;

  public String type1;

  public String type2;

  public String shape;

  public String path;

  public String status1;

  public String status2;

  public ImplementWeapons(String name, String description, String kind, String usage, int hands,
      String type1, String type2, String shape, String path, String status1, String status2) {
    this.name = name;
    this.description = description;
    this.kind = kind;
    this.usage = usage;
    this.hands = hands;
    this.type1 = type1;
    this.type2 = type2;
    this.shape = shape;
    this.path = path;
    this.status1 = status1;
    this.status2 = status2;
  }

  public static ImplementWeapons get(String item) {
    return MAPPING.get(item);
  }
}
