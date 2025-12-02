package fr.draconium.core.handlers;

import fr.draconium.core.init.items.others.OthersInit;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BowEventHandler
{
    /**
     * Empêche de commencer à bander l'arc (Clic droit enfoncé)
     */
    @SubscribeEvent
    public void onArrowNock(ArrowNockEvent event)
    {
        ItemStack bow = event.getBow();

        // Si ce n'est PAS ton arc en Draconium
        if (!(bow.getItem() instanceof fr.draconium.core.items.others.ItemDraconiumBow))
        {
            if (shouldBlockShot(event.getEntityPlayer()))
            {
                // CORRECTION CRASH : On a supprimé event.setCanceled(true);
                // On dit juste au jeu : "L'action a échoué"
                event.setAction(new ActionResult<>(EnumActionResult.FAIL, bow));
            }
        }
    }

    /**
     * Empêche de tirer la flèche (Relâchement du clic)
     * Sécurité supplémentaire au cas où le Nock passerait à travers.
     * Ici, setCanceled est autorisé car ArrowLooseEvent est annulable.
     */
    @SubscribeEvent
    public void onArrowLoose(ArrowLooseEvent event)
    {
        ItemStack bow = event.getBow();

        if (!(bow.getItem() instanceof fr.draconium.core.items.others.ItemDraconiumBow))
        {
            if (shouldBlockShot(event.getEntityPlayer()))
            {
                event.setCanceled(true); // Ici on a le droit !
            }
        }
    }

    // Logique commune pour vérifier les munitions
    private boolean shouldBlockShot(EntityPlayer player)
    {
        ItemStack ammo = findAmmoForVanillaBow(player);

        // Si l'arc a trouvé une Switch Arrow, on dit STOP
        if (!ammo.isEmpty() && ammo.getItem() == OthersInit.SWITCH_ARROW)
        {
            if (!player.world.isRemote) {
                // Petit message pour prévenir (avec un cooldown pour éviter le spam chat)
                if (player.ticksExisted % 10 == 0) {
                    player.sendMessage(new TextComponentString("§cImpossible ! Il faut un Draconium Bow."));
                }
            }
            return true; // Il faut bloquer !
        }
        return false; // C'est bon, c'est une flèche normale
    }

    private ItemStack findAmmoForVanillaBow(EntityPlayer player)
    {
        if (isArrow(player.getHeldItem(EnumHand.OFF_HAND))) return player.getHeldItem(EnumHand.OFF_HAND);
        if (isArrow(player.getHeldItem(EnumHand.MAIN_HAND))) return player.getHeldItem(EnumHand.MAIN_HAND);

        for (int i = 0; i < player.inventory.getSizeInventory(); ++i)
        {
            ItemStack itemstack = player.inventory.getStackInSlot(i);
            if (isArrow(itemstack)) return itemstack;
        }
        return ItemStack.EMPTY;
    }

    private boolean isArrow(ItemStack stack) {
        return stack.getItem() instanceof ItemArrow;
    }
}