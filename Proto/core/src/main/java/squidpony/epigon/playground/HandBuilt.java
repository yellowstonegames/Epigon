package squidpony.epigon.playground;

import squidpony.Maker;
import squidpony.epigon.Epigon;
import squidpony.epigon.data.blueprint.RecipeBlueprint;
import squidpony.epigon.data.generic.Ability;
import squidpony.epigon.data.generic.Formula;
import squidpony.epigon.data.generic.Modification;
import squidpony.epigon.data.generic.Skill;
import squidpony.epigon.data.mixin.Creature;
import squidpony.epigon.data.mixin.Profession;
import squidpony.epigon.data.mixin.Wieldable;
import squidpony.epigon.data.specific.Physical;
import squidpony.epigon.data.specific.Recipe;
import squidpony.epigon.data.specific.Weapon;
import squidpony.epigon.universe.*;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidmath.OrderedMap;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map.Entry;

import static squidpony.epigon.Epigon.*;
import static squidpony.epigon.data.specific.Physical.basePhysical;

/**
 * Contains objects to use to test out connections.
 */
public class HandBuilt {
    public Physical doorBlueprint;
    public Physical baseOpenDoor = new Physical();
    public Physical baseClosedDoor = new Physical();

    public Physical playerBlueprint;
    public Physical swordBlueprint;
    public Recipe swordRecipe;

    public Recipe doorRecipe;
    public Modification openDoor;
    public Modification closeDoor;

    public Modification makeWall;

    public Modification makeAlive;

    public Physical nan = new Physical();//trade currency (dust that's used for enchanting things and casting spells)

    // Cooking skills
    public Skill cooking = new Skill("cooking");
    public Skill baking = new Skill("baking", cooking);
    public Skill frying = new Skill("frying", cooking);
    public Skill boiling = new Skill("boiling", cooking);
    public Skill foodPrep = new Skill("food prep", cooking);
    public Skill foodChopping = new Skill("food chopping", foodPrep);
    public Skill foodMixing = new Skill("food mixing", foodPrep);
    public Skill canning = new Skill("canning", cooking);
    public Skill foodDrying = new Skill("food drying", cooking);

    // Gathering skills
    public Skill gathering = new Skill("gathering");
    public Skill butchering = new Skill("butchering", gathering);
    public Skill farming = new Skill("farming", gathering);
    public Skill fishing = new Skill("fishing", gathering);
    public Skill herbalism = new Skill("herbalism", gathering);
    public Skill hunting = new Skill("hunting", gathering);
    public Skill mining = new Skill("mining", gathering);
    public Skill woodcutting = new Skill("wood cutting", gathering);
    public Skill treeFellingAx = new Skill("tree felling (ax)", gathering);
    public Skill treeFellingSaw = new Skill("tree felling (saw)", gathering);

    // Base combat skills - NOTE: when shown, combat skills should indicate that they are combat oriented (so "fan" is clear that it's fighting with fans)
    public Skill combat = new Skill("combat");
    public Skill armedCombat = new Skill("armed combat", combat);
    public Skill unarmedCombat = new Skill("unarmed combat", combat);
    public Skill combatDefense = new Skill("combat defense", combat);

    // Armed combat skills
    public Skill ax = new Skill("ax", armedCombat);
    public Skill smallAx = new Skill("ax (small)", ax);
    public Skill largeAx = new Skill("ax (large)", ax);
    public Skill fist = new Skill("fist", armedCombat);
    public Skill fan = new Skill("fan", fist);
    public Skill glove = new Skill("glove", fist);
    public Skill knuckle = new Skill("knuckle", fist); // TODO - this might just be punch (why did I have them both on the design doc?)
    public Skill punchBlade = new Skill("punch blade", fist);
    public Skill flexible = new Skill("flexible", armedCombat);
    public Skill whip = new Skill("whip", flexible);
    public Skill hammer = new Skill("hammer", armedCombat);
    public Skill smallClub = new Skill("club (small)", hammer);

    public Ability cookSteak;

    public Profession chef;

    public HandBuilt() {
        initAbilities();
        initProfessions();
        initPlayer();
        initDoors();
        initItems();
        initAlive();

        makeWall = new Modification();
        Collections.addAll(makeWall.possiblePrefix, "solid", "shaped");
        makeWall.possiblePostfix.add("wall");
        makeWall.symbol = '#';
        makeWall.large = true;
        makeWall.attached = true;
    }

