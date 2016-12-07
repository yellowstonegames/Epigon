package squidpony.epigon.data.specific;

import squidpony.epigon.data.blueprints.ItemBlueprint;
import squidpony.epigon.data.interfaces.*;
import squidpony.epigon.data.interfaces.Readable;

import java.util.ArrayList;

/**
 * Represents a specific object in the game world.
 *
 * @author Eben Howard - http://squidpony.com
 */
public class Item extends Physical {
    public ItemBlueprint parent;
    public ArrayList<Item> createdFrom;//only important items should track this
    public int fragility, maxCondition, currentCondition;
    public boolean large;
    //
    //possible interfaces
    public Ammunition ammunitionData = null;
    public Object containerData = null;
    public Interactable interactableData = null;
    public Liquid liquidData = null;
    public Readable readableData = null;
    public Wearable wearableData = null;
    public Wieldable wieldableData = null;
    public Zappable zappableData = null;

    boolean hasParent(ItemBlueprint blueprint) {
        return parent.hasParent(blueprint);
    }
    
}
