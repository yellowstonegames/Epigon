package squidpony.epigon.playground;

import java.util.Arrays;
import java.util.HashSet;

import squidpony.Maker;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidmath.OrderedMap;

import static squidpony.epigon.Epigon.rng;

import squidpony.epigon.data.blueprint.PhysicalBlueprint;
import squidpony.epigon.data.generic.Skill;
import squidpony.epigon.data.mixin.Creature;
import squidpony.epigon.data.mixin.Humanoid;
import squidpony.epigon.data.specific.Physical;
import squidpony.epigon.universe.LiveValue;
import squidpony.epigon.universe.Rating;
import squidpony.epigon.universe.Stat;

/**
 * Contains objects to use to test out connections.
 */
public class Starter {

    public PhysicalBlueprint playerBlueprint;
    public Physical player;
    public Physical sword;

    public Starter() {
        playerBlueprint = new PhysicalBlueprint();
        playerBlueprint.name = "Player";
        playerBlueprint.description = "The main player's character.";
        playerBlueprint.notes = "Voted most likely to die.";
        playerBlueprint.symbol = '@';
        playerBlueprint.color = SColor.FOX;
        playerBlueprint.possibleAliases = Maker.makeList("Mario", "Link", "Sam");
        playerBlueprint.initialStats.put(Stat.OPACITY, new LiveValue(100));

        Humanoid cb = new Humanoid();
        playerBlueprint.creatureData = cb;
        cb.skills = new OrderedMap<>();
        Skill skill = new Skill();
        skill.name = "kendo";
        cb.skills.put(skill, Rating.HIGH);
        skill = new Skill();
        skill.name = "akido";
        cb.skills.put(skill, Rating.SLIGHT);

        player = new Physical();
        player.creatureData = new Creature();
        player.creatureData.abilities = new HashSet<>();
        player.name = "Great Hero";
        Arrays.stream(Stat.values()).forEach(s -> player.stats.put(s, new LiveValue(rng.between(20, 100))));

        sword = new Physical();
        sword.color = SColor.SILVER;
        sword.symbol = '/';
        sword.name = "Sword";
    }
}
