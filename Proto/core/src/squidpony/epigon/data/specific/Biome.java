package squidpony.epigon.data.specific;

import java.util.TreeMap;

import squidpony.epigon.data.BiomeSize;
import squidpony.epigon.data.EpiData;
import squidpony.epigon.data.blueprints.BiomeBlueprint;

import squidpony.squidmath.Coord;

/**
 * Represents a specific region of land.
 *
 * @author Eben Howard - http://squidpony.com
 */
public class Biome extends EpiData {

    public BiomeBlueprint parent;
    public BiomeSize size;//approximation of size
    public TreeMap<Dungeon, Coord> dungeons = new TreeMap<>();//contained dungeons and their location
    public Terrain[][] terrain;//layout of the biome itself
}
