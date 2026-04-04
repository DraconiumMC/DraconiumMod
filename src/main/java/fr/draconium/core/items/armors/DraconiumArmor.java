package fr.draconium.core.items.armors;

import fr.draconium.core.DraconiumCore;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class DraconiumArmor extends ItemArmor
{

    public DraconiumArmor(String name, ArmorMaterial materialIn, int renderIndexIn, EntityEquipmentSlot slot)
    {
        super(materialIn, renderIndexIn, slot);
        this.setTranslationKey(name);
        this.setRegistryName(name);
        this.setCreativeTab(DraconiumCore.DRACONIUM_TAB_ARMORS);
    }

    @Override
    public void onArmorTick(World world, EntityPlayer player, ItemStack stack)
    {
        if(world.isRemote) return;

        switch(this.armorType)
        {
            case HEAD:
                applyEffect(player, MobEffects.NIGHT_VISION, 300, 0);
                break;

            case CHEST:
                applyEffect(player, MobEffects.RESISTANCE, 20, 0);
                break;

            case LEGS:
                applyEffect(player, MobEffects.SPEED, 20, 0);
                break;

            case FEET:
                applyEffect(player, MobEffects.HASTE, 20, 0);
                break;
        }
    }

    private void applyEffect(EntityPlayer player, net.minecraft.potion.Potion potion, int duration, int amplifier)
    {
        player.addPotionEffect(
                new PotionEffect(potion, duration, amplifier, true, false)
        );
    }
}
