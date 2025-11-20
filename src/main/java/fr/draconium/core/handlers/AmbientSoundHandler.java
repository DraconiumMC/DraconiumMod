package fr.draconium.core.handlers;

import fr.draconium.core.init.sounds.BackgroundInit;
import fr.draconium.core.references.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(modid = Reference.MODID, value = Side.CLIENT)
public class AmbientSoundHandler
{
	private static boolean isPlaying = false;
	private static float originalMusicVolume = -1.0F;

	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event)
	{
		Minecraft minecraft = Minecraft.getMinecraft();

		if (minecraft.world != null)
		{
			if (!isPlaying)
			{
				if (originalMusicVolume == -1.0F)
				{
					originalMusicVolume = minecraft.gameSettings.getSoundLevel(SoundCategory.AMBIENT);
				}

				minecraft.gameSettings.setSoundLevel(SoundCategory.AMBIENT, 1.0F);
				minecraft.getSoundHandler().playSound(
						PositionedSoundRecord.getMusicRecord(BackgroundInit.BACKGROUND_MUSIC)
				);
				isPlaying = true;
			}
		}
		else
		{
			if (isPlaying)
			{
				isPlaying = false;

				if (originalMusicVolume != -1.0F)
				{
					minecraft.gameSettings.setSoundLevel(SoundCategory.AMBIENT, originalMusicVolume);
					originalMusicVolume = -1.0F;
				}

				minecraft.getSoundHandler().stopSounds();
			}
		}
	}
}
