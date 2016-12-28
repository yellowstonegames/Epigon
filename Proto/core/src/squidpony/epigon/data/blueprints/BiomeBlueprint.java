package squidpony.epigon.data.blueprints;

import java.util.ArrayList;

import squidpony.epigon.data.BiomeSize;
import squidpony.epigon.data.EpiData;
import squidpony.epigon.data.specific.Name;

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
    public ArrayList<ArrayList<BiomeBlueprint>> borders = new ArrayList<>();
    public ArrayList<DungeonBlueprint> possibleDungeons = new ArrayList<>();
    public ArrayList<TerrainBlueprint> possibleTerrains = new ArrayList<>();

    // TEMP - sample biome to be replaced
    public static final BiomeBlueprint SWAMP;

    static {
        SWAMP = new BiomeBlueprint();
        SWAMP.minimumSize = BiomeSize.MICRO;
        SWAMP.maximumSize = BiomeSize.MEDIUM;
        SWAMP.name = new Name("swamp");
        SWAMP.description = "dark slow moving shallow water";
    }
}
