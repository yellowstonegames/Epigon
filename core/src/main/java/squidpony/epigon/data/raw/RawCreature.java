package squidpony.epigon.data.raw;

import squidpony.squidmath.NumberTools;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;

import static squidpony.squidmath.OrderedMap.makeMap;

public class RawCreature implements Serializable {
  public static final long serialVersionUID = 1L;

  public static final RawCreature[] ENTRIES = new RawCreature[] {
    new RawCreature("goblin nincompoop", "The laughingstock of other goblins, who themselves are the laughingstock of everyone else.", "ģ/", "CW Drab Chartreuse", RawWeapon.get("Mobyuld style"), new RawWeapon[] {RawWeapon.get("carving knife"), RawWeapon.get("sickle"), RawWeapon.get("sling")}, 2, 1, 3, 4, 3, 1, 2, 6, 0, 0, 6, 1, 1, 1, 5, 36, makeMap("hunter", 1, "swordsman", 1), new String[] {"Mobyuld"}, new String[] {"The @name pick$ @my nose.", "The @name drool$ a little.", "The @name point$ and laugh$ at nothing.",
    "The @name belch$$, “[?]Hehe, butts![?]”"}),
    new RawCreature("goblin delinquent", "The toughest goblin... of a group of the weakest goblins. Possibly a threat, maybe? To a toy poodle?", "ɠ*", "CW Flush Chartreuse", RawWeapon.get("Mobyuld style"), new RawWeapon[] {RawWeapon.get("section staff"), RawWeapon.get("meteor flail"), RawWeapon.get("staff sling")}, 3, 2, 3, 4, 3, 2, 2, 5, 0, 0, 5, 2, 2, 1, 5, 39, makeMap("jester", 1, "brawler", 2), new String[] {"Mobyuld"}, new String[] {"The @name scratch$$ @my rear.", "The @name tr$$$ to look tough.", "The @name cackle$ nervously.",
    "The @name stutter$, “[?]Uh oh, it's the hero![?]”"}),
    new RawCreature("hobgoblin grunt", "Bigger than the average human, but a little lacking in the creative thinking department. Seems to like taking orders.", "ġ", "CW Faded Lime", RawWeapon.get("Mobyuld style"), new RawWeapon[] {RawWeapon.get("khopesh"), RawWeapon.get("heavy shield"), RawWeapon.get("hammer")}, 5, 6, 2, 1, 2, 4, 0, 6, 0, 0, 4, 5, 5, 1, 6, 47, makeMap("brute", 2, "guardian", 1), new String[] {"Mobyuld"}, new String[] {"The @name drool$ copiously.", "The @name yawn$ somewhat menacingly.", "The @name adjust$ @my codpiece... for far too long.",
    "The @name bark$, “[?]I got this bro![?]”"}),
    new RawCreature("hobgoblin bomber", "A pyromaniac hobgoblin who loves to throw bombs, perhaps to the detriment of his sneakier allies.", "ǵ*", "CW Flush Lime", RawWeapon.get("Mobyuld style"), new RawWeapon[] {RawWeapon.get("grenade")}, 1, 9, 3, 5, 0, 5, 4, 0, 0, 0, 4, 7, 5, 3, 1, 47, makeMap("jester", 3), new String[] {"Mobyuld"}, new String[] {"The @name burn$ @my own hair a little.", "The @name imitate$ explosion noises!",
    "The @name cackle$ maniacally.", "The @name holler$, “[?]Oh yeah broski![?]”"}),
    new RawCreature("hobgoblin captain", "The boss around here, or so he says. Bigger than other hobgoblins, which is probably why they listen to him.", "ǧ*", "CW Drab Lime", RawWeapon.get("Mobyuld style"), new RawWeapon[] {RawWeapon.get("light crossbow"), RawWeapon.get("musket"), RawWeapon.get("katana")}, 6, 7, 4, 2, 0, 5, 0, 4, 0, 0, 3, 8, 6, 2, 6, 53, makeMap("marksman", 2, "duelist", 2), new String[] {"Mobyuld"}, new String[] {"The @name grumble$ loudly.", "The @name scowl$ at the ground.", "The @name strike$ a pose!",
    "The @name shout$, “[?]Get em brochachos![?]”"}),
    new RawCreature("Hell lion", "Like a lion... from Hell. Big pointy teeth, serious claws, and fire magic.", "L*", "Coral Red", RawWeapon.get("fire magic"), new RawWeapon[] {RawWeapon.get("raking claws"), RawWeapon.get("mauling bite")}, 3, 9, 3, 2, 2, 6, 0, 0, 0, 0, 5, 20, 7, 6, 7, 70, makeMap("sorcerer", 3, "hunter", 4), new String[] {"Beast"}, new String[] {"The @name growl$...", "The @name hiss$$ with scorching breath!", "The @name purr$ with sinister pleasure.",
    "The @name arch$$ @my back and roar$!"}),
    new RawCreature("squishy", "A mobile slime mold the size of a small dog.", "s", "CW Faded Purple", RawWeapon.get("wriggling cilia"), new RawWeapon[] {RawWeapon.get("lashing tentacles")}, 7, 1, 1, 1, 6, 7, 4, 3, 0, 0, 3, 2, 3, 2, 5, 45, makeMap("scavenger", 3), new String[] {"Vroyuul"}, new String[] {"[/]squish...[/]"}),
    new RawCreature("bull squishy", "A large squishy with a wild tangle of hardened bits that act as antlers for fighting.", "š", "CW Drab Purple", RawWeapon.get("majestic antlers"), new RawWeapon[] {RawWeapon.get("lashing tentacles"), RawWeapon.get("wriggling cilia")}, 5, 8, 4, 2, 3, 8, 2, 0, 0, 0, 2, 8, 8, 2, 5, 57, makeMap("brute", 4, "guardian", 2), new String[] {"Vroyuul"}, new String[] {"[/][*]squish...[*][/]"}),
    new RawCreature("farmer squishy", "A very large squishy that actively seeks out organics to break down into food for its herd.", "ŝ*", "CW Flush Magenta", RawWeapon.get("lashing tentacles"), new RawWeapon[] {RawWeapon.get("wriggling cilia"), RawWeapon.get("toxic sting")}, 6, 6, 2, 4, 4, 7, 2, 0, 0, 0, 1, 12, 9, 2, 5, 60, makeMap("scavenger", 5, "hunter", 2, "farmer", 2), new String[] {"Vroyuul"}, new String[] {"[/][*]SQUISH![*][/]"}),
    new RawCreature("squishy farmer", "A human who specializes in raising and caring for squishy.", "@/", "CW Light Purple", RawWeapon.get("Siarrolla style"), new RawWeapon[] {RawWeapon.get("net"), RawWeapon.get("carving knife")}, 1, 2, 1, 2, 2, 3, 5, 4, 0, 0, 4, 1, 3, 2, 6, 36, makeMap("farmer", 3, "guardian", 1), new String[] {"Siarrolla"}, new String[] {"The @name scatter$ chum for the squishies!", "The @name pine$ for @my homeland...",
    "The @name attempt$ to call @my squishy herd!",
    "The @name shriek$, “[?]Eek![?]”"}),
    new RawCreature("rave mage", "An elf who seems very happy, ecstatic actually, while blasting the surroundings with magic.", "e/", "CW Bright Rose", RawWeapon.get("shining magic"), new RawWeapon[] {RawWeapon.get("eldritch prism"), RawWeapon.get("dancing fan"), RawWeapon.get("wizardly wand")}, 0, 3, 6, 9, 3, 1, 9, 0, 0, 0, 6, 4, 8, 10, 1, 60, makeMap("duelist", 2, "wizard", 1, "sorcerer", 1), new String[] {"Elethian"}, new String[] {"The @name stare$ into infinite space...", "The @name dance$ spasmodically!", "The @name attempt$ a drum solo without drums!",
    "The @name looks amazed and whispers, “[?]Dude...[?]”"}),
  };

