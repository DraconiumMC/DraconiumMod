package fr.draconium.core.handlers;

import fr.draconium.core.init.sounds.BackgroundInit;
import fr.draconium.core.references.Reference;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

@Mod.EventBusSubscriber(modid = Reference.MODID)
public class PlayerJoinHandler
{
	@SubscribeEvent
	public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event)
	{
		EntityPlayer player = event.player;
		World world = player.world;
		if (!world.isRemote && BackgroundInit.BACKGROUND_MUSIC != null)
		{
			world.playSound(null, player.getPosition(), BackgroundInit.BACKGROUND_MUSIC, SoundCategory.MUSIC, 1.0F, 1.0F);
		}
	}
}
