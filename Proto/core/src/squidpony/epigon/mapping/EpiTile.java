package squidpony.epigon.mapping;

import squidpony.epigon.data.specific.Physical;
import squidpony.epigon.universe.LiveValue;
import squidpony.epigon.universe.Stat;
import squidpony.squidgrid.gui.gdx.SColor;

import java.util.ArrayList;
import java.util.List;

/**
 * This class holds the objects in a single grid square.
 *
 * Through this class, one can get how the tile should be displayed, a compiled description of
 * what's in it, and the resistance factor to light, movement, etc.
 *
 * @author Eben Howard - http://squidpony.com
 */
public class EpiTile {

    public Physical floor;
    public List<Physical> contents = new ArrayList<>();

    /**
     * Returns the total combined opacity of this cell, with 1.0 being fully opaque and 0.0 being
     * fully transparent.
     */
    public double opacity() {
        double resistance = 0f;
        LiveValue lv = new LiveValue(resistance);
        Stat key = Stat.OPACITY;
        if (getLargeObject() != null) {
            resistance += getLargeObject().stats.getOrDefault(key, lv).actual;
        }
        if (getCreature() != null) {
            resistance += getCreature().stats.getOrDefault(key, lv).actual;
        }
        return Math.min(resistance, 1.0);
    }

    /**
     * Returns the character representation of this tile.
     *
     * @return
     */
    public char getSymbol() {
        char rep = ' ';//default to no representation
        Physical temp;
        //check in order of preference
        if ((temp = getCreature()) != null) {
            rep = temp.symbol;
        } else if ((temp = getLargeObject()) != null) {
            rep = temp.symbol;
        } else if (!contents.isEmpty()){
            rep = contents.get(0).symbol; // arbitrarily get first thing in list
        } else if (floor != null) {
            rep = floor.symbol;
        }

        return rep;
    }

    /**
     * Returns the character representation of this tile with no consideration for any Creature that may be present.
     *
     * @return
     */
    public char getSymbolUninhabited() {
        char rep = ' ';//default to no representation
        Physical temp;
        //check in order of preference
        if ((temp = getCreature()) != null) {
            contents.remove(temp);
        }
        if (getLargeObject() != null) {
            rep = getLargeObject().symbol;
        } else if (!contents.isEmpty()){
            rep = contents.get(0).symbol; // arbitrarily get first thing in list
        } else if (floor != null) {
            rep = floor.symbol;
        }
        if(temp != null)
            contents.add(0, temp);

        return rep;
    }

    /**
     * Returns the background color this tile should use. If there is no specific background color
     * for this tile, then null is returned.
     *
     * @return
     */
    public SColor getBackgroundColor() {
        return floor == null || floor.terrainData != null ? null : floor.terrainData.background;
    }

    public SColor getForegroundColor() {
        SColor fore = null;//indicates that no particular color is used

        //check in order of preference
        if (getCreature() != null) {
            fore = getCreature().color;
        } else if (getLargeObject() != null) {
            fore = getLargeObject().color;
        } else if (!contents.isEmpty()){
            fore = contents.get(0).color; // arbitrarily get first thing in list
        } else if (floor != null) {
            fore = floor.color;
        }

        return fore;
    }

    public void remove(Physical phys) {
        contents.remove(phys);
    }

    public void add(Physical phys) {
        // TODO - check that it can be added
        contents.add(phys);
    }

    public void add(List<Physical> adding) {
        for (Physical p : adding) {
            add(p);
        }
    }

    public Physical getCreature() {
        return contents.stream().filter(c -> c.creatureData != null).findAny().orElse(null);
    }

    public Physical getLargeObject() {
        return contents.stream().filter(l -> l.large).findAny().orElse(null);
    }
    public Physical getLargeNonCreature() {
        return contents.stream().filter(l -> l.large && l.creatureData == null).findAny().orElse(null);
    }
}
