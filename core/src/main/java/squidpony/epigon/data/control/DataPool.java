package squidpony.epigon.data.control;

import squidpony.epigon.data.Physical;
import squidpony.epigon.data.quality.Stone;
import squidpony.epigon.data.quality.Tree;
import squidpony.epigon.data.quality.Vegetable;
import squidpony.squidmath.EnumOrderedSet;
import squidpony.squidmath.IRNG;
import squidpony.squidmath.OrderedMap;

import java.util.EnumMap;

/**
 * Holds the needed references to objects in common use.
 */
public class DataPool {

    private static DataPool instance;

    private DataStarter dataStarter;

    private final EnumMap<Stone, Physical> walls = new EnumMap<>(Stone.class);
    private final EnumMap<Stone, Physical> floors = new EnumMap<>(Stone.class);
    private final OrderedMap<Character, EnumOrderedSet<Vegetable>> vegetablesByTerrain = new OrderedMap<>(8);
    private final OrderedMap<Character, EnumOrderedSet<Tree>> treesByTerrain = new OrderedMap<>(8);

    public static DataPool instance() {
        if (instance != null) {
            return instance;
        }

        instance = new DataPool();
        instance.dataStarter = new DataStarter();
        instance.initPlants();

        return instance;
    }

    private void initPlants() {
        Vegetable[] vegetables = Vegetable.ALL;
        for (int v = 0; v < vegetables.length; v++) {
            String terrains = vegetables[v].terrains();
            for (int i = 0; i < terrains.length(); i++) {
                if (!vegetablesByTerrain.containsKey(terrains.charAt(i))) {
                    vegetablesByTerrain.put(terrains.charAt(i), new EnumOrderedSet<>(vegetables[v]));
                }
                vegetablesByTerrain.get(terrains.charAt(i)).add(vegetables[v]);
            }
        }
        Tree[] trees = Tree.ALL;
        for (int t = 0; t < trees.length; t++) {
            String terrains = trees[t].terrains();
            for (int i = 0; i < terrains.length(); i++) {
                if (!treesByTerrain.containsKey(terrains.charAt(i))) {
                    treesByTerrain.put(terrains.charAt(i), new EnumOrderedSet<>(trees[t]));
                }
                treesByTerrain.get(terrains.charAt(i)).add(trees[t]);
            }
        }
    }

    public Physical getWall(Stone stone) {
        Physical wall = walls.get(stone);
        if (wall != null) {
            return wall;
        }

        wall = RecipeMixer.buildPhysical(RecipeMixer.buildPhysical(stone));
        RecipeMixer.applyModification(wall, dataStarter.makeWall);
        walls.put(stone, wall);
        return wall;
    }

    public Physical getFloor(Stone stone) {
        Physical floor = floors.get(stone);
        if (floor != null) {
            return floor;
        }

        floor = RecipeMixer.buildPhysical(RecipeMixer.buildPhysical(stone));
        floor.name = stone.toString() + " floor";
        floors.put(stone, floor);
        return floor;
    }

    public Vegetable getVegetable(Character c, IRNG rng) {
        EnumOrderedSet<Vegetable> veggies = vegetablesByTerrain.get(c);
        if (veggies == null || veggies.isEmpty()) {
            return null;
        }
        return veggies.randomItem(rng);
    }

    public Tree getTree(Character c, IRNG rng) {
        EnumOrderedSet<Tree> trees = treesByTerrain.get(c);
        if (trees == null || trees.isEmpty()) {
            return null;
        }
        return trees.randomItem(rng);
    }
}
