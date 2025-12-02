package fr.draconium.core.blocks.containers;

import fr.draconium.core.blocks.tileentity.TileEntityDraconiumFurnace;
import fr.draconium.core.recipes.DraconiumFurnaceRecipes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnaceOutput;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerDraconiumFurnace extends Container
{
    private final TileEntityDraconiumFurnace tileEntity;
    private int cookTime, totalCookTime, burnTime, currentBurnTime;
    private int speedLevel, fortuneLevel;

    public ContainerDraconiumFurnace(InventoryPlayer player, TileEntityDraconiumFurnace tileEntity)
    {
        this.tileEntity = tileEntity;

        // Slot 0 (Input)
        this.addSlotToContainer(new Slot(tileEntity, 0, 56, 17));

        // Slot 1 (Fuel) - CORRIGÉ : Bloque les items non-carburant
        this.addSlotToContainer(new Slot(tileEntity, 1, 56, 53) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                // Accepte Charbon, Bois, Seau de lave...
                return TileEntityFurnace.isItemFuel(stack);
            }
        });

        // Slot 2 (Output)
        this.addSlotToContainer(new SlotFurnaceOutput(player.player, tileEntity, 2, 116, 35));

        // Inventaire Joueur
        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 9; ++x) {
                this.addSlotToContainer(new Slot(player, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
            }
        }
        for (int x = 0; x < 9; ++x) {
            this.addSlotToContainer(new Slot(player, x, 8 + x * 18, 142));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return this.tileEntity.isUsableByPlayer(playerIn);
    }

    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        for (IContainerListener listener : this.listeners)
        {
            if (this.cookTime != this.tileEntity.cookTime) listener.sendWindowProperty(this, 2, this.tileEntity.cookTime);
            if (this.burnTime != this.tileEntity.burnTime) listener.sendWindowProperty(this, 0, this.tileEntity.burnTime);
            if (this.currentBurnTime != this.tileEntity.currentBurnTime) listener.sendWindowProperty(this, 1, this.tileEntity.currentBurnTime);
            if (this.totalCookTime != this.tileEntity.totalCookTime) listener.sendWindowProperty(this, 3, this.tileEntity.totalCookTime);

            // SYNCHRO DES UPGRADES (Important pour le texte)
            if (this.speedLevel != this.tileEntity.getField(4))
                listener.sendWindowProperty(this, 4, this.tileEntity.getField(4));

            if (this.fortuneLevel != this.tileEntity.getField(5))
                listener.sendWindowProperty(this, 5, this.tileEntity.getField(5));
        }

        this.cookTime = this.tileEntity.cookTime;
        this.burnTime = this.tileEntity.burnTime;
        this.currentBurnTime = this.tileEntity.currentBurnTime;
        this.totalCookTime = this.tileEntity.totalCookTime;
        this.speedLevel = this.tileEntity.getField(4);
        this.fortuneLevel = this.tileEntity.getField(5);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int data)
    {
        this.tileEntity.setField(id, data);
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

            if (index < 3) {
                if (!this.mergeItemStack(itemstack1, 3, 39, true)) return ItemStack.EMPTY;
                slot.onSlotChange(itemstack1, itemstack);
            }
            else {
                if (!DraconiumFurnaceRecipes.instance().getSmeltingResult(itemstack1).isEmpty()) {
                    if (!this.mergeItemStack(itemstack1, 0, 1, false)) return ItemStack.EMPTY;
                }
                else if (TileEntityFurnace.isItemFuel(itemstack1)) {
                    if (!this.mergeItemStack(itemstack1, 1, 2, false)) return ItemStack.EMPTY;
                }
                else if (index < 30) {
                    if (!this.mergeItemStack(itemstack1, 30, 39, false)) return ItemStack.EMPTY;
                }
                else if (index >= 30 && index < 39 && !this.mergeItemStack(itemstack1, 3, 30, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (itemstack1.isEmpty()) slot.putStack(ItemStack.EMPTY);
            else slot.onSlotChanged();

            if (itemstack1.getCount() == itemstack.getCount()) return ItemStack.EMPTY;
            slot.onTake(playerIn, itemstack1);
        }
        return itemstack;
    }
}