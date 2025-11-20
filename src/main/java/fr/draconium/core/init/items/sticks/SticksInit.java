package fr.draconium.core.init.items.sticks;

import java.util.ArrayList;
import java.util.List;

import fr.draconium.core.items.others.ItemRegenerationStick;
import fr.draconium.core.items.others.ItemSpongeOnAStick;
import fr.draconium.core.items.others.ItemStickOfGod;
import fr.draconium.core.messages.Console;
import fr.draconium.core.references.Reference;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = Reference.MODID)
public class SticksInit
{
	private static List<Item> sticks = new ArrayList<>();
	
	public static Item STICK_OF_GOD;
	public static Item REGENERATION_STICK;
	public static Item SPONGE_ON_A_STICK;
	
	public static void init()
	{
		sticks.add(STICK_OF_GOD 		= new ItemStickOfGod("stick_of_god"));
		sticks.add(REGENERATION_STICK 	= new ItemRegenerationStick("regeneration_stick"));
		sticks.add(SPONGE_ON_A_STICK 	= new ItemSpongeOnAStick("sponge_on_a_stick"));
	}
	
	@SubscribeEvent
	protected static void registerItems(RegistryEvent.Register<Item> event)
	{
		Console.debug("- Enregistrement des sticks:");
		for (Item stick : sticks)
		{
			event.getRegistry().registerAll(stick);
			Console.debug("  - #6FF7D0" + stick.getRegistryName());
		}
	}
	
	@SubscribeEvent
	protected static void registerRenders(ModelRegistryEvent event)
	{
		Console.debug("- Enregistrement des rendus des sticks:");
		for (Item stick : sticks)
		{
			registerRender(stick);
			Console.debug("  - #6FF794" + stick.getRegistryName());
		}
	}
	
	private static void registerRender(Item item)
	{
		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
	}
}
