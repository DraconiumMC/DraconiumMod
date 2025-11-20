package fr.draconium.core.init.items.tools;

import java.util.ArrayList;
import java.util.List;

import fr.draconium.core.items.tools.AxeBasic;
import fr.draconium.core.items.tools.HammerBasic;
import fr.draconium.core.items.tools.PickaxeBasic;
import fr.draconium.core.items.tools.ShovelBasic;
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
public class ToolsInit
{
	
	private static List<Item> tools = new ArrayList<>();
	
	public static Item PYRONITE_AXE;
	public static Item PYRONITE_PICKAXE;
	public static Item PYRONITE_SHOVEL;
	public static Item PYRONITE_HAMMER;

	public static Item DRACONIUM_AXE;
	public static Item DRACONIUM_PICKAXE;
	public static Item DRACONIUM_SHOVEL;
	public static Item DRACONIUM_HAMMER;
	
	public static void init()
	{	
		tools.add(PYRONITE_AXE 			= new AxeBasic("pyronite_axe", ToolsMaterial.TOOLS_MATERIAL_PYRONITE));
		tools.add(PYRONITE_PICKAXE 		= new PickaxeBasic("pyronite_pickaxe", ToolsMaterial.TOOLS_MATERIAL_PYRONITE));
		tools.add(PYRONITE_SHOVEL 		= new ShovelBasic("pyronite_shovel", ToolsMaterial.TOOLS_MATERIAL_PYRONITE));
		tools.add(PYRONITE_HAMMER 		= new HammerBasic("pyronite_hammer", ToolsMaterial.HAMMER_MATERIAL_PYRONITE));
		
		tools.add(DRACONIUM_AXE 		= new AxeBasic("draconium_axe", ToolsMaterial.TOOLS_MATERIAL_DRACONIUM));
		tools.add(DRACONIUM_PICKAXE 	= new PickaxeBasic("draconium_pickaxe", ToolsMaterial.TOOLS_MATERIAL_DRACONIUM));
		tools.add(DRACONIUM_SHOVEL 		= new ShovelBasic("draconium_shovel", ToolsMaterial.TOOLS_MATERIAL_DRACONIUM));
		tools.add(DRACONIUM_HAMMER 		= new HammerBasic("draconium_hammer", ToolsMaterial.HAMMER_MATERIAL_DRACONIUM));
	}
	
	@SubscribeEvent
	protected static void registerItems(RegistryEvent.Register<Item> event)
	{
		Console.debug("- Enregistrement des outils:");
		for (Item tool : tools)
		{
			event.getRegistry().registerAll(tool);
			Console.debug("  - #6FF7D0" + tool.getRegistryName());
		}
	}
	
	@SubscribeEvent
	protected static void registerRenders(ModelRegistryEvent event)
	{
		Console.debug("- Enregistrement du rendu des outils:");
		for (Item tool : tools)
		{
			registerRender(tool);
			Console.debug("  - #6FF794" + tool.getRegistryName());
		}
	}
	
	private static void registerRender(Item item)
	{
		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
	}
}
