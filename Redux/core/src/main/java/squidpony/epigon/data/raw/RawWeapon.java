package squidpony.epigon.data.raw;

import squidpony.squidmath.NumberTools;

import java.io.Serializable;
import java.util.Map;

import static squidpony.squidmath.OrderedMap.makeMap;

public class RawWeapon implements Serializable {
  public static final long serialVersionUID = 1L;

  public static final RawWeapon[] ENTRIES = new RawWeapon[] {
    new RawWeapon("knuckles", "An improvement over being unarmed, these metal rings augment their holder's punches nicely.", 'ɱ', "Melee", "Repeat", 1, "BLUNT", "BLUNT", "Dueling", "Dueling", "Multi", "Straight", "Yank", "Sweep", "Disarm", "Confound", 4, 2, 4, 3, 2, 0, 3, 6, 0, 0, 6, new String[] {"Metal"}, new String[] {"Martial Arts"}, new String[] {"Ugexiir"}),
    new RawWeapon("longsword", "Longswords have good accuracy, damage, and parrying ability, helping their bearer excel in every battle.", '†', "Melee", "Repeat", 1, "SLASHING", "SLASHING", "Blade", "Blade", "Sweep", "Straight", "Parry", "Thrust", "Disarm", "Sever", 4, 7, 1, 2, 2, 5, 3, 0, 0, 1, 4, new String[] {"Metal"}, new String[] {"Swordplay", "Elegance"}, new String[] {"Jalgeaux", "Chobesh"}),
    new RawWeapon("katana", "A curved blade attributed mystical power that can make effortless quick slices with impressive accuracy.", '†', "Melee", "Repeat", 1, "SLASHING", "SLASHING", "Blade", "Dueling", "Sweep", "Straight", "Sweep", "Blur", "Sever", "Bleed", 5, 5, 1, 1, 5, 0, 3, 0, 0, 2, 6, new String[] {"Metal"}, new String[] {"Swordplay", "Elegance"}, new String[] {"Otekai", "Ninja"}),
    new RawWeapon("broadsword", "A wide-bladed two-handed sword that mixes the savagery of an axe with the precision of a sword.", '†', "Melee", "Repeat", 2, "SLASHING", "SLASHING", "Blade", "Blade", "Sweep", "Straight", "Sweep", "Chop", "Sever", "Slay", 3, 9, 3, 2, 1, 3, 3, 0, 0, 2, 2, new String[] {"Metal"}, new String[] {"Swordplay"}, new String[] {"Hyrden", "Otekai"}),
    new RawWeapon("rapier", "An elegant dueling weapon that is sharp all along its blade; ideal for thrusting attacks and parries.", '†', "Melee", "Repeat", 1, "PIERCING", "PIERCING", "Blade", "Dueling", "Multi", "Straight", "Parry", "Lunge", "Impale", "Slay", 6, 3, 2, 1, 6, 0, 3, 1, 1, 0, 6, new String[] {"Metal"}, new String[] {"Swordplay", "Elegance"}, new String[] {"Jalgeaux", "Elethian"}),
    new RawWeapon("shortsword", "Not quite a knife or a sword, shortswords are heavier than they look, but can still be swung quickly.", '▶', "Melee", "Repeat", 1, "SLASHING", "PIERCING", "Blade", "Dueling", "Multi", "Straight", "Parry", "Chop", "Silence", "Impale", 5, 5, 2, 1, 5, 1, 3, 3, 0, 0, 5, new String[] {"Metal"}, new String[] {"Swordplay"}, new String[] {"Bididhayi", "Khainghal"}),
    new RawWeapon("carving knife", "This sharpened chef's implement is just as good at slicing up a steak as it is slashing living flesh.", '▶', "Melee", "Repeat", 1, "PIERCING", "SLASHING", "Dueling", "Dueling", "Multi", "Straight", "Parry", "Hurl", "Impale", "Silence", 6, 2, 3, 3, 5, 0, 0, 5, 0, 0, 6, new String[] {"Metal|Stone"}, new String[] {"Swordplay", "Assassination"}, new String[] {"Mobyuld", "Laathrik"}),
    new RawWeapon("handaxe", "Some call them tomahawks, others hunga munga; this is a throwing axe by any name.", '⚑', "Melee", "Repeat", 1, "SLASHING", "SLASHING", "Axe", "Axe", "Multi", "Straight", "Chop", "Hurl", "Sever", "Disable", 3, 5, 7, 1, 3, 1, 5, 1, 0, 0, 4, new String[] {"Metal|Stone", "Wood"}, new String[] {"Brutality", "Naturalism"}, new String[] {"Krort", "Mbegonda"}),
    new RawWeapon("hatchet", "A medium-sized axe balanced for carving wood rather than throwing, it can carve flesh just as well.", '⚑', "Melee", "Repeat", 1, "SLASHING", "SLASHING", "Axe", "Axe", "Sweep", "Straight", "Chop", "Heft", "Sever", "Sunder", 2, 7, 8, 1, 2, 2, 5, 0, 0, 0, 3, new String[] {"Metal|Stone", "Wood"}, new String[] {"Brutality", "Naturalism"}, new String[] {"Ugexiir", "Mbegonda"}),
    new RawWeapon("greataxe", "It's hard to beat this two-handed axe when it comes to overkill; the destruction these wreak is legendary.", '⚑', "Melee", "Repeat", 2, "SLASHING", "SLASHING", "Axe", "Axe", "Sweep", "Straight", "Chop", "Heft", "Sever", "Sunder", 1, 9, 6, 1, 0, 4, 5, 0, 0, 1, 2, new String[] {"Metal", "Wood"}, new String[] {"Brutality"}, new String[] {"Krort", "Ugexiir"}),
    new RawWeapon("halberd", "It's an axe on a pole, the pole ends in a spear, and there's also a hook on the pole; clearly this is versatile.", '↟', "Melee", "Repeat", 2, "SLASHING", "PIERCING", "Polearm", "Axe", "Multi", "Straight", "Chop", "Brace", "Sever", "Trip", 3, 7, 5, 3, 0, 4, 3, 0, 1, 0, 3, new String[] {"Metal", "Wood"}, new String[] {"Elegance", "Foresight"}, new String[] {"Keungeuh", "Jalgeaux"}),
    new RawWeapon("glaive", "Anything blade-like attached to the end of a pole, for sweeping slices at a comfortable distance.", '↑', "Melee", "Repeat", 2, "SLASHING", "PIERCING", "Polearm", "Blade", "Sweep", "Straight", "Sweep", "Brace", "Bleed", "Sever", 5, 8, 2, 1, 2, 5, 0, 0, 1, 1, 3, new String[] {"Metal", "Wood"}, new String[] {"Foresight"}, new String[] {"Otekai", "Chobesh"}),
    new RawWeapon("cape", "Among the least effective ways to kill someone, but great for confusing or disarming foes while keeping your hands free.", '⍝', "Melee", "Repeat", 4, "BLUNT", "BLUNT", "Flexible", "Flexible", "Sweep", "Straight", "Blur", "Yank", "Confound", "Disarm", 2, 1, 0, 6, 6, 0, 2, 3, 0, 2, 6, new String[] {"Cloth"}, new String[] {"Elegance", "Whirling Death"}, new String[] {"Jalgeaux"}),
    new RawWeapon("hammer", "A heavy one-handed tool converted into a weapon of war that is especially strong against armor.", '⊤', "Melee", "Repeat", 1, "BLUNT", "BLUNT", "Bludgeon", "Bludgeon", "Multi", "Straight", "Pound", "Heft", "Sunder", "Confound", 4, 7, 4, 2, 0, 8, 2, 1, 0, 0, 2, new String[] {"Metal|Stone"}, new String[] {"Brutality"}, new String[] {"Keungeuh"}),
    new RawWeapon("maul", "A brutal two-handed sledgehammer that seems impractical against all but the slowest foes -- or monsters.", '⊤', "Melee", "Repeat", 2, "BLUNT", "BLUNT", "Bludgeon", "Bludgeon", "Multi", "Straight", "Pound", "Heft", "Sunder", "Trip", 2, 9, 5, 2, 0, 9, 2, 0, 0, 0, 1, new String[] {"Metal|Stone"}, new String[] {"Brutality"}, new String[] {"Krort", "Keungeuh"}),
    new RawWeapon("club", "A plain-old heavy stick that can be swung with surprising speed, and can be passed off as a normal twig.", '╿', "Melee", "Repeat", 1, "BLUNT", "BLUNT", "Bludgeon", "Bludgeon", "Multi", "Straight", "Pound", "Hurl", "Trip", "Disarm", 3, 3, 1, 2, 2, 4, 5, 5, 0, 0, 5, new String[] {"Wood"}, new String[] {"Brutality", "Naturalism"}, new String[] {"Mbegonda", "Ninja"}),
    new RawWeapon("mace", "Not just any stick, this one's made of solid metal, and can pulverize armor with a good wallop.", '╿', "Melee", "Repeat", 1, "BLUNT", "BLUNT", "Bludgeon", "Bludgeon", "Multi", "Straight", "Parry", "Pound", "Disarm", "Confound", 4, 5, 3, 1, 0, 9, 4, 0, 0, 0, 4, new String[] {"Metal|Stone"}, new String[] {"Brutality"}, new String[] {"Hyrden", "Cheuritae"}),
    new RawWeapon("pole", "Whether an collapsible pole or just a piece of bamboo, a pole is excellent at whacking multiple foes.", '|', "Melee", "Repeat", 2, "BLUNT", "BLUNT", "Polearm", "Bludgeon", "Sweep", "Straight", "Sweep", "Lunge", "Trip", "Confound", 4, 3, 2, 2, 1, 4, 4, 0, 1, 2, 4, new String[] {"Wood"}, new String[] {"Foresight"}, new String[] {"Mbegonda", "Zedmedov"}),
    new RawWeapon("taiaha", "An unusual mix of a spear's pointed tip with a shield-shaped haft, usually made entirely from hard wood.", '⌽', "Melee", "Repeat", 2, "BLUNT", "PIERCING", "Polearm", "Dueling", "Beam", "Straight", "Parry", "Pound", "Trip", "Confound", 5, 4, 2, 1, 4, 5, 6, 0, 0, 0, 3, new String[] {"Wood"}, new String[] {"Foresight", "Naturalism"}, new String[] {"Hueztotli", "Cheuritae"}),
    new RawWeapon("shortspear", "Lighter than a lance and heavier than a javelin, a shortspear can be effectively wielded at close to medium range.", '↑', "Melee", "Repeat", 1, "PIERCING", "PIERCING", "Polearm", "Polearm", "Beam", "Straight", "Brace", "Hurl", "Impale", "Disable", 5, 4, 1, 2, 6, 4, 3, 0, 1, 0, 3, new String[] {"Wood", "Metal"}, new String[] {"Foresight", "Naturalism"}, new String[] {"Mbegonda", "Hueztotli"}),
    new RawWeapon("lance", "An uncommon weapon, the lance can't attack a close-by enemy but can annihilate further-away foes.", '↑', "Melee", "Repeat", 2, "PIERCING", "PIERCING", "Polearm", "Polearm", "Beam", "Straight", "Brace", "Lunge", "Impale", "Slay", 2, 7, 1, 2, 3, 4, 2, 0, 2, 2, 1, new String[] {"Wood", "Metal"}, new String[] {"Foresight"}, new String[] {"Hyrden", "Chobesh"}),
    new RawWeapon("leaf spear", "A heavier mid-length spear with a broad leaf-shaped blade at the end; it can be used for slicing and stabbing alike.", '⍋', "Melee", "Repeat", 2, "PIERCING", "SLASHING", "Polearm", "Polearm", "Sweep", "Straight", "Thrust", "Sweep", "Impale", "Bleed", 2, 8, 3, 2, 2, 4, 3, 0, 1, 1, 2, new String[] {"Metal", "Wood"}, new String[] {"Foresight"}, new String[] {"Mbegonda"}),
    new RawWeapon("nunchaku", "A martial-arts weapon consisting of two club-like bars connected with a short chain; excellent for defensive use.", '⋀', "Melee", "Repeat", 1, "BLUNT", "BLUNT", "Bludgeon", "Flexible", "Sweep", "Straight", "Parry", "Blur", "Confound", "Disarm", 5, 2, 0, 2, 6, 2, 2, 5, 0, 1, 4, new String[] {"Wood"}, new String[] {"Martial Arts", "Whirling Death"}, new String[] {"Ninja"}),
    new RawWeapon("section staff", "A series of three or more wooden rods connected with chains; originally meant for threshing rice but deadly when swung.", '⋀', "Melee", "Repeat", 2, "BLUNT", "BLUNT", "Bludgeon", "Flexible", "Sweep", "Straight", "Parry", "Sweep", "Confound", "Trip", 4, 4, 0, 2, 6, 4, 2, 0, 0, 2, 4, new String[] {"Wood"}, new String[] {"Martial Arts", "Whirling Death"}, new String[] {"Ninja", "Keungeuh"}),
    new RawWeapon("hand flail", "A nasty spiked ball whipped around on a heavy chain; it's simply brutal against armored foes.", '!', "Melee", "Repeat", 1, "BLUNT", "BLUNT", "Bludgeon", "Flexible", "Sweep", "Straight", "Sweep", "Yank", "Disarm", "Trip", 3, 7, 6, 4, 0, 6, 0, 0, 0, 1, 2, new String[] {"Metal|Stone", "Wood"}, new String[] {"Whirling Death", "Brutality"}, new String[] {"Krort", "Chobesh"}),
    new RawWeapon("meteor flail", "A small, heavy weight on the end of a long vine or rope; making one is easy, but fighting with one is very hard.", '!', "Melee", "Repeat", 2, "BLUNT", "BLUNT", "Bludgeon", "Flexible", "Multi", "Arc", "Heft", "Yank", "Disarm", "Trip", 3, 6, 3, 5, 2, 0, 3, 0, 3, 0, 2, new String[] {"Metal|Stone", "Hide"}, new String[] {"Martial Arts", "Whirling Death"}, new String[] {"Hueztotli", "Ninja"}),
    new RawWeapon("whip", "A leather bullwhip that's flexible enough to knock a weapon from the hands of nearby opponents.", '∫', "Melee", "Repeat", 1, "BLUNT", "SLASHING", "Flexible", "Flexible", "Multi", "Arc", "Lunge", "Yank", "Disarm", "Trip", 4, 1, 4, 6, 3, 0, 4, 0, 2, 0, 4, new String[] {"Hide"}, new String[] {"Whirling Death"}, new String[] {"Hidzaajji", "Mobyuld"}),
    new RawWeapon("chain", "A metal chain that can be spun to deliver painful strikes, and can wrap around an enemy's weapon to relieve him of it.", '∫', "Melee", "Repeat", 1, "BLUNT", "BLUNT", "Flexible", "Flexible", "Sweep", "Straight", "Parry", "Yank", "Trip", "Disable", 4, 4, 3, 2, 5, 1, 4, 0, 0, 2, 3, new String[] {"Metal"}, new String[] {"Whirling Death"}, new String[] {"Chobesh", "Cheuritae"}),
    new RawWeapon("sai", "A martial-arts weapon with three curving spikes that provide ample ability to disarm foes.", '⍦', "Melee", "Repeat", 1, "PIERCING", "PIERCING", "Dueling", "Dueling", "Multi", "Straight", "Thrust", "Yank", "Disarm", "Impale", 4, 2, 2, 3, 6, 2, 1, 4, 0, 0, 6, new String[] {"Metal"}, new String[] {"Martial Arts", "Foresight"}, new String[] {"Ninja"}),
    new RawWeapon("trident", "A fishing spear adapted for gladiatorial and maritime combat, the trident has three barbed prongs on a mid-size pole.", '⍦', "Melee", "Repeat", 2, "PIERCING", "PIERCING", "Polearm", "Dueling", "Multi", "Straight", "Brace", "Hurl", "Impale", "Bleed", 3, 6, 2, 1, 4, 4, 4, 0, 1, 0, 4, new String[] {"Metal", "Wood"}, new String[] {"Foresight", "Naturalism"}, new String[] {"Cheuritae"}),
    new RawWeapon("sickle", "A lightweight, deeply-curved blade that can be used for severing plant roots, or a foe's limbs, as well as disarming.", 'ʕ', "Melee", "Repeat", 1, "SLASHING", "SLASHING", "Blade", "Axe", "Sweep", "Straight", "Chop", "Yank", "Sever", "Trip", 4, 4, 5, 4, 0, 1, 5, 2, 0, 1, 3, new String[] {"Metal"}, new String[] {"Naturalism", "Swordplay"}, new String[] {"Chobesh", "Ugexiir"}),
    new RawWeapon("khopesh", "A sword-like weapon with a heavy blade that goes straight and then curves; challenging to wield and to confront.", 'ʕ', "Melee", "Repeat", 1, "SLASHING", "SLASHING", "Blade", "Axe", "Sweep", "Straight", "Heft", "Yank", "Sever", "Trip", 3, 8, 6, 3, 1, 3, 2, 0, 0, 1, 2, new String[] {"Metal"}, new String[] {"Swordplay", "Brutality"}, new String[] {"Hidzaajji", "Krort"}),
    new RawWeapon("scythe", "A hook-bladed farming tool meant for slicing wide swaths of grain, adapted to slicing wide swaths in necks.", 'ʕ', "Melee", "Repeat", 2, "SLASHING", "SLASHING", "Blade", "Axe", "Sweep", "Straight", "Chop", "Sweep", "Slay", "Sever", 2, 8, 9, 5, 0, 0, 0, 0, 0, 2, 2, new String[] {"Metal", "Wood"}, new String[] {"Naturalism", "Brutality"}, new String[] {"Chobesh", "Ugexiir"}),
    new RawWeapon("light shield", "A shield that's meant for actively parrying and blocking enemy attacks, mixing in quick jabs with the edge of the shield.", '⊕', "Melee", "Repeat", 1, "BLUNT", "BLUNT", "Bludgeon", "Dueling", "Multi", "Straight", "Parry", "Blur", "Confound", "Disarm", 3, 4, 0, 2, 9, 7, 3, 0, 0, 0, 2, new String[] {"Hide|Metal|Wood"}, new String[] {"Foresight", "Naturalism"}, new String[] {"Hueztotli", "Cheuritae"}),
    new RawWeapon("heavy shield", "A large shield that's effective at defending even while barely moving, but it's hard to precisely wield one.", '⊕', "Melee", "Repeat", 1, "BLUNT", "BLUNT", "Bludgeon", "Bludgeon", "Multi", "Straight", "Parry", "Heft", "Confound", "Sunder", 2, 7, 0, 3, 7, 9, 1, 0, 0, 0, 1, new String[] {"Metal|Wood"}, new String[] {"Foresight"}, new String[] {"Hyrden", "Keungeuh"}),
    new RawWeapon("macahuitl", "A heavy wooden shaft that's been augmented with razor-sharp stone blades along its edge; incredibly dangerous.", '≢', "Melee", "Repeat", 2, "SLASHING", "BLUNT", "Blade", "Bludgeon", "Sweep", "Straight", "Heft", "Sweep", "Sever", "Slay", 1, 9, 9, 3, 0, 0, 5, 0, 0, 1, 1, new String[] {"Wood", "Stone"}, new String[] {"Brutality", "Naturalism"}, new String[] {"Hueztotli"}),
    new RawWeapon("bladed boots", "A dastardly weapon typically used by assassins, this formal footwear conceals a wicked blade that extends on contact.", '˩', "Melee", "Repeat", 5, "PIERCING", "PIERCING", "Dueling", "Dueling", "Multi", "Straight", "Blur", "Pound", "Bleed", "Trip", 3, 2, 6, 5, 3, 0, 0, 6, 0, 0, 5, new String[] {"Hide"}, new String[] {"Martial Arts", "Assassination"}, new String[] {"Jalgeaux", "Hidzaajji"}),
    new RawWeapon("shortbow", "A smaller bow that isn't as unwieldy in close-quarters combat as a longbow, and isn't as conspicuous when carried.", '(', "Ranged", "Projectile", 2, "PIERCING", "PIERCING", "Forceful", "Forceful", "Multi", "Arc", "Pin", "Arrows", "Impale", "Disable", 4, 4, 2, 4, 1, 0, 1, 1, 4, 0, 5, new String[] {"Wood", "Hide"}, new String[] {"Marksmanship", "Naturalism"}, new String[] {"Otekai", "Mobyuld"}),
    new RawWeapon("longbow", "A very large bow, usually taller than its wielder, that boasts tremendous range and hefty power.", '(', "Ranged", "Projectile", 2, "PIERCING", "PIERCING", "Forceful", "Forceful", "Multi", "Arc", "Heave", "Arrows", "Impale", "Slay", 3, 5, 1, 4, 0, 0, 1, 0, 6, 0, 4, new String[] {"Wood", "Hide"}, new String[] {"Marksmanship", "Naturalism"}, new String[] {"Khainghal", "Elethian"}),
    new RawWeapon("light crosssbow", "A small machine that launches a bolt with the pull of a trigger; slow to reload, but capable of piercing thick armor.", '⊢', "Ranged", "Projectile", 1, "PIERCING", "PIERCING", "Precise", "Precise", "Beam", "Straight", "Weave", "Bolts", "Impale", "Disable", 6, 5, 2, 1, 0, 1, 0, 1, 5, 1, 2, new String[] {"Wood"}, new String[] {"Marksmanship"}, new String[] {"Laathrik", "Zedmedov"}),
    new RawWeapon("heavy crossbow", "A weighty machine that can launch a bolt in an instant, but takes a long time to reload; makes up for it with huge power.", '⊢', "Ranged", "Projectile", 2, "PIERCING", "PIERCING", "Precise", "Precise", "Beam", "Straight", "Weave", "Bolts", "Impale", "Slay", 5, 7, 1, 1, 0, 1, 0, 0, 6, 1, 1, new String[] {"Wood"}, new String[] {"Marksmanship"}, new String[] {"Cheuritae", "Keungeuh"}),
    new RawWeapon("pistol", "This hand-held firearm is very imprecise, but that doesn't always hinder it because its bullet keeps bouncing after missing.", 'ᵣ', "Ranged", "Projectile", 1, "PIERCING", "PIERCING", "Precise", "Precise", "Multi", "Straight", "Ricochet", "Powder", "Slay", "Confound", 3, 8, 7, 1, 0, 0, 0, 3, 4, 0, 0, new String[] {"Metal"}, new String[] {"Marksmanship", "Elegance"}, new String[] {"Jalgeaux", "Cheuritae"}),
    new RawWeapon("musket", "A heavy two-handed firearm that has longer range than a pistol and more power, but isn't easily concealed. ", 'ɼ', "Ranged", "Projectile", 2, "PIERCING", "PIERCING", "Precise", "Precise", "Multi", "Straight", "Ricochet", "Powder", "Slay", "Confound", 2, 9, 8, 1, 0, 0, 0, 0, 5, 0, 0, new String[] {"Metal"}, new String[] {"Marksmanship", "Elegance"}, new String[] {"Zedmedov", "Jalgeaux"}),
    new RawWeapon("sling", "An early stone-hurling weapon made from cord and hide, it has considerably better range than throwing a rock by hand.", '⍩', "Ranged", "Projectile", 1, "BLUNT", "BLUNT", "Forceful", "Forceful", "Multi", "Arc", "Yank", "Pellets", "Confound", "Slay", 3, 3, 2, 5, 2, 0, 2, 4, 3, 0, 3, new String[] {"Hide"}, new String[] {"Hurling", "Naturalism"}, new String[] {"Ikkutiq", "Cheuritae"}),
    new RawWeapon("staff sling", "A curved staff with cords to hold a projectile at one end, this weapon improves a sling's range even further.", 'Ḯ', "Ranged", "Projectile", 2, "BLUNT", "BLUNT", "Forceful", "Forceful", "Multi", "Arc", "Pound", "Pellets", "Confound", "Slay", 2, 4, 2, 5, 0, 2, 1, 1, 5, 0, 3, new String[] {"Wood", "Hide"}, new String[] {"Hurling", "Foresight"}, new String[] {"Ikkutiq"}),
    new RawWeapon("chakram", "A bladed hoop with just as much use at medium range as in melee combat, it can be drawn quickly and thrown far.", '°', "Ranged", "Thrown", 1, "SLASHING", "SLASHING", "Blade", "Forceful", "Multi", "Straight", "Chop", "Blur", "Sever", "Disable", 5, 5, 5, 3, 0, 4, 0, 0, 2, 0, 4, new String[] {"Metal"}, new String[] {"Martial Arts", "Hurling"}, new String[] {"Bididhayi"}),
    new RawWeapon("boomerang", "A chunk of hard wood, carefully fashioned into a hunting weapon that spins all around the target when it misses.", '❮', "Ranged", "Thrown", 1, "BLUNT", "BLUNT", "Bludgeon", "Forceful", "Sweep", "Arc", "Pound", "Ricochet", "Confound", "Disarm", 4, 3, 1, 4, 1, 3, 4, 1, 3, 0, 3, new String[] {"Wood"}, new String[] {"Hurling", "Naturalism"}, new String[] {"Ugexiir"}),
    new RawWeapon("javelin", "A very light spear that's primarily meant for throwing, but can be used as a melee weapon in desperate situations.", '↗', "Ranged", "Thrown", 1, "PIERCING", "PIERCING", "Polearm", "Forceful", "Multi", "Arc", "Pin", "Thrust", "Impale", "Disable", 5, 5, 1, 3, 1, 0, 3, 1, 4, 0, 3, new String[] {"Wood"}, new String[] {"Hurling", "Naturalism"}, new String[] {"Cheuritae", "Mbegonda"}),
    new RawWeapon("atlatl", "A simple-looking device that enables a spear to be thrown much further, and with greater precision, than it can by hand.", '⋁', "Ranged", "Projectile", 2, "PIERCING", "PIERCING", "Forceful", "Polearm", "Multi", "Arc", "Heave", "Darts", "Impale", "Slay", 4, 9, 2, 2, 0, 0, 1, 0, 5, 0, 2, new String[] {"Wood"}, new String[] {"Hurling", "Naturalism"}, new String[] {"Hueztotli"}),
    new RawWeapon("shuriken", "A throwing weapon, shaped like a star or tiny blade, that deals pitiful damage but can be laced with poison.", '★', "Ranged", "Thrown", 1, "PIERCING", "POISON", "Dueling", "Precise", "Multi", "Straight", "Pin", "Blur", "Disable", "Afflict", 3, 1, 1, 6, 1, 0, 0, 6, 2, 1, 6, new String[] {"Metal"}, new String[] {"Assassination"}, new String[] {"Ninja"}),
    new RawWeapon("blowgun", "A simple projectile weapon that allows a quick exhalation to propel a needle at the target; usually the needle is poisoned.", '⁻', "Ranged", "Projectile", 1, "POISON", "PIERCING", "Precise", "Precise", "Multi", "Straight", "Weave", "Needles", "Afflict", "Slay", 4, 1, 2, 6, 0, 0, 5, 6, 2, 0, 2, new String[] {"Wood"}, new String[] {"Assassination", "Naturalism"}, new String[] {"Ninja", "Hueztotli"}),
    new RawWeapon("bolas", "A pair of weights linked with a cord that can be thrown with a spinning motion to entangle a target's legs or arms.", 'ʊ', "Ranged", "Thrown", 1, "BLUNT", "BLUNT", "Flexible", "Forceful", "Multi", "Arc", "Yank", "Pin", "Trip", "Disarm", 3, 2, 0, 6, 3, 0, 4, 3, 3, 0, 3, new String[] {"Stone", "Hide"}, new String[] {"Whirling Death", "Naturalism"}, new String[] {"Ikkutiq", "Mobyuld"}),
    new RawWeapon("net", "A heavy mesh with barbed weights on the edge that could be used for fishing or ensnaring fugitives.", '⍯', "Ranged", "Thrown", 2, "BLUNT", "BLUNT", "Flexible", "Forceful", "Sweep", "Arc", "Pound", "Pin", "Trip", "Disable", 4, 0, 0, 6, 6, 3, 4, 0, 2, 1, 1, new String[] {"Cloth"}, new String[] {"Whirling Death", "Naturalism"}, new String[] {"Cheuritae", "Hidzaajji"}),
    new RawWeapon("grenade", "A precursor to other firearms, this is a container of shrapnel and blast powder that causes brutal damage when it works.", '¤', "Ranged", "Projectile", 2, "PIERCING", "SLASHING", "Forceful", "Precise", "Burst", "Arc", "Heave", "Powder", "Slay", "Bleed", 0, 9, 0, 6, 3, 0, 0, 4, 2, 2, 0, new String[] {"Metal"}, new String[] {"Hurling"}, new String[] {"Khainghal", "Cheuritae"}),
    new RawWeapon("handcannon", "A simpler model of firearm that has advanced in parallel to the musket, this weapon fires a shell in a very long arc.", 'ɼ', "Ranged", "Projectile", 2, "PIERCING", "SLASHING", "Forceful", "Precise", "Burst", "Arc", "Ricochet", "Powder", "Slay", "Bleed", 0, 9, 0, 5, 0, 0, 0, 0, 6, 2, 0, new String[] {"Metal"}, new String[] {"Foresight"}, new String[] {"Laathrik", "Khainghal"}),
    new RawWeapon("throwing dagger", "A well-balanced, light-weight knife that is primarily meant to be thrown, but is also usable in close combat in a pinch.", '¹', "Ranged", "Thrown", 1, "PIERCING", "PIERCING", "Dueling", "Precise", "Multi", "Straight", "Blur", "Thrust", "Impale", "Disable", 4, 2, 2, 6, 3, 0, 0, 3, 3, 0, 4, new String[] {"Metal"}, new String[] {"Assassination", "Hurling"}, new String[] {"Mobyuld", "Khainghal"}),
    new RawWeapon("channeling orb", "A small round crystal that a Mystic wielder can use to disrupt the energy flow of an enemy, or supplant a friend's.", '●', "Implement", "Repeat", 1, "CRYSTAL", "FATEFUL", "Mystic", "Mystic", "Through", "Straight", "Pound", "Shine", "Confound", "Energize", 5, 1, 0, 6, 1, 0, 2, 1, 5, 2, 0, new String[] {"Inclusion"}, new String[] {"Sorcery", "Foresight"}, new String[] {"Otekai", "Elethian"}),
    new RawWeapon("mystic deck", "A deck of many fortune-telling cards, each decorated with cryptic imagery; an interpretation can be used as a Mystic curse.", '⍰', "Implement", "Repeat", 2, "FATEFUL", "TEMPORAL", "Mystic", "Mystic", "Multi", "Straight", "Weave", "Hex", "Curse", "Favor", 2, 1, 5, 1, 0, 0, 6, 2, 4, 1, 3, new String[] {"Paper"}, new String[] {"Sorcery", "Hurling"}, new String[] {"Jalgeaux", "Keungeuh"}),
    new RawWeapon("soul pyramid", "A mysterious stone that seems fated to belong to a Mystic wielder; to such a bearer it grants esoteric powers.", '⍙', "Implement", "Repeat", 1, "DEATH", "PURE", "Mystic", "Mystic", "Burst", "Arc", "Exorcise", "Condemn", "Silence", "Curse", 1, 7, 0, 4, 0, 1, 4, 1, 3, 3, 0, new String[] {"Stone"}, new String[] {"Sorcery", "Foresight"}, new String[] {"Hidzaajji"}),
    new RawWeapon("dancing fan", "A paper fan that seems to move at the slightest touch; it can exert power over the wind and over fate.", '⍢', "Implement", "Repeat", 1, "AIR", "FATEFUL", "Mystic", "Mystic", "Wave", "Straight", "Seal", "Blur", "Trip", "Curse", 2, 1, 0, 5, 4, 0, 4, 0, 3, 3, 2, new String[] {"Paper"}, new String[] {"Sorcery", "Elegance"}, new String[] {"Otekai", "Hidzaajji"}),
    new RawWeapon("fossil fang", "A gift of the land to those who protect it, this massive stony tooth carries Primal power over rock and soil.", 'ɿ', "Implement", "Repeat", 1, "EARTH", "PIERCING", "Primal", "Dueling", "Through", "Straight", "Thrust", "Seal", "Trip", "Impale", 2, 5, 3, 1, 0, 3, 4, 0, 3, 2, 2, new String[] {"Stone"}, new String[] {"Sorcery", "Naturalism"}, new String[] {"Mbegonda", "Khainghal"}),
    new RawWeapon("green wreath", "A garland of leaves that lives without soil or water thanks to a Primal blessing; it can grow thorny vines to attack.", '◌', "Implement", "Repeat", 3, "PIERCING", "PURE", "Primal", "Flexible", "Multi", "Arc", "Exorcise", "Pin", "Afflict", "Regenerate", 2, 1, 1, 2, 0, 0, 4, 6, 4, 1, 4, new String[] {"Wood"}, new String[] {"Sorcery", "Naturalism"}, new String[] {"Elethian"}),
    new RawWeapon("primeval shield", "A hide shield inscribed with ancient symbols that beseech the land for protection; fiendish creatures shy away from it.", '⊛', "Implement", "Repeat", 1, "BLUNT", "FATEFUL", "Primal", "Bludgeon", "Wave", "Straight", "Parry", "Exorcise", "Favor", "Confound", 2, 1, 0, 3, 7, 6, 6, 0, 0, 2, 1, new String[] {"Hide"}, new String[] {"Sorcery", "Naturalism"}, new String[] {"Ikkutiq"}),
    new RawWeapon("pan pipes", "A traditional musical instrument that is said to make the favorite sound of woodland spirits, imploring them for aid in crises.", 'ˠ', "Implement", "Repeat", 1, "SOUND", "PRIMAL", "Primal", "Primal", "Wave", "Straight", "Pound", "Exorcise", "Silence", "Favor", 3, 1, 0, 5, 4, 0, 6, 0, 3, 2, 1, new String[] {"Wood"}, new String[] {"Sorcery", "Naturalism"}, new String[] {"Elethian", "Hyrden"}),
    new RawWeapon("holy symbol", "When carried by a Blessed wielder, this sacred icon can issue shining judgment even when simply worn or carried.", 'ᵸ', "Implement", "Repeat", 4, "DIVINE", "SHINING", "Blessed", "Blessed", "Multi", "Straight", "Shine", "Exorcise", "Regenerate", "Blind", 4, 4, 0, 4, 0, 0, 5, 0, 3, 2, 3, new String[] {"Metal|Wood"}, new String[] {"Sorcery", "Elegance"}, new String[] {"Hyrden", "Cheuritae"}),
    new RawWeapon("sacred book", "A ritually-purified holy text that serves to channel Blessed faith into waves of divine wrath against transgressors.", '⍐', "Implement", "Repeat", 2, "PURE", "DIVINE", "Blessed", "Blessed", "Wave", "Straight", "Seal", "Exorcise", "Favor", "Silence", 3, 5, 0, 3, 0, 1, 5, 0, 3, 3, 1, new String[] {"Paper", "Metal"}, new String[] {"Sorcery", "Elegance"}, new String[] {"Zedmedov", "Chobesh"}),
    new RawWeapon("prayer beads", "A necklace made of hundreds of tiny stone beads; Blessed wielders can throw the beads to channel divine wrath at targets.", '※', "Implement", "Repeat", 1, "DIVINE", "LIGHTNING", "Blessed", "Blessed", "Burst", "Arc", "Condemn", "Shine", "Blind", "Slay", 4, 7, 0, 2, 0, 0, 2, 1, 3, 3, 2, new String[] {"Stone"}, new String[] {"Sorcery", "Hurling"}, new String[] {"Bididhayi", "Hidzaajji"}),
    new RawWeapon("cherished icon", "A relic that once belonged to (or is made of) a long-dead hero of some faith; Blessed wielders hold these for protection.", 'ͼ', "Implement", "Repeat", 1, "DIVINE", "FATEFUL", "Blessed", "Blessed", "Wave", "Straight", "Exorcise", "Hex", "Favor", "Disable", 1, 1, 0, 3, 6, 6, 6, 0, 2, 0, 3, new String[] {"Stone"}, new String[] {"Sorcery", "Foresight"}, new String[] {"Hyrden", "Chobesh"}),
    new RawWeapon("vile grimoire", "An Occult wielder's best (or only) friend, this black book, bound in some kind of skin, can obliterate nearby enemies.", '⍌', "Implement", "Repeat", 2, "SINISTER", "ACID", "Occult", "Occult", "Wave", "Straight", "Condemn", "Hex", "Afflict", "Corrode", 2, 8, 0, 5, 0, 0, 0, 0, 4, 3, 1, new String[] {"Paper", "Hide"}, new String[] {"Sorcery", "Assassination"}, new String[] {"Krort", "Hueztotli"}),
    new RawWeapon("black lantern", "A rusted antique, this appears to be an ordinary lantern until lit with Occult magic, which makes it project a death ray.", '⍎', "Implement", "Repeat", 1, "SHADOW", "DEATH", "Occult", "Occult", "Beam", "Straight", "Reveal", "Hex", "Curse", "Slay", 4, 6, 1, 1, 0, 0, 0, 0, 6, 3, 0, new String[] {"Inclusion", "Metal"}, new String[] {"Sorcery", "Assassination"}, new String[] {"Mobyuld"}),
    new RawWeapon("eldritch prism", "A five-sided crystal shaft with motes of otherworldly light dancing inside it; Occult wielders can strike with this odd glow.", '⬠', "Implement", "Repeat", 2, "RADIATION", "SINISTER", "Occult", "Occult", "Beam", "Straight", "Shine", "Pound", "Disable", "Curse", 6, 3, 0, 4, 0, 2, 0, 0, 5, 2, 1, new String[] {"Inclusion"}, new String[] {"Sorcery", "Elegance"}, new String[] {"Ikkutiq"}),
    new RawWeapon("fractal knot", "A bend in the fabric of reality that has been entangled into a complex knot of animal hide; allows Warp attacks.", 'ʓ', "Implement", "Repeat", 1, "WARP", "VACUOUS", "Occult", "Occult", "Through", "Straight", "Reveal", "Yank", "Curse", "Disarm", 3, 4, 0, 2, 3, 0, 0, 1, 5, 1, 5, new String[] {"Hide"}, new String[] {"Sorcery", "Marksmanship"}, new String[] {"Laathrik", "Hueztotli"}),
    new RawWeapon("scholarly tome", "A thick Arcane encyclopedia, laden with enchantments that can drive off enemies who come too close.", '⌺', "Implement", "Repeat", 2, "CONTRACTUAL", "ICE", "Arcane", "Arcane", "Wave", "Straight", "Seal", "Reveal", "Silence", "Confound", 5, 3, 0, 4, 0, 2, 0, 0, 5, 3, 0, new String[] {"Paper", "Hide"}, new String[] {"Sorcery", "Foresight"}, new String[] {"Elethian", "Hidzaajji"}),
    new RawWeapon("magus scepter", "A strong effort by Arcane wielders to one-up firearms, this staff with various gems and sigils on it can fire energy blasts.", '∤', "Implement", "Repeat", 1, "FIRE", "RADIATION", "Arcane", "Bludgeon", "Beam", "Straight", "Pound", "Reveal", "Ignite", "Slay", 4, 7, 0, 2, 0, 1, 0, 0, 5, 2, 2, new String[] {"Wood", "Inclusion"}, new String[] {"Sorcery", "Marksmanship"}, new String[] {"Hueztotli", "Ikkutiq"}),
    new RawWeapon("wizardly wand", "A small but carefully-crafted wooden rod that enables Arcane wielders to manipulate magic strength in themselves and others.", 'ᶩ', "Implement", "Repeat", 1, "WATER", "AIR", "Arcane", "Arcane", "Beam", "Straight", "Seal", "Hex", "Disarm", "Energize", 5, 2, 1, 6, 0, 0, 0, 4, 4, 0, 4, new String[] {"Wood"}, new String[] {"Sorcery", "Marksmanship"}, new String[] {"Jalgeaux", "Keungeuh"}),
    new RawWeapon("stargazer lens", "A hand-held lens that has been modified with Arcane magic to be able to pull down any unusual objects spotted in the sky.", '○', "Implement", "Repeat", 1, "GRAVITY", "SHINING", "Arcane", "Arcane", "Beam", "Straight", "Pin", "Reveal", "Trip", "Flare", 6, 3, 3, 3, 0, 0, 0, 0, 6, 1, 1, new String[] {"Inclusion"}, new String[] {"Sorcery", "Foresight"}, new String[] {"Ikkutiq", "Khainghal"}),
    new RawWeapon("Zedmedov style", "The somber nation of Zedmedov uses unarmed combat to resist undead raiders that frequently tear through its land.", 'ɱ', "Unarmed", "Repeat", 0, "BLUNT", "BLUNT", "Brawling", "Brawling", "Multi", "Straight", "Parry", "Brace", "Disable", "Slay", 3, 4, 4, 1, 2, 4, 0, 6, 0, 0, 6, new String[] {}, new String[] {"Martial Arts", "Foresight"}, new String[] {"Zedmedov"}),
    new RawWeapon("Cheuritae style", "The island conglomerate of Cheuritae favors unarmed combat that takes advantage of chaotic battlefields, such as ships.", 'ɱ', "Unarmed", "Repeat", 0, "BLUNT", "BLUNT", "Brawling", "Flowing", "Multi", "Straight", "Lunge", "Sweep", "Trip", "Confound", 2, 2, 4, 2, 4, 0, 4, 6, 0, 0, 6, new String[] {}, new String[] {"Martial Arts", "Whirling Death"}, new String[] {"Cheuritae"}),
    new RawWeapon("Hyrden style", "The proud state of Hyrden goes on crusades against monsters; they often mix swordplay with unarmed combat.", 'ɱ', "Unarmed", "Repeat", 0, "BLUNT", "BLUNT", "Brawling", "Brawling", "Multi", "Straight", "Blur", "Sweep", "Favor", "Slay", 3, 3, 2, 3, 2, 1, 4, 6, 0, 0, 6, new String[] {}, new String[] {"Martial Arts", "Swordplay"}, new String[] {"Hyrden"}),
    new RawWeapon("Mbegonda style", "The alliance of tribes from the Mbegonda region uses improvised weapons to make their unarmed combat interesting.", 'ɱ', "Unarmed", "Repeat", 0, "BLUNT", "BLUNT", "Brawling", "Flowing", "Multi", "Straight", "Chop", "Yank", "Sunder", "Afflict", 1, 2, 3, 5, 2, 0, 5, 6, 0, 0, 6, new String[] {}, new String[] {"Martial Arts", "Naturalism"}, new String[] {"Mbegonda"}),
    new RawWeapon("Ninjutsu style", "The honor-bound empire of Otekai does not teach unarmed combat, but their ninja enemies rely on it heavily.", 'ɱ', "Unarmed", "Repeat", 0, "BLUNT", "BLUNT", "Flowing", "Flowing", "Multi", "Straight", "Blur", "Lunge", "Afflict", "Slay", 4, 1, 5, 5, 3, 0, 0, 6, 0, 0, 6, new String[] {}, new String[] {"Martial Arts", "Assassination"}, new String[] {"Ninja"}),
    new RawWeapon("Jalgeaux style", "The aristocracy of Jalgeaux likes to use a flashy sort of unarmed combat that makes enemies overestimate a practitioner.", 'ɱ', "Unarmed", "Repeat", 0, "BLUNT", "BLUNT", "Flowing", "Flowing", "Multi", "Straight", "Parry", "Heft", "Slay", "Confound", 3, 5, 2, 3, 1, 3, 3, 2, 0, 1, 6, new String[] {}, new String[] {"Martial Arts", "Elegance"}, new String[] {"Jalgeaux"}),
    new RawWeapon("Ikkutiq style", "The frozen lands of the Ikkutiq people are rich in magic, and their unarmed combat often seems like a ritual dance.", 'ɱ', "Unarmed", "Repeat", 0, "BLUNT", "BLUNT", "Flowing", "Flowing", "Multi", "Straight", "Seal", "Sweep", "Silence", "Trip", 4, 1, 1, 6, 1, 0, 4, 5, 0, 1, 6, new String[] {}, new String[] {"Martial Arts", "Elegance"}, new String[] {"Ikkutiq"}),
    new RawWeapon("Ugexiir style", "The fiercely-self-ruled nation of Ugexiir teaches its people vigilance against all enemies, whether they are armed or not.", 'ɱ', "Unarmed", "Repeat", 0, "BLUNT", "BLUNT", "Brawling", "Brawling", "Multi", "Straight", "Blur", "Brace", "Disable", "Disarm", 3, 3, 2, 2, 2, 4, 2, 6, 0, 0, 6, new String[] {}, new String[] {"Martial Arts", "Foresight"}, new String[] {"Ugexiir"}),
    new RawWeapon("Hidzaajji style", "The desert lands of Hidzaajji are home to a fighting style which emphasizes movement that flows like silk.", 'ɱ', "Unarmed", "Repeat", 0, "BLUNT", "BLUNT", "Flowing", "Flowing", "Multi", "Straight", "Blur", "Sweep", "Trip", "Favor", 3, 1, 5, 2, 5, 0, 3, 5, 0, 0, 6, new String[] {}, new String[] {"Martial Arts", "Whirling Death"}, new String[] {"Hidzaajji"}),
    new RawWeapon("Laathrik style", "The Kobold nation of Laathrik teaches unarmed combat that consists almost exclusively of dirty tricks and low blows.", 'ɱ', "Unarmed", "Repeat", 0, "BLUNT", "BLUNT", "Brawling", "Flowing", "Multi", "Straight", "Yank", "Hex", "Bleed", "Blind", 2, 1, 4, 4, 6, 0, 1, 6, 0, 0, 6, new String[] {}, new String[] {"Martial Arts", "Assassination"}, new String[] {"Laathrik"}),
    new RawWeapon("Hueztotli style", "The isolated land of Hueztotli practices a hard-hitting form of unarmed combat meant mainly for subduing monsters.", 'ɱ', "Unarmed", "Repeat", 0, "BLUNT", "BLUNT", "Brawling", "Brawling", "Multi", "Straight", "Heft", "Lunge", "Slay", "Trip", 1, 6, 2, 2, 2, 1, 4, 6, 0, 0, 6, new String[] {}, new String[] {"Martial Arts", "Brutality"}, new String[] {"Hueztotli"}),
    new RawWeapon("Keungeuh style", "The merchant-ruled state called Keungeuh practices unarmed combat that exerts control over the enemy's movement.", 'ɱ', "Unarmed", "Repeat", 0, "BLUNT", "BLUNT", "Brawling", "Flowing", "Multi", "Straight", "Yank", "Pin", "Trip", "Disarm", 4, 1, 1, 5, 4, 0, 3, 6, 0, 0, 6, new String[] {}, new String[] {"Martial Arts", "Hurling"}, new String[] {"Keungeuh"}),
    new RawWeapon("Khainghal style", "The steppe nomads of the Khainghal clan primarily use bows, but they practice unarmed combat with similar precision.", 'ɱ', "Unarmed", "Repeat", 0, "BLUNT", "BLUNT", "Brawling", "Flowing", "Multi", "Straight", "Thrust", "Pin", "Disable", "Silence", 6, 3, 5, 2, 1, 0, 1, 6, 0, 0, 6, new String[] {}, new String[] {"Martial Arts", "Marksmanship"}, new String[] {"Khainghal"}),
    new RawWeapon("Bididhayi style", "The pious nation of Bididhayi teaches a “thunderbolt” school of unarmed combat that strikes quickly with grabs and throws.", 'ɱ', "Unarmed", "Repeat", 0, "BLUNT", "BLUNT", "Brawling", "Flowing", "Multi", "Straight", "Lunge", "Pin", "Confound", "Favor", 4, 4, 2, 1, 3, 0, 4, 6, 0, 0, 6, new String[] {}, new String[] {"Martial Arts", "Hurling"}, new String[] {"Bididhayi"}),
    new RawWeapon("Mobyuld style", "The Goblin nation of Mobyuld teaches a sneaky style of unarmed combat based around striking pressure points.", 'ɱ', "Unarmed", "Repeat", 0, "BLUNT", "BLUNT", "Flowing", "Flowing", "Multi", "Straight", "Thrust", "Condemn", "Disable", "Curse", 6, 1, 5, 6, 0, 0, 0, 6, 0, 0, 6, new String[] {}, new String[] {"Martial Arts", "Marksmanship"}, new String[] {"Mobyuld"}),
    new RawWeapon("Chobesh style", "The nation of Chobesh collapsed after a century of war, so what's left emphasizes making do with minimal equipment.", 'ɱ', "Unarmed", "Repeat", 0, "BLUNT", "BLUNT", "Brawling", "Brawling", "Multi", "Straight", "Yank", "Brace", "Disarm", "Bleed", 2, 2, 5, 2, 1, 1, 5, 6, 0, 0, 6, new String[] {}, new String[] {"Martial Arts", "Naturalism"}, new String[] {"Chobesh"}),
    new RawWeapon("Elethian style", "The Elf nation of Elethian loves swordplay, and their unarmed combat similarly emphasizes balance and parrying.", 'ɱ', "Unarmed", "Repeat", 0, "BLUNT", "BLUNT", "Flowing", "Flowing", "Multi", "Straight", "Parry", "Sweep", "Disarm", "Trip", 5, 2, 1, 3, 5, 0, 2, 6, 0, 0, 6, new String[] {}, new String[] {"Martial Arts", "Swordplay"}, new String[] {"Elethian"}),
    new RawWeapon("Krort style", "The Orc culture of Krort likes big, heavy weapons, but they like it even more when an Orc can hit hard without one.", 'ɱ', "Unarmed", "Repeat", 0, "BLUNT", "BLUNT", "Brawling", "Brawling", "Multi", "Straight", "Heft", "Chop", "Confound", "Bleed", 1, 6, 6, 1, 1, 2, 1, 6, 0, 0, 6, new String[] {}, new String[] {"Martial Arts", "Brutality"}, new String[] {"Krort"}),
    new RawWeapon("fire magic", "Fire magic is anything but subtle, emphasizing heavy damage across wide areas.", '⚡', "Magic", "Repeat", 0, "FIRE", "FIRE", "Energy", "Energy", "Beam", "Straight", "Sweep", "Exorcise", "Slay", "Ignite", 1, 8, 0, 1, 2, 0, 0, 4, 3, 3, 2, new String[] {}, new String[] {"Sorcery", "Brutality"}, new String[] {"Mobyuld", "Chobesh"}),
    new RawWeapon("earth magic", "Earth magic is mainly defensive, using barriers of dirt and blocking stones mixed in between blows with rocks.", '⚡', "Magic", "Repeat", 0, "EARTH", "EARTH", "Energy", "Energy", "Wave", "Straight", "Pound", "Seal", "Trip", "Confound", 2, 6, 0, 2, 1, 5, 1, 4, 3, 1, 1, new String[] {}, new String[] {"Sorcery", "Foresight"}, new String[] {"Otekai", "Krort"}),
    new RawWeapon("water magic", "Water magic utilizes various unpleasant compounds swirled in with otherwise-harmless fluid to cause all sorts of trouble.", '⚡', "Magic", "Repeat", 0, "WATER", "WATER", "Energy", "Energy", "Wave", "Straight", "Blur", "Exorcise", "Trip", "Afflict", 4, 1, 0, 5, 3, 0, 2, 4, 3, 2, 1, new String[] {}, new String[] {"Sorcery", "Whirling Death"}, new String[] {"Elethian", "Cheuritae"}),
    new RawWeapon("air magic", "Air magic uses long-ranged, precise strikes with pieces of debris picked up on the wind.", '⚡', "Magic", "Repeat", 0, "AIR", "AIR", "Energy", "Energy", "Beam", "Arc", "Blur", "Reveal", "Disable", "Confound", 4, 0, 2, 1, 4, 0, 1, 4, 5, 0, 4, new String[] {}, new String[] {"Sorcery", "Marksmanship"}, new String[] {"Khainghal", "Ugexiir"}),
    new RawWeapon("crystal magic", "Crystal magic allows creating both beautifully shimmering defensive barriers and deadly slicing shards of gems.", '⚡', "Magic", "Repeat", 0, "CRYSTAL", "CRYSTAL", "Energy", "Energy", "Multi", "Arc", "Shine", "Ricochet", "Blind", "Sever", 3, 5, 5, 2, 0, 4, 3, 0, 3, 1, 0, new String[] {}, new String[] {"Sorcery", "Swordplay"}, new String[] {"Keungeuh", "Jalgeaux"}),
    new RawWeapon("sound magic", "Sound magic doesn't do much damage, but it can confuse huge swaths of foes at once.", '⚡', "Magic", "Repeat", 0, "SOUND", "SOUND", "Energy", "Energy", "Wave", "Straight", "Sweep", "Blur", "Confound", "Afflict", 2, 1, 0, 6, 1, 0, 1, 5, 3, 3, 2, new String[] {}, new String[] {"Sorcery", "Hurling"}, new String[] {"Mbegonda", "Hyrden"}),
    new RawWeapon("ice magic", "Ice magic delivers prolonged suffering by freezing foes in place and chilling them to the bone.", '⚡', "Magic", "Repeat", 0, "ICE", "ICE", "Energy", "Energy", "Wave", "Straight", "Pin", "Seal", "Disable", "Chill", 5, 2, 0, 6, 1, 2, 0, 4, 3, 1, 2, new String[] {}, new String[] {"Sorcery", "Assassination"}, new String[] {"Zedmedov", "Ikkutiq"}),
    new RawWeapon("lightning magic", "Lightning magic strikes suddenly with potent but inaccurate thunderbolts.", '⚡', "Magic", "Repeat", 0, "LIGHTNING", "LIGHTNING", "Energy", "Energy", "Beam", "Straight", "Ricochet", "Yank", "Electrify", "Energize", 0, 5, 0, 2, 2, 0, 0, 4, 4, 2, 5, new String[] {}, new String[] {"Sorcery", "Hurling"}, new String[] {"Bididhayi", "Hidzaajji"}),
    new RawWeapon("poison magic", "Poison magic is usually studied in lawless places, where the cruel death it delivers is already commonplace.", '⚡', "Magic", "Repeat", 0, "POISON", "POISON", "Energy", "Energy", "Burst", "Arc", "Condemn", "Seal", "Afflict", "Slay", 2, 3, 6, 6, 0, 0, 0, 6, 3, 0, 1, new String[] {}, new String[] {"Sorcery", "Assassination"}, new String[] {"Ninja", "Mobyuld"}),
    new RawWeapon("acid magic", "Acid magic is considered marginally less dishonorable than poison magic because it also has practical uses in etching.", '⚡', "Magic", "Repeat", 0, "ACID", "ACID", "Energy", "Energy", "Wave", "Straight", "Heave", "Sweep", "Sunder", "Corrode", 3, 4, 2, 6, 0, 0, 0, 4, 3, 2, 1, new String[] {}, new String[] {"Sorcery", "Hurling"}, new String[] {"Laathrik", "Hueztotli"}),
    new RawWeapon("caustic magic", "Caustic magic is exceptionally rare, and though it mainly finds industrial uses, some mages can wield it in battle.", '⚡', "Magic", "Repeat", 0, "CAUSTIC", "CAUSTIC", "Energy", "Energy", "Wave", "Straight", "Exorcise", "Reveal", "Afflict", "Blind", 4, 0, 0, 6, 2, 1, 0, 4, 4, 1, 3, new String[] {}, new String[] {"Sorcery", "Swordplay"}, new String[] {"Keungeuh", "Ugexiir"}),
    new RawWeapon("disease magic", "Disease magic revolves just as much around curing illnesses as causing them because it backfires so often.", '⚡', "Magic", "Repeat", 0, "DISEASE", "DISEASE", "Energy", "Energy", "Multi", "Arc", "Ricochet", "Condemn", "Afflict", "Regenerate", 3, 1, 0, 6, 4, 0, 0, 6, 4, 0, 2, new String[] {}, new String[] {"Sorcery", "Naturalism"}, new String[] {"Mbegonda", "Chobesh"}),
    new RawWeapon("death magic", "Death magic is reviled by most of the world because it has no practical applications other than overt warfare or murder.", '⚡', "Magic", "Repeat", 0, "DEATH", "DEATH", "Energy", "Energy", "Multi", "Straight", "Condemn", "Hex", "Slay", "Wither", 4, 9, 4, 1, 0, 0, 0, 4, 4, 0, 0, new String[] {}, new String[] {"Sorcery", "Brutality"}, new String[] {"Krort", "Ninja"}),
    new RawWeapon("life magic", "Life magic can be used offensively by moving vitality around in opponents, potentially weakening or even killing them.", '⚡', "Magic", "Repeat", 0, "LIFE", "LIFE", "Energy", "Energy", "Multi", "Straight", "Exorcise", "Weave", "Regenerate", "Bleed", 5, 1, 3, 6, 0, 0, 3, 4, 4, 0, 0, new String[] {}, new String[] {"Sorcery", "Foresight"}, new String[] {"Elethian", "Bididhayi"}),
    new RawWeapon("shining magic", "Shining magic can wreak havoc on wide areas with laser-like beams of searing light.", '⚡', "Magic", "Repeat", 0, "SHINING", "SHINING", "Energy", "Energy", "Beam", "Straight", "Exorcise", "Reveal", "Blind", "Flare", 6, 1, 2, 2, 0, 0, 1, 0, 6, 2, 2, new String[] {}, new String[] {"Sorcery", "Marksmanship"}, new String[] {"Hyrden", "Hidzaajji"}),
    new RawWeapon("shadow magic", "Shadow magic is frequently used by thieves because of how effortlessly it lets them sneak past guards.", '⚡', "Magic", "Repeat", 0, "SHADOW", "SHADOW", "Energy", "Energy", "Burst", "Arc", "Sweep", "Hex", "Blind", "Curse", 3, 1, 0, 6, 6, 0, 0, 6, 2, 1, 2, new String[] {}, new String[] {"Sorcery", "Assassination"}, new String[] {"Laathrik", "Khainghal"}),
    new RawWeapon("temporal magic", "Temporal magic manipulates time in minor ways to cause significant headaches for enemies.", '⚡', "Magic", "Repeat", 0, "TEMPORAL", "TEMPORAL", "Energy", "Energy", "Through", "Straight", "Blur", "Pin", "Confound", "Trip", 2, 0, 0, 6, 4, 0, 0, 4, 3, 1, 6, new String[] {}, new String[] {"Sorcery", "Whirling Death"}, new String[] {"Cheuritae", "Ikkutiq"}),
    new RawWeapon("fateful magic", "Fateful magic seeks to alter enemies' fortunes while simultaneously improving the user's odds.", '⚡', "Magic", "Repeat", 0, "FATEFUL", "FATEFUL", "Energy", "Energy", "Through", "Straight", "Condemn", "Hex", "Favor", "Curse", 1, 0, 2, 6, 2, 0, 6, 4, 2, 2, 1, new String[] {}, new String[] {"Sorcery", "Foresight"}, new String[] {"Jalgeaux", "Hueztotli"}),
    new RawWeapon("pure magic", "Pure magic is meant to drive away despoilers of nature, especially undead, but almost any foe is “impure” in some way.", '⚡', "Magic", "Repeat", 0, "PURE", "PURE", "Energy", "Energy", "Through", "Straight", "Exorcise", "Seal", "Slay", "Silence", 3, 3, 0, 6, 1, 1, 6, 0, 3, 1, 2, new String[] {}, new String[] {"Sorcery", "Naturalism"}, new String[] {"Zedmedov", "Otekai"}),
    new RawWeapon("primal magic", "Primal magic draws on nature's power to strike foes in unpredictable ways, from creating sinkholes to dropping trees.", '⚡', "Magic", "Repeat", 0, "PRIMAL", "PRIMAL", "Energy", "Energy", "Burst", "Arc", "Pin", "Reveal", "Trip", "Sunder", 1, 5, 0, 1, 2, 2, 4, 4, 3, 2, 1, new String[] {}, new String[] {"Sorcery", "Naturalism"}, new String[] {"Mbegonda", "Ugexiir"}),
    new RawWeapon("contractual magic", "Contractual magic uses the power of laws, both laws written by people and laws that define the cosmos, to disable foes.", '⚡', "Magic", "Repeat", 0, "CONTRACTUAL", "CONTRACTUAL", "Energy", "Energy", "Through", "Straight", "Reveal", "Seal", "Disable", "Confound", 6, 0, 0, 6, 0, 4, 2, 0, 3, 1, 4, new String[] {}, new String[] {"Sorcery", "Elegance"}, new String[] {"Otekai", "Keungeuh"}),
    new RawWeapon("sinister magic", "Sinister magic steals strength from terrible demons, then redirects the fiendish punishment for that theft onto others.", '⚡', "Magic", "Repeat", 0, "SINISTER", "SINISTER", "Energy", "Energy", "Burst", "Straight", "Condemn", "Ricochet", "Slay", "Curse", 1, 9, 0, 2, 4, 0, 0, 4, 2, 2, 2, new String[] {}, new String[] {"Sorcery", "Swordplay"}, new String[] {"Mobyuld", "Hueztotli"}),
    new RawWeapon("divine magic", "Divine magic channels the power of a god to unleash awesome vengeance on heretics.", '⚡', "Magic", "Repeat", 0, "DIVINE", "DIVINE", "Energy", "Energy", "Burst", "Arc", "Exorcise", "Shine", "Punish", "Regenerate", 2, 6, 0, 3, 0, 3, 4, 0, 3, 2, 2, new String[] {}, new String[] {"Sorcery", "Elegance"}, new String[] {"Bididhayi", "Hyrden"}),
    new RawWeapon("gravity magic", "Gravity magic is tricky and can be an excellent way to immobilize an enemy, but gravity doesn't need to pull downwards...", '⚡', "Magic", "Repeat", 0, "GRAVITY", "GRAVITY", "Energy", "Energy", "Burst", "Straight", "Pin", "Yank", "Trip", "Disarm", 3, 1, 0, 5, 3, 0, 2, 4, 3, 2, 2, new String[] {}, new String[] {"Sorcery", "Whirling Death"}, new String[] {"Jalgeaux", "Hidzaajji"}),
    new RawWeapon("vacuous magic", "Vacuous magic deals with a peculiar subject: regions that contain no other elements, such as the void of space.", '⚡', "Magic", "Repeat", 0, "VACUOUS", "VACUOUS", "Energy", "Energy", "Burst", "Arc", "Yank", "Reveal", "Silence", "Disarm", 1, 4, 0, 4, 2, 0, 0, 4, 3, 3, 3, new String[] {}, new String[] {"Sorcery", "Assassination"}, new String[] {"Chobesh", "Cheuritae"}),
    new RawWeapon("warp magic", "Warp magic is a skill in high demand from criminals and police alike, since it can move objects or people instantly.", '⚡', "Magic", "Repeat", 0, "WARP", "WARP", "Energy", "Energy", "Through", "Straight", "Yank", "Seal", "Disarm", "Confound", 2, 1, 0, 3, 6, 0, 0, 4, 5, 2, 0, new String[] {}, new String[] {"Sorcery", "Whirling Death"}, new String[] {"Ninja", "Ikkutiq"}),
    new RawWeapon("radiation magic", "Radiation magic isn't well-understood by common folk, and its users are generally feared for the illnesses that follow them.", '⚡', "Magic", "Repeat", 0, "RADIATION", "RADIATION", "Energy", "Energy", "Beam", "Straight", "Shine", "Exorcise", "Irradiate", "Afflict", 4, 6, 0, 5, 0, 0, 0, 4, 4, 1, 1, new String[] {}, new String[] {"Sorcery", "Marksmanship"}, new String[] {"Zedmedov", "Khainghal"}),
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
  ENTRIES[34], "macahuitl", ENTRIES[35], "bladed boots", ENTRIES[36],
  "shortbow", ENTRIES[37], "longbow", ENTRIES[38], "light crosssbow",
  ENTRIES[39], "heavy crossbow", ENTRIES[40], "pistol", ENTRIES[41],
  "musket", ENTRIES[42], "sling", ENTRIES[43], "staff sling", ENTRIES[44],
  "chakram", ENTRIES[45], "boomerang", ENTRIES[46], "javelin", ENTRIES[47],
  "atlatl", ENTRIES[48], "shuriken", ENTRIES[49], "blowgun", ENTRIES[50],
  "bolas", ENTRIES[51], "net", ENTRIES[52], "grenade", ENTRIES[53], "handcannon",
  ENTRIES[54], "throwing dagger", ENTRIES[55], "channeling orb", ENTRIES[56],
  "mystic deck", ENTRIES[57], "soul pyramid", ENTRIES[58], "dancing fan",
  ENTRIES[59], "fossil fang", ENTRIES[60], "green wreath", ENTRIES[61],
  "primeval shield", ENTRIES[62], "pan pipes", ENTRIES[63], "holy symbol",
  ENTRIES[64], "sacred book", ENTRIES[65], "prayer beads", ENTRIES[66],
  "cherished icon", ENTRIES[67], "vile grimoire", ENTRIES[68], "black lantern",
  ENTRIES[69], "eldritch prism", ENTRIES[70], "fractal knot", ENTRIES[71],
  "scholarly tome", ENTRIES[72], "magus scepter", ENTRIES[73], "wizardly wand",
  ENTRIES[74], "stargazer lens", ENTRIES[75], "Zedmedov style", ENTRIES[76],
  "Cheuritae style", ENTRIES[77], "Hyrden style", ENTRIES[78], "Mbegonda style",
  ENTRIES[79], "Ninjutsu style", ENTRIES[80], "Jalgeaux style",
  ENTRIES[81], "Ikkutiq style", ENTRIES[82], "Ugexiir style", ENTRIES[83],
  "Hidzaajji style", ENTRIES[84], "Laathrik style", ENTRIES[85], "Hueztotli style",
  ENTRIES[86], "Keungeuh style", ENTRIES[87], "Khainghal style",
  ENTRIES[88], "Bididhayi style", ENTRIES[89], "Mobyuld style",
  ENTRIES[90], "Chobesh style", ENTRIES[91], "Elethian style", ENTRIES[92],
  "Krort style", ENTRIES[93], "fire magic", ENTRIES[94], "earth magic",
  ENTRIES[95], "water magic", ENTRIES[96], "air magic", ENTRIES[97],
  "crystal magic", ENTRIES[98], "sound magic", ENTRIES[99], "ice magic",
  ENTRIES[100], "lightning magic", ENTRIES[101], "poison magic", ENTRIES[102],
  "acid magic", ENTRIES[103], "caustic magic", ENTRIES[104], "disease magic",
  ENTRIES[105], "death magic", ENTRIES[106], "life magic", ENTRIES[107],
  "shining magic", ENTRIES[108], "shadow magic", ENTRIES[109], "temporal magic",
  ENTRIES[110], "fateful magic", ENTRIES[111], "pure magic", ENTRIES[112],
  "primal magic", ENTRIES[113], "contractual magic", ENTRIES[114], "sinister magic",
  ENTRIES[115], "divine magic", ENTRIES[116], "gravity magic", ENTRIES[117],
  "vacuous magic", ENTRIES[118], "warp magic", ENTRIES[119], "radiation magic",
  ENTRIES[120]);

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