  public static final Map<String, RawCreature> MAPPING = makeMap(
  "goblin nincompoop", ENTRIES[0], "goblin delinquent", ENTRIES[1], "hobgoblin grunt",
  ENTRIES[2], "hobgoblin bomber", ENTRIES[3], "hobgoblin captain",
  ENTRIES[4], "Hell lion", ENTRIES[5], "squishy", ENTRIES[6],
  "bull squishy", ENTRIES[7], "farmer squishy", ENTRIES[8], "squishy farmer",
  ENTRIES[9], "rave mage", ENTRIES[10]);

  public String name;

  public String description;

  public String symbol;

  public String color;

  public RawWeapon baseWeapon;

  public RawWeapon[] weapons;

  public double precision;

  public double damage;

  public double crit;

  public double influence;

  public double evasion;

  public double defense;

  public double luck;

  public double stealth;

  public double range;

  public double area;

  public double quickness;

  public double vigor;

  public double endurance;

  public double spirit;

  public double sanity;

  public int total;

  public Map<String, Integer> training;

  public String[] culture;

  public String[] sayings;

  public RawCreature() {
  }

  public RawCreature(String name, String description, String symbol, String color,
      RawWeapon baseWeapon, RawWeapon[] weapons, double precision, double damage, double crit,
      double influence, double evasion, double defense, double luck, double stealth, double range,
      double area, double quickness, double vigor, double endurance, double spirit, double sanity,
      int total, Map<String, Integer> training, String[] culture, String[] sayings) {
    this.name = name;
    this.description = description;
    this.symbol = symbol;
    this.color = color;
    this.baseWeapon = baseWeapon;
    this.weapons = weapons;
    this.precision = precision;
    this.damage = damage;
    this.crit = crit;
    this.influence = influence;
    this.evasion = evasion;
    this.defense = defense;
    this.luck = luck;
    this.stealth = stealth;
    this.range = range;
    this.area = area;
    this.quickness = quickness;
    this.vigor = vigor;
    this.endurance = endurance;
    this.spirit = spirit;
    this.sanity = sanity;
    this.total = total;
    this.training = training;
    this.culture = culture;
    this.sayings = sayings;
  }

