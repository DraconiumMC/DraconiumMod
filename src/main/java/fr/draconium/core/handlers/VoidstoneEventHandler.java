package fr.draconium.core.handlers;

import fr.draconium.core.items.others.ItemVoidstone;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class VoidstoneEventHandler {

    @SubscribeEvent
    public void onHarvest(BlockEvent.HarvestDropsEvent event) {
        if (event.getHarvester() == null) return;

        Block block = event.getState().getBlock();
        // On vérifie si ce qui tombe est de la pierre ou de la cobble
        if (block == Blocks.STONE || block == Blocks.COBBLESTONE) {
            EntityPlayer player = event.getHarvester();

            for (ItemStack invStack : player.inventory.mainInventory) {
                if (!invStack.isEmpty() && invStack.getItem() instanceof ItemVoidstone) {
                    // On récupère le nombre de blocs qui allaient tomber (ex: 9 avec un hammer)
                    int amount = event.getDrops().size();
                    if (amount == 0) amount = 1; // Sécurité

                    ((ItemVoidstone) invStack.getItem()).addCobble(invStack, amount);

                    // On vide la liste des drops pour que rien ne tombe au sol
                    event.getDrops().clear();
                    break;
                }
            }
        }
    }
}