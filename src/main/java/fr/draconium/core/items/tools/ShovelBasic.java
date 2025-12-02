package fr.draconium.core.items.tools;

import fr.draconium.core.DraconiumCore;
import net.minecraft.item.ItemSpade;

public class ShovelBasic extends ItemSpade
{

	public ShovelBasic(String name, ToolMaterial material)
	{
		super(material);
		this.setTranslationKey(name);
		this.setRegistryName(name);
		this.setCreativeTab(DraconiumCore.DRACONIUM_TAB_TOOLS);
	}
}
