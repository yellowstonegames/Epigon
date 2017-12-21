package squidpony.epigon.data.raw;

import java.io.Serializable;
import java.util.Map;

import static squidpony.squidmath.OrderedMap.makeMap;

public class RawRole implements Serializable {
  public static final long serialVersionUID = 1L;

  public static final RawRole[] ENTRIES = new RawRole[] {
    new RawRole("Assassin", "A killer-for-hire who lurks in the shadows, then strikes with concealed weapons drawn in an instant.", "Assassination"),
    new RawRole("Brawler", "A disciplined fighter who trains in both hand-to-hand combat and with various weapons.", "Martial Arts"),
    new RawRole("Brute", "A muscular thug who recognizes that his physique is his greatest asset; he uses heavy weapons.", "Brutality"),
    new RawRole("Dervish", "A deadly dancer in battle who sprints between skirmishes with an unpredictable weapon.", "Whirling Death"),
    new RawRole("Duelist", "A nimble noble who is courteous to a fault, unless dishonored… then a duel is called for.", "Elegance"),
    new RawRole("Guardian", "A cautious defender who prefers to use polearms and shields to keep foes at bay.", "Foresight"),
    new RawRole("Hunter", "A tireless tracker who stalks prey through the wild before striking with a so-called \"primitive\" weapon.", "Naturalism"),
    new RawRole("Jester", "A cackling comedian who gauges his foes at a distance before hurling an arsenal of thrown weapons.", "Hurling"),
    new RawRole("Marksman", "A quiet, calm sniper who is skilled with all forms of projectile weaponry, and uses it to great effect.", "Marksmanship"),
    new RawRole("Swordsman", "A humble warrior who seeks a balanced approach to mastering the blade.", "Swordplay"),
  };

  public static final Map<String, RawRole> MAPPING = makeMap(
  "Assassin", ENTRIES[0], "Brawler", ENTRIES[1], "Brute", ENTRIES[2], "Dervish", ENTRIES[3],
  "Duelist", ENTRIES[4], "Guardian", ENTRIES[5], "Hunter", ENTRIES[6],
  "Jester", ENTRIES[7], "Marksman", ENTRIES[8], "Swordsman", ENTRIES[9]);

  public String name;

  public String description;

  public String training;

  public RawRole() {
  }

  public RawRole(String name, String description, String training) {
    this.name = name;
    this.description = description;
    this.training = training;
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
    result += (a ^= 0x8329C6EB9E6AD3E3L * hash64(training));
    return result * (a | 1L) ^ (result >>> 27 | result << 37);
  }

  public int hashCode() {
    return (int)(hash64() & 0xFFFFFFFFL);
  }

  public static RawRole get(String item) {
    return MAPPING.get(item);
  }
}
