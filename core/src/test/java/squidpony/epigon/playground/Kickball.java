package squidpony.epigon.playground;

import squidpony.epigon.data.control.RecipeMixer;
import squidpony.epigon.data.control.DataStarter;
import com.badlogic.gdx.utils.JsonWriter;
import squidpony.DataConverter;
import squidpony.epigon.ConstantKey;
import squidpony.epigon.util.Utilities;
import squidpony.epigon.data.*;
import squidpony.epigon.data.quality.*;
import squidpony.epigon.mapping.MapDecorator;
import squidpony.epigon.mapping.LocalAreaGenerator;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidmath.OrderedSet;

import java.util.ArrayList;
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
//        testImmutableKeys();
//        testJSON();
//        testFormulas();
//        testJSON();
//        printStones();
        testWorldBuild();
    }

    private static class TimeTracker implements Comparable<TimeTracker> {

        long time;
        int width, height, depth;

        public TimeTracker(long time, int width, int height, int depth) {
            this.time = time;
            this.width = width;
            this.height = height;
            this.depth = depth;
        }

        @Override
        public int compareTo(TimeTracker o) {
            return Long.compare(time, o.time);
        }

        @Override
        public String toString() {
            return "" + time + "," + width + "," + height + "," + depth;
        }
    }

    private void testWorldBuild() {
        System.out.println("Testing world building.");

        DataStarter hand = new DataStarter();
        MapDecorator mapDecorator = new MapDecorator(hand);
        LocalAreaGenerator gen = new LocalAreaGenerator(mapDecorator);

        int hStep = 4;
        int zStep = 3;
        int width = 21;
        int height = 21;
        long milli;

        ArrayList<TimeTracker> list = new ArrayList<>(100 * 500 / zStep);
        for (int i = 0; i < 9; i++) {
            for (int z = 1; z < 100; z += zStep) {
                milli = System.currentTimeMillis();
                gen.buildWorld(width, height, z);
                milli = System.currentTimeMillis() - milli;
                list.add(new TimeTracker(milli, width, height, z));
            }
            System.out.println("Iteration " + i);
            if (i % 2 == 0) {
                width += hStep;
            } else {
                height += hStep;
            }
        }

        list.sort(null);
        TimeTracker last = list.get(list.size() - 1);
        System.out.println("");
        System.out.println("Worst cases, at time " + last.time);
        list.stream()
            .filter(t -> t.compareTo(last) == 0)
            .forEach(t -> System.out.println("" + t.width + " x " + t.height + " x " + t.depth));
        System.out.println("");
        String csv = list.stream().map(TimeTracker::toString).collect(Collectors.joining("\n"));
        System.out.println(csv);
    }

    private void testImmutableKeys() {
        OrderedSet<Material> materials = new OrderedSet<>(ConstantKey.ConstantKeyHasher.instance);
        materials.addAll(Cloth.values());
        materials.addAll(Hide.values());
        materials.addAll(Inclusion.values());
        materials.addAll(Metal.values());
        materials.addAll(Paper.values());
        materials.addAll(Stone.values());
        materials.addAll(Wood.values());
        for(Material m : materials)
        {
            System.out.println(Utilities.capitalizeFirst(m.toString()) + "? I can pay $" + m.getValue() + ", not a nan more.");
        }
    }
    private void testFormulas() {
        DataStarter handBuilt = new DataStarter();
        Physical source = RecipeMixer.buildPhysical(handBuilt.playerBlueprint);
        Physical target = RecipeMixer.buildPhysical(Inclusion.ANDALUSITE);
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
        pb.color = SColor.toRandomizedFloat(stone.front, pb, 0.05f, 0f, 0.15f);

        return pb;
    }

    private void testJSON() {
        DataConverter convert = new DataConverter(JsonWriter.OutputType.json);
        convert.setIgnoreUnknownFields(true);
        convert.setUsePrototypes(false);

        DataStarter starter = new DataStarter();

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
    }
}
