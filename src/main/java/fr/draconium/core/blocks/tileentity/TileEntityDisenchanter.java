package fr.draconium.core.blocks.tileentity;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.Random;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.EnumFacing;

/**
 * Logique du Désenchanteur : Gère l'inventaire, le temps, et l'automatisation (ISidedInventory)
 */
public class TileEntityDisenchanter extends TileEntity implements ISidedInventory, ITickable {

    private NonNullList<ItemStack> inventory = NonNullList.withSize(4, ItemStack.EMPTY);

    public int disenchantTime = 0;
    public int totalDisenchantTime = 100; // ticks

    // --- VARIABLES D'ANIMATION (TESR) ---
    public int ticks;
    public float bookRotation;
    public float bookRotationPrev;
    public float pageFlip;
    public float pageFlipPrev;
    public float bookSpread;
    public float bookSpreadPrev;
    public float flipT;
    public float flipA;
    public float bookRotationTarget;
    public float bookRotationOffset; // Correction: Ajoutée pour la logique

    private static final Random rand = new Random();

    // Slots mapping for hoppers
    private static final int[] SLOTS_TOP = new int[] {0, 1};     // insertion: item (0), book (1)
    private static final int[] SLOTS_SIDE = new int[] {0, 1};    // insertion: item (0), book (1)
    private static final int[] SLOTS_BOTTOM = new int[] {3, 2};  // extraction: enchanted books first (3), then final item (2)

