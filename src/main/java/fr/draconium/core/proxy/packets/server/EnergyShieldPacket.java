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

public class EnergyShieldPacket implements IMessage
{
    // Cooldown duration: 864000 ticks = 12 hours
    public static final long SHIELD_COOLDOWN_DURATION = 864000L;

    @Override
    public void fromBytes(ByteBuf buf)
    {

    }

    @Override
    public void toBytes(ByteBuf buf)
    {

    }

    public static class Handler implements IMessageHandler<EnergyShieldPacket, IMessage>
    {
        // Méthode utilitaire pour formater le temps restant (en long)
        private String formatTime(long ticks)
        {
            if (ticks <= 0) return "0:00";

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
        public IMessage onMessage(EnergyShieldPacket message, MessageContext messageContexte)
        {
            EntityPlayerMP player = messageContexte.getServerHandler().player;
            player.getServerWorld().addScheduledTask(() ->
            {
                ExtendedPlayerData data = ExtendedPlayerData.get(player);
                long worldTime = player.world.getTotalWorldTime(); // Temps du serveur
                long cooldownExpiry = data.draconiumArmorAbilities.getShieldCooldownExpiry();

                // 1. VÉRIFICATION DU COOLDOWN PERSISTANT
                if (worldTime < cooldownExpiry)
                {
                    long remainingTicks = cooldownExpiry - worldTime;
                    player.sendMessage(new TextComponentString("§cCette abilité est en cooldown. Temps restant: " + formatTime(remainingTicks)));
                    Console.error("Tentative d'utilisation pendant le cooldown");
                    return;
                }

                // 2. UTILISATION DE LA CAPACITÉ
                if (DraconiqueArmor.isArmorComplet(player))
                {
                    KeyAction.applyEnergyShield(player);
                    // 3. APPLICATION DU COOLDOWN EN TEMPS MONDE (Persistant)
                    data.draconiumArmorAbilities.setShieldCooldownExpiry(worldTime + SHIELD_COOLDOWN_DURATION);
                    Console.debug("Amure en draconique complète");
                }
                else
                {
                    player.sendMessage(new TextComponentString("Vous devez porter une armure complète en Draconique pour activer le bouclier énergétique."));
                    Console.error("Amure en draconique incomplète");
                }
            });
            return null;
        }
    }
}