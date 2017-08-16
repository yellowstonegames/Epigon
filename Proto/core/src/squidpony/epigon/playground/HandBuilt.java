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

import java.util.Arrays;
import java.util.Collections;

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

    public HandBuilt() {
        basePhysical.generic = true;
        basePhysical.unique = true;

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

    private void initPlayer() {
        playerBlueprint = new Physical();
        playerBlueprint.name = "Plae Haa";
        playerBlueprint.description = "It's you!";
        playerBlueprint.notes = "Voted most likely to die in Adventurer's Middle School.";
        playerBlueprint.symbol = '@';
        playerBlueprint.color = SColor.BRIGHT_PINK;
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
        doorBlueprint.color = SColor.WALNUT;
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
        swordBlueprint.color = SColor.SILVER;
        swordBlueprint.symbol = '(';

        RecipeBlueprint swordRecipeBlueprint = new RecipeBlueprint();
        swordRecipeBlueprint.requiredConsumed.put(basePhysical, 1);
        swordRecipeBlueprint.result.put(swordBlueprint, 1);

        swordRecipe = mixer.createRecipe(swordRecipeBlueprint);
    }

    private void initAlive() {
        makeAlive = new Modification();
        makeAlive.possiblePrefix = Arrays.asList(new String[]{"living", "animated"});
        makeAlive.symbol = 's';
        makeAlive.large = true;
        Arrays.stream(Stat.values()).forEach(s -> {
            LiveValueModification lvm = new LiveValueModification(rng.between(10, 20));
            makeAlive.statChanges.put(s, lvm);
        });
        makeAlive.statChanges.put(Stat.MOBILITY, new LiveValueModification(100));
        makeAlive.statChanges.put(Stat.SIGHT, new LiveValueModification(9));
        Creature c = new Creature();
        makeAlive.creatureOverwrite = c;
    }
}
