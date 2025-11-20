package fr.draconium.core.init.items.others;

import java.util.ArrayList;
import java.util.List;

import fr.draconium.core.DraconiumCore;
import fr.draconium.core.items.ItemBasic;
import fr.draconium.core.items.others.ItemGrenade;
import fr.draconium.core.items.others.ItemRadar;
import fr.draconium.core.items.others.ItemRegenerationStick;
import fr.draconium.core.items.others.ItemSpongeOnAStick;
import fr.draconium.core.items.others.ItemStickOfGod;
import fr.draconium.core.items.others.ItemTimer;
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
public class OthersInit
{

	private static List<Item> others = new ArrayList<>();
	
	public static Item RADAR;
	public static Item GRENADE;
	public static Item DEBRIS_GRENADE;
	
	public static Item TEST;
	
	public static void init()
	{
		others.add(RADAR 				= new ItemRadar("radar"));
		others.add(GRENADE 				= new ItemGrenade("grenade"));
		others.add(DEBRIS_GRENADE 		= new ItemBasic("debris_grenade").setCreativeTab(DraconiumCore.DRACONIUM_TAB_OTHERS));
		//others.add(TEST 				= new ItemTimer("test"));
	}
	
	@SubscribeEvent
	protected static void registerItems(RegistryEvent.Register<Item> event)
	{
		Console.debug("- Enregistrement des items:");
		for (Item other : others)
		{
			event.getRegistry().registerAll(other);
			Console.debug("  - #6FF7D0" + other.getRegistryName());
		}
	}
	
	@SubscribeEvent
	protected static void registerRenders(ModelRegistryEvent event)
	{
		Console.debug("- Enregistrement des items:");
		for (Item other : others)
		{
			registerRender(other);
			Console.debug("  - #6FF794" + other.getRegistryName());
		}
	}
	
	private static void registerRender(Item item)
	{
		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
	}
}