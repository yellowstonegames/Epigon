package squidpony.epigon.playground;

import squidpony.Maker;
import squidpony.epigon.data.blueprint.RecipeBlueprint;
import squidpony.epigon.data.generic.Formula;
import squidpony.epigon.data.generic.Modification;
import squidpony.epigon.data.generic.Skill;
import squidpony.epigon.data.mixin.Creature;
import squidpony.epigon.data.specific.Physical;
import squidpony.epigon.data.specific.Recipe;
import squidpony.epigon.universe.LiveValue;
import squidpony.epigon.universe.LiveValueModification;
import squidpony.epigon.universe.Rating;
import squidpony.epigon.universe.Stat;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidmath.OrderedMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static squidpony.epigon.Epigon.mixer;
import static squidpony.epigon.Epigon.rng;

/**
 * Contains objects to use to test out connections.
 */
public class HandBuilt {

    public Physical basePhysical = new Physical();

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

    public List<Skill> skills = new ArrayList<>();
    
    public HandBuilt() {
        basePhysical.generic = true;
        basePhysical.unique = true;

        initSkills();
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

    private void initSkills(){

        // Cooking skills
        Skill cooking = new Skill();
        cooking.name = "cooking";
        skills.add(cooking);
        Skill baking = new Skill();
        baking.name = "baking";
        baking.parent = cooking;
        skills.add(baking);
        Skill frying  = new Skill();
        frying.name = "frying";
        frying.parent = cooking;
        skills.add(frying);
        Skill boiling = new Skill();
        boiling.name = "boiling";
        boiling.parent = cooking;
        skills.add(boiling);
        Skill foodPrep = new Skill();
        foodPrep.name = "food prep";
        foodPrep.parent = cooking;
        skills.add(foodPrep);
        Skill foodChopping = new Skill();
        foodChopping.name = "food chopping";
        foodChopping.parent = foodPrep;
        skills.add(foodChopping);
        Skill foodMixing = new Skill();
        foodMixing.name = "food mixing";
        foodMixing.parent = foodPrep;
        skills.add(foodMixing);
        Skill canning = new Skill();
        canning.name = "canning";
        canning.parent = cooking;
        skills.add(canning);
        Skill foodDrying = new Skill();
        foodDrying.name = "food drying";
        foodDrying.parent = cooking;
        skills.add(foodDrying);

        // Gathering skills
        Skill gathering = new Skill();
        gathering.name = "gathering";
        skills.add(gathering);
        Skill butchering = new Skill();
        butchering.name = "butchering";
        butchering.parent = gathering;
        skills.add(butchering);
        Skill farming = new Skill();
        farming.name = "farming";
        farming.parent = gathering;
        skills.add(farming);
        Skill fishing = new Skill();
        fishing.name = "fishing";
        fishing.parent = gathering;
        skills.add(fishing);
        Skill herbalism  = new Skill();
        herbalism.name = "herbalism";
        herbalism.parent = gathering;
        skills.add(herbalism);
        Skill hunting = new Skill();
        hunting.name = "hunting";
        hunting.parent = gathering;
        skills.add(hunting);
        Skill mining = new Skill();
        mining.name = "mining";
        mining.parent = gathering;
        skills.add(mining);
        Skill woodcutting = new Skill();
        woodcutting.name = "woodc utting";
        woodcutting.parent = gathering;
        skills.add(woodcutting);
        Skill treeFellingAx = new Skill();
        treeFellingAx.name = "tree felling (ax)";
        treeFellingAx.parent = woodcutting;
        skills.add(treeFellingAx);
        Skill treeFellingSaw = new Skill();
        treeFellingSaw.name = "tree felling (saw)";
        treeFellingSaw.parent = woodcutting;
        skills.add(treeFellingSaw);
    }

    private void initPlayer() {
        playerBlueprint = new Physical();
        playerBlueprint.name = "Plae Haa";
        playerBlueprint.description = "It's you!";
        playerBlueprint.notes = "Voted most likely to die in Adventurer's Middle School.";
        playerBlueprint.symbol = '@';
        playerBlueprint.color = SColor.BRIGHT_PINK.toFloatBits();
        playerBlueprint.large = true;
        playerBlueprint.unique = true;
        playerBlueprint.attached = true;
        playerBlueprint.possibleAliases = Maker.makeList("Mario", "Link", "Sam");
        for (Stat s : Stat.bases) {
            Rating rating = rng.getRandomElement(Rating.values());
            if (rating == Rating.NONE) {
                rating = Rating.SLIGHT;
            }
            LiveValue lv = new LiveValue(Formula.randomizedStartingStatLevel());
            playerBlueprint.stats.put(s, lv);
            playerBlueprint.statProgression.put(s, rating);
        }
        for (Stat s : Stat.healths) {
            Rating rating = rng.getRandomElement(Rating.values());
            if (rating == Rating.NONE) {
                rating = Rating.SLIGHT;
            }
            LiveValue lv = new LiveValue(Formula.healthForLevel(1, rating));
            playerBlueprint.stats.put(s, lv);
            playerBlueprint.statProgression.put(s, rating);
        }
        for (Stat s : Stat.needs) {
            Rating rating = rng.getRandomElement(Rating.values());
            if (rating == Rating.NONE) {
                rating = Rating.SLIGHT;
            }
            LiveValue lv = new LiveValue(Formula.healthForLevel(1, rating));
            playerBlueprint.stats.put(s, lv);
            playerBlueprint.statProgression.put(s, rating);
        }
        for (Stat s : Stat.utilities) {
            Rating rating = rng.getRandomElement(Rating.values());
            if (rating == Rating.NONE) {
                rating = Rating.SLIGHT;
            }
            LiveValue lv = new LiveValue(Formula.healthForLevel(1, rating));
            playerBlueprint.stats.put(s, lv);
            playerBlueprint.statProgression.put(s, rating);
        }
        playerBlueprint.stats.put(Stat.SIGHT, new LiveValue(8));
        playerBlueprint.stats.put(Stat.HEARING, new LiveValue(12));
        playerBlueprint.stats.put(Stat.MOBILITY, new LiveValue(100));

        Creature cb = new Creature();
        playerBlueprint.creatureData = cb;
        cb.skills = new OrderedMap<>();
        Skill skill = new Skill();
        skill.name = "kendo";
        cb.skills.put(skill, Rating.HIGH);
        skill = new Skill();
        skill.name = "akido";
        cb.skills.put(skill, Rating.SLIGHT);
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
        doorRecipeBlueprint.requiredConsumed.put(basePhysical, 1);
        doorRecipeBlueprint.result.put(doorBlueprint, 1);

        doorRecipe = mixer.createRecipe(doorRecipeBlueprint);
    }

    private void initItems() {
        swordBlueprint = new Physical();
        swordBlueprint.name = "sword";
        swordBlueprint.color = SColor.SILVER.toRandomizedFloat(rng, 0.1f, 0f, 0.2f);
        swordBlueprint.symbol = '(';

        RecipeBlueprint swordRecipeBlueprint = new RecipeBlueprint();
        swordRecipeBlueprint.requiredConsumed.put(basePhysical, 1);
        swordRecipeBlueprint.result.put(swordBlueprint, 1);

        swordRecipe = mixer.createRecipe(swordRecipeBlueprint);
    }

    private void initAlive() {
        makeAlive = new Modification();
        makeAlive.possiblePrefix = Arrays.asList("living", "animated");
        makeAlive.symbol = 's';
        makeAlive.large = true;
        Arrays.stream(Stat.values()).forEach(s -> {
            LiveValueModification lvm = new LiveValueModification(rng.between(10, 20));
            makeAlive.statChanges.put(s, lvm);
        });
        makeAlive.statChanges.put(Stat.MOBILITY, new LiveValueModification(100));
        makeAlive.statChanges.put(Stat.SIGHT, new LiveValueModification(9));
        makeAlive.creatureOverwrite = new Creature();
    }
}
