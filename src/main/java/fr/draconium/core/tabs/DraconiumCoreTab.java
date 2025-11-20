package fr.draconium.core.tabs;

import fr.draconium.core.references.Reference;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class DraconiumCoreTab extends CreativeTabs
{
	private ItemStack block;
	private ItemStack item;

	public DraconiumCoreTab(String name)
	{
	    super(name);
	}
	
	public DraconiumCoreTab setIcon(Block block)
	{
		this.block = new ItemStack(Item.getItemFromBlock(block));
		return this;
	}

	public DraconiumCoreTab setIcon(Item item)
	{
		this.item = new ItemStack(item);
		return this;
	}
	
	@Override
	public ItemStack getTabIconItem()
	{
		return this.item != null ? this.item : this.block;
	}
	
	@Override
	public boolean hasSearchBar()
	{
		return false;
	}
	
	@Override
	public ResourceLocation getBackgroundImage()
	{
		return new ResourceLocation(Reference.MODID, "textures/guis/background_creative_inventory_draconiumcore.png");
	}
}
