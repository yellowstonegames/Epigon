package squidpony.epigon.data.raw;

import static squidpony.squidmath.OrderedMap.makeMap;

import java.io.Serializable;
import java.util.Map;

public class RawWeapon implements Serializable {
  public static final long serialVersionUID = 1L;

  public static final RawWeapon[] ENTRIES = new RawWeapon[] {
    new RawWeapon("knuckles", "An improvement over being unarmed, these metal rings augment their holder's punches nicely.", 'ɱ', "Melee", "Repeat", 1, "Blunt", "Blunt", "Dueling", "Dueling", "Multi", "Straight", "Yank", "Sweep", "Disarm", "Confound", 4, 2, 4, 3, 2, 0, 3, 6, 0, 0, 6, new String[] {"Metal"}),
    new RawWeapon("longsword", "Longswords have good accuracy, damage, and parrying ability, helping their bearer excel in every battle.", '†', "Melee", "Repeat", 1, "Slashing", "Slashing", "Blade", "Blade", "Sweep", "Straight", "Parry", "Thrust", "Disarm", "Sever", 4, 7, 1, 2, 2, 5, 3, 0, 0, 1, 4, new String[] {"Metal"}),
    new RawWeapon("katana", "A curved blade attributed mystical power that can make effortless quick slices with impressive accuracy.", '†', "Melee", "Repeat", 1, "Slashing", "Slashing", "Blade", "Dueling", "Sweep", "Straight", "Sweep", "Blur", "Linger", "Sever", 5, 5, 1, 1, 5, 0, 3, 0, 0, 2, 6, new String[] {"Metal"}),
    new RawWeapon("broadsword", "A wide-bladed two-handed sword that mixes the savagery of an axe with the precision of a sword.", '†', "Melee", "Repeat", 2, "Slashing", "Slashing", "Blade", "Blade", "Sweep", "Straight", "Sweep", "Chop", "Sever", "Slay", 3, 9, 3, 2, 1, 3, 3, 0, 0, 2, 2, new String[] {"Metal"}),
    new RawWeapon("rapier", "An elegant dueling weapon that is sharp all along its blade; ideal for thrusting attacks and parries.", '†', "Melee", "Repeat", 1, "Piercing", "Piercing", "Blade", "Dueling", "Multi", "Straight", "Parry", "Lunge", "Impale", "Slay", 6, 3, 2, 1, 6, 0, 3, 1, 1, 0, 6, new String[] {"Metal"}),
    new RawWeapon("shortsword", "Not quite a knife or a sword, Shortswords are heavier than they look, but can still be swung quickly.", '▶', "Melee", "Repeat", 1, "Slashing", "Piercing", "Blade", "Dueling", "Multi", "Straight", "Parry", "Chop", "Silence", "Impale", 5, 5, 2, 1, 5, 1, 3, 3, 0, 0, 5, new String[] {"Metal"}),
    new RawWeapon("carving knife", "This sharpened chef's implement is just as good at slicing up a steak as it is slashing living flesh.", '▶', "Melee", "Repeat", 1, "Piercing", "Slashing", "Dueling", "Dueling", "Multi", "Straight", "Parry", "Hurl", "Impale", "Silence", 6, 2, 3, 3, 5, 0, 0, 5, 0, 0, 6, new String[] {"Metal|Stone"}),
    new RawWeapon("handaxe", "Some call them tomahawks, others hunga munga; this is a throwing axe by any name.", '⚑', "Melee", "Repeat", 1, "Slashing", "Slashing", "Axe", "Axe", "Multi", "Straight", "Chop", "Hurl", "Sever", "Disable", 3, 5, 7, 1, 3, 1, 5, 1, 0, 0, 4, new String[] {"Metal|Stone", "Wood"}),
    new RawWeapon("hatchet", "A medium-sized axe balanced for carving wood rather than throwing, it can carve flesh just as well.", '⚑', "Melee", "Repeat", 1, "Slashing", "Slashing", "Axe", "Axe", "Sweep", "Straight", "Chop", "Heft", "Sever", "Sunder", 2, 7, 8, 1, 2, 2, 5, 0, 0, 0, 3, new String[] {"Metal|Stone", "Wood"}),
    new RawWeapon("greataxe", "It's hard to beat this two-handed axe when it comes to overkill; the destruction these wreak is legendary.", '⚑', "Melee", "Repeat", 2, "Slashing", "Slashing", "Axe", "Axe", "Sweep", "Straight", "Chop", "Heft", "Sever", "Sunder", 1, 9, 6, 1, 0, 4, 5, 0, 0, 1, 2, new String[] {"Metal", "Wood"}),
    new RawWeapon("halberd", "It's an axe on a pole, the pole ends in a spear, and there's also a hook on the pole; clearly this is versatile.", '↟', "Melee", "Repeat", 2, "Slashing", "Piercing", "Polearm", "Axe", "Multi", "Straight", "Chop", "Brace", "Sever", "Trip", 3, 7, 5, 3, 0, 4, 3, 0, 1, 0, 3, new String[] {"Metal", "Wood"}),
    new RawWeapon("glaive", "Anything blade-like attached to the end of a pole, for sweeping slices at a comfortable distance.", '↑', "Melee", "Repeat", 2, "Slashing", "Piercing", "Polearm", "Blade", "Sweep", "Straight", "Sweep", "Brace", "Sever", "Linger", 5, 8, 2, 1, 2, 5, 0, 0, 1, 1, 3, new String[] {"Metal", "Wood"}),
    new RawWeapon("cape", "Among the least effective ways to kill someone, but great for confusing or disarming foes while keeping your hands free.", '⍝', "Melee", "Repeat", 0, "Blunt", "Blunt", "Flexible", "Flexible", "Sweep", "Straight", "Blur", "Yank", "Confound", "Disarm", 2, 1, 0, 6, 6, 0, 2, 3, 0, 2, 6, new String[] {"Cloth"}),
    new RawWeapon("hammer", "A heavy one-handed tool converted into a weapon of war that is especially strong against armor.", '⊤', "Melee", "Repeat", 1, "Blunt", "Blunt", "Bludgeon", "Bludgeon", "Multi", "Straight", "Pound", "Heft", "Sunder", "Confound", 4, 7, 4, 2, 0, 8, 2, 1, 0, 0, 2, new String[] {"Metal|Stone"}),
    new RawWeapon("maul", "A brutal two-handed sledgehammer that seems impractical against all but the slowest foes -- or monsters.", '⊤', "Melee", "Repeat", 2, "Blunt", "Blunt", "Bludgeon", "Bludgeon", "Multi", "Straight", "Pound", "Heft", "Sunder", "Trip", 2, 9, 5, 2, 0, 9, 2, 0, 0, 0, 1, new String[] {"Metal|Stone"}),
    new RawWeapon("club", "A plain-old heavy stick that can be swung with surprising speed, and can be passed off as a normal twig.", '╿', "Melee", "Repeat", 1, "Blunt", "Blunt", "Bludgeon", "Bludgeon", "Multi", "Straight", "Pound", "Hurl", "Trip", "Disarm", 3, 3, 1, 2, 2, 4, 5, 5, 0, 0, 5, new String[] {"Wood"}),
    new RawWeapon("mace", "Not just any stick, this one's made of solid metal, and can pulverize armor with a good wallop.", '╿', "Melee", "Repeat", 1, "Blunt", "Blunt", "Bludgeon", "Bludgeon", "Multi", "Straight", "Parry", "Pound", "Disarm", "Confound", 4, 5, 3, 1, 0, 9, 4, 0, 0, 0, 4, new String[] {"Metal|Stone"}),
    new RawWeapon("pole", "Whether an collapsible pole or just a piece of bamboo, a Pole is excellent at whacking multiple foes.", '|', "Melee", "Repeat", 2, "Blunt", "Blunt", "Polearm", "Bludgeon", "Sweep", "Straight", "Sweep", "Lunge", "Trip", "Confound", 4, 3, 2, 2, 1, 4, 4, 0, 1, 2, 4, new String[] {"Wood"}),
    new RawWeapon("taiaha", "An unusual mix of a spear's pointed tip with a shield-shaped haft, usually made entirely from hard wood.", '⌽', "Melee", "Repeat", 2, "Blunt", "Piercing", "Polearm", "Dueling", "Beam", "Straight", "Parry", "Pound", "Trip", "Confound", 5, 4, 2, 1, 4, 5, 6, 0, 0, 0, 3, new String[] {"Wood"}),
    new RawWeapon("shortspear", "Lighter than a Lance and heavier than a Javelin, a Shortspear can be effectively wielded at close to medium range.", '↑', "Melee", "Repeat", 1, "Piercing", "Piercing", "Polearm", "Polearm", "Beam", "Straight", "Brace", "Hurl", "Impale", "Disable", 5, 4, 1, 2, 6, 4, 3, 0, 1, 0, 3, new String[] {"Wood", "Metal"}),
    new RawWeapon("lance", "An uncommon weapon, the Lance can't attack a close-by enemy but can annihilate further-away foes.", '↑', "Melee", "Repeat", 2, "Piercing", "Piercing", "Polearm", "Polearm", "Beam", "Straight", "Brace", "Lunge", "Impale", "Slay", 2, 7, 1, 2, 3, 4, 2, 0, 2, 2, 1, new String[] {"Wood", "Metal"}),
    new RawWeapon("leaf spear", "A heavier mid-length spear with a broad leaf-shaped blade at the end; it can be used for slicing and stabbing alike.", '⍋', "Melee", "Repeat", 2, "Piercing", "Slashing", "Polearm", "Polearm", "Sweep", "Straight", "Thrust", "Sweep", "Impale", "Linger", 2, 8, 3, 2, 2, 4, 3, 0, 1, 1, 2, new String[] {"Metal", "Wood"}),
    new RawWeapon("nunchaku", "A martial-arts weapon consisting of two club-like bars connected with a short chain; excellent for defensive use.", '⋀', "Melee", "Repeat", 1, "Blunt", "Blunt", "Bludgeon", "Flexible", "Sweep", "Straight", "Parry", "Blur", "Confound", "Disarm", 5, 2, 0, 2, 6, 2, 2, 5, 0, 1, 4, new String[] {"Wood"}),
    new RawWeapon("section staff", "A series of three or more wooden rods connected with chains; originally meant for threshing rice but deadly when swung.", '⋀', "Melee", "Repeat", 2, "Blunt", "Blunt", "Bludgeon", "Flexible", "Sweep", "Straight", "Parry", "Sweep", "Confound", "Trip", 4, 4, 0, 2, 6, 4, 2, 0, 0, 2, 4, new String[] {"Wood"}),
    new RawWeapon("hand flail", "A nasty spiked ball whipped around on a heavy chain; it's simply brutal against armored foes.", '!', "Melee", "Repeat", 1, "Blunt", "Blunt", "Bludgeon", "Flexible", "Sweep", "Straight", "Sweep", "Yank", "Disarm", "Trip", 3, 7, 6, 4, 0, 6, 0, 0, 0, 1, 2, new String[] {"Metal|Stone", "Wood"}),
    new RawWeapon("meteor flail", "A small, heavy weight on the end of a long vine or rope; making one is easy, but fighting with one is very hard.", '!', "Melee", "Repeat", 2, "Blunt", "Blunt", "Bludgeon", "Flexible", "Multi", "Arc", "Heft", "Yank", "Disarm", "Trip", 3, 6, 3, 5, 2, 0, 3, 0, 3, 0, 2, new String[] {"Metal|Stone", "Hide"}),
    new RawWeapon("whip", "A leather bullwhip that's flexible enough to knock a weapon from the hands of nearby opponents.", '∫', "Melee", "Repeat", 1, "Blunt", "Slashing", "Flexible", "Flexible", "Multi", "Arc", "Lunge", "Yank", "Disarm", "Trip", 4, 1, 4, 6, 3, 0, 4, 0, 2, 0, 4, new String[] {"Hide"}),
    new RawWeapon("chain", "A metal chain that can be spun to deliver painful strikes, and can wrap around an enemy's weapon to relieve him of it.", '∫', "Melee", "Repeat", 1, "Blunt", "Blunt", "Flexible", "Flexible", "Sweep", "Straight", "Parry", "Yank", "Trip", "Disable", 4, 4, 3, 2, 5, 1, 4, 0, 0, 2, 3, new String[] {"Metal"}),
    new RawWeapon("sai", "A martial-arts weapon with three curving spikes that provide ample ability to disarm foes.", '⍦', "Melee", "Repeat", 1, "Piercing", "Piercing", "Dueling", "Dueling", "Multi", "Straight", "Thrust", "Yank", "Disarm", "Impale", 4, 2, 2, 3, 6, 2, 1, 4, 0, 0, 6, new String[] {"Metal"}),
    new RawWeapon("trident", "A fishing spear adapted for gladiatorial and maritime combat, the trident has three barbed prongs on a mid-size pole.", '⍦', "Melee", "Repeat", 2, "Piercing", "Piercing", "Polearm", "Dueling", "Multi", "Straight", "Brace", "Hurl", "Impale", "Linger", 3, 6, 2, 1, 4, 4, 4, 0, 1, 0, 4, new String[] {"Metal", "Wood"}),
    new RawWeapon("sickle", "A lightweight, deeply-curved blade that can be used for severing plant roots, or a foe's limbs, as well as disarming.", 'ʕ', "Melee", "Repeat", 1, "Slashing", "Slashing", "Blade", "Axe", "Sweep", "Straight", "Chop", "Yank", "Sever", "Trip", 4, 4, 5, 4, 0, 1, 5, 2, 0, 1, 3, new String[] {"Metal"}),
    new RawWeapon("khopesh", "A sword-like weapon with a heavy blade that goes straight and then curves; challenging to wield and to confront.", 'ʕ', "Melee", "Repeat", 1, "Slashing", "Slashing", "Blade", "Axe", "Sweep", "Straight", "Heft", "Yank", "Sever", "Trip", 3, 8, 6, 3, 1, 3, 2, 0, 0, 1, 2, new String[] {"Metal"}),
    new RawWeapon("scythe", "A hook-bladed farming tool meant for slicing wide swaths of grain, adapted to slicing wide swaths in necks.", 'ʕ', "Melee", "Repeat", 2, "Slashing", "Slashing", "Blade", "Axe", "Sweep", "Straight", "Chop", "Sweep", "Slay", "Sever", 2, 8, 9, 5, 0, 0, 0, 0, 0, 2, 2, new String[] {"Metal", "Wood"}),
    new RawWeapon("light shield", "A shield that's meant for actively parrying and blocking enemy attacks, mixing in quick jabs with the edge of the shield.", '⍟', "Melee", "Repeat", 1, "Blunt", "Blunt", "Bludgeon", "Dueling", "Multi", "Straight", "Parry", "Blur", "Confound", "Disarm", 3, 4, 0, 2, 9, 7, 3, 0, 0, 0, 2, new String[] {"Hide|Metal|Wood"}),
    new RawWeapon("heavy shield", "A large shield that's effective at defending even while barely moving, but it's hard to precisely wield one.", '⍟', "Melee", "Repeat", 1, "Blunt", "Blunt", "Bludgeon", "Bludgeon", "Multi", "Straight", "Parry", "Heft", "Confound", "Sunder", 2, 7, 0, 3, 7, 9, 1, 0, 0, 0, 1, new String[] {"Metal|Wood"}),
    new RawWeapon("macahuitl", "A heavy wooden shaft that's been augmented with razor-sharp stone blades along its edge; incredibly dangerous.", '≢', "Melee", "Repeat", 2, "Slashing", "Blunt", "Blade", "Bludgeon", "Sweep", "Straight", "Heft", "Sweep", "Sever", "Slay", 1, 9, 9, 3, 0, 0, 5, 0, 0, 1, 1, new String[] {"Wood", "Stone"}),
    new RawWeapon("shortbow", "A smaller bow that isn't as unwieldy in close-quarters combat as a Longbow, and isn't as conspicuous when carried.", '(', "Ranged", "Projectile", 2, "Piercing", "Piercing", "Forceful", "Forceful", "Multi", "Arc", "Pin", "Arrows", "Impale", "Disable", 4, 4, 2, 4, 1, 0, 1, 1, 4, 0, 5, new String[] {"Wood", "Hide"}),
    new RawWeapon("longbow", "A very large bow, usually taller than its wielder, that boasts tremendous range and hefty power.", '(', "Ranged", "Projectile", 2, "Piercing", "Piercing", "Forceful", "Forceful", "Multi", "Arc", "Heave", "Arrows", "Impale", "Slay", 3, 5, 1, 4, 0, 0, 1, 0, 6, 0, 4, new String[] {"Wood", "Hide"}),
    new RawWeapon("light crosssbow", "A small machine that launches a bolt with the pull of a trigger; slow to reload, but capable of piercing thick armor.", '⊢', "Ranged", "Projectile", 1, "Piercing", "Piercing", "Precise", "Precise", "Beam", "Straight", "Weave", "Bolts", "Impale", "Disable", 6, 5, 2, 1, 0, 1, 0, 1, 5, 1, 2, new String[] {"Wood"}),
    new RawWeapon("heavy crossbow", "A weighty machine that can launch a bolt in an instant, but takes a long time to reload; makes up for it with huge power.", '⊢', "Ranged", "Projectile", 2, "Piercing", "Piercing", "Precise", "Precise", "Beam", "Straight", "Weave", "Bolts", "Impale", "Slay", 5, 7, 1, 1, 0, 1, 0, 0, 6, 1, 1, new String[] {"Wood"}),
    new RawWeapon("pistol", "This hand-held firearm is very imprecise, but that doesn't always hinder it because its bullet keeps bouncing after missing.", 'ᵣ', "Ranged", "Projectile", 1, "Piercing", "Piercing", "Precise", "Precise", "Multi", "Straight", "Ricochet", "Powder", "Slay", "Confound", 3, 8, 7, 1, 0, 0, 0, 3, 4, 0, 0, new String[] {"Metal"}),
    new RawWeapon("musket", "A heavy two-handed firearm with better accuracy than a Pistol and longer range, but more lengthy reload times.", 'ɼ', "Ranged", "Projectile", 2, "Piercing", "Piercing", "Precise", "Precise", "Multi", "Straight", "Ricochet", "Powder", "Slay", "Confound", 2, 9, 8, 1, 0, 0, 0, 0, 5, 0, 0, new String[] {"Metal"}),
    new RawWeapon("sling", "An early stone-hurling weapon made from cord and hide, it has considerably better range than throwing a rock by hand.", '⍩', "Ranged", "Projectile", 1, "Blunt", "Blunt", "Forceful", "Forceful", "Multi", "Arc", "Yank", "Pellets", "Confound", "Slay", 3, 3, 2, 5, 2, 0, 2, 4, 3, 0, 3, new String[] {"Hide"}),
    new RawWeapon("staff sling", "A curved staff with cords to hold a projectile at one end, this weapon improves a Sling's range even further.", 'Ḯ', "Ranged", "Projectile", 2, "Blunt", "Blunt", "Forceful", "Forceful", "Multi", "Arc", "Pound", "Pellets", "Confound", "Slay", 2, 4, 2, 5, 0, 2, 1, 1, 5, 0, 3, new String[] {"Hide"}),
    new RawWeapon("chakram", "A bladed hoop with just as much use at medium range as in melee combat, it can be drawn quickly and thrown far.", '°', "Ranged", "Thrown", 1, "Slashing", "Slashing", "Blade", "Forceful", "Multi", "Straight", "Chop", "Blur", "Sever", "Disable", 5, 5, 5, 3, 0, 4, 0, 0, 2, 0, 4, new String[] {"Metal"}),
    new RawWeapon("boomerang", "A chunk of hard wood, carefully fashioned into a hunting weapon that spins all around the target when it misses.", '❮', "Ranged", "Thrown", 1, "Blunt", "Blunt", "Bludgeon", "Forceful", "Sweep", "Arc", "Pound", "Ricochet", "Confound", "Disarm", 4, 3, 1, 4, 1, 3, 4, 1, 3, 0, 3, new String[] {"Wood"}),
    new RawWeapon("javelin", "A very light spear that's primarily meant for throwing, but can be used as a melee weapon in desperate situations.", '↗', "Ranged", "Thrown", 1, "Piercing", "Piercing", "Polearm", "Forceful", "Multi", "Arc", "Pin", "Thrust", "Impale", "Disable", 5, 5, 1, 3, 1, 0, 3, 1, 4, 0, 3, new String[] {"Wood"}),
    new RawWeapon("atlatl", "A simple-looking device that enables a spear to be thrown much further, and with greater precision, than it can by hand.", '⋁', "Ranged", "Projectile", 2, "Piercing", "Piercing", "Forceful", "Polearm", "Multi", "Arc", "Heave", "Darts", "Impale", "Slay", 4, 9, 2, 2, 0, 0, 1, 0, 5, 0, 2, new String[] {"Wood"}),
    new RawWeapon("shuriken", "A throwing weapon, shaped like a star or tiny blade, that deals pitiful damage but can be laced with poison.", '★', "Ranged", "Thrown", 1, "Piercing", "Piercing", "Dueling", "Precise", "Multi", "Straight", "Pin", "Blur", "Disable", "Linger", 3, 1, 1, 6, 1, 0, 0, 6, 2, 1, 6, new String[] {"Metal"}),
    new RawWeapon("blowgun", "A simple projectile weapon that allows a quick exhalation to propel a needle at the target; usually the needle is poisoned.", '⁻', "Ranged", "Projectile", 1, "Piercing", "Piercing", "Precise", "Precise", "Multi", "Straight", "Weave", "Needles", "Linger", "Slay", 4, 1, 2, 6, 0, 0, 5, 6, 2, 0, 2, new String[] {"Wood"}),
    new RawWeapon("bolas", "A pair of weights linked with a cord that can be thrown with a spinning motion to entangle a target's legs or arms.", 'ʊ', "Ranged", "Thrown", 1, "Blunt", "Blunt", "Flexible", "Forceful", "Multi", "Arc", "Yank", "Pin", "Trip", "Disarm", 3, 2, 0, 6, 3, 0, 4, 3, 3, 0, 3, new String[] {"Stone", "Hide"}),
    new RawWeapon("net", "A heavy mesh with barbed weights on the edge that could be used for fishing or ensnaring fugitives.", '⍯', "Ranged", "Thrown", 2, "Blunt", "Blunt", "Flexible", "Forceful", "Sweep", "Arc", "Pound", "Pin", "Trip", "Disable", 4, 0, 0, 6, 6, 3, 4, 0, 2, 1, 1, new String[] {"Cloth"}),
    new RawWeapon("grenade", "A precursor to other firearms, this is a container of shrapnel and blast powder that causes brutal damage when it works.", '¤', "Ranged", "Projectile", 2, "Piercing", "Slashing", "Forceful", "Precise", "Burst", "Arc", "Heave", "Powder", "Slay", "Linger", 0, 9, 0, 6, 3, 0, 0, 4, 2, 2, 0, new String[] {"Metal"}),
    new RawWeapon("handcannon", "A simpler model of firearm that has advanced in parallel to the Musket, this weapon fires a shell in a very long arc.", 'ɼ', "Ranged", "Projectile", 2, "Piercing", "Slashing", "Forceful", "Precise", "Burst", "Arc", "Ricochet", "Powder", "Slay", "Linger", 0, 9, 0, 5, 0, 0, 0, 0, 6, 2, 0, new String[] {"Metal"}),
    new RawWeapon("throwing dagger", "A well-balanced, light-weight knife that is primarily meant to be thrown, but is also usable in close combat in a pinch.", '¹', "Ranged", "Thrown", 1, "Piercing", "Piercing", "Dueling", "Precise", "Multi", "Straight", "Blur", "Thrust", "Impale", "Disable", 4, 2, 2, 6, 3, 0, 0, 3, 3, 0, 4, new String[] {"Metal"}),
    new RawWeapon("channeling orb", "A small round crystal that a Mystic wielder can use to disrupt the energy flow of an enemy, or supplant a friend's.", '●', "Implement", "Repeat", 1, "Light", "Fate", "Mystic", "Mystic", "Through", "Straight", "Pound", "Shine", "Confound", "Energize", 5, 1, 0, 6, 1, 0, 2, 1, 5, 2, 0, new String[] {"Inclusion"}),
    new RawWeapon("mystic deck", "A deck of many fortune-telling cards, each decorated with cryptic imagery; an interpretation can be used as a Mystic curse.", '⍰', "Implement", "Repeat", 2, "Fate", "Shadow", "Mystic", "Mystic", "Multi", "Straight", "Weave", "Hex", "Curse", "Favor", 2, 1, 5, 1, 0, 0, 6, 2, 4, 1, 3, new String[] {"Paper"}),
    new RawWeapon("fossil fang", "A gift of the land to those who protect it, this massive stony tooth carries Primal power over rock and soil.", 'ɿ', "Implement", "Repeat", 1, "Earth", "Piercing", "Primal", "Dueling", "Through", "Straight", "Thrust", "Seal", "Trip", "Impale", 2, 5, 3, 1, 0, 3, 4, 0, 3, 2, 2, new String[] {"Stone"}),
    new RawWeapon("green wreath", "A garland of leaves that lives without soil or water thanks to a Primal blessing; it can grow thorny vines to attack.", '◌', "Implement", "Repeat", 0, "Piercing", "Pure", "Primal", "Flexible", "Multi", "Arc", "Exorcise", "Pin", "Disable", "Regenerate", 2, 1, 1, 2, 0, 0, 4, 6, 4, 1, 4, new String[] {"Wood"}),
    new RawWeapon("holy symbol", "When carried by a Blessed wielder, this sacred icon can issue shining judgment even when simply worn or carried.", 'ᵸ', "Implement", "Repeat", 0, "Divine", "Light", "Blessed", "Blessed", "Multi", "Straight", "Shine", "Exorcise", "Regenerate", "Awe", 4, 4, 0, 4, 0, 0, 5, 0, 3, 2, 3, new String[] {"Metal|Wood"}),
    new RawWeapon("sacred book", "A ritually-purified holy text that serves to channel Blessed faith into waves of divine wrath against transgressors.", '⍐', "Implement", "Repeat", 2, "Pure", "Divine", "Blessed", "Blessed", "Wave", "Straight", "Seal", "Exorcise", "Favor", "Silence", 3, 5, 0, 3, 0, 0, 5, 0, 3, 3, 1, new String[] {"Paper", "Metal"}),
    new RawWeapon("vile grimoire", "An Occult wielder's best (or only) friend, this black book, bound in some kind of skin, can obliterate nearby enemies.", '⍌', "Implement", "Repeat", 2, "Death", "Storm", "Occult", "Occult", "Wave", "Straight", "Seal", "Hex", "Linger", "Disable", 2, 8, 0, 5, 0, 0, 0, 0, 4, 3, 1, new String[] {"Paper", "Hide"}),
    new RawWeapon("black lantern", "A rusted antique, this appears to be an ordinary lantern until lit with Occult magic, which makes it project a death ray.", '⍎', "Implement", "Repeat", 1, "Shadow", "Death", "Occult", "Occult", "Beam", "Straight", "Reveal", "Hex", "Curse", "Slay", 4, 6, 1, 1, 0, 0, 0, 0, 6, 3, 0, new String[] {"Inclusion", "Metal"}),
    new RawWeapon("scholarly tome", "A thick Arcane encyclopedia, laden with enchantments that can drive off enemies who come too close.", '⌺', "Implement", "Repeat", 2, "Fire", "Earth", "Arcane", "Arcane", "Wave", "Straight", "Seal", "Reveal", "Silence", "Confound", 5, 3, 0, 4, 0, 2, 0, 0, 5, 3, 0, new String[] {"Paper", "Hide"}),
    new RawWeapon("magus scepter", "A strong effort by Arcane wielders to one-up firearms, this staff with various gems and sigils on it can fire energy blasts.", '∤', "Implement", "Repeat", 1, "Storm", "Fire", "Arcane", "Bludgeon", "Beam", "Straight", "Pound", "Reveal", "Slay", "Energize", 4, 7, 0, 2, 0, 1, 0, 0, 5, 2, 2, new String[] {"Wood", "Inclusion"}),
  };

