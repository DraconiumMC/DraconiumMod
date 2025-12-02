package fr.draconium.core.items.others;

import fr.draconium.core.DraconiumCore;
import fr.draconium.core.init.items.others.OthersInit;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBucket;

public class ItemCustomBucket extends ItemBucket
{
    public ItemCustomBucket(String name, Block containedBlock)
    {
        super(containedBlock);
        this.setTranslationKey(name);
        this.setRegistryName(name);
        this.setCreativeTab(DraconiumCore.DRACONIUM_TAB_OTHERS); // Lui, on le met dans le tab !
        this.setContainerItem(Items.BUCKET);// Quand on le vide, il rend un sceau vide


    }
}