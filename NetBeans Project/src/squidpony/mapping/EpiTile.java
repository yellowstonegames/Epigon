package squidpony.mapping;

import java.util.Stack;
import squidpony.data.specific.Creature;
import squidpony.data.specific.Item;
import squidpony.data.specific.Physical;
import squidpony.data.specific.Terrain;
import squidpony.squidcolor.SColor;

/**
 * This class holds the objects in a single grid square.
 *
 * Through this class, one can get how the tile should be displayed, a compiled
 * description of what's in it, and the resistance factor to light, movement,
 * etc.
 *
 * @author Eben Howard - http://squidpony.com - howard@squidpony.com
 */
public class EpiTile {

    public Terrain floor;
    public Item largeObject;
    public Stack<Item> smallObjects = new Stack<>();
    public Creature creature;

    /**
     * Returns the resistance this tile has to the provided key.
     *
     * @param key
     * @return
     */
    public float geResistance(String key) {//TODO -- determine if resistance should be additive or just max value
        float resistance = 0f;
        Float check;
        if (floor != null) {
            resistance = floor.resistances.get(key);
        }
        if (largeObject != null) {
            check = largeObject.resistances.get(key);
            if (check != null) {
                resistance = Math.max(resistance, check);
            }
        }
        if (creature != null) {
            check = creature.resistances.get(key);
            if (check != null) {
                resistance = Math.max(resistance, creature.resistances.get(key));
            }
        }
        return resistance;
    }

    public boolean isPassable(String key) {
        return geResistance(key) < 1f;
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
            rep = creature.parent.symbol;
        } else if (largeObject != null) {
            rep = largeObject.parent.symbol;
        } else if (floor != null) {
            rep = floor.symbol;
        }

        return rep;
    }

    /**
     * Returns the background color this tile should use. If there is no
     * specific background color for this tile, then null is returned.
     *
     * @return
     */
    public SColor getBackgroundColor() {
        SColor back = null;//indicates that no particular color is used

        if (floor != null) {
            back = floor.color;
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
        } else if (floor != null) {
            fore = floor.color;
        }

        return fore;
    }

    public void remove(Physical phys) {
        if (phys instanceof Creature && (Creature) phys == creature) {
            creature = null;
        } else if (phys instanceof Item) {
            Item item = (Item) phys;
            if (item.large && largeObject == item) {
                largeObject = null;
            } else {
                smallObjects.remove(item);
            }
        }
    }

    /**
     * Adds the provided creature or item as appropriate. Overwrites the current
     * one if the item is large or a creature.
     *
     * @param phys
     */
    public void add(Physical phys) {
        if (phys instanceof Creature) {
            creature = (Creature) phys;
        } else if (phys instanceof Item) {
            Item item = (Item) phys;
            if (item.large) {
                largeObject = item;
            } else {
                smallObjects.add(item);
            }
        }
    }
}
