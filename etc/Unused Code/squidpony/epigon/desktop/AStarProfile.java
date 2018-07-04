package squidpony.epigon.desktop;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.DefaultConnection;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.Heuristic;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectIntMap;
import squidpony.squidgrid.Direction;
import squidpony.squidgrid.mapping.DungeonGenerator;
import squidpony.squidmath.Coord;
import squidpony.squidmath.GreasedRegion;
import squidpony.squidmath.RNG;

/**
 * Created by Tommy Ettinger on 6/19/2018.
 */
public class AStarProfile {
    static class GridGraph implements IndexedGraph<Coord>
    {
        public ObjectIntMap<Coord> points;
        public char[][] map;
        public Heuristic<Coord> heu = new Heuristic<Coord>() {
            @Override
            public float estimate(Coord node, Coord endNode) {
                return Math.abs(node.x - endNode.x) + Math.abs(node.y - endNode.y);
            }
        };

        public GridGraph(Coord[] floors, char[][] map)
        {
            this.map = map;
            int floorCount = floors.length;
            points = new ObjectIntMap<>(floorCount);
            for (int i = 0; i < floorCount; i++) {
                points.put(floors[i], i);
            }
        }
        @Override
        public int getIndex(Coord node) {
            return points.get(node, -1);
        }

        @Override
        public int getNodeCount() {
            return points.size;
        }

        @Override
        public Array<Connection<Coord>> getConnections(Coord fromNode) {
            Array<Connection<Coord>> conn = new Array<>(false, 4);
            if(map[fromNode.x][fromNode.y] != '.')
                return conn;
            Coord t;
            for (int i = 0; i < 4; i++) {
                t = fromNode.translate(Direction.CARDINALS_CLOCKWISE[i]);
                if (t.isWithin(map.length, map[0].length) && map[t.x][t.y] == '.')
                    conn.add(new DefaultConnection<>(fromNode, t));
            }
            return conn;
        }
    }
    
    public static void main(String[] args) {
        RNG rng = new RNG(123456789L);
        //Coord.expandPoolTo(401, 401);
        DungeonGenerator dg = new DungeonGenerator(100, 100, rng);
        char[][] dun = dg.generate();
        Coord[] g = new GreasedRegion(dun, '.').asCoords();
        int gs = g.length;
        GridGraph gg = new GridGraph(g, dun);
        IndexedAStarPathFinder<Coord> astar = new IndexedAStarPathFinder<>(gg);
        DefaultGraphPath<Coord> dgp = new DefaultGraphPath<>(5000);
        int count = 0;
        long startTime = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            //dijkstra.setGoal(g.singleRandom(rng));
            //dijkstra.scan(g.singleRandom(rng), empty);
            if(astar.searchNodePath(g[rng.nextIntHasty(gs)], g[rng.nextIntHasty(gs)], gg.heu, dgp)) 
                count += dgp.getCount();
            dgp.clear();
        }
        System.out.println((System.nanoTime() - startTime) * 0.001 + "ns/iteration");
        System.out.println(count);
    }
}
