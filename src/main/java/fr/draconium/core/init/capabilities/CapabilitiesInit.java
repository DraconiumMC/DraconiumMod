package fr.draconium.core.init.capabilities;

import fr.draconium.core.capabilities.player.ExtendedPlayerData;
import fr.draconium.core.references.Reference;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod.EventBusSubscriber(modid = Reference.MODID)
public class CapabilitiesInit
{
	public static void init()
	{
		ExtendedPlayerData.register();
	}
	
	@SubscribeEvent
	public static void onAttachCapabilitiesEntity(AttachCapabilitiesEvent<Entity> event)
	{
		if (event.getObject() instanceof EntityPlayer)
		{
			event.addCapability(ExtendedPlayerData.KEY, new ExtendedPlayerData());
		}
	}

	@SubscribeEvent
	public static void onPlayerClone(PlayerEvent.Clone event)
	{
		ExtendedPlayerData oldData = event.getOriginal().getCapability(ExtendedPlayerData.CAPABILITY, null);
		ExtendedPlayerData newData = event.getEntityPlayer().getCapability(ExtendedPlayerData.CAPABILITY, null);
		if (oldData == null || newData == null)
		{
			return;
		}
		if (!event.isWasDeath())
		{
			newData.deserializeNBT(oldData.serializeNBT());
		}
	}

	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event)
	{
		if (event.side.isServer() && event.phase == TickEvent.Phase.END)
		{
			ExtendedPlayerData.get(event.player).tick();
		}
	}
}
