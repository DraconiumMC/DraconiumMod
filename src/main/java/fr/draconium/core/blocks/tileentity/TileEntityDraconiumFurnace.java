package fr.draconium.core.blocks.tileentity;

import fr.draconium.core.blocks.BlockDraconiumFurnace;
import fr.draconium.core.init.items.others.OthersInit;
import fr.draconium.core.recipes.DraconiumFurnaceRecipes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;

public class TileEntityDraconiumFurnace extends TileEntity implements IInventory, ITickable
{
    private NonNullList<ItemStack> inventory = NonNullList.withSize(3, ItemStack.EMPTY);
    private String customName;

    // Niveaux d'amélioration
    public int speedLevel = 0;
    public int fortuneLevel = 0;

    public int burnTime;
    public int currentBurnTime;
    public int cookTime;
    public int totalCookTime;

    @Override
    public void update()
    {
        boolean wasBurning = this.isBurning();
        boolean dirty = false;

        if (this.isBurning()) --this.burnTime;

        if (!this.world.isRemote)
        {
            ItemStack input = this.inventory.get(0);
            ItemStack fuel = this.inventory.get(1);

            // --- CORRECTION DU BUG DE CUISSON ICI ---
            // On vérifie si on doit allumer le feu.
            // Condition : Le feu est éteint ET il y a un truc à cuire ET on a du carburant
            if (!this.isBurning() && !input.isEmpty() && !fuel.isEmpty())
            {
                // On vérifie si la recette est valide AVANT de consommer le charbon
                if (canSmelt())
                {
                    this.burnTime = TileEntityFurnace.getItemBurnTime(fuel);
                    this.currentBurnTime = this.burnTime;

                    if (this.isBurning())
                    {
                        dirty = true;
                        if (!fuel.isEmpty()) fuel.shrink(1);
                    }
                }
            }

            // Gestion de la progression
            if (this.isBurning() && canSmelt())
            {
                int speedBonus = this.speedLevel;

                this.cookTime += (1 + speedBonus);
                this.totalCookTime = 200;

                if (this.cookTime >= this.totalCookTime)
                {
                    this.cookTime = 0;
                    this.totalCookTime = 200;
                    smeltItem();
                    dirty = true;
                }
            }
            else {
                this.cookTime = 0;
            }

            if (wasBurning != this.isBurning()) {
                dirty = true;
                BlockDraconiumFurnace.setState(this.isBurning(), this.world, this.pos);
            }
        }

        if (dirty) this.markDirty();
    }

    private boolean canSmelt() {
        if (this.inventory.get(0).isEmpty()) return false;
        ItemStack result = DraconiumFurnaceRecipes.instance().getSmeltingResult(this.inventory.get(0));
        if (result.isEmpty()) return false;

        ItemStack outputSlot = this.inventory.get(2);
        if (outputSlot.isEmpty()) return true;
        if (!outputSlot.isItemEqual(result)) return false;
        int res = outputSlot.getCount() + result.getCount(); // Pas de +1 ici, on reste safe
        return res <= getInventoryStackLimit() && res <= outputSlot.getMaxStackSize();
    }

    public void smeltItem() {
        if (this.canSmelt()) {
            ItemStack input = this.inventory.get(0);
            ItemStack result = DraconiumFurnaceRecipes.instance().getSmeltingResult(input).copy();
            ItemStack outputSlot = this.inventory.get(2);

            if (this.fortuneLevel > 0) {
                // 12.5% de chance par niveau (Niveau 8 = 100%)
                float chance = this.world.rand.nextFloat() * 100;
                if (chance < (this.fortuneLevel * 12.5F)) {
                    result.grow(1);
                }
            }

            if (outputSlot.isEmpty()) this.inventory.set(2, result);
            else if (outputSlot.getItem() == result.getItem()) outputSlot.grow(result.getCount());

            input.shrink(1);
        }
    }

    public boolean isBurning() { return this.burnTime > 0; }

    // --- NBT & SYNC ---

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.inventory = NonNullList.withSize(getSizeInventory(), ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(compound, this.inventory);
        this.burnTime = compound.getInteger("BurnTime");
        this.cookTime = compound.getInteger("CookTime");
        this.currentBurnTime = compound.getInteger("CurrentBurnTime");
        this.speedLevel = compound.getInteger("SpeedLevel");
        this.fortuneLevel = compound.getInteger("FortuneLevel");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        ItemStackHelper.saveAllItems(compound, this.inventory);
        compound.setInteger("BurnTime", (short)this.burnTime);
        compound.setInteger("CookTime", (short)this.cookTime);
        compound.setInteger("CurrentBurnTime", this.currentBurnTime);
        compound.setInteger("SpeedLevel", this.speedLevel);
        compound.setInteger("FortuneLevel", this.fortuneLevel);
        return compound;
    }

    @Override
    public int getField(int id) {
        switch (id) {
            case 0: return this.burnTime;
            case 1: return this.currentBurnTime;
            case 2: return this.cookTime;
            case 3: return this.totalCookTime;
            case 4: return this.speedLevel;   // Vital pour le GUI
            case 5: return this.fortuneLevel; // Vital pour le GUI
            default: return 0;
        }
    }

    @Override
    public void setField(int id, int value) {
        switch (id) {
            case 0: this.burnTime = value; break;
            case 1: this.currentBurnTime = value; break;
            case 2: this.cookTime = value; break;
            case 3: this.totalCookTime = value; break;
            case 4: this.speedLevel = value; break;
            case 5: this.fortuneLevel = value; break;
        }
    }

    @Override public int getFieldCount() { return 6; }

    // Méthodes standards IInventory...
    @Override public int getSizeInventory() { return this.inventory.size(); }
    @Override public boolean isEmpty() { for (ItemStack stack : this.inventory) if (!stack.isEmpty()) return false; return true; }
    @Override public ItemStack getStackInSlot(int index) { return this.inventory.get(index); }
    @Override public ItemStack decrStackSize(int index, int count) { return ItemStackHelper.getAndSplit(this.inventory, index, count); }
    @Override public ItemStack removeStackFromSlot(int index) { return ItemStackHelper.getAndRemove(this.inventory, index); }
    @Override public void setInventorySlotContents(int index, ItemStack stack) { this.inventory.set(index, stack); if (stack.getCount() > this.getInventoryStackLimit()) stack.setCount(this.getInventoryStackLimit()); }
    @Override public int getInventoryStackLimit() { return 64; }
    @Override public boolean isUsableByPlayer(EntityPlayer player) { return this.world.getTileEntity(this.pos) == this && player.getDistanceSq(this.pos.add(0.5, 0.5, 0.5)) <= 64.0; }
    @Override public void openInventory(EntityPlayer player) {}
    @Override public void closeInventory(EntityPlayer player) {}
    @Override public boolean isItemValidForSlot(int index, ItemStack stack) { return index != 2; }
    @Override public void clear() { this.inventory.clear(); }
    @Override public String getName() { return this.hasCustomName() ? this.customName : "container.draconium_furnace"; }
    @Override public boolean hasCustomName() { return this.customName != null && !this.customName.isEmpty(); }
    public void setCustomName(String customName) { this.customName = customName; }
}