  public static final Map<String, RawWeapon> MAPPING = makeMap(
  "knuckles", ENTRIES[0], "longsword", ENTRIES[1], "katana", ENTRIES[2], "broadsword",
  ENTRIES[3], "rapier", ENTRIES[4], "shortsword", ENTRIES[5], "carving knife",
  ENTRIES[6], "handaxe", ENTRIES[7], "hatchet", ENTRIES[8], "greataxe",
  ENTRIES[9], "halberd", ENTRIES[10], "glaive", ENTRIES[11], "cape", ENTRIES[12],
  "hammer", ENTRIES[13], "maul", ENTRIES[14], "club", ENTRIES[15], "mace",
  ENTRIES[16], "pole", ENTRIES[17], "taiaha", ENTRIES[18], "shortspear", ENTRIES[19],
  "lance", ENTRIES[20], "leaf spear", ENTRIES[21], "nunchaku", ENTRIES[22],
  "section staff", ENTRIES[23], "hand flail", ENTRIES[24], "meteor flail",
  ENTRIES[25], "whip", ENTRIES[26], "chain", ENTRIES[27], "sai", ENTRIES[28],
  "trident", ENTRIES[29], "sickle", ENTRIES[30], "khopesh", ENTRIES[31],
  "scythe", ENTRIES[32], "light shield", ENTRIES[33], "heavy shield",
  ENTRIES[34], "macahuitl", ENTRIES[35], "shortbow", ENTRIES[36],
  "longbow", ENTRIES[37], "light crosssbow", ENTRIES[38], "heavy crossbow", ENTRIES[39],
  "pistol", ENTRIES[40], "musket", ENTRIES[41], "sling", ENTRIES[42],
  "staff sling", ENTRIES[43], "chakram", ENTRIES[44], "boomerang", ENTRIES[45],
  "javelin", ENTRIES[46], "atlatl", ENTRIES[47], "shuriken", ENTRIES[48],
  "blowgun", ENTRIES[49], "bolas", ENTRIES[50], "net", ENTRIES[51], "grenade",
  ENTRIES[52], "handcannon", ENTRIES[53], "throwing dagger", ENTRIES[54],
  "channeling orb", ENTRIES[55], "mystic deck", ENTRIES[56], "fossil fang",
  ENTRIES[57], "green wreath", ENTRIES[58], "holy symbol", ENTRIES[59],
  "sacred book", ENTRIES[60], "vile grimoire", ENTRIES[61], "black lantern",
  ENTRIES[62], "scholarly tome", ENTRIES[63], "magus scepter", ENTRIES[64]);

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

