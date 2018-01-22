package squidpony.epigon.playground;

import squidpony.Maker;
import squidpony.epigon.Epigon;
import squidpony.epigon.GauntRNG;
import squidpony.epigon.data.blueprint.Cloth;
import squidpony.epigon.data.blueprint.RecipeBlueprint;
import squidpony.epigon.data.generic.Ability;
import squidpony.epigon.data.generic.Formula;
import squidpony.epigon.data.generic.Modification;
import squidpony.epigon.data.generic.Skill;
import squidpony.epigon.data.mixin.Creature;
import squidpony.epigon.data.mixin.Profession;
import squidpony.epigon.data.specific.Physical;
import squidpony.epigon.data.specific.Recipe;
import squidpony.epigon.data.specific.Weapon;
import squidpony.epigon.dm.RecipeMixer;
import squidpony.epigon.universe.*;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidmath.NumberTools;
import squidpony.squidmath.OrderedMap;
import squidpony.squidmath.StatefulRNG;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import static squidpony.epigon.Epigon.rootChaos;
import static squidpony.epigon.data.specific.Physical.basePhysical;

/**
 * Contains objects to use to test out connections.
 */
public class HandBuilt {
    public StatefulRNG rng;
    public long chaos;
    public RecipeMixer mixer;

    public Physical doorBlueprint;
    public Physical baseOpenDoor;
    public Physical baseClosedDoor;

    public Recipe doorRecipe;
    public Modification openDoor;
    public Modification closeDoor;

    public Physical playerBlueprint;

    public Recipe hatRecipe;
    public Recipe shirtRecipe;
    public Recipe pantsRecipe;

    public Recipe swordRecipe;

    public Modification makeWall;

    public Physical nan;//trade currency (dust that's used for enchanting things and casting spells)

    public Physical rawMeat; // base item for dead animal chunks
    public Recipe steakRecipe;

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
    public Skill fist = new Skill("fist", unarmedCombat);
    public Skill fan = new Skill("fan", fist);
    public Skill glove = new Skill("glove", fist);
    public Skill knuckle = new Skill("knuckle", fist); // TODO - this might just be punch (why did I have them both on the design doc?)
    public Skill punchBlade = new Skill("punch blade", fist);
    public Skill flexible = new Skill("flexible", armedCombat);
    public Skill whip = new Skill("whip", flexible);
    public Skill hammer = new Skill("hammer", armedCombat);
    public Skill smallClub = new Skill("club (small)", hammer);

    public Ability unarmedStrike;
    public Ability armedStrike;
    public Ability cookSteak;

    public Profession chef;

    public HandBuilt()
    {
        this(new StatefulRNG(), new RecipeMixer());
    }

    public HandBuilt(StatefulRNG rng, RecipeMixer mixer) {
        this.rng = rng.copy();
        this.chaos = rootChaos.nextLong();
        this.mixer = mixer;
        baseOpenDoor = new Physical();
        baseClosedDoor = new Physical();
        nan = new Physical();
        nan.name = "nan";
        nan.description = "currency of power";
        rawMeat = new Physical();
        rawMeat.name = "meat";
        rawMeat.description = "chunk of something";
        rawMeat.symbol = '%';
        rawMeat.color = SColor.DB_FAWN.toFloatBits();

        initAbilities();
        initProfessions();
        initItems();
        initPlayer();
        initDoors();

        makeWall = new Modification();
        Collections.addAll(makeWall.possiblePrefix, "solid", "shaped");
        makeWall.possibleSuffix.add("wall");
        makeWall.symbol = '#';
        makeWall.large = true;
        makeWall.attached = true;
    }

