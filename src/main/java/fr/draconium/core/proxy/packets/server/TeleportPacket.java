package fr.draconium.core.proxy.packets.server;

import fr.draconium.core.actions.KeyAction;
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
		@Override
		public IMessage onMessage(TeleportPacket message, MessageContext messageContexte)
		{
			EntityPlayerMP player = messageContexte.getServerHandler().player;
			player.getServerWorld().addScheduledTask(() ->
			{
				if (DraconiqueArmor.isAmrorComplet(player))
				{
					KeyAction.teleportRandomly(player);
					Console.debug("Amure de draconique complète");
				}
				else
				{
					player.sendMessage(new TextComponentString("Vous devez porter une armure complète en Draconique pour invoquer des alliés."));
					Console.error("Amure de draconique incomplète");
				}
			});
			return null;
		}
	}
}
