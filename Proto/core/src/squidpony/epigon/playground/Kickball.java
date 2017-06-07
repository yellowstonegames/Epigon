package squidpony.epigon.playground;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.badlogic.gdx.utils.JsonWriter;

import squidpony.DataConverter;
import squidpony.epigon.data.blueprint.PhysicalBlueprint;
import squidpony.epigon.data.blueprint.Stone;
import squidpony.epigon.data.specific.Physical;

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
        //convert.setIgnoreUnknownFields(true);
        //convert.setUsePrototypes(true);

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

        Starter starter = new Starter();

        // Create an actual player
        Physical player = starter.player;

//        System.out.println(convert.prettyPrint(player));

        Physical sword = starter.sword;
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
        dm.add(player);
        dm.add(sword);
        //dm.add(pj);
        System.out.println(convert.prettyPrint(dm.getKnown()));
    }
}
