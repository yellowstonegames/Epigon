package squidpony.epigon.mapping;

import java.util.ArrayList;
import java.util.List;

import squidpony.Maker;
import squidpony.epigon.data.Physical;
import squidpony.epigon.data.control.RecipeMixer;
import squidpony.epigon.data.control.DataPool;
import squidpony.epigon.data.control.DataStarter;
import squidpony.epigon.data.quality.Inclusion;
import squidpony.squidgrid.gui.gdx.SColor;
import squidpony.squidmath.Coord;

/**
 * Adds fun frilly things to maps.
 */
public class MapDecorator {

    public DataStarter handBuilt;

    public MapDecorator(DataStarter handBuilt) {
        this.handBuilt = handBuilt;
    }

    public void placeDoor(EpiTile tile) {
        Physical adding = RecipeMixer.buildPhysical(tile.floor.terrainData.stone);
        List<Physical> adds = RecipeMixer.mix(handBuilt.doorRecipe, Maker.makeList(adding), new ArrayList<>(0));
        Physical door = adds.get(0);
        handBuilt.setDoorOpen(door, handBuilt.rng.nextBoolean());
        tile.add(door);
    }

    public void placeWater(EpiTile tile) {
        tile.floor = RecipeMixer.buildPhysical(handBuilt.water);
    }

    public void placeLava(EpiTile tile) {
        tile.floor = RecipeMixer.buildPhysical(handBuilt.lava);
        tile.floor.radiance.color = SColor.lerpFloatColors(SColor.CW_ORANGE.toFloatBits(), SColor.CW_YELLOW.toFloatBits(), tile.floor.nextFloat() * (tile.floor.nextFloat(0.75F) + 0.25F));
        tile.floor.radiance.delay = tile.floor.nextFloat();
    }

    public void placeMud(EpiTile tile) {
        tile.floor = RecipeMixer.buildPhysical(handBuilt.mud);
    }

    public void placeStairs(EpiMap top, EpiMap bottom, Coord c) {
        placeStairs(top.contents[c.x][c.y], false);
        placeStairs(bottom.contents[c.x][c.y], true);
    }

    public void placeStairs(EpiTile tile, boolean up) {
        Physical adding;
        if (tile.floor != null) {
            if (tile.floor.terrainData != null && tile.floor.terrainData.stone != null) {
                adding = RecipeMixer.buildPhysical(tile.floor.terrainData.stone);
            } else {
                adding = tile.floor;
            }
        } else {
            adding = RecipeMixer.buildPhysical(Inclusion.DIAMOND); // TODO - replace with base of whatever is appropriate
        }
        if (up) {
            tile.contents.addAll(RecipeMixer.mix(handBuilt.upStairRecipe, Maker.makeList(adding), new ArrayList<>(0)));
        } else {
            tile.contents.addAll(RecipeMixer.mix(handBuilt.downStairRecipe, Maker.makeList(adding), new ArrayList<>(0)));
        }
    }

    public void placeWall(EpiTile tile) {
        Physical adding = DataPool.instance().getWall(tile.floor.terrainData.stone);
        tile.add(adding);
    }

}
