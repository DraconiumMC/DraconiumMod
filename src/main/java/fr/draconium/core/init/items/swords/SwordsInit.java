package fr.draconium.core.init.items.swords;

import java.util.ArrayList;
import java.util.List;

import fr.draconium.core.items.swords.SwordBasic;
import fr.draconium.core.materials.ToolsMaterial;
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
public class SwordsInit
{
	
	private static List<Item> swords = new ArrayList<>();
	
	public static Item PYRONITE_SWORD;
	public static Item DRACONIUM_SWORD;
	
	public static void init()
	{
		swords.add(PYRONITE_SWORD 	= new SwordBasic("pyronite_sword", ToolsMaterial.TOOLS_MATERIAL_PYRONITE));
		swords.add(DRACONIUM_SWORD 	= new SwordBasic("draconium_sword", ToolsMaterial.TOOLS_MATERIAL_DRACONIUM));
	}
	
	@SubscribeEvent
	protected static void registerItems(RegistryEvent.Register<Item> event)
	{
		Console.debug("- Enregistrement des éppées:");
		for (Item sword : swords)
		{
			event.getRegistry().registerAll(sword);
			Console.debug("  - #6FF7D0" + sword.getRegistryName());
		}
	}
	
	@SubscribeEvent
	protected static void registerRenders(ModelRegistryEvent event)
	{
		Console.debug("- Enregistrement du rendu des éppées:");
		for (Item sword : swords)
		{
			registerRender(sword);
			Console.debug("  - #6FF794" + sword.getRegistryName());
		}
	}
	
	private static void registerRender(Item item)
	{
		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
	}
}
