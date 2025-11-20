package fr.draconium.core.init.sounds;

import java.util.ArrayList;
import java.util.List;

import fr.draconium.core.messages.Console;
import fr.draconium.core.references.Reference;
import fr.draconium.core.sounds.BackgroundSound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = Reference.MODID)
public class BackgroundInit
{
	private static List<SoundEvent> sounds = new ArrayList<>();
	
    public static SoundEvent BACKGROUND_MUSIC;
	
    public static void init()
    {
    	sounds.add(BACKGROUND_MUSIC = new BackgroundSound(new ResourceLocation(Reference.MODID, "dragon_roar")));
    }
    
	@SubscribeEvent
	public static void registerSounds(RegistryEvent.Register<SoundEvent> event)
	{
		Console.debug("- Enregistrement des sons:");
		for (SoundEvent sound : sounds)
		{
			event.getRegistry().registerAll(sound);
			Console.debug("  - #F3F76F" + sound.getRegistryName());
		}
	}
}
