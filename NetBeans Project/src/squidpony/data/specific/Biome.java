package squidpony.data.specific;

import java.awt.Point;
import java.util.TreeMap;
import squidpony.data.BiomeSize;
import squidpony.data.EpiData;
import squidpony.data.blueprints.BiomeBlueprint;

/**
 * Represents a specific region of land.
 *
 * @author Eben Howard - http://squidpony.com - howard@squidpony.com
 */
public class Biome extends EpiData{
    public BiomeBlueprint parent;
    public BiomeSize size;//approximation of size
    public TreeMap<Dungeon, Point> dungeons = new TreeMap<>();//contained dungeons and their location
    public Item[][] terrain;//layout of the biome itself
}
