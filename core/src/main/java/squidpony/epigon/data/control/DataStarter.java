package squidpony.epigon.data.control;

import com.badlogic.gdx.graphics.Color;

import squidpony.Maker;
import squidpony.epigon.ConstantKey;
import squidpony.epigon.Epigon;
import squidpony.epigon.GauntRNG;
import squidpony.epigon.data.*;
import squidpony.epigon.data.quality.Cloth;
import squidpony.epigon.data.quality.Element;
import squidpony.epigon.data.quality.Inclusion;
import squidpony.epigon.data.slot.ClothingSlot;
import squidpony.epigon.data.trait.*;
import squidpony.epigon.mapping.EpiMap;
import squidpony.epigon.mapping.EpiTile;
import squidpony.squidgrid.gui.gdx.Radiance;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidmath.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import static squidpony.epigon.Epigon.rootChaos;
import static squidpony.epigon.data.Physical.basePhysical;

/**
 * Contains objects to use to test out connections.
 */
public class DataStarter {

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
//    public Radiance playerRadiance;

    public Recipe hatRecipe;
    public Recipe shirtRecipe;
    public Recipe pantsRecipe;
    public Recipe glovesRecipe;

    public Recipe swordRecipe;

    public Modification makeWall;
    public Physical emptySpace;

    public Physical nan;//trade currency (dust that's used for enchanting things and casting spells)
    public Physical money;

    public Physical baseFood; // base item for anything edible

    public Physical rawMeat; // base item for dead animal chunks
    public Physical steak; // may need some larger storage for this stuff
//    public Recipe steakRecipe;

//    public Physical carrotOfTruth; // when eaten the player can see through walls for a while
    public Physical torch;

    public Physical lava;

    public Interactable eat;
    public Interactable cookSteak;

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
//    public Ability unarmedStrike;
//    public Ability armedStrike;
//    public Ability cookSteak;
    public Profession chef;
    public Physical water;
    public Physical mud;
    public Physical grass;
    public Physical shadedGrass;

    public DataStarter() {
        this(new RecipeMixer());
    }

    public DataStarter(RecipeMixer mixer) {
        chaos = rootChaos.nextLong();
        rng = new StatefulRNG(new DiverRNG(chaos));
        this.mixer = mixer;
        baseOpenDoor = Physical.makeBasic("base open door", '/', Color.CLEAR);
        baseClosedDoor = Physical.makeBasic("base closed door", '+', Color.CLEAR);

        nan = Physical.makeBasic("nan", 'ᶯ', SColor.DB_PLATINUM);
        nan.description = "currency of power";

        money = Physical.makeBasic("gold coin", '$', SColor.CW_GOLD);
        money.groupingData = new Grouping(1);

        baseFood = Physical.makeBasic("fūd", '℉', SColor.AMBER_DYE);
        rawMeat = Physical.makeBasic("meat", 'ₘ', SColor.DB_FAWN);
        steak = Physical.makeBasic("steak", 'ᴤ', SColor.DB_MUD);
        eat = new Interactable("eat", true, false, (actor, target, level) -> {
            actor.stats.get(Stat.NUTRITION).addActual(target.stats.getOrDefault(Stat.VIGOR, LiveValue.ONE).actual());
            return "@Name eat$ the " + target.name + ", and feel$ less hungry.";
        });

        cookSteak = new Interactable("cook steak", true, false, (actor, target, level) -> {
            actor.inventory.add(RecipeMixer.buildPhysical(steak));
            return "@Name cook$ the " + target.name + " into a steak.";
        });

        baseFood.description = "base food item";
        baseFood.interactableData = new ArrayList<>(1);
        baseFood.interactableData.add(eat);

        rawMeat.description = "chunk of something";
        rawMeat.stats.put(Stat.VIGOR, new LiveValue(2.0));
        rawMeat.interactableData = new ArrayList<>(2);
        rawMeat.interactableData.add(cookSteak);
        rawMeat.interactableData.add(eat);

        steak.countsAs.add(baseFood);
        steak.stats.put(Stat.VIGOR, new LiveValue(20.0));
        steak.interactableData = new ArrayList<>(1);
        steak.interactableData.add(eat);

        torch = Physical.makeBasic("torch", 'ῗ', SColor.CREAM);
        torch.description = "burning rags on a stick";
        torch.radiance = new Radiance(6f, SColor.CREAM.toFloatBits(), 0.71f, 0f);

        lava = Physical.makeBasic("lava", '£', SColor.CW_BRIGHT_ORANGE);
        lava.description = "molten stone";
        lava.radiance = new Radiance(1.8f, lava.color, 0.42f, 0f);
        lava.attached = true; // pick up the FLESH-SEARING MOLTEN CORE OF THE PLANET? n/n

        water = Physical.makeBasic("water", '~', SColor.AZUL);
        water.description = "shallow water";
        water.attached = true;
        water.blocking = false;
        water.terrainData = new Terrain();
        water.terrainData.background = SColor.translucentColor(SColor.LAPIS_LAZULI, 0.9f);
        water.terrainData.noise = new FastNoise(1234567, 0.03f, FastNoise.SIMPLEX_FRACTAL, 2);
        water.stats.put(Stat.OPACITY, LiveValue.ZERO);

        mud = Physical.makeBasic("mud", '≁', SColor.DISTANT_RIVER_BROWN);
        mud.description = "dirty slick mud fit for some wrestling";
        mud.attached = true;
        mud.blocking = false;
        mud.stats.put(Stat.OPACITY, LiveValue.ZERO);

        grass = Physical.makeBasic("grass", '¸', SColor.CW_RICH_GREEN);
        grass.description = "scraggly, patchy grass";
        grass.attached = true;
        grass.blocking = false;
        grass.stats.put(Stat.OPACITY, LiveValue.ZERO);

        shadedGrass = Physical.makeBasic("shaded grass", '¸', SColor.CW_DARK_GREEN);
        shadedGrass.description = "grass under the shade of a tree";
        shadedGrass.attached = true;
        shadedGrass.blocking = false;
        shadedGrass.stats.put(Stat.OPACITY, LiveValue.ZERO);

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

        emptySpace = Physical.makeBasic("∅", ' ', SColor.TRANSPARENT);
    }

