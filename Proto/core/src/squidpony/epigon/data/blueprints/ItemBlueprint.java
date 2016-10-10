package squidpony.epigon.data.blueprints;

import squidpony.epigon.data.interfaceBlueprints.AmmunitionBlueprint;
import squidpony.epigon.data.interfaceBlueprints.ContainerBlueprint;
import squidpony.epigon.data.interfaceBlueprints.InteractableBlueprint;
import squidpony.epigon.data.interfaceBlueprints.LiquidBlueprint;
import squidpony.epigon.data.interfaceBlueprints.ReadableBlueprint;
import squidpony.epigon.data.interfaceBlueprints.WearableBlueprint;
import squidpony.epigon.data.interfaceBlueprints.WieldableBlueprint;
import squidpony.epigon.data.interfaceBlueprints.ZappableBlueprint;

/**
 * Blueprint to create Items from.
 *
 * @author Eben Howard - http://squidpony.com
 */
public class ItemBlueprint extends PhysicalBlueprint {

    public ItemBlueprint parent;
    public int fragility, maxCondition;
    public int value;
    public boolean large;
    //
    //possible interfaces
    public AmmunitionBlueprint ammunitionData = null;
    public ContainerBlueprint containerData = null;
    public InteractableBlueprint interactableData = null;
    public LiquidBlueprint liquidData = null;
    public ReadableBlueprint readableData = null;
    public WearableBlueprint wearableData = null;
    public WieldableBlueprint wieldableData = null;
    public ZappableBlueprint zappableData = null;

    public boolean hasParent(ItemBlueprint blueprint) {
        if (this == blueprint) {
            return true;
        } else if (this.parent == null) {
            return false;
        } else {
            return parent.hasParent(blueprint);
        }
    }
}
