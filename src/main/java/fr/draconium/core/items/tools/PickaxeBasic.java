package fr.draconium.core.items.tools;

import fr.draconium.core.DraconiumCore;
import net.minecraft.item.ItemPickaxe;

public class PickaxeBasic extends ItemPickaxe
{

	public PickaxeBasic(String name, ToolMaterial material)
	{
		super(material);
		this.setUnlocalizedName(name);
		this.setRegistryName(name);
		this.setCreativeTab(DraconiumCore.DRACONIUM_TAB_TOOLS);
	}
	
}
