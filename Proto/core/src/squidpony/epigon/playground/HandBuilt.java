package squidpony.epigon.playground;

import java.util.Arrays;
import squidpony.Maker;
import static squidpony.epigon.Epigon.rng;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidmath.OrderedMap;

import squidpony.epigon.data.blueprint.PhysicalBlueprint;
import squidpony.epigon.data.generic.Skill;
import squidpony.epigon.data.mixin.Creature;
import squidpony.epigon.data.specific.Physical;
import squidpony.epigon.dm.RecipeMixer;
import squidpony.epigon.universe.LiveValue;
import squidpony.epigon.universe.Rating;
import squidpony.epigon.universe.Stat;

/**
 * Contains objects to use to test out connections.
 */
public class HandBuilt {

    public RecipeMixer mixer = new RecipeMixer();
    public PhysicalBlueprint playerBlueprint;
    public Physical player;
    public PhysicalBlueprint swordBlueprint;
    public Physical sword;

    public HandBuilt() {
        playerBlueprint = new PhysicalBlueprint();
        playerBlueprint.name = "Plae Haa";
        playerBlueprint.description = "The main player's character.";
        playerBlueprint.notes = "Voted most likely to die in Adventurer's Middle School.";
        playerBlueprint.symbol = '@';
        playerBlueprint.color = SColor.FOX;
        playerBlueprint.large = true;
        playerBlueprint.possibleAliases = Maker.makeList("Mario", "Link", "Sam");
        Arrays.stream(Stat.values()).forEach(s -> {
            LiveValue lv = new LiveValue(rng.between(20, 100));
            lv.actual = lv.base * (rng.nextDouble() + 0.1);
            playerBlueprint.initialStats.put(s, lv);
        });
        playerBlueprint.initialStats.put(Stat.OPACITY, new LiveValue(100)); // Make sure player's opaque after randomizing stats

        Creature cb = new Creature();
        playerBlueprint.creatureData = cb;
        cb.skills = new OrderedMap<>();
        Skill skill = new Skill();
        skill.name = "kendo";
        cb.skills.put(skill, Rating.HIGH);
        skill = new Skill();
        skill.name = "akido";
        cb.skills.put(skill, Rating.SLIGHT);

        player = mixer.createFrom(playerBlueprint);


        swordBlueprint = new PhysicalBlueprint();
        swordBlueprint.name = "sword";
        swordBlueprint.color =  SColor.SILVER;
        swordBlueprint.symbol = '/';

        sword = mixer.createFrom(swordBlueprint);
    }
}
