package squidpony.data.blueprints;

import java.util.ArrayList;
import squidpony.data.BiomeSize;
import squidpony.data.EpiData;
import squidpony.data.generic.TerrainBlueprint;
import squidpony.squidcolor.SColor;

/**
 * Base information for world level biomes. The lists of lists of biomes that
 * can border a specific biome indicates what has to be present for this biome
 * to be placed. All the biomes in at least one of the sublists must be present.
 *
 * Note that the world should start with one biome ignoring its requirements so
 * that more can be added after that one.
 *
 * An arbitrary example might be that a lake can have mountain and glacier as a
 * border or swamp and river, but must have one of those two sets to exist.
 *
 * @author Eben Howard - http://squidpony.com - howard@squidpony.com
 */
public class BiomeBlueprint extends EpiData {

    public BiomeBlueprint parent;
    public BiomeSize minimumSize;
    public BiomeSize maximumSize;
    public ArrayList<ArrayList<BiomeBlueprint>> borders = new ArrayList<>();
    public ArrayList<DungeonBlueprint> possibleDungeons = new ArrayList<>();
    public ArrayList<TerrainBlueprint> possibleTerrains = new ArrayList<>();
    public static final BiomeBlueprint SWAMP = new BiomeBlueprint(null, BiomeSize.MICRO, BiomeSize.MEDIUM, "swamp", "swamp", "swamps", "dark slow moving shallow water", "", SColor.MOUSY_INDIGO);

    public BiomeBlueprint(BiomeBlueprint parent, BiomeSize minimumSize, BiomeSize maximumSize, String internalName, String name, String plural, String description, String notes, SColor color) {
        super(internalName, name, plural, description, notes, color);
        this.parent = parent;
        this.minimumSize = minimumSize;
        this.maximumSize = maximumSize;
    }
    
}