    @Override
    public void update() {
        // 1. Mise à jour des variables 'Prev'
        this.bookSpreadPrev = this.bookSpread;
        this.bookRotationPrev = this.bookRotation;
        this.pageFlipPrev = this.pageFlip;
        this.ticks++; // Le temps doit avancer pour l'animation

        // --- LOGIQUE D'ANIMATION (Intégration de la rotation de la Table d'Enchantement) ---

        // La logique d'animation complète (même si on n'utilise pas toutes les variables dans le TESR)
        EntityPlayer entityplayer = this.world.getClosestPlayer(
                (double)((float)this.pos.getX() + 0.5F),
                (double)((float)this.pos.getY() + 0.5F),
                (double)((float)this.pos.getZ() + 0.5F),
                3.0D, false);

        if (entityplayer != null) {
            double dx = entityplayer.posX - ((float)this.pos.getX() + 0.5F);
            double dz = entityplayer.posZ - ((float)this.pos.getZ() + 0.5F);
            this.bookRotationTarget = (float)MathHelper.atan2(dz, dx);
            this.bookSpread += 0.1F;

            if (this.bookSpread < 0.5F || rand.nextInt(40) == 0) {
                float old = this.flipT;
                do {
                    this.flipT += (float)(rand.nextInt(4) - rand.nextInt(4));
                } while (old == this.flipT);
            }
        } else {
            this.bookRotationTarget += 0.02F;
            this.bookSpread -= 0.1F;
        }

        // normalisation des angles
        while (this.bookRotation >= (float)Math.PI) this.bookRotation -= ((float)Math.PI * 2F);
        while (this.bookRotation < -(float)Math.PI) this.bookRotation += ((float)Math.PI * 2F);
        while (this.bookRotationTarget >= (float)Math.PI) this.bookRotationTarget -= ((float)Math.PI * 2F);
        while (this.bookRotationTarget < -(float)Math.PI) this.bookRotationTarget += ((float)Math.PI * 2F);

        float delta = this.bookRotationTarget - this.bookRotation;
        while (delta >= (float)Math.PI) delta -= ((float)Math.PI * 2F);
        while (delta < -(float)Math.PI) delta += ((float)Math.PI * 2F);

        this.bookRotation += delta * 0.4F;
        this.bookSpread = MathHelper.clamp(this.bookSpread, 0.0F, 1.0F);

        float f = (this.flipT - this.pageFlip) * 0.4F;
        f = MathHelper.clamp(f, -0.2F, 0.2F);
        this.flipA += (f - this.flipA) * 0.9F;
        this.pageFlip += this.flipA;

        // ----------------------------------------------------
        // LOGIQUE DU DÉSENCHANTEMENT (Serveur uniquement)
        // ----------------------------------------------------

        if (!this.world.isRemote) {
            if (canDisenchant()) {
                this.disenchantTime++;
                if (this.disenchantTime >= this.totalDisenchantTime) {
                    this.disenchantTime = 0;
                    processDisenchant();
                    markDirty();
                    // Assure le rendu TESR (pas seulement le GUI) de voir le changement
                    this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 3);
                }
            } else {
                // S'arrête si l'item est retiré
                if (this.disenchantTime != 0) {
                    this.disenchantTime = 0;
                    markDirty();
                }
            }
        }
    }

    /**
     * Conditions pour démarrer une étape de désenchantement
     */
    private boolean canDisenchant() {
        ItemStack input = this.inventory.get(0);
        ItemStack bookIn = this.inventory.get(1);
        ItemStack outItem = this.inventory.get(2);
        ItemStack outBook = this.inventory.get(3);

        if (input.isEmpty() || bookIn.isEmpty()) return false;
        if (bookIn.getItem() != Items.BOOK) return false;
        if (!input.isItemEnchanted()) return false;

        Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(input);
        if (enchants == null || enchants.isEmpty()) return false;

        // Check book output slot (must be empty or stackable with a single book)
        ItemStack sampleBook = new ItemStack(Items.ENCHANTED_BOOK);
        if (!outBook.isEmpty() && !ItemStack.areItemsEqual(outBook, sampleBook)) return false;
        if (!outBook.isEmpty() && outBook.getCount() >= outBook.getMaxStackSize()) return false;

        // Simulate removal of first enchant to check final state
        Map<Enchantment, Integer> ordered = new LinkedHashMap<>(enchants);
        Iterator<Map.Entry<Enchantment, Integer>> it = ordered.entrySet().iterator();
        if (it.hasNext()) it.next();
        it.remove(); // Removes the first one

        if (ordered.isEmpty()) {
            // Check final item output slot space (NPE SAFE CHECK)
            ItemStack finalItem = input.copy();
            finalItem.setCount(1);
            if (finalItem.hasTagCompound()) {
                NBTTagCompound tag = finalItem.getTagCompound();
                tag.removeTag("ench");
                tag.removeTag("StoredEnchantments");
                if (tag.isEmpty()) finalItem.setTagCompound(null);
            }

            if (outItem.isEmpty()) return true;
            if (!ItemStack.areItemsEqual(outItem, finalItem)) return false;
            int total = outItem.getCount() + finalItem.getCount();
            return total <= getInventoryStackLimit();
        }

        // If not final step, output item slot doesn't matter yet (only book output matters)
        return true;
    }

    /**
     * Exécute une étape : retire 1 enchant de l'item, produit 1 book.
     */
    private void processDisenchant() {
        if (!canDisenchant()) return;

        ItemStack input = this.inventory.get(0);
        ItemStack bookIn = this.inventory.get(1);

        Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(input);
        if (enchants == null || enchants.isEmpty()) return;

        Map<Enchantment, Integer> ordered = new LinkedHashMap<>(enchants);

        Enchantment first = null;
        int lvl = 0;
        Iterator<Map.Entry<Enchantment, Integer>> it = ordered.entrySet().iterator();
        if (it.hasNext()) {
            Map.Entry<Enchantment, Integer> e = it.next();
            first = e.getKey();
            lvl = e.getValue();
            it.remove();
        }
        if (first == null) return;

        // make the enchanted book
        ItemStack resultBook = new ItemStack(Items.ENCHANTED_BOOK);
        ItemEnchantedBook.addEnchantment(resultBook, new net.minecraft.enchantment.EnchantmentData(first, lvl));

        // update input's enchants: set remaining enchants or remove tag if none
        ItemStack remaining = input.copy();
        remaining.setCount(input.getCount());

        if (ordered.isEmpty()) {
            // Final step: clean the input item completely
            if (remaining.hasTagCompound()) {
                NBTTagCompound tag = remaining.getTagCompound();
                tag.removeTag("ench");
                tag.removeTag("StoredEnchantments");
                if (tag.isEmpty()) remaining.setTagCompound(null);
            }
        } else {
            // Update NBT with remaining enchants
            EnchantmentHelper.setEnchantments(ordered, remaining);
        }

        // put the book into slot3 (stacking)
        ItemStack outBook = this.inventory.get(3);
        if (outBook.isEmpty()) {
            this.inventory.set(3, resultBook.copy());
        } else if (ItemStack.areItemsEqual(outBook, resultBook)) {
            outBook.grow(resultBook.getCount());
            this.inventory.set(3, outBook);
        }

        // consume one blank book
        this.inventory.get(1).shrink(1);
        if (this.inventory.get(1).getCount() <= 0) this.inventory.set(1, ItemStack.EMPTY);

        // If no enchants left -> move final item to slot2, decrement slot0
        if (ordered.isEmpty()) {
            ItemStack finalItem = remaining.copy();
            finalItem.setCount(1);
            ItemStack outItem = this.inventory.get(2);
            if (outItem.isEmpty()) {
                this.inventory.set(2, finalItem);
            } else if (ItemStack.areItemsEqual(outItem, finalItem)) {
                outItem.grow(1);
                this.inventory.set(2, outItem);
            }
            // reduce input stack by 1
            this.inventory.get(0).shrink(1);
            if (this.inventory.get(0).getCount() <= 0) this.inventory.set(0, ItemStack.EMPTY);
        } else {
            // still enchants remain -> update slot0 with remaining NBT (keep full count)
            this.inventory.set(0, remaining);
        }

        markDirty();
    }

    // ---------------- ISidedInventory ----------------

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        if (side == EnumFacing.DOWN) return SLOTS_BOTTOM;
        if (side == EnumFacing.UP) return SLOTS_TOP;
        return SLOTS_SIDE;
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        // Slot 0 (input item): Accept enchanted items, reject books
        if (index == 0) return itemStackIn.isItemEnchanted() && itemStackIn.getItem() != Items.BOOK;
        // Slot 1 (input book): Accept only books
        if (index == 1) return itemStackIn.getItem() == Items.BOOK;
        return false;
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        // Book Output (3) is always extractable
        if (index == 3) return true;

        // Item Output (2) is extractable ONLY IF fully stripped (no "ench" tag)
        if (index == 2) {
            return !stack.isItemEnchanted();
        }

        return false;
    }

    // ---------------- IInventory required methods ----------------

    @Override public int getSizeInventory() { return inventory.size(); }
    @Override public boolean isEmpty() { for (ItemStack s : inventory) if (!s.isEmpty()) return false; return true; }
    @Override public ItemStack getStackInSlot(int index) { return inventory.get(index); }
    @Override public ItemStack decrStackSize(int index, int count) { return ItemStackHelper.getAndSplit(this.inventory, index, count); }
    @Override public ItemStack removeStackFromSlot(int index) { return ItemStackHelper.getAndRemove(this.inventory, index); }
    @Override public void setInventorySlotContents(int index, ItemStack stack) {
        this.inventory.set(index, stack);
        if (stack.getCount() > this.getInventoryStackLimit()) stack.setCount(this.getInventoryStackLimit());
        markDirty();
    }
    @Override public int getInventoryStackLimit() { return 64; }
    @Override public boolean isUsableByPlayer(EntityPlayer player) {
        if (this.world.getTileEntity(this.pos) != this) return false;
        return player.getDistanceSq((double)this.pos.getX()+0.5D, (double)this.pos.getY()+0.5D, (double)this.pos.getZ()+0.5D) <= 64.0D;
    }
    @Override public void openInventory(EntityPlayer player) {}
    @Override public void closeInventory(EntityPlayer player) {}
    @Override public boolean isItemValidForSlot(int index, ItemStack stack) {
        // Pas de validation complète ici, c'est fait dans canInsertItem / Container
        return index == 0 || index == 1;
    }

    // GUI field sync
    @Override public int getField(int id) {
        if (id == 0) return this.disenchantTime;
        return 0;
    }

    @Override public void setField(int id, int value) {
        if (id == 0) this.disenchantTime = value;
    }

    @Override public int getFieldCount() { return 1; }

    @Override public void clear() { this.inventory.clear(); }

    @Override public String getName() { return "container.disenchanter"; }

    @Override public boolean hasCustomName() { return false; }

    // ---------------- NBT ----------------

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.inventory = NonNullList.withSize(getSizeInventory(), ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(compound, this.inventory);
        this.disenchantTime = compound.getInteger("Time");

        // animation
        this.bookRotation = compound.getFloat("BookRot");
        this.bookRotationPrev = compound.getFloat("BookRotPrev");
        this.pageFlip = compound.getFloat("PageFlip");
        this.pageFlipPrev = compound.getFloat("PageFlipPrev");
        this.bookSpread = compound.getFloat("BookSpread");
        this.bookSpreadPrev = compound.getFloat("BookSpreadPrev");
        this.ticks = compound.getInteger("Ticks");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        ItemStackHelper.saveAllItems(compound, this.inventory);
        compound.setInteger("Time", this.disenchantTime);

        // animation
        compound.setFloat("BookRot", this.bookRotation);
        compound.setFloat("BookRotPrev", this.bookRotationPrev);
        compound.setFloat("PageFlip", this.pageFlip);
        compound.setFloat("PageFlipPrev", this.pageFlipPrev);
        compound.setFloat("BookSpread", this.bookSpread);
        compound.setFloat("BookSpreadPrev", this.bookSpreadPrev);
        compound.setInteger("Ticks", this.ticks);
        return compound;
    }
}