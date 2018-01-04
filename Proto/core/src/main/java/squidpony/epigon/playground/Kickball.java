package squidpony.epigon.playground;

import com.badlogic.gdx.utils.JsonWriter;
import squidpony.DataConverter;
import squidpony.epigon.GauntRNG;
import squidpony.epigon.data.DataMaster;
import squidpony.epigon.data.blueprint.Inclusion;
import squidpony.epigon.data.blueprint.Stone;
import squidpony.epigon.data.generic.Formula;
import squidpony.epigon.data.specific.Physical;
import squidpony.epigon.universe.LiveValue;
import squidpony.epigon.universe.Rating;
import squidpony.epigon.universe.Stat;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A class for doing various tests and utilities work.
 */
public class Kickball {

    public static void main(String... args) {
        new Kickball().go();
    }

    private void go() {
        testFormulas();
//        testJSON();
//        printStones();
    }

    private void testFormulas() {
        HandBuilt handBuilt = new HandBuilt();
        Physical source = handBuilt.mixer.buildPhysical(handBuilt.playerBlueprint);
        Physical target = handBuilt.mixer.buildPhysical(Inclusion.ANDALUSITE);
        source.stats.put(Stat.AIM, new LiveValue(52.5));
        target.stats.put(Stat.DODGE, new LiveValue(52));
        source.stats.put(Stat.IMPACT, new LiveValue(64));
        target.stats.put(Stat.TOUGHNESS, new LiveValue(57));

        testBaseHitChance(source, target);
        testBaseDamageDealt(source, target);

        int[] tests = new int[]{2, 4, 30};
        for (int n : tests) {
            System.out.println("Primary for " + n + " is " + Formula.healthForLevel(n, Rating.GOOD));
        }
    }

    private void testBaseHitChance(Physical source, Physical target) {
        System.out.println("Hit chance of " + source.stats.get(Stat.AIM).actual()
            + " vs " + target.stats.get(Stat.DODGE).actual()
            + " is " + Formula.baseHitChance(source, target));

    }

    private void testBaseDamageDealt(Physical source, Physical target) {
        System.out.println("Damage dealt of " + source.stats.get(Stat.IMPACT).actual()
            + " vs " + target.stats.get(Stat.TOUGHNESS).actual()
            + " is " + Formula.baseDamageDealt(source, target));
    }

    private void printStones() {
        DataConverter convert = new DataConverter(JsonWriter.OutputType.json);
        //convert.setIgnoreUnknownFields(true);
        //convert.setUsePrototypes(true);

        List<Physical> stones = Arrays.stream(Stone.values())
            .map(s -> makePhysicalFromStone(s))
            .collect(Collectors.toList());

        String json = convert.prettyPrint(stones);
        stones = convert.fromJson(List.class, json);

        System.out.println(convert.prettyPrint(stones));
    }

    private Physical makePhysicalFromStone(Stone stone) {
        Physical pb = new Physical();
        pb.name = stone.toString();
        pb.color = GauntRNG.toRandomizedFloat(stone.front, pb.chaos += 3, 0.05f, 0f, 0.15f);

        return pb;
    }

    private void testJSON() {
        DataConverter convert = new DataConverter(JsonWriter.OutputType.json);
        convert.setIgnoreUnknownFields(true);
        convert.setUsePrototypes(false);

        HandBuilt starter = new HandBuilt();

        // Create an actual player
        //Physical player = starter.player;
//        System.out.println(convert.prettyPrint(player));
        // Physical sword = starter.sword;
//        System.out.println(convert.prettyPrint(sword));
        //PhysicalBlueprint pj = starter.playerBlueprint;
//        System.out.println(convert.prettyPrint(playerBlueprint));
//        String playerFile = Gdx.files.internal("config/player.json").readString();
//        playerBlueprint = convert.fromJson(PhysicalBlueprint.class, playerFile);
        //String json = convert.prettyPrint(pj);
//        System.out.println(json);
        //pj = convert.fromJson(PhysicalBlueprint.class, json);
        // System.out.println(convert.prettyPrint(playerBlueprint));
        DataMaster dm = new DataMaster();
//        dm.add(player);
//        dm.add(sword);
        //dm.add(pj);
        System.out.println(convert.prettyPrint(dm.getKnown()));
    }
}
