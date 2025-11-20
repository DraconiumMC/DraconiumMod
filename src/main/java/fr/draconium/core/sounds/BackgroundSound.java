package fr.draconium.core.sounds;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

public class BackgroundSound extends SoundEvent
{
	public BackgroundSound(ResourceLocation soundNameIn)
	{
		super(soundNameIn);
		this.setRegistryName(soundNameIn);
	}
}
