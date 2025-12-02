package fr.draconium.core.items.tools;

import fr.draconium.core.DraconiumCore;
import net.minecraft.item.ItemAxe;

public class AxeBasic extends ItemAxe
{
	public AxeBasic(String name, ToolMaterial material)
	{
		super(material, 9.0F, -3.0F);
		this.setTranslationKey(name);
		this.setRegistryName(name);
		this.setCreativeTab(DraconiumCore.DRACONIUM_TAB_TOOLS);
	}
}