    private void initAbilities() {
        cookSteak = new Ability();
        cookSteak.name = "cook steak";
    }

    private static RatingValueModification rvmSkill(Rating rating)
    {
        RatingValueModification rvm = new RatingValueModification();
        rvm.overwriteIncrease = rating;
        return rvm;
    }

    private static RatingValueModification rvmSkill(Integer deltaLevel, Rating deltaMax)
    {
        RatingValueModification rvm = new RatingValueModification();
        rvm.deltaLevel = deltaLevel;
        rvm.deltaMax = deltaMax;
        return rvm;
    }

    private void initProfessions() {
        chef = new Profession();
        chef.name = "chef";
        chef.titlePrefix = "Chef";
        chef.initialStatRequirements.put(Stat.AIM, 1.0);
        chef.initialStatRequirements.put(Stat.CREATIVITY, 2.0);
        chef.initialStatRequirements.put(Stat.IMPACT, 1.0);
        chef.initialSkillRequirements.put(cooking, Rating.SLIGHT);

        Modification mod = new Modification();
        mod.skillChanges.put(cooking, rvmSkill(Rating.GOOD));
        mod.skillChanges.put(baking, rvmSkill(Rating.SLIGHT));
        mod.skillChanges.put(frying, rvmSkill(Rating.SLIGHT));
        mod.skillChanges.put(boiling, rvmSkill(Rating.SLIGHT));
        mod.skillChanges.put(foodPrep, rvmSkill(Rating.SLIGHT));
        mod.skillChanges.put(foodChopping, rvmSkill(Rating.SLIGHT));
        mod.skillChanges.put(foodMixing, rvmSkill(Rating.SLIGHT));

        mod.name = "chef slight";
        chef.improvements.put(Rating.SLIGHT, mod);

        mod = new Modification();
        mod.skillChanges.put(baking, rvmSkill(1, Rating.HIGH));
        mod.skillChanges.put(foodChopping, rvmSkill(1, Rating.SUPERB));
        mod.skillChanges.put(foodMixing, rvmSkill(1, Rating.HIGH));

        mod.name = "chef typical";
        chef.improvements.put(Rating.TYPICAL, mod);
    }

    private void initPlayer() {
        playerBlueprint = new Physical();
        playerBlueprint.name = "Plae Haa";
        playerBlueprint.description = "It's you!";
        playerBlueprint.notes = "Voted most likely to die in Adventurer's Middle School.";
        playerBlueprint.symbol = '@';
        playerBlueprint.color = SColor.BRIGHT_PINK.toFloatBits();
        playerBlueprint.blocking = true;
        playerBlueprint.unique = true;
        playerBlueprint.attached = true;
        playerBlueprint.possibleAliases = Maker.makeList("Mario", "Link", "Sam");

        Rating[] ratingChoices = new Rating[]{Rating.SLIGHT, Rating.TYPICAL, Rating.GOOD, Rating.HIGH};
        for (Stat s : Stat.bases) {
            Rating rating = rng.getRandomElement(ratingChoices);
            LiveValue lv = new LiveValue(Formula.randomizedStartingStatLevel());
            playerBlueprint.stats.put(s, lv);
            playerBlueprint.statProgression.put(s, rating);
        }
        for (Stat s : Stat.healths) {
            Rating rating = rng.getRandomElement(ratingChoices);
            LiveValue lv = new LiveValue(Formula.healthForLevel(1, rating));
            playerBlueprint.stats.put(s, lv);
            playerBlueprint.statProgression.put(s, rating);
        }
        for (Stat s : Stat.needs) {
            Rating rating = rng.getRandomElement(ratingChoices);
            LiveValue lv = new LiveValue(Formula.needForLevel(1, rating));
            playerBlueprint.stats.put(s, lv);
            playerBlueprint.statProgression.put(s, rating);
        }
        for (Stat s : Stat.senses) {
            Rating rating = Rating.GOOD;
            LiveValue lv = new LiveValue(Formula.senseForLevel(1, rating));
            playerBlueprint.stats.put(s, lv);
            playerBlueprint.statProgression.put(s, rating);
        }
        for (Stat s : Stat.utilities) {
            Rating rating = rng.getRandomElement(ratingChoices);
            playerBlueprint.stats.put(s, new LiveValue(100));
            playerBlueprint.statProgression.put(s, rating);
        }

        Creature cb = new Creature();
        playerBlueprint.creatureData = cb;
        cb.skills = new OrderedMap<>();
        cb.skills.put(unarmedCombat, Rating.HIGH);

        // make sure the player has prereqs for chef
        for (Entry<Stat, Double> entry : chef.initialStatRequirements.entrySet()) {
            if (playerBlueprint.stats.get(entry.getKey()).base() < entry.getValue()) {
                playerBlueprint.stats.get(entry.getKey()).base(entry.getValue());
            }
        }
        for (Entry<Skill, Rating> entry : chef.initialSkillRequirements.entrySet()) {
            Rating current = cb.skills.get(entry.getKey());
            if (current == null || current.lessThan(entry.getValue())) {
                cb.skills.replace(entry.getKey(), entry.getValue());
            }
        }

        cb.skills.put(cooking, Rating.TYPICAL);
        playerBlueprint.wieldableData = Wieldable.UNARMED;
        playerBlueprint.inventory.add(mixer.buildWeapon(Weapon.weapons.randomValue(chaos)));
        playerBlueprint.inventory.add(mixer.buildWeapon(Weapon.weapons.randomValue(chaos)));
        playerBlueprint.inventory.add(mixer.buildWeapon(Weapon.weapons.randomValue(chaos)));
        mixer.addProfession(chef, playerBlueprint);
    }

