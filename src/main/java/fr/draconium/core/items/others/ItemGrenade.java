package fr.draconium.core.items.others;

import fr.draconium.core.DraconiumCore;
import fr.draconium.core.entitys.EntityGrenade;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

public class ItemGrenade extends Item
{
	public ItemGrenade(String name)
	{
		this.setUnlocalizedName(name);
		this.setRegistryName(name);
		this.setMaxStackSize(16);
		this.setCreativeTab(DraconiumCore.DRACONIUM_TAB_OTHERS);
	}
	
	/**
	 * @apiNote Action lorsque le joueur utilise l'item grenade
	 */
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
	{
		ItemStack itemstack = playerIn.getHeldItem(handIn);

		// Créer et lancer une entitée de grenade
		if (!worldIn.isRemote)
		{
			EntityGrenade grenade = new EntityGrenade(worldIn, playerIn);
			grenade.shoot(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0.0F, 1.5F, 1.0F);
			worldIn.spawnEntity(grenade);
		}

		// Jouer un son de lancer de grenade
		worldIn.playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ, SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.PLAYERS, 1.0F, 1.0F);

		// Retirer une grenade de l'inventaire du joueur
		itemstack.shrink(1);

		return super.onItemRightClick(worldIn, playerIn, handIn);
	}
}