  public double prepare;

  public String[] materials;

  public String[] training;

  public String[] culture;

  public RawWeapon() {
  }

  public RawWeapon(String name, String description, char glyph, String kind, String usage,
      int hands, String type1, String type2, String group1, String group2, String shape,
      String path, String maneuver1, String maneuver2, String status1, String status2,
      double precision, double damage, double crit, double influence, double evasion,
      double defense, double luck, double stealth, double range, double area, double prepare,
      String[] materials, String[] training, String[] culture) {
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
    this.training = training;
    this.culture = culture;
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
    result += (a ^= 0x8329C6EB9E6AD3E3L * NumberTools.doubleToLongBits(prepare));
    innerR = 0x9E3779B97F4A7C94L;
    innerA = 0x632BE59BD9B4E019L;
    len = (materials == null ? 0 : materials.length);
    for (int i = 0; i < len; i++) innerR += (innerA ^= 0x8329C6EB9E6AD3E3L * hash64(materials[i]));
    a += innerA;
    result ^= innerR * (innerA | 1L) ^ (innerR >>> 27 | innerR << 37);
    innerR = 0x9E3779B97F4A7C94L;
    innerA = 0x632BE59BD9B4E019L;
    len = (training == null ? 0 : training.length);
    for (int i = 0; i < len; i++) innerR += (innerA ^= 0x8329C6EB9E6AD3E3L * hash64(training[i]));
    a += innerA;
    result ^= innerR * (innerA | 1L) ^ (innerR >>> 27 | innerR << 37);
    innerR = 0x9E3779B97F4A7C94L;
    innerA = 0x632BE59BD9B4E019L;
    len = (culture == null ? 0 : culture.length);
    for (int i = 0; i < len; i++) innerR += (innerA ^= 0x8329C6EB9E6AD3E3L * hash64(culture[i]));
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
    RawWeapon other = (RawWeapon) o;
    if (name != null ? !name.equals(other.name) : other.name != null) return false;
    if (description != null ? !description.equals(other.description) : other.description != null) return false;
    if (glyph != other.glyph) return false;
    if (kind != null ? !kind.equals(other.kind) : other.kind != null) return false;
    if (usage != null ? !usage.equals(other.usage) : other.usage != null) return false;
    if (hands != other.hands) return false;
    if (type1 != null ? !type1.equals(other.type1) : other.type1 != null) return false;
    if (type2 != null ? !type2.equals(other.type2) : other.type2 != null) return false;
    if (group1 != null ? !group1.equals(other.group1) : other.group1 != null) return false;
    if (group2 != null ? !group2.equals(other.group2) : other.group2 != null) return false;
    if (shape != null ? !shape.equals(other.shape) : other.shape != null) return false;
    if (path != null ? !path.equals(other.path) : other.path != null) return false;
    if (maneuver1 != null ? !maneuver1.equals(other.maneuver1) : other.maneuver1 != null) return false;
    if (maneuver2 != null ? !maneuver2.equals(other.maneuver2) : other.maneuver2 != null) return false;
    if (status1 != null ? !status1.equals(other.status1) : other.status1 != null) return false;
    if (status2 != null ? !status2.equals(other.status2) : other.status2 != null) return false;
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
    if (prepare != other.prepare) return false;
    if(!stringArrayEquals(materials, other.materials)) return false;
    if(!stringArrayEquals(training, other.training)) return false;
    if(!stringArrayEquals(culture, other.culture)) return false;
    return true;
  }

  public static RawWeapon get(String item) {
    return MAPPING.get(item);
  }
}
