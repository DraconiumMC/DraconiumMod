package fr.draconium.core.items.foods;

import fr.draconium.core.DraconiumCore;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class FoodEffect extends ItemFood
{

	/**
	 * 
	 * @apiNote name = item name
	 * @apiNote amount = number heal
	 */
	public FoodEffect(String name, int amount)
	{
		// isWolfFood = if wolf like apple (false)
		super(amount, false);
		this.setUnlocalizedName(name);
		this.setRegistryName(name);
		this.setAlwaysEdible();
		this.setCreativeTab(DraconiumCore.DRACONIUM_TAB_FOOD);
	}
	
	@Override
	protected void onFoodEaten(ItemStack itemStack, World world, EntityPlayer player)
	{
		if (!world.isRemote)
		{
			player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 100, 2));
			player.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 2400, 1));
		}
		super.onFoodEaten(itemStack, world, player);
	}
}
