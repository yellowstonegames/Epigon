package squidpony.epigon.playground;

import squidpony.Maker;
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
public class Starter {

    public RecipeMixer mixer = new RecipeMixer();
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
        playerBlueprint.large = true;
        playerBlueprint.possibleAliases = Maker.makeList("Mario", "Link", "Sam");
        playerBlueprint.initialStats.put(Stat.OPACITY, new LiveValue(100));

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
        sword = new Physical();
        sword.color = SColor.SILVER;
        sword.symbol = '/';
        sword.name = "Sword";
    }
}
