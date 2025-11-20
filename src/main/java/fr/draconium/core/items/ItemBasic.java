package fr.draconium.core.items;

import fr.draconium.core.DraconiumCore;
import net.minecraft.item.Item;

public class ItemBasic extends Item
{
	public ItemBasic(String name)
	{
		this.setUnlocalizedName(name);
		this.setRegistryName(name);
	}
}