    private void initAbilities() {
//        cookSteak = new Ability();
//        cookSteak.name = "cook steak";
//        cookSteak.maxTargets = 1;
//        cookSteak.mustHaveSkillRatings.put(Skill.COOKING, Rating.TYPICAL);
//        cookSteak.mustPossess = Maker.makeList(Collections.singletonMap(rawMeat, 1));
//        cookSteak.validTargets.add(rawMeat);
    }

    private static RatingValueModification rvmSkill(Rating rating) {
        RatingValueModification rvm = new RatingValueModification();
        rvm.overwriteIncrease = rating;
        return rvm;
    }

    private static RatingValueModification rvmSkill(Integer deltaLevel, Rating deltaMax) {
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
//        mod.abilitiesAdditive = new ArrayList<>();
//        mod.abilitiesAdditive.add(cookSteak);

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
            Rating rating = playerBlueprint.getRandomElement(ratingChoices);
            LiveValue lv = new LiveValue(Formula.randomizedStartingStatLevel(playerBlueprint));
            playerBlueprint.stats.put(s, lv);
            playerBlueprint.statProgression.put(s, rating);
        }
        playerBlueprint.stats.put(CalcStat.LUCK, new LiveValue(1));
        for (ConstantKey s : Stat.healths) {
            Rating rating = playerBlueprint.getRandomElement(ratingChoices);
            LiveValue lv = new LiveValue(Formula.healthForLevel(1, rating));
            playerBlueprint.stats.put(s, lv);
            playerBlueprint.statProgression.put(s, rating);
        }
        for (ConstantKey s : Stat.needs) {
            Rating rating = playerBlueprint.getRandomElement(ratingChoices);
            LiveValue lv = new LiveValue(Formula.needForLevel(1, rating));
            playerBlueprint.stats.put(s, lv);
            playerBlueprint.statProgression.put(s, rating);
        }
        for (ConstantKey s : Stat.senses) {
            Rating rating = Rating.NONE;
            LiveValue lv = new LiveValue(12);
            playerBlueprint.stats.put(s, lv);
            playerBlueprint.statProgression.put(s, rating);
        }
        for (ConstantKey s : Stat.utilities) {
            Rating rating = playerBlueprint.getRandomElement(ratingChoices);
            playerBlueprint.stats.put(s, new LiveValue(100));
            playerBlueprint.statProgression.put(s, rating);
        }

