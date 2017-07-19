package squidpony.epigon.playground;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.badlogic.gdx.utils.JsonWriter;

import squidpony.DataConverter;
import static squidpony.epigon.Epigon.handBuilt;
import static squidpony.epigon.Epigon.mixer;
import squidpony.epigon.data.blueprint.Stone;
import squidpony.epigon.data.specific.Physical;
import squidpony.epigon.data.DataMaster;
import squidpony.epigon.data.blueprint.Inclusion;
import squidpony.epigon.data.generic.Formula;
import squidpony.epigon.universe.LiveValue;
import squidpony.epigon.universe.Stat;

/**
 * A class for doing various tests and utility work.
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
        Physical source = mixer.buildPhysical(handBuilt.playerBlueprint);
        Physical target = mixer.buildPhysical(Inclusion.ANDALUSITE);
        source.stats.put(Stat.AIM, new LiveValue(60));
        target.stats.put(Stat.DODGE, new LiveValue(1));
        source.stats.put(Stat.IMPACT, new LiveValue(60));
        target.stats.put(Stat.TOUGHNESS, new LiveValue(57));

        testBaseHitChance(source, target);
        testBaseDamageDealt(source, target);
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
        pb.color = stone.front;

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
