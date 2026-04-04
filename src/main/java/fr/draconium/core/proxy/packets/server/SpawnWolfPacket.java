package fr.draconium.core.proxy.packets.server;

import fr.draconium.core.actions.KeyAction;
import fr.draconium.core.capabilities.player.ExtendedPlayerData;
import fr.draconium.core.items.armors.DraconiqueArmor;
import fr.draconium.core.messages.Console;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SpawnWolfPacket implements IMessage
{
    // Durée du cooldown en ticks (Exemple : 40 secondes)
    public static final long WOLF_COOLDOWN_DURATION = 864000L;

    @Override
    public void fromBytes(ByteBuf buf)
    {

    }

    @Override
    public void toBytes(ByteBuf buf)
    {

    }


    public static class Handler implements IMessageHandler<SpawnWolfPacket, IMessage>
    {
        // La méthode formatTime doit maintenant accepter un LONG (le temps restant)
        private String formatTime(long ticks)
        {
            if (ticks <= 0) return "0:00"; // Sécurité si l'heure actuelle dépasse l'expiration

            int totalSeconds = (int) (ticks / 20);

            int hours = totalSeconds / 3600;
            int minutes = (totalSeconds % 3600) / 60;
            int seconds = totalSeconds % 60;

            if (hours > 0)
            {
                return String.format("%d:%02d:%02d", hours, minutes, seconds);
            }
            else
            {
                return String.format("%d:%02d", minutes, seconds);
            }
        }

        @Override
        public IMessage onMessage(SpawnWolfPacket message, MessageContext messageContexte)
        {
            EntityPlayerMP player = messageContexte.getServerHandler().player;
            player.getServerWorld().addScheduledTask(() ->
            {
                ExtendedPlayerData data = ExtendedPlayerData.get(player);
                long worldTime = player.world.getTotalWorldTime(); // Temps du serveur
                long cooldownExpiry = data.draconiumArmorAbilities.getWolfCooldownExpiry(); // Récupère l'heure d'expiration

                // 1. VÉRIFICATION DU COOLDOWN PERSISTANT
                if (worldTime < cooldownExpiry)
                {
                    long remainingTicks = cooldownExpiry - worldTime;
                    player.sendMessage(new TextComponentString("§cCette abilité est en cooldown. Temps restant: " + formatTime(remainingTicks)));
                    return;
                }

                // 2. UTILISATION DE LA CAPACITÉ
                if (DraconiqueArmor.isArmorComplet(player))
                {
                    KeyAction.spawnAllies(player);

                    // APPLICATION DU COOLDOWN PERSISTANT : Heure actuelle du monde + durée
                    data.draconiumArmorAbilities.setWolfCooldownExpiry(worldTime + WOLF_COOLDOWN_DURATION);

                    Console.debug("Amure en draconique complète");
                }
                else
                {
                    player.sendMessage(new TextComponentString("Vous devez porter une armure complète en Draconique pour invoquer des alliés."));
                    Console.error("Amure en draconique incomplète");
                }
            });
            return null;
        }
    }
}