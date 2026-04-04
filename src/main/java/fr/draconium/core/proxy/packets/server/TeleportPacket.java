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

public class TeleportPacket implements IMessage
{
    // Cooldown duration in ticks (15 seconds)
    public static final long TELEPORT_COOLDOWN_DURATION = 864000L;

    @Override
    public void toBytes(ByteBuf buffered)
    {

    }

    @Override
    public void fromBytes(ByteBuf buffered)
    {

    }


    public static class Handler implements IMessageHandler<TeleportPacket, IMessage>
    {
        // Method now accepts long for remaining time
        private String formatTime(long ticks)
        {
            if (ticks <= 0) return "0:00";

            int totalSeconds = (int) (ticks / 20);

            int hours = totalSeconds / 3600;
            int minutes = (totalSeconds % 3600) / 60;
            int seconds = totalSeconds % 60;

            if (hours > 0)
            {
                // Format H:MM:SS
                return String.format("%d:%02d:%02d", hours, minutes, seconds);
            }
            else
            {
                // Format M:SS
                return String.format("%d:%02d", minutes, seconds);
            }
        }

        @Override
        public IMessage onMessage(TeleportPacket message, MessageContext messageContexte)
        {
            EntityPlayerMP player = messageContexte.getServerHandler().player;
            player.getServerWorld().addScheduledTask(() ->
            {
                ExtendedPlayerData data = ExtendedPlayerData.get(player);
                long worldTime = player.world.getTotalWorldTime(); // Get current server time
                long cooldownExpiry = data.draconiumArmorAbilities.getTeleportCooldownExpiry(); // Get expiration time

                // 1. CHECK PERSISTENT COOLDOWN
                if (worldTime < cooldownExpiry)
                {
                    long remainingTicks = cooldownExpiry - worldTime;
                    player.sendMessage(new TextComponentString("§cCette abilité est en cooldown. Temps restant: " + formatTime(remainingTicks)));
                    return;
                }

                // 2. ABILITY USAGE (Cooldown is expired)
                if (DraconiqueArmor.isArmorComplet(player))
                {
                    KeyAction.teleportRandomly(player);

                    // APPLICATION OF PERSISTENT COOLDOWN: current world time + duration
                    data.draconiumArmorAbilities.setTeleportCooldownExpiry(worldTime + TELEPORT_COOLDOWN_DURATION);

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