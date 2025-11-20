package fr.draconium.core.items.others;

import fr.draconium.core.DraconiumCore;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemStickOfGod extends Item
{
	private int COOLDOWN_TICKS = 120 * 20;
	private int MAX_DURABILITY = 11;
	
	public ItemStickOfGod(String name)
	{
		this.setUnlocalizedName(name);
		this.setRegistryName(name);
		this.setMaxDamage(MAX_DURABILITY - 1); // Adjusted to start at 0 and go up to MAX_DURABILITY - 1
		this.setMaxStackSize(1);
		this.setCreativeTab(DraconiumCore.DRACONIUM_TAB_STIKCS);
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
	{
		ItemStack itemstack = playerIn.getHeldItem(handIn);

		if (!worldIn.isRemote)
		{
			if (!playerIn.getCooldownTracker().hasCooldown(this) && itemstack.getItemDamage() < itemstack.getMaxDamage())
			{
				playerIn.getCooldownTracker().setCooldown(this, COOLDOWN_TICKS);
				itemstack.damageItem(1, playerIn);
				playerIn.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 1850, 2));
				playerIn.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 4440, 1));
				playerIn.addPotionEffect(new PotionEffect(MobEffects.SPEED, 100, 2));
				return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
			}
			return new ActionResult<>(EnumActionResult.PASS, itemstack);
		}
		return new ActionResult<>(EnumActionResult.PASS, itemstack);
	}
	
}
