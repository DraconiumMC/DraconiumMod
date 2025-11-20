package fr.draconium.core.items.armors;

import fr.draconium.core.DraconiumCore;
import fr.draconium.core.init.items.armors.ArmorsInit;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class DraconiqueArmor extends ItemArmor
{
	public DraconiqueArmor(String name, ArmorMaterial materialIn, int renderIndexIn, EntityEquipmentSlot equipmentSlotIn)
	{
		super(materialIn, renderIndexIn, equipmentSlotIn);
		this.setUnlocalizedName(name);
		this.setRegistryName(name);
		this.setCreativeTab(DraconiumCore.DRACONIUM_TAB_ARMORS);
	}
	
	public static boolean isAmrorComplet(EntityPlayer player)
	{
	    return player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem().equals(ArmorsInit.DRACONIQUE_HELMET) &&
	    		player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem().equals(ArmorsInit.DRACONIQUE_CHESTPLATE) &&
	    		player.getItemStackFromSlot(EntityEquipmentSlot.LEGS).getItem().equals(ArmorsInit.DRACONIQUE_LEGGINGS) &&
	    		player.getItemStackFromSlot(EntityEquipmentSlot.FEET).getItem().equals(ArmorsInit.DRACONIQUE_BOOTS);
	}
	
	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack)
	{
		if (this.isAmrorComplet(player) == true) this.handleDraconiqueArmorEffects(player);
		else return;
	}
	
	private void handleDraconiqueArmorEffects(EntityPlayer player)
	{
		player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 300, 0, true, true)); 		// 300 ticks 	= 15 secondes
		player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 20, 0, true, true)); 		// 20 ticks 	= 1 seconde
		player.addPotionEffect(new PotionEffect(MobEffects.SPEED, 20, 0, true, true)); 				// 20 ticks 	= 1 seconde
		player.addPotionEffect(new PotionEffect(MobEffects.HASTE, 20, 0, true, true)); 				// 20 ticks 	= 1 seconde
	}
}
