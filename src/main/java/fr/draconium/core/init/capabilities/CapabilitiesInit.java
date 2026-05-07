package fr.draconium.core.init.capabilities;

import fr.draconium.core.capabilities.player.ExtendedPlayerData;
import fr.draconium.core.references.Reference;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent; // Import correct pour Clone
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod.EventBusSubscriber(modid = Reference.MODID)
public class CapabilitiesInit
{
    /**
     * Appelé depuis la phase PreInit de ta classe principale.
     */
    public static void init()
    {
        ExtendedPlayerData.register();
    }

    @SubscribeEvent
    public static void onAttachCapabilitiesEntity(AttachCapabilitiesEvent<Entity> event)
    {
        // On attache la Capability uniquement aux joueurs
        if (event.getObject() instanceof EntityPlayer)
        {
            event.addCapability(ExtendedPlayerData.KEY, new ExtendedPlayerData());
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event)
    {
        // Récupération des données (Ancien vs Nouveau joueur)
        ExtendedPlayerData oldData = event.getOriginal().getCapability(ExtendedPlayerData.CAPABILITY, null);
        ExtendedPlayerData newData = event.getEntityPlayer().getCapability(ExtendedPlayerData.CAPABILITY, null);

        if (oldData != null && newData != null)
        {
            // On ne récupère les données que si ce n'est pas une mort
            // (ou si tu veux garder les pouvoirs après la mort, retire le !event.isWasDeath())
            if (!event.isWasDeath())
            {
                newData.deserializeNBT(oldData.serializeNBT());
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event)
    {
        // On tick uniquement côté serveur pour éviter les désynchronisations
        if (event.side.isServer() && event.phase == TickEvent.Phase.END)
        {
            ExtendedPlayerData data = ExtendedPlayerData.get(event.player);
            if (data != null)
            {
                data.tick();
            }
        }
    }
}