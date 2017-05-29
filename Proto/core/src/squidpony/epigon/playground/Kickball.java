package squidpony.epigon.playground;

import com.badlogic.gdx.utils.JsonWriter;
import squidpony.DataConverter;
import squidpony.Maker;
import squidpony.epigon.data.blueprint.PhysicalBlueprint;
import squidpony.epigon.data.blueprint.Stone;
import squidpony.epigon.data.generic.Skill;
import squidpony.epigon.data.mixin.Creature;
import squidpony.epigon.data.mixin.Humanoid;
import squidpony.epigon.data.specific.Physical;
import squidpony.epigon.universe.Rating;
import squidpony.epigon.universe.Stat;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidmath.OrderedMap;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static squidpony.epigon.Epigon.rng;
import squidpony.epigon.data.DataMaster;

/**
 * A class for doing various tests and utility work.
 */
public class Kickball {

    public static void main(String... args) {
        new Kickball().go();
    }

    private void go() {
        testJSON();
//        printStones();
    }

    private void printStones() {
        DataConverter convert = new DataConverter(JsonWriter.OutputType.json);
        convert.setIgnoreUnknownFields(true);
        convert.setUsePrototypes(true);

        List<PhysicalBlueprint> stones = Arrays.stream(Stone.values())
            .map(s -> makePhysicalFromStone(s))
            .collect(Collectors.toList());

        String json = convert.prettyPrint(stones);
        stones = convert.fromJson(List.class, json);

        System.out.println(convert.prettyPrint(stones));
    }

    private PhysicalBlueprint makePhysicalFromStone(Stone stone) {
        PhysicalBlueprint pb = new PhysicalBlueprint();
        pb.name = stone.getName();
        pb.color = stone.front;

        return pb;
    }

    private void testJSON() {
        DataConverter convert = new DataConverter(JsonWriter.OutputType.json);
        convert.setIgnoreUnknownFields(true);
        convert.setUsePrototypes(false);

        // Create an actual player
        Physical player = new Physical();
        player.creatureData = new Creature();
        player.creatureData.abilities = new HashSet<>();
        player.name = "Great Hero";
 //       Arrays.stream(Stat.values()).forEach(s -> player.stats.put(s, rng.between(20, 100)));
//        Arrays.stream(Stat.values()).forEach(s -> player.currentStats.put(s, player.stats.get(s) + rng.between(-10, 30)));

//        System.out.println(convert.prettyPrint(player));

        Physical sword = new Physical();
        sword.color = SColor.SILVER;
        sword.symbol = '/';
        sword.name = "Sword";
//        System.out.println(convert.prettyPrint(sword));

        PhysicalBlueprint pj = new PhysicalBlueprint();
        pj.name = "Player";
        pj.description = "The main player's character.";
        pj.notes = "Voted most likely to die.";
        pj.symbol = '@';
        pj.color = SColor.FOX;
        pj.possibleAliases = Maker.makeList("Mario", "Link", "Sam");
        pj.baseStats.put(Stat.OPACITY, 100);
        Humanoid cb = new Humanoid();
        pj.creatureData = cb;
        cb.skills = new OrderedMap<>();
        Skill skill = new Skill();
        skill.name = "kendo";
        cb.skills.put(skill, Rating.HIGH);
        skill = new Skill();
        skill.name = "akido";
        cb.skills.put(skill, Rating.SLIGHT);
//        System.out.println(convert.prettyPrint(pj));

//        String playerFile = Gdx.files.internal("config/player.json").readString();
//        pj = convert.fromJson(PhysicalBlueprint.class, playerFile);
        String json = convert.prettyPrint(pj);
//        System.out.println(json);
        pj = convert.fromJson(PhysicalBlueprint.class, json);
       // System.out.println(convert.prettyPrint(pj));

        DataMaster dm = new DataMaster();
        dm.add(player);
        dm.add(sword);
        dm.add(pj);
        System.out.println(convert.prettyPrint(dm.getKnown()));
    }
}