  public String shape;

  public String path;

  public String maneuver1;

  public String maneuver2;

  public String status1;

  public String status2;

  public int precision;

  public int damage;

  public int crit;

  public int influence;

  public int evasion;

  public int defense;

  public int luck;

  public int stealth;

  public int range;

  public int area;

  public int prepare;

  public String[] materials;

  public RawWeapon() {
  }

  public RawWeapon(String name, String description, char glyph, String kind, String usage,
      int hands, String type1, String type2, String group1, String group2, String shape,
      String path, String maneuver1, String maneuver2, String status1, String status2,
      int precision, int damage, int crit, int influence, int evasion, int defense, int luck,
      int stealth, int range, int area, int prepare, String[] materials) {
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
    this.shape = shape;
    this.path = path;
    this.maneuver1 = maneuver1;
    this.maneuver2 = maneuver2;
    this.status1 = status1;
    this.status2 = status2;
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
    this.prepare = prepare;
    this.materials = materials;
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
    result += (a ^= 0x8329C6EB9E6AD3E3L * glyph);
    result += (a ^= 0x8329C6EB9E6AD3E3L * hash64(kind));
    result += (a ^= 0x8329C6EB9E6AD3E3L * hash64(usage));
    result += (a ^= 0x8329C6EB9E6AD3E3L * hands);
    result += (a ^= 0x8329C6EB9E6AD3E3L * hash64(type1));
    result += (a ^= 0x8329C6EB9E6AD3E3L * hash64(type2));
    result += (a ^= 0x8329C6EB9E6AD3E3L * hash64(group1));
    result += (a ^= 0x8329C6EB9E6AD3E3L * hash64(group2));
    result += (a ^= 0x8329C6EB9E6AD3E3L * hash64(shape));
    result += (a ^= 0x8329C6EB9E6AD3E3L * hash64(path));
    result += (a ^= 0x8329C6EB9E6AD3E3L * hash64(maneuver1));
    result += (a ^= 0x8329C6EB9E6AD3E3L * hash64(maneuver2));
    result += (a ^= 0x8329C6EB9E6AD3E3L * hash64(status1));
    result += (a ^= 0x8329C6EB9E6AD3E3L * hash64(status2));
    result += (a ^= 0x8329C6EB9E6AD3E3L * precision);
    result += (a ^= 0x8329C6EB9E6AD3E3L * damage);
    result += (a ^= 0x8329C6EB9E6AD3E3L * crit);
    result += (a ^= 0x8329C6EB9E6AD3E3L * influence);
    result += (a ^= 0x8329C6EB9E6AD3E3L * evasion);
    result += (a ^= 0x8329C6EB9E6AD3E3L * defense);
    result += (a ^= 0x8329C6EB9E6AD3E3L * luck);
    result += (a ^= 0x8329C6EB9E6AD3E3L * stealth);
    result += (a ^= 0x8329C6EB9E6AD3E3L * range);
    result += (a ^= 0x8329C6EB9E6AD3E3L * area);
    result += (a ^= 0x8329C6EB9E6AD3E3L * prepare);
    innerR = 0x9E3779B97F4A7C94L;
    innerA = 0x632BE59BD9B4E019L;
    len = (materials == null ? 0 : materials.length);
    for (int i = 0; i < len; i++) innerR += (innerA ^= 0x8329C6EB9E6AD3E3L * hash64(materials[i]));
    a += innerA;
    result ^= innerR * (innerA | 1L) ^ (innerR >>> 27 | innerR << 37);
    return result * (a | 1L) ^ (result >>> 27 | result << 37);
  }

  public int hashCode() {
    return (int)(hash64() & 0xFFFFFFFFL);
  }

  public static RawWeapon get(String item) {
    return MAPPING.get(item);
  }
}
