package squidpony.epigon.data.mixin;

import java.util.EnumMap;
import squidpony.epigon.data.specific.Physical;
import squidpony.epigon.universe.ClothingSlot;
import squidpony.epigon.universe.WieldSlot;
import squidpony.epigon.universe.JewelrySlot;
import squidpony.epigon.universe.OverArmorSlot;

/**
 * A humanoid creature which can wear and wield things.
 */
public class Humanoid extends Creature {

    public EnumMap<JewelrySlot, Physical> jewelry = new EnumMap<>(JewelrySlot.class);
    public EnumMap<ClothingSlot, Physical> clothing = new EnumMap<>(ClothingSlot.class);
    public EnumMap<ClothingSlot, Physical> armor = new EnumMap<>(ClothingSlot.class);
    public EnumMap<OverArmorSlot, Physical> overArmor = new EnumMap<>(OverArmorSlot.class);
    public EnumMap<WieldSlot, Physical> equipment = new EnumMap<>(WieldSlot.class);
}
