package squidpony.epigon.mapping;

import squidpony.epigon.GauntRNG;
import squidpony.squidgrid.gui.gdx.Radiance;
import squidpony.epigon.data.LiveValue;
import squidpony.epigon.data.Physical;
import squidpony.epigon.data.Stat;
import squidpony.squidgrid.gui.gdx.SColor;

import java.util.ArrayList;
import java.util.List;

import static squidpony.squidgrid.gui.gdx.SColor.*;

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
    public Physical blockage;

    private final float tintColor;
    private final float tintAmount;
    
    public EpiTile()
    {
        tintColor = dbColors[System.identityHashCode(this) & 31];
        tintAmount = GauntRNG.nextFloat(System.identityHashCode(this)) * 0.3f;
    }
    
    public EpiTile(Physical floor)
    {
        this.floor = floor;
        tintColor = dbColors[floor.next(5)];
        tintAmount = floor.nextFloat(0.3f);
    }
    
    /**
     * Returns the total combined opacity of this cell, with 1.0 being fully opaque and 0.0 being
     * fully transparent.
     */
    public double opacity() {
        if (blockage != null) {
            if (blockage.creatureData == null) {
                LiveValue lv = blockage.stats.get(Stat.OPACITY);
                if (lv != null)
                    return Math.min(lv.actual(), 1.0);
            }
        }
        return 0.0;
    }

    /**
     * Returns the character representation of this tile.
     *
     * @return a char representing the contents of the tile, with creatures not rendered (they render themselves)
     */
    public char getSymbol() {
        //check in order of preference
        if (blockage != null) {
            if (blockage.creatureData != null && floor != null) {
                return floor.symbol;
            } else {
                return blockage.symbol;
            }
        } else if (!contents.isEmpty()) {
            return contents.get(0).symbol; // arbitrarily get first thing in list
        } else if (floor != null) {
            return floor.symbol;
        }

        return ' '; //default to no representation;
    }

    /**
     * Returns the character representation of this tile with no consideration for any Creature that may be present.
     *
     * @return
     */
    public char getSymbolUninhabited() {
        if (blockage != null && blockage.creatureData == null) {
                return blockage.symbol;
        } else if (!contents.isEmpty()){
            return contents.get(0).symbol; // arbitrarily get first thing in list
        } else if (floor != null) {
            return floor.symbol;
        }
        return ' '; //default to no representation
    }

    /**
     * Returns the background color this tile should use. If there is no specific background color
     * for this tile, then a fully transparent color is returned.
     *
     * @return
     */
    public float getBackgroundColor() {
        return 0x1.fffffep-126f; // fully transparent, but not equal to 0 (0 is used to leave the current background)
                //floor != null && floor.terrainData != null ? floor.terrainData.background : SColor.TRANSPARENT ;
    }

    public static final float[] dbColors = {
            DB_BLACK.toFloatBits(),
            DB_INK.toFloatBits(),
            DB_SEAL_BROWN.toFloatBits(),
            DB_CHESTNUT.toFloatBits(),
            DB_CAPPUCCINO.toFloatBits(),
            DB_PUMPKIN.toFloatBits(),
            DB_FAWN.toFloatBits(),
            DB_NUDE.toFloatBits(),
            DB_DAFFODIL.toFloatBits(),
            DB_KEY_LIME.toFloatBits(),
            DB_SHAMROCK.toFloatBits(),
            DB_JUNGLE.toFloatBits(),
            DB_OLIVE.toFloatBits(),
            DB_MUD.toFloatBits(),
            DB_SHADOW.toFloatBits(),
            DB_COBALT.toFloatBits(),
            DB_CERULEAN.toFloatBits(),
            DB_DENIM.toFloatBits(),
            DB_SKY_BLUE.toFloatBits(),
            DB_SEAFOAM.toFloatBits(),
            DB_PLATINUM.toFloatBits(),
            DB_WHITE.toFloatBits(),
            DB_STORM_CLOUD.toFloatBits(),
            DB_ELEPHANT.toFloatBits(),
            DB_GRAPHITE.toFloatBits(),
            DB_SOOT.toFloatBits(),
            DB_EGGPLANT.toFloatBits(),
            DB_BLOOD.toFloatBits(),
            DB_CORAL.toFloatBits(),
            DB_LAVENDER.toFloatBits(),
            DB_ARMY_GREEN.toFloatBits(),
            DB_COMPOST.toFloatBits()
    };

    public float getForegroundColor() {
        //check in order of preference
        if (blockage != null) {
            if (blockage.creatureData != null) {
                return SColor.lerpFloatColorsBlended(getCreature().color, tintColor, tintAmount);
            } else {
                return SColor.lerpFloatColorsBlended(blockage.color, tintColor, tintAmount);
            }
        } else if (!contents.isEmpty()) {
            return contents.get(0).color; // arbitrarily get first thing in list
        } else if (floor != null) {
            return SColor.lerpFloatColorsBlended(floor.color, tintColor, tintAmount);
        }

        return 0f;
    }

    public void remove(Physical phys) {
        if(phys != null)
        {
            if(phys.blocking)
            {
                if(phys.equals(blockage))
                    blockage = null;
            }
            else
            {
                contents.remove(phys);
            }
        }
    }

    public void add(Physical phys) {
        if(phys != null)
        {
            if(phys.blocking)
            {
                if(blockage == null)
                    blockage = phys;
            }
            else
            {
                contents.add(phys);
            }
        }
    }

    public void add(Iterable<Physical> adding) {
        for (Physical p : adding) {
            add(p);
        }
    }

    public Physical getCreature() {
        return blockage != null && blockage.creatureData != null ? blockage : null;
    }

    public Physical getLargeNonCreature() {
        return blockage != null && blockage.creatureData == null ? blockage : null;
    }

    public Radiance getAnyRadiance() {
        if (blockage != null) {
            if (blockage.creatureData != null && blockage.creatureData.lastUsedItem != null && blockage.creatureData.lastUsedItem.radiance != null) {
                return blockage.creatureData.lastUsedItem.radiance;
            }
            if (blockage.radiance != null) {
                return blockage.radiance;
            }
        }
        if (!contents.isEmpty() && contents.get(0).radiance != null) {
            return contents.get(0).radiance;
        }
        return floor == null ? null : floor.radiance;
    }
}