    private void initDoors() {
        openDoor = new Modification();
        openDoor.countsAsLost = Collections.singleton(baseClosedDoor);
        openDoor.countsAsGained = Collections.singleton(baseOpenDoor);
        openDoor.symbol = '/';
        openDoor.large = false;
        openDoor.statChanges.put(Stat.OPACITY, new LiveValueModification(0.0));

        closeDoor = new Modification();
        closeDoor.countsAsLost = Collections.singleton(baseOpenDoor);
        closeDoor.countsAsGained = Collections.singleton(baseClosedDoor);
        closeDoor.symbol = '+';
        closeDoor.large = true;
        closeDoor.statChanges.put(Stat.OPACITY, new LiveValueModification(1.0));

        doorBlueprint = new Physical();
        doorBlueprint.name = "door";
        doorBlueprint.color = SColor.WALNUT.toRandomizedFloat(rng, 0.05f, 0f, 0.15f);
        doorBlueprint.generic = true;
        doorBlueprint.attached = true;
        mixer.applyModification(doorBlueprint, closeDoor);

        RecipeBlueprint doorRecipeBlueprint;
        doorRecipeBlueprint = new RecipeBlueprint();
        doorRecipeBlueprint.requiredCatalyst.put(basePhysical, 1);
        doorRecipeBlueprint.result.put(doorBlueprint, 1);

        doorRecipe = mixer.createRecipe(doorRecipeBlueprint);
    }

    private void initItems() {
        swordBlueprint = new Physical();
        swordBlueprint.name = "sword";
        swordBlueprint.color = SColor.SILVER.toRandomizedFloat(rng, 0.1f, 0f, 0.2f);
        swordBlueprint.symbol = '(';

        RecipeBlueprint swordRecipeBlueprint = new RecipeBlueprint();
        swordRecipeBlueprint.requiredCatalyst.put(basePhysical, 1);
        swordRecipeBlueprint.result.put(swordBlueprint, 1);

        swordRecipe = mixer.createRecipe(swordRecipeBlueprint);
    }

    private void initAlive() {
        makeAlive = new Modification();
        makeAlive.possiblePrefix = Arrays.asList("living", "animated");
        makeAlive.symbol = 's' | Epigon.BOLD | Epigon.ITALIC;
        makeAlive.large = true;
        Arrays.stream(Stat.values()).forEach(s -> {
            LiveValueModification lvm = new LiveValueModification(rng.between(5, 19));
            makeAlive.statChanges.put(s, lvm);
        });
        makeAlive.statChanges.put(Stat.MOBILITY, new LiveValueModification(100));
        makeAlive.statChanges.put(Stat.SIGHT, new LiveValueModification(9));
        makeAlive.creatureOverwrite = new Creature();
        makeAlive.wieldableDamageOverwrite = rng.between(1, 4);
        makeAlive.wieldableHitChanceOverwrite = rng.betweenWeighted(20, 80, 3);
        makeAlive.wieldableDistanceOverwrite = 0;
        makeAlive.wieldableElementsOverwrite = Arrays.asList(Element.BLUNT, Element.BLUNT, Element.BLUNT,
                Weapon.elementRename.randomValue(rng), Weapon.elementRename.randomValue(rng));
    }
}
