package fr.draconium.core.tabs;

import fr.draconium.core.init.items.armors.PrimalAvatarsInit;
import fr.draconium.core.references.Reference;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
	public ItemStack createIcon()
	{
		return this.item != null ? this.item : this.block;
	}
	
	@Override
	public boolean hasSearchBar()
	{
		return false;
	}

    @Override
    @SideOnly(Side.CLIENT)
    public void displayAllRelevantItems(NonNullList<ItemStack> items) {
        // 1. On ajoute les items de base du mod (ceux qui ont gardé un CreativeTab)
        super.displayAllRelevantItems(items);

        // 2. On ajoute les Avatars UNIQUEMENT dans l'onglet Armor
        if (this.getTabLabel().equals("draconium_armor")) {
            // Cette boucle va suivre l'ordre de ton fichier PrimalAvatarsInit
            for (net.minecraft.item.Item item : fr.draconium.core.init.items.armors.PrimalAvatarsInit.ITEMS) {
                items.add(new ItemStack(item));
            }
        }
    }
	

	public ResourceLocation getBackgroundImage()
	{
		return new ResourceLocation(Reference.MODID, "textures/guis/background_creative_inventory_draconiumcore.png");
	}
}
