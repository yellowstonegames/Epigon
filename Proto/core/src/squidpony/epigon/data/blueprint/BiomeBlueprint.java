package squidpony.epigon.data.blueprint;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import squidpony.epigon.data.BiomeSize;
import squidpony.epigon.data.EpiData;

/**
 * Base information for world level biomes. The lists of lists of biomes that can border a specific
 * biome indicates what has to be present for this biome to be placed. All the biomes in at least
 * one of the sublists must be present.
 *
 * Note that the world should start with one biome ignoring its requirements so that more can be
 * added after that one.
 *
 * An arbitrary example might be that a lake can have mountain and glacier as a border or swamp and
 * river, but must have one of those two sets to exist.
 *
 * @author Eben Howard - http://squidpony.com
 */
public class BiomeBlueprint extends EpiData {

    public BiomeBlueprint parent;
    public BiomeSize minimumSize;
    public BiomeSize maximumSize;
    public List<Set<BiomeBlueprint>> borderRequirements = new LinkedList<>();
    public Set<DungeonBlueprint> possibleDungeons = new HashSet<>();
    public Set<TerrainBlueprint> possibleTerrains = new HashSet<>();
}
