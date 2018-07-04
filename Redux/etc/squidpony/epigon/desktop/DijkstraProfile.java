package squidpony.epigon.desktop;

import squidpony.squidai.DijkstraMap;
import squidpony.squidgrid.mapping.DungeonGenerator;
import squidpony.squidmath.Coord;
import squidpony.squidmath.GreasedRegion;
import squidpony.squidmath.RNG;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

/**
 * Created by Tommy Ettinger on 6/19/2018.
 */
public class DijkstraProfile {
    public static void main(String[] args) {
        RNG rng = new RNG(123456789L);
        //Coord.expandPoolTo(401, 401);
        DungeonGenerator dg = new DungeonGenerator(100, 100, rng);
        char[][] dun = dg.generate();
        DijkstraMap dijkstra = new DijkstraMap(dun, DijkstraMap.Measurement.MANHATTAN, new RNG(1L));
        GreasedRegion gr = new GreasedRegion(dun, '.');
        Coord[] g = gr.asCoords();
        int gs = g.length;
        Set<Coord> empty = Collections.emptySet();
        ArrayList<Coord> buffer = new ArrayList<>(5000);
        int count = 0;
        Coord[] targets = new Coord[1];
        Coord start;
        long startTime = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
//            start = g[rng.nextIntHasty(gs)];//gr.singleRandom(rng);
//            targets[0] = g[rng.nextIntHasty(gs)];//gr.singleRandom(rng);
            start = gr.singleRandom(rng);
            targets[0] = gr.singleRandom(rng);
            dijkstra.findPath(buffer, 0x7FFFFFF0, -1,  empty, empty, start, targets);
            count += buffer.size();
            buffer.clear();
            dijkstra.clearGoals();
            dijkstra.resetMap();
        }
        System.out.println((System.nanoTime() - startTime) * 0.001 + "ns/iteration");
        System.out.println(count);
    }
}
