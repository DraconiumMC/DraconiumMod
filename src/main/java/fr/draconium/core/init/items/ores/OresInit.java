package fr.draconium.core.init.items.ores;

import java.util.ArrayList;
import java.util.List;

import fr.draconium.core.DraconiumCore;
import fr.draconium.core.items.ItemBasic;
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
public class OresInit
{

	private static List<Item> ingots = new ArrayList<>();
	
	public static Item PYRONITE_INGOT;
	public static Item DRACONIUM_INGOT;
	public static Item FINDIUM_CRISTAL;
	
	public static void init()
	{	
		ingots.add(PYRONITE_INGOT 	= new ItemBasic("pyronite_ingot").setCreativeTab(DraconiumCore.DRACONIUM_TAB_BLOCK));
		ingots.add(DRACONIUM_INGOT 	= new ItemBasic("draconium_ingot").setCreativeTab(DraconiumCore.DRACONIUM_TAB_BLOCK));
		ingots.add(FINDIUM_CRISTAL 	= new ItemBasic("findium_cristal").setCreativeTab(DraconiumCore.DRACONIUM_TAB_BLOCK));
	}
	
	@SubscribeEvent
	protected static void registerItems(RegistryEvent.Register<Item> event)
	{
		Console.debug("- Enregistrement des l'ingots:");
		for (Item ingot : ingots)
		{
			event.getRegistry().registerAll(ingot);
			Console.debug("  - #6FF7D0" + ingot.getRegistryName());
		}
	}
	
	@SubscribeEvent
	protected static void registerRenders(ModelRegistryEvent event)
	{
		Console.debug("- Enregistrement du rendu des l'ingots:");
		for (Item ingot : ingots)
		{
			registerRender(ingot);
			Console.debug("  - #6FF794" + ingot.getRegistryName());
		}
	}
	
	private static void registerRender(Item item)
	{
		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
	}
}
