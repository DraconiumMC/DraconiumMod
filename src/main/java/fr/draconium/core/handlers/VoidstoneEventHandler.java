package fr.draconium.core.handlers;

import fr.draconium.core.items.others.ItemVoidstone;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class VoidstoneEventHandler {

    @SubscribeEvent
    public void onHarvest(BlockEvent.HarvestDropsEvent event) {
        EntityPlayer player = event.getHarvester();
        if (player == null || event.isCanceled()) return;

        Block block = event.getState().getBlock();

        // 1. Optimisation : On vérifie le bloc AVANT de chercher dans l'inventaire
        if (block == Blocks.STONE || block == Blocks.COBBLESTONE) {

            // 2. Priorité à la main : C'est beaucoup plus efficient
            ItemStack voidStack = getVoidstone(player);

            if (!voidStack.isEmpty()) {
                ItemVoidstone item = (ItemVoidstone) voidStack.getItem();

                // On calcule le nombre total d'items qui allaient tomber
                int amount = 0;
                for (ItemStack drop : event.getDrops()) {
                    amount += drop.getCount();
                }

                if (amount > 0) {
                    item.addCobble(voidStack, amount);
                    // On vide les drops pour que rien ne tombe au sol
                    event.getDrops().clear();
                }
            }
        }
    }

    /**
     * Méthode utilitaire pour trouver la Voidstone efficacement
     */
    private ItemStack getVoidstone(EntityPlayer player) {
        // On check la main principale
        ItemStack main = player.getHeldItemMainhand();
        if (!main.isEmpty() && main.getItem() instanceof ItemVoidstone) return main;

        // On check la main secondaire (Offhand)
        ItemStack off = player.getHeldItemOffhand();
        if (!off.isEmpty() && off.getItem() instanceof ItemVoidstone) return off;

        // SEULEMENT SI PAS DANS LES MAINS : On parcourt l'inventaire
        // C'est ici qu'on gagne en performance
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            ItemStack stack = player.inventory.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() instanceof ItemVoidstone) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }
}