package squidpony.epigon.data.mixin;

import squidpony.epigon.data.specific.Physical;
import squidpony.epigon.universe.ClothingSlot;
import squidpony.epigon.universe.JewelrySlot;
import squidpony.epigon.universe.OverArmorSlot;
import squidpony.epigon.universe.WieldSlot;
import squidpony.squidmath.EnumOrderedMap;

/**
 * A humanoid creature which can wear and wield things.
 */
public class Humanoid implements EquippedData {

    public EnumOrderedMap<JewelrySlot, Physical> jewelry = new EnumOrderedMap<>(JewelrySlot.class);
    public EnumOrderedMap<ClothingSlot, Physical> clothing = new EnumOrderedMap<>(ClothingSlot.class);
    public EnumOrderedMap<ClothingSlot, Physical> armor = new EnumOrderedMap<>(ClothingSlot.class);
    public EnumOrderedMap<OverArmorSlot, Physical> overArmor = new EnumOrderedMap<>(OverArmorSlot.class);
    public EnumOrderedMap<WieldSlot, Physical> equipment = new EnumOrderedMap<>(WieldSlot.class);

    @Override
    public EnumOrderedMap<JewelrySlot, Physical> getJewelry() {
        return jewelry;
    }

    @Override
    public EnumOrderedMap<ClothingSlot, Physical> getClothing() {
        return clothing;
    }

    @Override
    public EnumOrderedMap<ClothingSlot, Physical> getArmor() {
        return armor;
    }

    @Override
    public EnumOrderedMap<OverArmorSlot, Physical> getOverArmor() {
        return overArmor;
    }

    @Override
    public EnumOrderedMap<WieldSlot, Physical> getEquipment() {
        return equipment;
    }

}
