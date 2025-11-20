package fr.draconium.core.materials;

import net.minecraft.item.Item;
import net.minecraftforge.common.util.EnumHelper;

public class ToolsMaterial
{
	public static final Item.ToolMaterial TOOLS_MATERIAL_PYRONITE 	= EnumHelper.addToolMaterial("tools_material_pyronite", 4, 2890, 9.0F, 4.0F, 17);
	public static final Item.ToolMaterial HAMMER_MATERIAL_PYRONITE 	= EnumHelper.addToolMaterial("hammer_material_pyronite", 4, 2890, 1.0F, 2.0F, 17);
	public static final Item.ToolMaterial TOOLS_MATERIAL_DRACONIUM 	= EnumHelper.addToolMaterial("tools_material_draconium", 5, 3890, 10.0F, 6.0F, 25);
	public static final Item.ToolMaterial HAMMER_MATERIAL_DRACONIUM = EnumHelper.addToolMaterial("hammer_material_draconium", 5, 3890, 3.0F, 3.0F, 25);
}