        Creature cb = new Creature();
        playerBlueprint.creatureData = cb;

        // Put on some clothes
        Physical hat = RecipeMixer.mix(hatRecipe, Cloth.LINEN).get(0);
        hat.rarity = Rating.SUPERB;
        playerBlueprint.equipItem(hat);

        Physical shirt = RecipeMixer.mix(shirtRecipe, Cloth.VELVET).get(0);
        playerBlueprint.inventory.add(shirt);
        shirt = RecipeMixer.mix(shirtRecipe, Cloth.LEATHER).get(0);
        playerBlueprint.equipItem(shirt);

        Physical pants = RecipeMixer.mix(pantsRecipe, Cloth.DENIM).get(0); // jeans, why not
        pants.rarity = Rating.SLIGHT;
        playerBlueprint.equipItem(pants);

        playerBlueprint.equipItem(RecipeMixer.mix(glovesRecipe, Cloth.LEATHER).get(0));

        cb.skills = new OrderedMap<>();
        int[] ordering = rng.randomOrdering(Skill.combatSkills.size());
        for (int i = 0; i < 5; i++) {
            cb.skills.put(Skill.combatSkills.keyAt(ordering[i]), Rating.allRatings[i + 1]);
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
//        Weapon unarmed = Weapon.getWeapons().get("sinister magic");
        Weapon unarmed = Weapon.randomUnarmedWeapon(playerBlueprint).copy();
        playerBlueprint.inventory.add(torch);

        //playerBlueprint.creatureData.lastUsedItem = torch;
        //Weapon unarmed = Weapon.getUnarmedWeapons().get("fire magic").copy();
        playerBlueprint.creatureData.weaponChoices = new ProbabilityTable<>(++chaos);
        playerBlueprint.creatureData.weaponChoices.add(unarmed, 1);
        String culture = playerBlueprint.getRandomElement(unarmed.rawWeapon.culture);
        System.out.println("Player's culture is " + culture);
        List<Weapon> possibleItems = rng.shuffle(Weapon.cultures.get(culture));
        playerBlueprint.inventory.add(RecipeMixer.applyModification(
            RecipeMixer.buildWeapon(possibleItems.get(0).copy(), playerBlueprint),
            beamWeaponModification()));
        for (int i = 1; i < 3 && i < possibleItems.size(); i++) {
            playerBlueprint.inventory.add(RecipeMixer.buildWeapon(possibleItems.get(i).copy(), playerBlueprint));
        }
        // and one weapon from some other group
        playerBlueprint.inventory.add(RecipeMixer.buildWeapon(Weapon.randomPhysicalWeapon(playerBlueprint).copy(), playerBlueprint));
        mixer.addProfession(chef, playerBlueprint);
    }

