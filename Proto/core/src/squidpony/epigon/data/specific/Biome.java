package squidpony.epigon.data.specific;

import squidpony.epigon.data.mixin.Terrain;
import squidpony.epigon.data.BiomeSize;
import squidpony.epigon.data.EpiData;
import squidpony.epigon.data.blueprint.BiomeBlueprint;

import squidpony.squidmath.Coord;
import squidpony.squidmath.OrderedMap;

/**
 * Represents a specific region of land.
 *
 * @author Eben Howard - http://squidpony.com
 */
public class Biome extends EpiData {

    public BiomeBlueprint parent;
    public BiomeSize size;//approximation of size
    public OrderedMap<Dungeon, Coord> dungeons;//contained dungeons and their location
    public Terrain[][] terrain;//layout of the biome itself

}
