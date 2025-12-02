package fr.draconium.core.items.others;

import fr.draconium.core.DraconiumCore;
import fr.draconium.core.entitys.EntitySwitchArrow;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemSwitchArrow extends ItemArrow {

    public ItemSwitchArrow(String name) {
        super();
        this.setTranslationKey(name); // Nom interne
        this.setRegistryName(name);   // Nom de registre
        this.setCreativeTab(DraconiumCore.DRACONIUM_TAB_OTHERS); // Onglet dans l’inventaire créatif
        this.setMaxStackSize(64); // Max stackable
    }

    @Override
    public EntityArrow createArrow(World worldIn, ItemStack stack, EntityLivingBase shooter) {
        return new EntitySwitchArrow(worldIn, shooter);
    }
}