    private void initDoors() {
        openDoor = new Modification();
        openDoor.countsAsLost = Maker.makeUOS(baseClosedDoor);
        openDoor.countsAsGained = Maker.makeUOS(baseOpenDoor);
        openDoor.symbol = '/';
        openDoor.large = false;
        openDoor.statChanges.put(Stat.OPACITY, new LiveValueModification(0.0));

        closeDoor = new Modification();
        closeDoor.countsAsLost = Maker.makeUOS(baseOpenDoor);
        closeDoor.countsAsGained = Maker.makeUOS(baseClosedDoor);
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
        swordRecipe = createSimpleRecipe("sword", SColor.SILVER.toRandomizedFloat(rng, 0.1f, 0f, 0.2f), '†');

        Physical hatBlueprint = Physical.makeBasic("hat", 'ʍ', SColor.DARK_SPRING_GREEN);
        hatBlueprint.rarity = Rating.TYPICAL;
        hatBlueprint.wearableData = new Wearable();
        hatBlueprint.wearableData.slotsUsed.add(ClothingSlot.HEAD);
        hatRecipe = createBasicConsumptionRecipe(basePhysical, hatBlueprint);

        Physical shirtBlueprint = Physical.makeBasic("shirt", 'τ', SColor.BRASS);
        shirtBlueprint.rarity = Rating.TYPICAL;
        shirtBlueprint.wearableData = new Wearable();
        shirtBlueprint.wearableData.slotsUsed.addAll(Maker.makeList(
            ClothingSlot.TORSO,
            ClothingSlot.NECK,
            ClothingSlot.LEFT_SHOULDER,
            ClothingSlot.RIGHT_SHOULDER,
            ClothingSlot.LEFT_UPPER_ARM,
            ClothingSlot.RIGHT_UPPER_ARM
        ));
        shirtRecipe = createBasicConsumptionRecipe(basePhysical, shirtBlueprint);

        Physical pantsBlueprint = Physical.makeBasic("pants", '∏', SColor.PINE_GREEN);
        pantsBlueprint.rarity = Rating.TYPICAL;
        pantsBlueprint.wearableData = new Wearable();
        pantsBlueprint.wearableData.slotsUsed.addAll(Maker.makeList(
            ClothingSlot.WAIST,
            ClothingSlot.LEFT_LEG,
            ClothingSlot.RIGHT_LEG
        ));
        pantsRecipe = createBasicConsumptionRecipe(basePhysical, pantsBlueprint);

        // TODO - split into left and right versions
        Physical glovesBlueprint = Physical.makeBasic("gloves", '∏', SColor.MOUSY_WISTERIA);
        glovesBlueprint.rarity = Rating.TYPICAL;
        glovesBlueprint.wearableData = new Wearable();
        glovesBlueprint.wearableData.slotsUsed.addAll(Maker.makeList(
            ClothingSlot.LEFT_HAND,
            ClothingSlot.RIGHT_HAND
        ));
        glovesRecipe = createBasicConsumptionRecipe(basePhysical, glovesBlueprint);

//        Modification hungerUp = new Modification();
//        LiveValueModification lvm = LiveValueModification.add(20);
//        hungerUp.statChanges.put(Stat.NUTRITION, lvm);
//        RecipeBlueprint rb = new RecipeBlueprint();
//        rb.requiredConsumed.put(rawMeat, 1);
//        rb.result.put(steak, 1);
//        steakRecipe = RecipeMixer.createRecipe(rb);
    }

    private Recipe createSimpleRecipe(String name, float color, char symbol) {
        Physical blueprint = new Physical();
        blueprint.name = name;
        blueprint.color = color;
        blueprint.symbol = symbol;
        return createBasicConsumptionRecipe(basePhysical, blueprint);
    }

    private Recipe createBasicConsumptionRecipe(Physical input, Physical output) {
        RecipeBlueprint recipeBlueprint = new RecipeBlueprint();
        recipeBlueprint.requiredConsumed.put(input, 1);
        recipeBlueprint.result.put(output, 1);
        return RecipeMixer.createRecipe(recipeBlueprint);
    }

    public Modification makeAlive() {
        Modification liven = new Modification();
        liven.possiblePrefix = Maker.makeList("living", "animated");
        liven.symbol = ((char) ('s' | Epigon.BOLD | Epigon.ITALIC));
        liven.large = true;

        for (ConstantKey s : CalcStat.all) {
            LiveValueModification lvm = new LiveValueModification((rng.next(2) + rng.next(2) + rng.next(1)) + 2); // 0-3 + 0-3 + 0-1 == 0-7 biased centrally
            liven.statChanges.put(s, lvm);
        }
        for (ConstantKey s : Stat.healths) {
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

    public Modification makeMeats() {
        Modification meaten = new Modification();
        meaten.possibleSuffix = Maker.makeList("meat");
        meaten.countsAs = Maker.makeUOS(rawMeat);
        meaten.interactable = rawMeat.interactableData;
        meaten.symbol = 'ₘ';
        meaten.large = false;
        meaten.removeCreature = true;
        meaten.statChanges.put(Stat.MOBILITY, new LiveValueModification(0));
        meaten.statChanges.put(Stat.SIGHT, new LiveValueModification(0));
        meaten.quantity = rng.between(1, 3);
        return meaten;
    }

    public static Modification beamWeaponModification() {
        Modification mod = new Modification();
        mod.possiblePrefix.add("beam");
        mod.color = mod.getRandomElement(SColor.COLOR_WHEEL_PALETTE_BRIGHT);
        mod.radiance = new Radiance(5.8f, ((SColor) mod.color).toEditedFloat(0f, -0.1f, 0.3f, 0f), 0f, 2.1f);
        return mod;
    }

    /**
     * Sets the door to the open state, true means open and false means closed.
     *
     * @param open
     */
    public void setDoorOpen(Physical door, boolean open) {
        RecipeMixer.applyModification(door, open ? openDoor : closeDoor);
    }

}