  private static long hash64(String data) {
    if (data == null) return 0;
    long result = 0x9E3779B97F4A7C94L, a = 0x632BE59BD9B4E019L;
    final int len = data.length();
    for (int i = 0; i < len; i++)
      result += (a ^= 0x8329C6EB9E6AD3E3L * data.charAt(i));
    return result * (a | 1L) ^ (result >>> 27 | result << 37);
  }

  private static long hashBasic(Object data) {
    return (data == null) ? 0 : data.hashCode() * 0x5851F42D4C957F2DL + 0x14057B7EF767814FL;
  }

  public long hash64() {
    long result = 0x9E3779B97F4A7C94L, a = 0x632BE59BD9B4E019L, innerR, innerA;
    int len;
    result += (a ^= 0x8329C6EB9E6AD3E3L * hash64(name));
    result += (a ^= 0x8329C6EB9E6AD3E3L * hash64(description));
    result += (a ^= 0x8329C6EB9E6AD3E3L * hash64(symbol));
    result += (a ^= 0x8329C6EB9E6AD3E3L * hash64(color));
    result += (a ^= 0x8329C6EB9E6AD3E3L * hashBasic(baseWeapon));
    innerR = 0x9E3779B97F4A7C94L;
    innerA = 0x632BE59BD9B4E019L;
    len = (weapons == null ? 0 : weapons.length);
    for (int i = 0; i < len; i++) innerR += (innerA ^= 0x8329C6EB9E6AD3E3L * hashBasic(weapons[i]));
    a += innerA;
    result ^= innerR * (innerA | 1L) ^ (innerR >>> 27 | innerR << 37);
    result += (a ^= 0x8329C6EB9E6AD3E3L * NumberTools.doubleToLongBits(precision));
    result += (a ^= 0x8329C6EB9E6AD3E3L * NumberTools.doubleToLongBits(damage));
    result += (a ^= 0x8329C6EB9E6AD3E3L * NumberTools.doubleToLongBits(crit));
    result += (a ^= 0x8329C6EB9E6AD3E3L * NumberTools.doubleToLongBits(influence));
    result += (a ^= 0x8329C6EB9E6AD3E3L * NumberTools.doubleToLongBits(evasion));
    result += (a ^= 0x8329C6EB9E6AD3E3L * NumberTools.doubleToLongBits(defense));
    result += (a ^= 0x8329C6EB9E6AD3E3L * NumberTools.doubleToLongBits(luck));
    result += (a ^= 0x8329C6EB9E6AD3E3L * NumberTools.doubleToLongBits(stealth));
    result += (a ^= 0x8329C6EB9E6AD3E3L * NumberTools.doubleToLongBits(range));
    result += (a ^= 0x8329C6EB9E6AD3E3L * NumberTools.doubleToLongBits(area));
    result += (a ^= 0x8329C6EB9E6AD3E3L * NumberTools.doubleToLongBits(quickness));
    result += (a ^= 0x8329C6EB9E6AD3E3L * NumberTools.doubleToLongBits(vigor));
    result += (a ^= 0x8329C6EB9E6AD3E3L * NumberTools.doubleToLongBits(endurance));
    result += (a ^= 0x8329C6EB9E6AD3E3L * NumberTools.doubleToLongBits(spirit));
    result += (a ^= 0x8329C6EB9E6AD3E3L * NumberTools.doubleToLongBits(sanity));
    result += (a ^= 0x8329C6EB9E6AD3E3L * total);
    result += (a ^= 0x8329C6EB9E6AD3E3L * hashBasic(training));
    innerR = 0x9E3779B97F4A7C94L;
    innerA = 0x632BE59BD9B4E019L;
    len = (culture == null ? 0 : culture.length);
    for (int i = 0; i < len; i++) innerR += (innerA ^= 0x8329C6EB9E6AD3E3L * hash64(culture[i]));
    a += innerA;
    result ^= innerR * (innerA | 1L) ^ (innerR >>> 27 | innerR << 37);
    innerR = 0x9E3779B97F4A7C94L;
    innerA = 0x632BE59BD9B4E019L;
    len = (sayings == null ? 0 : sayings.length);
    for (int i = 0; i < len; i++) innerR += (innerA ^= 0x8329C6EB9E6AD3E3L * hash64(sayings[i]));
    a += innerA;
    result ^= innerR * (innerA | 1L) ^ (innerR >>> 27 | innerR << 37);
    return result * (a | 1L) ^ (result >>> 27 | result << 37);
  }

