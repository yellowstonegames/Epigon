package squidpony.epigon.data.mixin;

import java.util.EnumMap;
import squidpony.epigon.data.specific.Physical;
import squidpony.epigon.universe.ClothingSlot;
import squidpony.epigon.universe.JewelrySlot;
import squidpony.epigon.universe.OverArmorSlot;
import squidpony.epigon.universe.WieldSlot;

/**
 *
 * @author Eben Howard
 */
public interface EquippedData {

    public EnumMap<JewelrySlot, Physical> getJewelry();
    public EnumMap<ClothingSlot, Physical> getClothing();
    public EnumMap<ClothingSlot, Physical> getArmor();
    public EnumMap<OverArmorSlot, Physical> getOverArmor();
    public EnumMap<WieldSlot, Physical> getEquipment();
}