    private void initAbilities() {
        cookSteak = new Ability();
        cookSteak.name = "cook steak";
        cookSteak.maxTargets = 1;
        cookSteak.mustHaveSkillRatings.put(cooking, Rating.TYPICAL);
        cookSteak.mustPossess = Collections.singletonList(Collections.singletonMap(rawMeat, 1));
        cookSteak.validTargets.add(rawMeat);
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
        mod.abiliitiesAdditive = new ArrayList<>();
        mod.abiliitiesAdditive.add(cookSteak);

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
        //playerBlueprint.notes = "Voted most likely to die in Adventurer's Middle School.";
        playerBlueprint.symbol = '@';
        playerBlueprint.color = SColor.BRIGHT_PINK.toFloatBits();
        playerBlueprint.blocking = true;
        playerBlueprint.unique = true;
        playerBlueprint.attached = true;
        playerBlueprint.possibleAliases = Maker.makeList("Mario", "Link", "Sam");

        Rating[] ratingChoices = new Rating[]{Rating.SLIGHT, Rating.TYPICAL, Rating.GOOD, Rating.HIGH};
        for (Stat s : Stat.bases) {
            Rating rating = rng.getRandomElement(ratingChoices);
            LiveValue lv = new LiveValue(Formula.randomizedStartingStatLevel(rng));
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

        // Put on some clothes
        Physical hat = mixer.mix(hatRecipe, Collections.singletonList(mixer.buildMaterial(Cloth.LINEN)), Collections.emptyList()).get(0);
        hat.rarity = Rating.SUPERB;
        hat.color = SColor.FOREST_GREEN.toFloatBits();
        cb.clothing.put(ClothingSlot.HEAD, hat);
        Physical shirt = mixer.mix(shirtRecipe, Collections.singletonList(mixer.buildMaterial(Cloth.VELVET)), Collections.emptyList()).get(0);
        shirt.rarity = Rating.TYPICAL;
        shirt.color = SColor.CW_PALE_JADE.toFloatBits();
        cb.clothing.put(ClothingSlot.TORSO, shirt);
        cb.clothing.put(ClothingSlot.LEFT_SHOULDER, shirt);
        cb.clothing.put(ClothingSlot.RIGHT_SHOULDER, shirt);
        cb.clothing.put(ClothingSlot.LEFT_UPPER_ARM, shirt);
        cb.clothing.put(ClothingSlot.RIGHT_UPPER_ARM, shirt);
        Physical pants = mixer.mix(pantsRecipe, Collections.singletonList(mixer.buildMaterial(Cloth.LEATHER)), Collections.emptyList()).get(0);
        pants.rarity = Rating.SLIGHT;
        pants.color = SColor.WISTERIA.toFloatBits();
        cb.clothing.put(ClothingSlot.WAIST, pants);
        cb.clothing.put(ClothingSlot.LEFT_UPPER_LEG, pants);
        cb.clothing.put(ClothingSlot.RIGHT_UPPER_LEG, pants);

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
        playerBlueprint.unarmedData = Weapon.randomUnarmedWeapon(++chaos).copy();
        playerBlueprint.weaponData = playerBlueprint.unarmedData.copy();
        String culture = GauntRNG.getRandomElement(++chaos, playerBlueprint.unarmedData.rawWeapon.culture);
        List<Weapon> possibleItems = rng.shuffle(Weapon.cultures.get(culture));
        for (int i = 0; i < 3 && i < possibleItems.size(); i++) {
            playerBlueprint.inventory.add(mixer.buildWeapon(possibleItems.get(i).copy(), ++chaos));
        }
        // and one weapon from some other group
        playerBlueprint.inventory.add(mixer.buildWeapon(Weapon.randomPhysicalWeapon(++chaos).copy(), chaos));
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
        swordRecipe = createSimpleRecipe("sword", SColor.SILVER.toRandomizedFloat(rng, 0.1f, 0f, 0.2f), '(');
        hatRecipe = createSimpleRecipe("hat", SColor.CHERRY_BLOSSOM.toFloatBits(), '^');
        shirtRecipe = createSimpleRecipe("shirt", SColor.BRASS.toFloatBits(), 'ҭ');
        pantsRecipe = createSimpleRecipe("pants", SColor.PINE_GREEN.toFloatBits(), ')');

        Physical pb = new Physical();
        pb.name = "steak";
        pb.symbol = '%';
        pb.color = SColor.DB_MUD.toFloatBits();
        RecipeBlueprint rb = new RecipeBlueprint();
        rb.requiredConsumed.put(rawMeat, 1);
        rb.result.put(pb, 1);
        steakRecipe = mixer.createRecipe(rb);
    }

    private Recipe createSimpleRecipe(String name, float color, char symbol){
        Physical blueprint = new Physical();
        blueprint.name = name;
        blueprint.color = color;
        blueprint.symbol = symbol;

        RecipeBlueprint recipeBlueprint = new RecipeBlueprint();
        recipeBlueprint.requiredConsumed.put(basePhysical, 1);
        recipeBlueprint.result.put(blueprint, 1);
        return mixer.createRecipe(recipeBlueprint);
    }

    public Modification makeAlive() {
        Modification liven = new Modification();
        liven.possiblePrefix = Arrays.asList("living", "animated");
        liven.symbol = 's' | Epigon.BOLD | Epigon.ITALIC;
        liven.large = true;
        for(Stat s : Stat.bases) {
            LiveValueModification lvm = new LiveValueModification((rng.minIntOf(28, 4) >> 2) + 1);
            liven.statChanges.put(s, lvm);
        }
        for(Stat s : Stat.healths) {
            LiveValueModification lvm = new LiveValueModification(NumberTools.formCurvedFloat(rng.nextLong()) * 6 + 18);
            liven.statChanges.put(s, lvm);
        }

        liven.statChanges.put(Stat.MOBILITY, new LiveValueModification(100));
        liven.statChanges.put(Stat.SIGHT, new LiveValueModification(9));
        liven.creature = new Creature();

        liven.weaponData = Weapon.randomWeapon(++chaos);
        liven.weaponElementsAdditive = OrderedMap.makeMap(GauntRNG.getRandomElement(++chaos, Element.allDamage), 1.0, GauntRNG.getRandomElement(++chaos, Element.allDamage), 2.0);
        return liven;
    }
    
    public Modification makeMeats(){
        Modification meaten = new Modification();
        meaten.possibleSuffix = Arrays.asList("meat");
        meaten.countsAs = Maker.makeUOS(rawMeat);
        meaten.symbol = '%';
        meaten.large = false;
        meaten.removeCreature = true;
        meaten.statChanges.put(Stat.MOBILITY, new LiveValueModification(0));
        meaten.statChanges.put(Stat.SIGHT, new LiveValueModification(0));
        meaten.quantity = rng.between(3, 12);
        return meaten;
    }
}
