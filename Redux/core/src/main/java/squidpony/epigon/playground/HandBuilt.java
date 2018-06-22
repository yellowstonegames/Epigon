package squidpony.epigon.playground;

import squidpony.Maker;
import squidpony.epigon.ConstantKey;
import squidpony.epigon.Epigon;
import squidpony.epigon.GauntRNG;
import squidpony.epigon.Radiance;
import squidpony.epigon.data.*;
import squidpony.epigon.data.quality.Cloth;
import squidpony.epigon.data.RecipeBlueprint;
import squidpony.epigon.data.quality.Element;
import squidpony.epigon.data.slot.ClothingSlot;
import squidpony.epigon.data.trait.Creature;
import squidpony.epigon.data.trait.Grouping;
import squidpony.epigon.data.trait.Interactable;
import squidpony.epigon.data.trait.Profession;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidmath.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import static squidpony.epigon.Epigon.rootChaos;
import static squidpony.epigon.data.Physical.basePhysical;

/**
 * Contains objects to use to test out connections.
 * TODO: Too much of this is currently critical to the game, and should not be in the playground package. 
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

    public Physical upStairBlueprint, downStairBlueprint;
//    public Physical baseUpStair;
//    public Physical baseDownStair;

    public Recipe upStairRecipe;
    public Recipe downStairRecipe;


    public Physical playerBlueprint;
    public Radiance playerRadiance;

    public Recipe hatRecipe;
    public Recipe shirtRecipe;
    public Recipe pantsRecipe;

    public Recipe swordRecipe;

    public Modification makeWall;
    public Physical emptySpace;

    public Physical nan;//trade currency (dust that's used for enchanting things and casting spells)
    public Physical money;

    public Physical baseFood; // base item for anything edible

    public Physical rawMeat; // base item for dead animal chunks
    public Recipe steakRecipe;

    // Cooking skills
//    public Skill cooking = new Skill("cooking");
//    public Skill baking = new Skill("baking", cooking);
//    public Skill frying = new Skill("frying", cooking);
//    public Skill boiling = new Skill("boiling", cooking);
//    public Skill foodPrep = new Skill("food prep", cooking);
//    public Skill foodChopping = new Skill("food chopping", foodPrep);
//    public Skill foodMixing = new Skill("food mixing", foodPrep);
//    public Skill canning = new Skill("canning", cooking);
//    public Skill foodDrying = new Skill("food drying", cooking);
//
//    // Gathering skills
//    public Skill gathering = new Skill("gathering");
//    public Skill butchering = new Skill("butchering", gathering);
//    public Skill farming = new Skill("farming", gathering);
//    public Skill fishing = new Skill("fishing", gathering);
//    public Skill herbalism = new Skill("herbalism", gathering);
//    public Skill hunting = new Skill("hunting", gathering);
//    public Skill mining = new Skill("mining", gathering);
//    public Skill woodcutting = new Skill("wood cutting", gathering);
//    public Skill treeFellingAx = new Skill("tree felling (ax)", gathering);
//    public Skill treeFellingSaw = new Skill("tree felling (saw)", gathering);
//
//    // Base combat skills - NOTE: when shown, combat skills should indicate that they are combat oriented (so "fan" is clear that it's fighting with fans)
//    public Skill combat = new Skill("combat");
//    public Skill armedCombat = new Skill("armed combat", combat);
//    public Skill unarmedCombat = new Skill("unarmed combat", combat);
//    public Skill combatDefense = new Skill("combat defense", combat);
//
//    // Armed combat skills
//    public Skill ax = new Skill("ax", armedCombat);
//    public Skill smallAx = new Skill("ax (small)", ax);
//    public Skill largeAx = new Skill("ax (large)", ax);
//    public Skill fist = new Skill("fist", unarmedCombat);
//    public Skill fan = new Skill("fan", fist);
//    public Skill glove = new Skill("glove", fist);
//    public Skill knuckle = new Skill("knuckle", fist); // TODO - this might just be punch (why did I have them both on the design doc?)
//    public Skill punchBlade = new Skill("punch blade", fist);
//    public Skill flexible = new Skill("flexible", armedCombat);
//    public Skill whip = new Skill("whip", flexible);
//    public Skill hammer = new Skill("hammer", armedCombat);
//    public Skill smallClub = new Skill("club (small)", hammer);

    public Ability unarmedStrike;
    public Ability armedStrike;
    public Ability cookSteak;

    public Profession chef;

    public HandBuilt()
    {
        this(new RecipeMixer());
    }

    public HandBuilt(RecipeMixer mixer) {
//        this.rng = rng.copy();
        chaos = rootChaos.nextLong();
        this.rng = new StatefulRNG(new LinnormRNG(chaos));
        this.mixer = mixer;
        baseOpenDoor = new Physical();
        baseClosedDoor = new Physical();

        nan = new Physical();
        nan.name = "nan";
        nan.description = "currency of power";
        nan.color = SColor.DB_PLATINUM.toFloatBits();
        nan.symbol = 'ᶯ';
        nan.blocking = false;

        money = new Physical();
        money.name = "Gold Coin";
        money.color = SColor.CW_GOLD.toFloatBits();
        money.symbol = '$';
        money.blocking = false;
        money.groupingData = new Grouping(1);
        
        baseFood = new Physical();
        baseFood.name = "fūd";
        baseFood.description = "base food item";
        baseFood.symbol = '℉';
        baseFood.color = SColor.AMBER_DYE.toFloatBits();

        rawMeat = new Physical();
        rawMeat.name = "meat";
        rawMeat.description = "chunk of something";
        rawMeat.symbol = 'ₘ';
        rawMeat.color = SColor.DB_FAWN.toFloatBits();

        initAbilities();
        initProfessions();
        initItems();
        initPlayer();
        initDoors();
        initStairs();

        makeWall = new Modification();
        Collections.addAll(makeWall.possiblePrefix, "solid", "shaped");
        makeWall.possibleSuffix.add("wall");
        makeWall.symbol = '#';
        makeWall.large = true;
        makeWall.attached = true;

        emptySpace = new Physical();
        emptySpace.name = "ø";
        emptySpace.symbol = ' ';
        emptySpace.color = SColor.TRANSPARENT.toFloatBits();
    }

    private void initAbilities() {
        cookSteak = new Ability();
        cookSteak.name = "cook steak";
        cookSteak.maxTargets = 1;
        cookSteak.mustHaveSkillRatings.put(Skill.COOKING, Rating.TYPICAL);
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
        chef.initialStatRequirements.put(CalcStat.PRECISION, 1.0);
        chef.initialStatRequirements.put(CalcStat.INFLUENCE, 2.0);
        chef.initialStatRequirements.put(CalcStat.QUICKNESS, 1.0);
        chef.initialSkillRequirements.put(Skill.COOKING, Rating.SLIGHT);

        Modification mod = new Modification();
        mod.skillChanges.put(Skill.COOKING, rvmSkill(Rating.GOOD));
        mod.skillChanges.put(Skill.BAKING, rvmSkill(Rating.SLIGHT));
        mod.skillChanges.put(Skill.FRYING, rvmSkill(Rating.SLIGHT));
        mod.skillChanges.put(Skill.BOILING, rvmSkill(Rating.SLIGHT));
        mod.skillChanges.put(Skill.FOOD_PREP, rvmSkill(Rating.SLIGHT));
        mod.skillChanges.put(Skill.FOOD_CHOPPING, rvmSkill(Rating.SLIGHT));
        mod.skillChanges.put(Skill.FOOD_MIXING, rvmSkill(Rating.SLIGHT));
        mod.abilitiesAdditive = new ArrayList<>();
        mod.abilitiesAdditive.add(cookSteak);

        mod.name = "chef slight";
        chef.improvements.put(Rating.SLIGHT, mod);

        mod = new Modification();
        mod.skillChanges.put(Skill.BAKING, rvmSkill(1, Rating.HIGH));
        mod.skillChanges.put(Skill.FOOD_CHOPPING, rvmSkill(1, Rating.SUPERB));
        mod.skillChanges.put(Skill.FOOD_MIXING, rvmSkill(1, Rating.HIGH));

        mod.name = "chef typical";
        chef.improvements.put(Rating.TYPICAL, mod);
    }

    private void initPlayer() {
        playerBlueprint = new Physical();
        playerBlueprint.name = "Plae Haa";
        playerBlueprint.description = "It's you!";
        //playerBlueprint.notes = "Voted most likely to die in Adventurer's Middle School.";
        playerBlueprint.symbol = '@';
        playerBlueprint.color = playerBlueprint.getRandomElement(SColor.COLOR_WHEEL_PALETTE_BRIGHT).toFloatBits();
        playerBlueprint.blocking = true;
        playerBlueprint.unique = true;
        playerBlueprint.attached = true;
        playerBlueprint.possibleAliases = Maker.makeList("Mario", "Link", "Sam");

        Rating[] ratingChoices = new Rating[]{Rating.SLIGHT, Rating.TYPICAL, Rating.GOOD, Rating.HIGH};
        for (ConstantKey s : CalcStat.all) {
            Rating rating = rng.getRandomElement(ratingChoices);
            LiveValue lv = new LiveValue(Formula.randomizedStartingStatLevel(rng));
            playerBlueprint.stats.put(s, lv);
            playerBlueprint.statProgression.put(s, rating);
        }
        for (ConstantKey s : Stat.healths) {
            Rating rating = rng.getRandomElement(ratingChoices);
            LiveValue lv = new LiveValue(Formula.healthForLevel(1, rating));
            playerBlueprint.stats.put(s, lv);
            playerBlueprint.statProgression.put(s, rating);
        }
        for (ConstantKey s : Stat.needs) {
            Rating rating = rng.getRandomElement(ratingChoices);
            LiveValue lv = new LiveValue(Formula.needForLevel(1, rating));
            playerBlueprint.stats.put(s, lv);
            playerBlueprint.statProgression.put(s, rating);
        }
        for (ConstantKey s : Stat.senses) {
            Rating rating = Rating.NONE;
            LiveValue lv = new LiveValue(9);
            playerBlueprint.stats.put(s, lv);
            playerBlueprint.statProgression.put(s, rating);
        }
        for (ConstantKey s : Stat.utilities) {
            Rating rating = rng.getRandomElement(ratingChoices);
            playerBlueprint.stats.put(s, new LiveValue(100));
            playerBlueprint.statProgression.put(s, rating);
        }
        playerRadiance = new Radiance((float) playerBlueprint.stats.get(Stat.SIGHT).actual(), SColor.CREAM.toFloatBits(), 0.8f, 0f);
        Creature cb = new Creature();
        playerBlueprint.creatureData = cb;

        // Put on some clothes
        Physical hat = RecipeMixer.mix(hatRecipe, Cloth.LINEN).get(0);
        hat.rarity = Rating.SUPERB;
        hat.color = SColor.DARK_SPRING_GREEN.toFloatBits();
        cb.equippedBySlot.put(ClothingSlot.HEAD, hat);
        Physical shirt = RecipeMixer.mix(shirtRecipe, Cloth.VELVET).get(0);
        shirt.rarity = Rating.TYPICAL;
        shirt.color = SColor.CW_FADED_JADE.toFloatBits();
        cb.equippedBySlot.put(ClothingSlot.TORSO, shirt);
        cb.equippedBySlot.put(ClothingSlot.LEFT_SHOULDER, shirt);
        cb.equippedBySlot.put(ClothingSlot.RIGHT_SHOULDER, shirt);
        cb.equippedBySlot.put(ClothingSlot.LEFT_UPPER_ARM, shirt);
        cb.equippedBySlot.put(ClothingSlot.RIGHT_UPPER_ARM, shirt);
        Physical pants = RecipeMixer.mix(pantsRecipe, Cloth.DENIM).get(0); // jeans, why not
        pants.rarity = Rating.SLIGHT;
        pants.color = SColor.CW_DRAB_AZURE.toFloatBits(); // blue jeans
        cb.equippedBySlot.put(ClothingSlot.WAIST, pants);
        cb.equippedBySlot.put(ClothingSlot.LEFT_LEG, pants);
        cb.equippedBySlot.put(ClothingSlot.RIGHT_LEG, pants);

        cb.skills = new OrderedMap<>();
        int[] ordering = rng.randomOrdering(Skill.combatSkills.size());
        for (int i = 0; i < 5; i++) {
            cb.skills.put(Skill.combatSkills.keyAt(ordering[i]), Rating.allRatings[i+1]);
        }

        // make sure the player has prereqs for chef
        for (Entry<ConstantKey, Double> entry : chef.initialStatRequirements.entrySet()) {
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

        cb.skills.put(Skill.COOKING, Rating.TYPICAL);
        Weapon unarmed = Weapon.randomUnarmedWeapon(playerBlueprint).copy();
        //Weapon unarmed = Weapon.getUnarmedWeapons().get("fire magic").copy();
        playerBlueprint.creatureData.weaponChoices = new ProbabilityTable<>(++chaos);
        playerBlueprint.creatureData.weaponChoices.add(unarmed, 1);
        String culture = playerBlueprint.getRandomElement(unarmed.rawWeapon.culture);
        List<Weapon> possibleItems = rng.shuffle(Weapon.cultures.get(culture));
        for (int i = 0; i < 3 && i < possibleItems.size(); i++) {
            playerBlueprint.inventory.add(RecipeMixer.buildWeapon(possibleItems.get(i).copy(), playerBlueprint));
        }
        // and one weapon from some other group
        playerBlueprint.inventory.add(RecipeMixer.buildWeapon(Weapon.randomPhysicalWeapon(playerBlueprint).copy(), playerBlueprint));
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
        RecipeMixer.applyModification(doorBlueprint, closeDoor);

        RecipeBlueprint doorRecipeBlueprint;
        doorRecipeBlueprint = new RecipeBlueprint();
        doorRecipeBlueprint.requiredCatalyst.put(basePhysical, 1);
        doorRecipeBlueprint.result.put(doorBlueprint, 1);

        doorRecipe = RecipeMixer.createRecipe(doorRecipeBlueprint);
    }

    private void initStairs() {
        upStairBlueprint = new Physical();
        upStairBlueprint.name = "stairs up";
        upStairBlueprint.symbol = '≤';
        upStairBlueprint.color = SColor.GOLDEN.toRandomizedFloat(rng, 0.05f, 0f, 0.15f);
        upStairBlueprint.radiance = new Radiance(1.9f, SColor.CREAM.toFloatBits(), 0f, 0f);
        upStairBlueprint.generic = true;
        upStairBlueprint.attached = true;
        upStairBlueprint.blocking = false;

        RecipeBlueprint upStairRecipeBlueprint;
        upStairRecipeBlueprint = new RecipeBlueprint();
        upStairRecipeBlueprint.requiredCatalyst.put(basePhysical, 1);
        upStairRecipeBlueprint.result.put(upStairBlueprint, 1);
        upStairRecipe = RecipeMixer.createRecipe(upStairRecipeBlueprint);

        downStairBlueprint = new Physical();
        downStairBlueprint.name = "stairs down";
        downStairBlueprint.symbol = '≥';
        downStairBlueprint.color = SColor.GOLDEN_FALLEN_LEAVES.toRandomizedFloat(rng, 0.08f, 0.04f, 0.1f);
        downStairBlueprint.generic = true;
        downStairBlueprint.attached = true;
        downStairBlueprint.blocking = false;
        
        RecipeBlueprint downStairRecipeBlueprint;
        downStairRecipeBlueprint = new RecipeBlueprint();
        downStairRecipeBlueprint.requiredCatalyst.put(basePhysical, 1);
        downStairRecipeBlueprint.result.put(downStairBlueprint, 1);
        downStairRecipe = RecipeMixer.createRecipe(downStairRecipeBlueprint);

    }

    private void initItems() {
        swordRecipe = createSimpleRecipe("sword", SColor.SILVER.toRandomizedFloat(rng, 0.1f, 0f, 0.2f), '(');
        hatRecipe = createSimpleRecipe("hat", SColor.CHERRY_BLOSSOM.toFloatBits(), 'ʍ');
        shirtRecipe = createSimpleRecipe("shirt", SColor.BRASS.toFloatBits(), 'τ');
        pantsRecipe = createSimpleRecipe("pants", SColor.PINE_GREEN.toFloatBits(), '∏');

        // Steak
        Physical pb = new Physical();
        pb.name = "steak";
        pb.symbol = 'ᴤ';
        pb.color = SColor.DB_MUD.toFloatBits();
        pb.countsAs.add(baseFood);

        Modification hungerUp = new Modification();
        LiveValueModification lvm = LiveValueModification.add(20);
        hungerUp.statChanges.put(Stat.HUNGER, lvm);

        Interactable eat = new Interactable();
        eat.phrasing = "eat";
        eat.actorModifications = Maker.makeList(hungerUp);
        eat.consumes = true;
        pb.interactableData = new ArrayList<>();
        pb.interactableData.add(eat);

        RecipeBlueprint rb = new RecipeBlueprint();
        rb.requiredConsumed.put(rawMeat, 1);
        rb.result.put(pb, 1);
        steakRecipe = RecipeMixer.createRecipe(rb);
    }

    private Recipe createSimpleRecipe(String name, float color, char symbol){
        Physical blueprint = new Physical();
        blueprint.name = name;
        blueprint.color = color;
        blueprint.symbol = symbol;

        RecipeBlueprint recipeBlueprint = new RecipeBlueprint();
        recipeBlueprint.requiredConsumed.put(basePhysical, 1);
        recipeBlueprint.result.put(blueprint, 1);
        return RecipeMixer.createRecipe(recipeBlueprint);
    }

    public Modification makeAlive() {
        Modification liven = new Modification();
        liven.possiblePrefix = Arrays.asList("living", "animated");
        liven.symbol = ((char)('s' | Epigon.BOLD | Epigon.ITALIC));
        liven.large = true;

        for(ConstantKey s : CalcStat.all) {
            LiveValueModification lvm = new LiveValueModification((rng.next(2) + rng.next(2) + rng.next(1)) + 2); // 0-3 + 0-3 + 0-1 == 0-7 biased centrally
            liven.statChanges.put(s, lvm);
        }
        for(ConstantKey s : Stat.healths) {
            LiveValueModification lvm = new LiveValueModification(NumberTools.formCurvedFloat(rng.nextLong()) * 8 + 20); // 12 to 28, biased on 20
            liven.statChanges.put(s, lvm);
        }
        liven.statChanges.put(Stat.MOBILITY, new LiveValueModification(100));
        liven.statChanges.put(Stat.SIGHT, new LiveValueModification(9));
        liven.statChanges.put(Stat.SANITY, new LiveValueModification(50));
        liven.creature = new Creature();
        liven.creature.weaponChoices = new ProbabilityTable<>(++chaos);
        //liven.creature.weaponChoices.add(Weapon.getWeapons().get("acid magic"), 4);
        liven.creature.weaponChoices.add(Weapon.randomWeapon(++chaos), 4);
        liven.weaponElementsAdditive = OrderedMap.makeMap(GauntRNG.getRandomElement(++chaos, Element.allDamage), 4.0, GauntRNG.getRandomElement(++chaos, Element.allDamage), 8.0);
        return liven;
    }
    
    public Modification makeMeats(){
        Modification meaten = new Modification();
        meaten.possibleSuffix = Collections.singletonList("meat");
        meaten.countsAs = Collections.singleton(rawMeat);
        meaten.symbol = 'ₘ';
        meaten.large = false;
        meaten.removeCreature = true;
        meaten.statChanges.put(Stat.MOBILITY, new LiveValueModification(0));
        meaten.statChanges.put(Stat.SIGHT, new LiveValueModification(0));
        meaten.quantity = rng.between(1, 3);
        return meaten;
    }
}
