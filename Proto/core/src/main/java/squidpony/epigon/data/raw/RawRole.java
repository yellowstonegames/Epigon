package squidpony.epigon.data.raw;

import java.io.Serializable;
import java.util.Map;

import static squidpony.squidmath.OrderedMap.makeMap;

public class RawRole implements Serializable {
  public static final long serialVersionUID = 1L;

  public static final RawRole[] ENTRIES = new RawRole[] {
    new RawRole("Assassin", "A killer-for-hire who lurks in the shadows, then strikes with concealed weapons drawn in an instant.", "Assassination", "You enter an area with a large bonus to Crit and Stealth, but this bonus is reduced as Zone Tension rises."),
    new RawRole("Brawler", "A disciplined fighter who trains in both hand-to-hand combat and with various weapons.", "Martial Arts", "Each time you attack an enemy, you have a small chance to repeat that attack immediately."),
    new RawRole("Brute", "A muscular thug who recognizes that his physique is his greatest asset; he uses heavy weapons.", "Brutality", "Each time you take damage, there is a moderate chance that you enter a Power Burst."),
    new RawRole("Dervish", "A deadly dancer in battle who sprints between skirmishes with an unpredictable weapon.", "Whirling Death", "Your attacks have a small bonus to Area, and this bonus increases slowly as Zone Tension rises."),
    new RawRole("Duelist", "A nimble noble who is courteous to a fault, unless dishonored… then a duel is called for.", "Elegance", "When an enemy inflicts an ailment on you, you have a moderate chance to enter a Power Burst."),
    new RawRole("Guardian", "A cautious defender who prefers to use polearms and shields to keep foes at bay.", "Foresight", "You gain a small Damage, Defense, and Evasion bonus for each enemy currently adjacent to you."),
    new RawRole("Hunter", "A tireless tracker who stalks prey through the wild before striking with a so-called “primitive” weapon.", "Naturalism", "On even-numbered rounds, you gain a moderate bonus to Precision and Evasion."),
    new RawRole("Jester", "A cackling comedian who gauges his foes at a distance before hurling an arsenal of thrown weapons.", "Hurling", "On odd-numbered rounds, you have a low chance to enter a Power Burst; this chance rises as Zone Tension rises."),
    new RawRole("Marksman", "A quiet, calm sniper who is skilled with all forms of projectile weaponry, and uses it to great effect.", "Marksmanship", "Each time an enemy moves to a cell you can attack, you have a low chance to make a reaction attack against them."),
    new RawRole("Swordsman", "A humble warrior who seeks a balanced approach to mastering the blade.", "Swordplay", "As Zone Tension rises, you gain a very small bonus to all stats."),
    new RawRole("Seer", "A wise wielder of Mystic powers who can sense impending danger; fortune smiles on him.", "Mystic", "When you first enter an area, you gain a bonus to Luck based on how many enemies are present then."),
    new RawRole("Shaman", "A protector of nature who can commune with spirits of the land to gain Primal powers.", "Primal", "You recover from ailments in half the time it normally takes."),
    new RawRole("Priest", "A devoted servant of a god who gains Blessed power through prayer and the support of a church.", "Blessed", "When an enemy inflicts an ailment on you, you gain a moderate bonus to Damage and Defense."),
    new RawRole("Diabolist", "A rogue magician who seeks out Occult power everywhere he can, signing deals with devils regularly.", "Occult", "You enter an area with a small penalty to Influence and Crit that becomes a large bonus as Zone Tension rises."),
    new RawRole("Wizard", "A learned sage who has studied magic enough to wield Arcane powers with gestures and words.", "Arcane", "Every time you attack, you gain a small bonus to Precision and Influence."),
//    new RawRole("", "", "", "A Power Burst is a 2-turn period where a character has doubled Precision, Damage, Crit, and Influence."),
//    new RawRole("", "", "", "Each area has a Zone Tension score that rises with each attack an NPC makes; various abilities rely on this."),
  };

  public static final Map<String, RawRole> MAPPING = makeMap(
  "Assassin", ENTRIES[0], "Brawler", ENTRIES[1], "Brute", ENTRIES[2], "Dervish", ENTRIES[3],
  "Duelist", ENTRIES[4], "Guardian", ENTRIES[5], "Hunter", ENTRIES[6],
  "Jester", ENTRIES[7], "Marksman", ENTRIES[8], "Swordsman", ENTRIES[9],
  "Seer", ENTRIES[10], "Shaman", ENTRIES[11], "Priest", ENTRIES[12], "Diabolist",
  ENTRIES[13], "Wizard", ENTRIES[14]);

  public String name;

  public String description;

  public String training;

  public String signature;

  public RawRole() {
  }

  public RawRole(String name, String description, String training, String signature) {
    this.name = name;
    this.description = description;
    this.training = training;
    this.signature = signature;
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
    result += (a ^= 0x8329C6EB9E6AD3E3L * hash64(signature));
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
    RawRole other = (RawRole) o;
    if (name != null ? !name.equals(other.name) : other.name != null) return false;
    if (description != null ? !description.equals(other.description) : other.description != null) return false;
    if (training != null ? !training.equals(other.training) : other.training != null) return false;
    if (signature != null ? !signature.equals(other.signature) : other.signature != null) return false;
    return true;
  }

  public static RawRole get(String item) {
    return MAPPING.get(item);
  }
}
