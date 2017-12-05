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

    EnumOrderedMap<JewelrySlot, Physical> getJewelry();
    EnumOrderedMap<ClothingSlot, Physical> getClothing();
    EnumOrderedMap<ClothingSlot, Physical> getArmor();
    EnumOrderedMap<OverArmorSlot, Physical> getOverArmor();
    EnumOrderedMap<WieldSlot, Physical> getEquipment();
}
