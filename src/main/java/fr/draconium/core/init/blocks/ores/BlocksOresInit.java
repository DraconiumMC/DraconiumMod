package fr.draconium.core.init.blocks.ores;

import java.util.ArrayList;
import java.util.List;

import fr.draconium.core.blocks.BlockBasic;
import fr.draconium.core.blocks.BlockExplosiveOre;
import fr.draconium.core.blocks.BlockPyroniteOre;
import fr.draconium.core.blocks.BlockRandomOre;
import fr.draconium.core.messages.Console;
import fr.draconium.core.references.Reference;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = Reference.MODID)
public class BlocksOresInit
{
	
	private static List<Block> block_ores = new ArrayList<>();
	
	public static Block PYRONITE_ORE;
	public static Block DRACONIUM_ORE;
	public static Block FINDIUM_ORE;
	public static Block EXPLOSIVE_ORE;
    public static Block RANDOM_ORE;
    public static Block NETHER_PYRONITE_ORE;
	
	public static void init()
	{
		block_ores.add(PYRONITE_ORE 		= new BlockPyroniteOre("pyronite_ore", Material.ROCK));
		block_ores.add(DRACONIUM_ORE 	= new BlockBasic("draconium_ore", Material.ROCK));
		block_ores.add(FINDIUM_ORE 		= new BlockBasic("findium_ore", Material.ROCK));
		block_ores.add(EXPLOSIVE_ORE 	= new BlockExplosiveOre("explosive_ore", Material.ROCK));
        block_ores.add(RANDOM_ORE 	= new BlockRandomOre("random_ore", Material.ROCK));
        block_ores.add(NETHER_PYRONITE_ORE = new BlockPyroniteOre("nether_pyronite_ore", Material.ROCK));
	}
	
	@SubscribeEvent
	protected static void registerBlocks(RegistryEvent.Register<Block> event)
	{
		Console.debug("- Enregistrement des blocks de minerais:");
		
		for (Block block_ore : block_ores)
		{
			event.getRegistry().registerAll(block_ore);
			Console.debug("  - #6FF7D0" + block_ore.getRegistryName());
		}
	}
	
	@SubscribeEvent
	protected static void registerItemBlocks(RegistryEvent.Register<Item> event)
	{
		for (Block block_ore : block_ores)
		{
			event.getRegistry().registerAll(new ItemBlock(block_ore).setRegistryName(block_ore.getRegistryName()));
		}
	}
	
	/**
	 * @apiNote Get json model
	 */
	@SubscribeEvent
	protected static void regsiterRenders(ModelRegistryEvent event)
	{
		for (Block block_ore : block_ores)
		{
			registerRender(Item.getItemFromBlock(block_ore));
		}
	}
	
	private static void registerRender(Item item)
	{
		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
	}
}
