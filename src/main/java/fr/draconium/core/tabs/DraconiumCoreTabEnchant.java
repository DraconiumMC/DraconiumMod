package fr.draconium.core.tabs;

import fr.draconium.core.DraconiumCore;
import fr.draconium.core.init.enchants.EnchantementsInit;
import fr.draconium.core.references.Reference;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

public class DraconiumCoreTabEnchant extends CreativeTabs
{
	private ItemStack block;
	private ItemStack item;

	public DraconiumCoreTabEnchant(String name)
	{
	    super(name);
	}
	
	public DraconiumCoreTabEnchant setIcon(Block block)
	{
		this.block = new ItemStack(Item.getItemFromBlock(block));
		return this;
	}

	public DraconiumCoreTabEnchant setIcon(Item item)
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
	public ResourceLocation getBackgroundImage()
	{
		return new ResourceLocation(Reference.MODID, "textures/guis/background_creative_inventory_draconiumcore.png");
	}
	
	public static ItemStack createEnchantedBook(Enchantment enchantment, int level)
	{
	    ItemStack enchantedBook = new ItemStack(Items.ENCHANTED_BOOK);
	    NBTTagList enchantments = new NBTTagList();

	    NBTTagCompound enchantmentTag = new NBTTagCompound();
	    enchantmentTag.setShort("id", (short) Enchantment.getEnchantmentID(enchantment));
	    enchantmentTag.setShort("lvl", (short) level);
	    enchantments.appendTag(enchantmentTag);

	    enchantedBook.getOrCreateSubCompound("StoredEnchantments").setTag("StoredEnchantments", enchantments);
	    return enchantedBook;
	}
	
	@Override
	public void displayAllRelevantItems(NonNullList<ItemStack> items)
	{
	    super.displayAllRelevantItems(items);

	    // Ajout du livre enchanté avec EnchantRange au créatif
	    ItemStack enchantedBook = this.createEnchantedBook(EnchantementsInit.RANGE_ENCHANT, 1);
	    items.add(enchantedBook);
	}
}
