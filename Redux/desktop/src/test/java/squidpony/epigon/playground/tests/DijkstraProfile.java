package squidpony.epigon.playground.tests;

import squidpony.squidai.DijkstraMap;
import squidpony.squidgrid.mapping.DungeonGenerator;
import squidpony.squidmath.Coord;
import squidpony.squidmath.GreasedRegion;
import squidpony.squidmath.RNG;

import java.util.Collections;
import java.util.Set;

/**
 * Created by Tommy Ettinger on 6/19/2018.
 */
public class DijkstraProfile {
    public static void main(String... args) {
        RNG rng = new RNG(123456789L);
        Coord.expandPoolTo(401, 401);
        DungeonGenerator dg = new DungeonGenerator(400, 400, rng);
        char[][] dun = dg.generate();
        DijkstraMap dijkstra = new DijkstraMap(dun, DijkstraMap.Measurement.MANHATTAN, rng);
        GreasedRegion g = new GreasedRegion(dun, '.');
        Set<Coord> empty = Collections.emptySet();
        int count = 0;
        for (int i = 0; i < 10000000; i++) {
            dijkstra.setGoal(g.singleRandom(rng));
            dijkstra.scan(g.singleRandom(rng), empty);
            count += dijkstra.getMappedCount();
            dijkstra.clearGoals();
            dijkstra.resetMap();
        }
        System.out.println(count);
    }
}
