package squidpony.epigon.mapping;

import java.util.Stack;
import squidpony.epigon.data.blueprint.TerrainBlueprint;

import squidpony.squidgrid.gui.gdx.SColor;

import squidpony.epigon.data.specific.Physical;
import squidpony.epigon.data.specific.Terrain;
import squidpony.epigon.universe.Element;
import squidpony.epigon.universe.LiveValue;

/**
 * This class holds the objects in a single grid square.
 *
 * Through this class, one can get how the tile should be displayed, a compiled description of
 * what's in it, and the resistance factor to light, movement, etc.
 *
 * @author Eben Howard - http://squidpony.com
 */
public class EpiTile {

    public TerrainBlueprint floorBlueprint; // Goes away if the actual floor is uniqued in some way
    public Terrain floor;
    public TerrainBlueprint wallBlueprint; // Counts as the large object here, instantiate when needed
    public Physical largeObject;
    public Stack<Physical> smallObjects = new Stack<>();
    public Physical creature;

    /**
     * Returns the resistance this tile has to the provided key.
     *
     * @param key
     * @return
     */
    public double resistance(Element key) {//TODO -- determine if resistance should be additive or just max baseValue
        double resistance = 0f;
        Double check;
        LiveValue lv = new LiveValue(resistance);
        if (floor != null) {
            resistance = floor.passthroughResistances.getOrDefault(key, lv).actual;
        } else if (floorBlueprint != null) {
            resistance = floorBlueprint.passthroughResistances.getOrDefault(key, lv).actual;
        }
        if (largeObject != null) {
            check = largeObject.passthroughResistances.getOrDefault(key, lv).actual;
            resistance = Math.max(resistance, check);
        } else if (wallBlueprint != null) {
            check = wallBlueprint.passthroughResistances.getOrDefault(key, lv).actual;
            resistance = Math.max(resistance, check);
        }
        if (creature != null) {
            check = creature.passthroughResistances.getOrDefault(key, lv).actual;
            resistance = Math.max(resistance,check);
        }
        return resistance;
    }

    public boolean isPassable(Element key) {
        return key == null || resistance(key) < 1;
    }

    /**
     * Returns the character representation of this tile.
     *
     * @return
     */
    public char getSymbol() {
        char rep = ' ';//default to no representation

        //check in order of preference
        if (creature != null) {
            rep = creature.symbol;
        } else if (largeObject != null) {
            rep = largeObject.symbol;
        } else if (wallBlueprint != null) {
            rep = wallBlueprint.symbol;
        } else if (floor != null) {
            rep = floor.symbol;
        } else if (floorBlueprint != null) {
            rep = floorBlueprint.symbol;
        }

        return rep;
    }

    /**
     * Returns the background color this tile should use. If there is no specific background color
     * for this tile, then null is returned.
     *
     * @return
     */
    public SColor getBackgroundColor() {
        SColor back = null;//indicates that no particular color is used

        if (floor != null) {
            back = floor.background;
        } else if (floorBlueprint != null) {
            back = floorBlueprint.background;
        }

        return back;
    }

    public SColor getForegroundColor() {
        SColor fore = null;//indicates that no particular color is used

        //check in order of preference
        if (creature != null) {
            fore = creature.color;
        } else if (largeObject != null) {
            fore = largeObject.color;
        } else if (wallBlueprint != null) {
            fore = wallBlueprint.color;
        } else if (floor != null) {
            fore = floor.color;
        } else if (floorBlueprint != null) {
            fore = floorBlueprint.color;
        }

        return fore;
    }

    public void remove(Physical phys) {
        if (phys == creature) {
            creature = null;
        } else if (largeObject == phys) {
            largeObject = null;
        } else {
            smallObjects.remove(phys);
        }
    }

    /**
     * Adds the provided creature or item as appropriate. Overwrites the current one if the item is
     * large or a creature.
     *
     * @param phys
     */
    public void add(Physical phys) {
        if (phys.creatureData != null) {
            creature = phys;
        } else if (phys.parent.large) {
            largeObject = phys;
        } else {
            smallObjects.add(phys);
        }
    }
}