  public int hashCode() {
    return (int)(hash64() & 0xFFFFFFFFL);
  }

  private static boolean stringArrayEquals(String[] left, String[] right) {
    if (left == right) return true;
    if (left == null || right == null) return false;
    final int len = left.length;
    if(len != right.length) return false;
    String l, r;
    for (int i = 0; i < len; i++) { if(((l = left[i]) != (r = right[i])) && (((l == null) != (r == null)) || !l.equals(r))) { return false; } }
    return true;
  }

  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    RawCreature other = (RawCreature) o;
    if (name != null ? !name.equals(other.name) : other.name != null) return false;
    if (description != null ? !description.equals(other.description) : other.description != null) return false;
    if (symbol != null ? !symbol.equals(other.symbol) : other.symbol != null) return false;
    if (color != null ? !color.equals(other.color) : other.color != null) return false;
    if (baseWeapon != null ? !baseWeapon.equals(other.baseWeapon) : other.baseWeapon != null) return false;
    if(!Arrays.deepEquals(weapons, other.weapons)) return false;
    if (precision != other.precision) return false;
    if (damage != other.damage) return false;
    if (crit != other.crit) return false;
    if (influence != other.influence) return false;
    if (evasion != other.evasion) return false;
    if (defense != other.defense) return false;
    if (luck != other.luck) return false;
    if (stealth != other.stealth) return false;
    if (range != other.range) return false;
    if (area != other.area) return false;
    if (quickness != other.quickness) return false;
    if (vigor != other.vigor) return false;
    if (endurance != other.endurance) return false;
    if (spirit != other.spirit) return false;
    if (sanity != other.sanity) return false;
    if (total != other.total) return false;
    if (training != null ? !training.equals(other.training) : other.training != null) return false;
    if(!stringArrayEquals(culture, other.culture)) return false;
    if(!stringArrayEquals(sayings, other.sayings)) return false;
    return true;
  }

  public static RawCreature get(String item) {
    return MAPPING.get(item);
  }
}
