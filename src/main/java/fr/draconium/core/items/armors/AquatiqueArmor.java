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

public class AquatiqueArmor extends ItemArmor
{
	
	public AquatiqueArmor(String name, ArmorMaterial materialIn, int renderIndexIn, EntityEquipmentSlot equipmentSlotIn)
	{
		super(materialIn, renderIndexIn, equipmentSlotIn);
		this.setUnlocalizedName(name);
		this.setRegistryName(name);
		this.setCreativeTab(DraconiumCore.DRACONIUM_TAB_ARMORS);
	}
	
	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack)
	{
		if (this.isAmrorComplet(player) == true) this.handleAquatiqueArmorEffects(player);
	}
	
	
	protected boolean isAmrorComplet(EntityPlayer player)
	{
		return player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem().equals(ArmorsInit.AQUATIQUE_HELMET);
				/*
				&& player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem().equals(ArmorsInit.AQUATIQUE_CHESTPLATE)
				&& player.getItemStackFromSlot(EntityEquipmentSlot.LEGS).getItem().equals(ArmorsInit.AQUATIQUE_LEGGINGS)
				&& player.getItemStackFromSlot(EntityEquipmentSlot.FEET).getItem().equals(ArmorsInit.AQUATIQUE_BOOTS);
				*/
	}
	
	private void handleAquatiqueArmorEffects(EntityPlayer player)
	{
		player.addPotionEffect(new PotionEffect(MobEffects.WATER_BREATHING, 300, 0, true, true)); 	// 300 ticks 	= 15 secondes
	}
	
	
}
