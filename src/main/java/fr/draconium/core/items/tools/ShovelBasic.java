package fr.draconium.core.items.tools;

import fr.draconium.core.DraconiumCore;
import net.minecraft.item.ItemSpade;

public class ShovelBasic extends ItemSpade
{

	public ShovelBasic(String name, ToolMaterial material)
	{
		super(material);
		this.setUnlocalizedName(name);
		this.setRegistryName(name);
		this.setCreativeTab(DraconiumCore.DRACONIUM_TAB_TOOLS);
	}
}
