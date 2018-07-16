package squidpony.epigon.data.raw;

import static squidpony.squidmath.OrderedMap.makeMap;

import java.io.Serializable;
import java.util.Map;

public class RawCulture implements Serializable {
  public static final long serialVersionUID = 1L;

  public static final RawCulture[] ENTRIES = new RawCulture[] {
    new RawCulture("Bididhayi", "A now-peaceful theocracy that has benefited greatly from their warlike Chobesh neighbors' collapse.", "South Mid-Cap"),
    new RawCulture("Cheuritae", "A conglomerate of various island groups that sends privateers to raid other nations' ships.", "East Rim"),
    new RawCulture("Chobesh", "A formerly-powerful kingdom that lost it all after a century at war with its neighbors, and lies in ruin.", "West Mid-Cap"),
    new RawCulture("Elethian", "The unified nation of elves; these long-lived people have little interest in short-term power grabs.", "North Mid-Cap"),
    new RawCulture("Hidzaajji", "A desert nation that has built up strength through alliances and trade; they compete with Geuhwae.", "South Outer-Cap"),
    new RawCulture("Siarrolla", "A formidable naval armada that controls a puppet kingdom while frequently invading the Tip nations.", "North Inner-Cap"),
    new RawCulture("Hyrden", "An alliance of a church and a kingdom; they routinely crusade against Xuruk's nearby undead.", "West Outer-Cap"),
    new RawCulture("Ikkutiq", "A magically-formidable utopia that thrives deep in the dangerous Bowl using their arcane barriers.", "North Bowl"),
    new RawCulture("Jalgeaux", "A potent nation where the sizable aristocratic class manipulates the lower classes away from revolt.", "West Inner-Cap"),
    new RawCulture("Geuhwae", "A massive empire that formed from the merger of various rival states; it is an economic titan.", "East Mid-Cap"),
    new RawCulture("Khainghal", "A nomadic group that moves between dodging monsters near Hueztotli and raiding Geuhwae.", "East Inner-Cap"),
    new RawCulture("Krort", "A grouping of similar Orc tribes that, unable to easily raid Ikkutiq, have settled down to learn magic.", "North Rim"),
    new RawCulture("Laathrik", "A group of Kobold clans that tunnel under undead palaces to raid them of their riches.", "East Bowl"),
    new RawCulture("Tekikerrek", "A hive-state of insect-people whose Queen's word is law; they seem to be allied with Khanghal.", "South Inner-Cap"),
    new RawCulture("Mobyuld", "An attempt at a Goblin nation, modeled after Ugexiir, that wants to be a dominant force in the Bowl.", "South Bowl"),
    new RawCulture("Notsurashi", "A rebel state that has split off of Otekai, and has been embroiled in that civil war for centuries.", "East Outer-Cap"),
    new RawCulture("Otekai", "An honor-driven nation that refuses to admit surrender or sign a treaty in their war against Notsurashi.", "East Outer-Cap"),
    new RawCulture("Ugexiir", "A land that has driven off many Goblin invasions, and watches with curiosity as Mobyuld forms.", "South Rim"),
    new RawCulture("Zedmedov", "A grim nation that has the misfortune of being located closest to the undead hordes of Xuruk.", "West Rim"),
    new RawCulture("Vroyuul", "An organized civilization of amphibious tentacled humanoids that trades heavily with the Deep Lands.", "North Tip"),
    new RawCulture("Hueztotli", "A nation that has somehow survived despite being based in a jungle with terrifyingly-strong monsters.", "East Tip"),
    new RawCulture("Mbegonda", "A group of nations across a wide area that speak a common language, but don't share a government.", "South Tip"),
    new RawCulture("Whareowa", "A small island nation that has nonetheless intimidated its neighbors into respecting its territory.", "West Tip"),
    new RawCulture("Xuruk", "A fallen empire of necromancers overthrown by their undead minions; the walking dead own this land.", "West Bowl"),
    new RawCulture("Beast", "Monsters that aren't part of a civilization are generally called beasts, regardless of their intelligence.", "Everywhere"),
  };

  public static final Map<String, RawCulture> MAPPING = makeMap(
  "Bididhayi", ENTRIES[0], "Cheuritae", ENTRIES[1], "Chobesh", ENTRIES[2], "Elethian",
  ENTRIES[3], "Hidzaajji", ENTRIES[4], "Siarrolla", ENTRIES[5], "Hyrden",
  ENTRIES[6], "Ikkutiq", ENTRIES[7], "Jalgeaux", ENTRIES[8], "Geuhwae",
  ENTRIES[9], "Khainghal", ENTRIES[10], "Krort", ENTRIES[11], "Laathrik",
  ENTRIES[12], "Tekikerrek", ENTRIES[13], "Mobyuld", ENTRIES[14], "Notsurashi",
  ENTRIES[15], "Otekai", ENTRIES[16], "Ugexiir", ENTRIES[17], "Zedmedov",
  ENTRIES[18], "Vroyuul", ENTRIES[19], "Hueztotli", ENTRIES[20], "Mbegonda",
  ENTRIES[21], "Whareowa", ENTRIES[22], "Xuruk", ENTRIES[23], "Beast",
  ENTRIES[24]);

  public String name;

  public String description;

  public String location;

  public RawCulture() {
  }

  public RawCulture(String name, String description, String location) {
    this.name = name;
    this.description = description;
    this.location = location;
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
    result += (a ^= 0x8329C6EB9E6AD3E3L * hash64(location));
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
    RawCulture other = (RawCulture) o;
    if (name != null ? !name.equals(other.name) : other.name != null) return false;
    if (description != null ? !description.equals(other.description) : other.description != null) return false;
    if (location != null ? !location.equals(other.location) : other.location != null) return false;
    return true;
  }

  public static RawCulture get(String item) {
    return MAPPING.get(item);
  }
}
