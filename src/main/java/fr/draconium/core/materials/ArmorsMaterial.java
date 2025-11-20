package fr.draconium.core.materials;

import fr.draconium.core.references.Reference;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraftforge.common.util.EnumHelper;

public class ArmorsMaterial
{
	public static final ArmorMaterial MATERIAL_PYRONITE	= EnumHelper.addArmorMaterial("armor_material_pyronite", Reference.MODID + ":azurite", 175, new int[] { 4, 7, 9, 4 }, 17, SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, 1.0F);
	public static final ArmorMaterial MATERIAL_DRACONIUM 	= EnumHelper.addArmorMaterial("armor_material_draconium", Reference.MODID + ":draconium", 220, new int[] { 5, 8, 10, 5 }, 25, SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, 2.0F);
	public static final ArmorMaterial MATERIAL_AQUATIQUE 	= EnumHelper.addArmorMaterial("armor_material_aquatique", Reference.MODID + ":aquatique", 100, new int[] { 2, 5, 7, 2 }, 17, SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, 0.0F);
	public static final ArmorMaterial MATERIAL_DRACONIQUE 	= EnumHelper.addArmorMaterial("armor_material_draconique", Reference.MODID + ":draconique", 340, new int[] { 7, 10, 12, 7 }, 30, SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, 4.0F);
}
