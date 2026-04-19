package fr.draconium.core.blocks.containers;

import fr.draconium.core.items.others.ItemVoidstone;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerVoidstone extends Container {

    private final ItemStack voidstone;

    public ContainerVoidstone(InventoryPlayer playerInv, ItemStack voidstone) {
        this.voidstone = voidstone;

        // --- AFFICHAGE DE L'INVENTAIRE PRINCIPAL (3 lignes de 9) ---
        // Ces chiffres (8, 84, etc.) doivent correspondre au dessin de tes cases
        // Inventaire principal (3 lignes x 9)
        // Inventaire principal
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlotToContainer(new Slot(playerInv, col + row * 9 + 9,
                        18 + col * 18,
                        97+ row * 18));
            }
        }

// Hotbar
        for (int col = 0; col < 9; ++col) {
            this.addSlotToContainer(new Slot(playerInv, col,
                    18 + col * 18,
                    155));
        }
    }

    @Override
    public boolean enchantItem(EntityPlayer playerIn, int id) {
        if (id == 2) { // Bouton Concasser
            ItemVoidstone item = (ItemVoidstone) voidstone.getItem();
            if (item.canCrushCobble(voidstone)) {
                item.crushCobble(voidstone, playerIn);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }
}