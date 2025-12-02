package fr.draconium.core.items.others;

import javax.annotation.Nullable;

import fr.draconium.core.DraconiumCore;
import fr.draconium.core.init.items.others.OthersInit;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemDraconiumBow extends ItemBow
{
    public ItemDraconiumBow(String name)
    {
        this.setRegistryName(name);
        this.setTranslationKey(name);
        this.setMaxStackSize(1);
        this.setMaxDamage(384);
        this.setCreativeTab(DraconiumCore.DRACONIUM_TAB_OTHERS);

        // ================================================================
        // CORRECTION : CE CODE DOIT ETRE *DANS* LE CONSTRUCTEUR
        // ================================================================

        // 1. Propriété "pull" (Force du tir)
        this.addPropertyOverride(new ResourceLocation("pull"), new IItemPropertyGetter()
        {
            @SideOnly(Side.CLIENT)
            public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn)
            {
                if (entityIn == null)
                {
                    return 0.0F;
                }
                else
                {
                    // Attention : J'ai mis OthersInit ici car c'est là que tu as déclaré ton arc
                    return entityIn.getActiveItemStack().getItem() != OthersInit.DRACONIUM_BOW ? 0.0F : (float)(stack.getMaxItemUseDuration() - entityIn.getItemInUseCount()) / 20.0F;
                }
            }
        });

        // 2. Propriété "pulling" (Est en train de tirer ?)
        this.addPropertyOverride(new ResourceLocation("pulling"), new IItemPropertyGetter()
        {
            @SideOnly(Side.CLIENT)
            public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn)
            {
                return entityIn != null && entityIn.isHandActive() && entityIn.getActiveItemStack() == stack ? 1.0F : 0.0F;
            }
        });
        // ================================================================
    } // <--- L'accolade fermante du constructeur est ICI maintenant

    /**
     * Définit quelles flèches cet arc accepte.
     */
    @Override
    protected boolean isArrow(ItemStack stack)
    {
        return stack.getItem() == OthersInit.SWITCH_ARROW;
    }

    /**
     * On surcharge le clic droit pour envoyer le message d'erreur.
     */
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
        ItemStack bow = playerIn.getHeldItem(handIn);
        boolean hasInfinite = playerIn.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, bow) > 0;

        // On cherche si le joueur a des munitions VALIDES (Switch Arrow)
        ItemStack ammo = this.findAmmo(playerIn);

        if (ammo.isEmpty() && !hasInfinite)
        {
            if (!worldIn.isRemote)
            {
                if (hasVanillaArrows(playerIn)) {
                    playerIn.sendMessage(new TextComponentString("§cCet arc est trop puissant pour des flèches ordinaires !"));
                    playerIn.sendMessage(new TextComponentString("§7Utilisez des Switch Arrows."));
                }
            }
            return hasInfinite ? new ActionResult<>(EnumActionResult.PASS, bow) : new ActionResult<>(EnumActionResult.FAIL, bow);
        }

        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

    private boolean hasVanillaArrows(EntityPlayer player)
    {
        for (ItemStack stack : player.inventory.mainInventory) {
            if (stack.getItem() == Items.ARROW || stack.getItem() == Items.TIPPED_ARROW || stack.getItem() == Items.SPECTRAL_ARROW) {
                return true;
            }
        }
        ItemStack offhand = player.getHeldItemOffhand();
        return offhand.getItem() == Items.ARROW || offhand.getItem() == Items.TIPPED_ARROW || offhand.getItem() == Items.SPECTRAL_ARROW;
    }

    // J'ai mis protected pour respecter la méthode originale de ItemBow
    public ItemStack findAmmo(EntityPlayer player)
    {
        if (this.isArrow(player.getHeldItem(EnumHand.OFF_HAND)))
        {
            return player.getHeldItem(EnumHand.OFF_HAND);
        }
        else if (this.isArrow(player.getHeldItem(EnumHand.MAIN_HAND)))
        {
            return player.getHeldItem(EnumHand.MAIN_HAND);
        }
        else
        {
            for (int i = 0; i < player.inventory.getSizeInventory(); ++i)
            {
                ItemStack itemstack = player.inventory.getStackInSlot(i);

                if (this.isArrow(itemstack))
                {
                    return itemstack;
                }
            }

            return ItemStack.EMPTY;
        }
    }
}