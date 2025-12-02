package fr.draconium.core.blocks.containers;

import fr.draconium.core.blocks.tileentity.TileEntityDisenchanter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerDisenchanter extends Container
{
    private final TileEntityDisenchanter tileEntity;

    public ContainerDisenchanter(InventoryPlayer player, TileEntityDisenchanter tileEntity)
    {
        this.tileEntity = tileEntity;

        // --- SLOTS DE L'ENTONNOIR (Y=20) ---

// Slot 0 : Item Enchanté (Doit être enchanté, ne doit PAS être un livre)
        this.addSlotToContainer(new Slot(tileEntity, 0, 44, 20) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                // Accepte un item enchanté (outil, armure) QUI N'EST PAS un livre
                return stack.isItemEnchanted() && stack.getItem() != Items.BOOK && stack.getItem() != Items.ENCHANTED_BOOK;
            }
        });

        // Slot 1 : Livre Vierge (Doit être un livre)
        this.addSlotToContainer(new Slot(tileEntity, 1, 62, 20) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                // Accepte UNIQUEMENT le livre vierge de base
                return stack.getItem() == Items.BOOK;
            }
        });

        // (On saute la Case 3 pour faire une séparation visuelle)

        // Slot 2 : Item Sortie (Case 4) -> X=98
        this.addSlotToContainer(new Slot(tileEntity, 2, 98, 20) {
            @Override public boolean isItemValid(ItemStack stack) { return false; }
        });

        // Slot 3 : Livre Sortie (Case 5) -> X=116
        this.addSlotToContainer(new Slot(tileEntity, 3, 116, 20) {
            @Override public boolean isItemValid(ItemStack stack) { return false; }
        });

        // --- INVENTAIRE JOUEUR (Standard pour le GUI Hopper) ---
        // Attention : Le GUI Hopper affiche l'inventaire un peu plus bas (Y=51)

        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 9; ++x) {
                this.addSlotToContainer(new Slot(player, x + y * 9 + 9, 8 + x * 18, 51 + y * 18));
            }
        }

        for (int x = 0; x < 9; ++x) {
            this.addSlotToContainer(new Slot(player, x, 8 + x * 18, 109));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return this.tileEntity.isUsableByPlayer(playerIn);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            // Si c'est un slot de la machine (0, 1, 2, 3) -> Vers Inventaire
            if (index < 4)
            {
                if (!this.mergeItemStack(itemstack1, 4, 40, true)) return ItemStack.EMPTY;
                slot.onSlotChange(itemstack1, itemstack);
            }
            // Si c'est l'inventaire -> Vers Machine (Slots 0 ou 1)
            else
            {
                // On essaie de mettre dans l'Input Item (0) ou Input Livre (1)
                if (!this.mergeItemStack(itemstack1, 0, 2, false)) return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) slot.putStack(ItemStack.EMPTY);
            else slot.onSlotChanged();

            if (itemstack1.getCount() == itemstack.getCount()) return ItemStack.EMPTY;
            slot.onTake(playerIn, itemstack1);
        }
        return itemstack;
    }
}