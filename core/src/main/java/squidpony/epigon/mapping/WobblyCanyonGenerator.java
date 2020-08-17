package squidpony.epigon.mapping;

import squidpony.epigon.data.Physical;
import squidpony.epigon.data.WeightedTableWrapper;
import squidpony.epigon.data.control.DataStarter;
import squidpony.epigon.data.control.RecipeMixer;
import squidpony.epigon.data.quality.Inclusion;
import squidpony.epigon.data.trait.Grouping;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidmath.Coord;
import squidpony.squidmath.DiverRNG;
import squidpony.squidmath.GreasedRegion;
import squidpony.squidmath.Noise;
import squidpony.squidmath.NumberTools;

import squidpony.squidmath.StatefulRNG;

/**
 * Builds the kind of wobbly canyon used in Dive
 */
public class WobblyCanyonGenerator {

    private DataStarter dataStarter;
    private MapDecorator decorator;
    private StatefulRNG rng;
    int width, height, depth;

    public WobblyCanyonGenerator(MapDecorator decorator) {
        this.decorator = decorator;
        dataStarter = decorator.dataStarter;
        rng = dataStarter.rng.copy();
    }

    public EpiMap buildDive(EpiMap[] world, int width, int depth) {
        this.width = width;
        this.height = depth;
        this.depth = 1;
        EpiMap map = new EpiMap(width, height);
        GreasedRegion safeSpots = new GreasedRegion(width, height);
        for (int x = 0; x < width; x++) {
            for (int y = MapConstants.DIVE_HEADER.length, y0 = 0; y < depth; y++, y0++) {
                map.contents[x][y] = world[y0].contents[x][0];
            }
        }

        // Add in dive header
        for (int x = 0; x < MapConstants.DIVE_HEADER[0].length(); x++) {
            for (int y = 0; y < MapConstants.DIVE_HEADER.length; y++) {
                char c = MapConstants.DIVE_HEADER[y].charAt(x);
                switch (c) {
                    case ' ':
                        map.contents[x][y] = new EpiTile(dataStarter.emptySpace);
                        break;
                    case '$':
                        map.contents[x][y] = new EpiTile(dataStarter.emptySpace);
                        map.contents[x][y].add(dataStarter.money);
                        break;
                    default:
                        Physical p = new Physical();
                        p.symbol = c;
                        p.color = SColor.SCARLET.toFloatBits();
                        p.blocking = true;
                        map.contents[x][y] = new EpiTile(p);
                        break;
                }
            }
        }

        int centerGap = width / 2;
        int gapSize = (int) (width * 0.4);
        long seed1 = dataStarter.rng.nextLong() + System.nanoTime(),
                seed2 = dataStarter.rng.nextLong() + seed1,
                seed3 = dataStarter.rng.nextLong() + seed2 ^ seed1;
        final double portionGapSize = 0.08 * width, offGapSize = 0.12 * width,
                halfWidth = 0.5 * width, centerOff = 0.135 * width, extraWiggle = 0.02 * width;
        for (int level = MapConstants.DIVE_HEADER.length; level < height; level++) {
            for (int x = centerGap - gapSize; x < centerGap + gapSize; x++) {
                map.contents[x][level].floor = dataStarter.emptySpace;
                map.contents[x][level].blockage = null;
                safeSpots.insert(x, level);
            }
            // Basic1D noise is more wobbly, with small changes frequently and frequent (cyclical) major changes
            gapSize = (int) (Noise.Basic1D.noise(level * 0.17, seed1) * portionGapSize + offGapSize
                    + NumberTools.randomFloatCurved(seed3 * (level + seed2)) * extraWiggle);
            // swayRandomized spends a little more time at extremes before shifting suddenly to a very different value
            centerGap = (int) ((NumberTools.swayRandomized(seed2, level * 0.08) + NumberTools.swayRandomized(seed3, level * 0.135)) * centerOff + halfWidth);
            centerGap = Math.max(centerGap, gapSize / 2 + 1); // make sure it's not off the left side
            centerGap = Math.min(centerGap, width - gapSize / 2 - 1); // make sure it's not off the right side
        }
        rng = new StatefulRNG(new DiverRNG(dataStarter.rng.nextLong() ^ seed3));
        safeSpots.retract(2).randomScatter(rng, 8);

        Inclusion[] inclusions = Inclusion.ALL;
        Physical[] contents = new Physical[inclusions.length + 1];
        double[] weights = new double[inclusions.length + 1];
        for (int i = 0; i < inclusions.length; i++) {
            Physical gem = RecipeMixer.buildPhysical(inclusions[i]);
            gem.symbol = '♦';
            gem.groupingData = new Grouping(1);
            contents[i] = gem;
            weights[i] = rng.between(1.0, 3.0);
        }
        contents[inclusions.length] = dataStarter.money;
        weights[inclusions.length] = inclusions.length * 3.25;
        WeightedTableWrapper<Physical> table = new WeightedTableWrapper<>(rng.nextLong(), contents, weights);

        for (Coord cash : safeSpots) {
            if (map.contents[cash.x][cash.y].blockage == null) {
                map.contents[cash.x][cash.y].add(table.random());
            }
        }

        // Close off bottom with "goal"
        Physical goal = new Physical();
        goal.color = SColor.GOLDEN.toFloatBits();
        goal.symbol = '♥';
        goal.blocking = false;
        goal.unique = true; // misusing this intentionally to mark special "objects"
        for (int x = 0; x < width; x++) {
            map.contents[x][height - 2].floor = goal;
            map.contents[x][height - 2].add(RecipeMixer.buildPhysical(goal));
            map.contents[x][height - 2].blockage = null;
            map.contents[x][height - 1].floor = goal;
            map.contents[x][height - 1].add(RecipeMixer.buildPhysical(goal));
            map.contents[x][height - 1].blockage = null;
        }

        return map;
    }

}
