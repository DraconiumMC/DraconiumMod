package fr.draconium.core.items.swords;

import fr.draconium.core.DraconiumCore;
import net.minecraft.item.ItemSword;

public class SwordBasic extends ItemSword
{

	public SwordBasic(String name, ToolMaterial material)
	{
		super(material);
		this.setUnlocalizedName(name);
		this.setRegistryName(name);
		this.setCreativeTab(DraconiumCore.DRACONIUM_TAB_ARMORS);
	}

}
