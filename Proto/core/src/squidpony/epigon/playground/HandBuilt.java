package squidpony.epigon.playground;

import squidpony.Maker;
import squidpony.epigon.data.blueprint.Inclusion;
import squidpony.epigon.data.blueprint.Stone;
import squidpony.epigon.data.generic.Modification;
import squidpony.epigon.data.generic.Skill;
import squidpony.epigon.data.mixin.Creature;
import squidpony.epigon.data.specific.Condition;
import squidpony.epigon.data.specific.Physical;
import squidpony.epigon.universe.LiveValue;
import squidpony.epigon.universe.Rating;
import squidpony.epigon.universe.Stat;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidmath.OrderedMap;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import static squidpony.epigon.Epigon.mixer;

import static squidpony.epigon.Epigon.rng;
import squidpony.epigon.data.blueprint.RecipeBlueprint;
import squidpony.epigon.data.specific.Recipe;

/**
 * Contains objects to use to test out connections.
 */
public class HandBuilt {

    public Physical basePhysical = new Physical();

    public Physical playerBlueprint;
    public Physical swordBlueprint;

    public Recipe doorRecipe;

    public Modification makeWall;

    public HandBuilt() {
        basePhysical.generic = true;
        basePhysical.unique = true;

        initDoors();

        playerBlueprint = new Physical();
        playerBlueprint.name = "Plae Haa";
        playerBlueprint.description = "It's you!";
        playerBlueprint.notes = "Voted most likely to die in Adventurer's Middle School.";
        playerBlueprint.symbol = '@';
        playerBlueprint.color = SColor.BRIGHT_PINK;
        playerBlueprint.large = true;
        playerBlueprint.unique = true;
        playerBlueprint.possibleAliases = Maker.makeList("Mario", "Link", "Sam");
        Arrays.stream(Stat.values()).forEach(s -> {
            LiveValue lv = new LiveValue(rng.between(20, 100));
            lv.actual = lv.base * (rng.nextDouble() + 0.1);
            playerBlueprint.stats.put(s, lv);
        });
        playerBlueprint.stats.put(Stat.SIGHT, new LiveValue(8));

        Creature cb = new Creature();
        playerBlueprint.creatureData = cb;
        cb.skills = new OrderedMap<>();
        Skill skill = new Skill();
        skill.name = "kendo";
        cb.skills.put(skill, Rating.HIGH);
        skill = new Skill();
        skill.name = "akido";
        cb.skills.put(skill, Rating.SLIGHT);

        swordBlueprint = new Physical();
        swordBlueprint.name = "sword";
        swordBlueprint.color = SColor.SILVER;
        swordBlueprint.symbol = '/';

        makeWall = new Modification();
        Collections.addAll(makeWall.possiblePrefix, "solid", "shaped");
        makeWall.possiblePostfix.add("wall");
        makeWall.symbol = '#';
        makeWall.large = true;
    }

    private void initDoors() {
        Physical doorBlueprint;
        Condition openDoor;

        doorBlueprint = new Physical();
        doorBlueprint.name = "door";
        doorBlueprint.symbol = '+';
        doorBlueprint.color = SColor.WALNUT;
        doorBlueprint.large = true;

        RecipeBlueprint doorRecipeBlueprint;
        doorRecipeBlueprint = new RecipeBlueprint();
        doorRecipeBlueprint.requiredConsumed.put(basePhysical, 1);
        doorRecipeBlueprint.result.put(doorBlueprint, 1);

        doorRecipe = mixer.createRecipe(doorRecipeBlueprint);
    }
}
