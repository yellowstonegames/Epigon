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

    // TEMP - sample biomes to be replaced
    public static final BiomeBlueprint SWAMP, PLAIN, HILL, MOUNTAIN, DESERT, OCEAN, LAKE;

    static {
        SWAMP = new BiomeBlueprint();
        SWAMP.minimumSize = BiomeSize.MICRO;
        SWAMP.maximumSize = BiomeSize.MEDIUM;
        SWAMP.name = "swamp";
        SWAMP.description = "dark slow moving shallow water";

        PLAIN = new BiomeBlueprint();
        PLAIN.minimumSize = BiomeSize.MEDIUM;
        PLAIN.maximumSize = BiomeSize.GIGANTIC;
        PLAIN.name = "pains";

        HILL = new BiomeBlueprint();
        HILL.minimumSize = BiomeSize.MICRO;
        HILL.maximumSize = BiomeSize.MEDIUM;
        HILL.name = "hill";

        MOUNTAIN = new BiomeBlueprint();
        MOUNTAIN.minimumSize = BiomeSize.MICRO;
        MOUNTAIN.maximumSize = BiomeSize.MEDIUM;
        MOUNTAIN.name = "mountain";

        DESERT = new BiomeBlueprint();
        DESERT.minimumSize = BiomeSize.MICRO;
        DESERT.maximumSize = BiomeSize.MEDIUM;
        DESERT.name = "desert";

        OCEAN = new BiomeBlueprint();
        OCEAN.minimumSize = BiomeSize.MICRO;
        OCEAN.maximumSize = BiomeSize.MEDIUM;
        OCEAN.name = "ocean";

        LAKE = new BiomeBlueprint();
        LAKE.minimumSize = BiomeSize.MICRO;
        LAKE.maximumSize = BiomeSize.MEDIUM;
        LAKE.name = "lake";
    }
}
