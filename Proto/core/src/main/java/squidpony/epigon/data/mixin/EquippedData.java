package squidpony.epigon.data.mixin;

import squidpony.epigon.data.specific.Physical;
import squidpony.epigon.universe.ClothingSlot;
import squidpony.epigon.universe.JewelrySlot;
import squidpony.epigon.universe.OverArmorSlot;
import squidpony.epigon.universe.WieldSlot;
import squidpony.squidmath.EnumOrderedMap;

/**
 *
 * @author Eben Howard
 */
public interface EquippedData {

    public EnumOrderedMap<JewelrySlot, Physical> getJewelry();
    public EnumOrderedMap<ClothingSlot, Physical> getClothing();
    public EnumOrderedMap<ClothingSlot, Physical> getArmor();
    public EnumOrderedMap<OverArmorSlot, Physical> getOverArmor();
    public EnumOrderedMap<WieldSlot, Physical> getEquipment();
}
