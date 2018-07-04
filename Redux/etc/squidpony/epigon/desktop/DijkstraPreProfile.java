package squidpony.epigon.desktop;

import squidpony.squidai.DijkstraMap;
import squidpony.squidgrid.mapping.DungeonGenerator;
import squidpony.squidmath.Coord;
import squidpony.squidmath.GreasedRegion;
import squidpony.squidmath.RNG;

import java.util.ArrayList;

/**
 * Created by Tommy Ettinger on 6/19/2018.
 */
public class DijkstraPreProfile {
    public static void main(String[] args) {
        RNG rng = new RNG(123456789L);
        //Coord.expandPoolTo(401, 401);
        DungeonGenerator dg = new DungeonGenerator(100, 100, rng);
        char[][] dun = dg.generate();
        DijkstraMap dijkstra = new DijkstraMap(dun, DijkstraMap.Measurement.MANHATTAN, new RNG(1L));
        Coord[] g = new GreasedRegion(dun, '.').asCoords();
        int gs = g.length;
        ArrayList<Coord> buffer = new ArrayList<>(5000);
        int count = 0;
        long startTime = System.nanoTime();
        dijkstra.setGoal(g[rng.nextIntHasty(gs)]);
        dijkstra.scan();
        for (int i = 0; i < 1000; i++) {
            dijkstra.findPathPreScanned(buffer, g[rng.nextIntHasty(gs)]);
            count += buffer.size();
            buffer.clear();
        }
        System.out.println((System.nanoTime() - startTime) * 0.001 + "ns/iteration");
        System.out.println(count);
    }
}
