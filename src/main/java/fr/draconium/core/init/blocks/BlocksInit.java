package fr.draconium.core.init.blocks;

import java.util.ArrayList;
import java.util.List;

import fr.draconium.core.blocks.*;
import fr.draconium.core.init.items.liquids.FluidInit;
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
public class BlocksInit
{
	private static List<Block> blocks = new ArrayList<>();
	
	//Blocks
	public static Block PYRONITE_BLOCK;
	public static Block DRACONIUM_BLOCK;
	
	//Others
	public static Block CAVE_BLOCK;
	public static Block ELEVATOR;
	public static Block FAKE_WATER_FLUID_BLOCK;
    public static Block DRACONIUM_FURNACE;
    public static Block DISENCHANTER;
	
	public static void init()
	{
		blocks.add(PYRONITE_BLOCK			= new BlockBasic("pyronite_block", Material.IRON));
		blocks.add(DRACONIUM_BLOCK 			= new BlockBasic("draconium_block", Material.IRON));
		
		blocks.add(CAVE_BLOCK 				= new BlockCaveBlock("cave_block", Material.GLASS));
		blocks.add(ELEVATOR 				= new BlockElevator("elevator", Material.IRON));
        blocks.add(DRACONIUM_FURNACE		= new BlockDraconiumFurnace("draconium_furnace"));
        blocks.add(DISENCHANTER             = new BlockDisenchanter("disenchanter"));
		
		blocks.add(FAKE_WATER_FLUID_BLOCK 	= new BlockFluid("fake_water_fluid", FluidInit.FAKE_WATER_FLUID, Material.WATER));
	}
	
	
	
	@SubscribeEvent
	protected static void registerBlocks(RegistryEvent.Register<Block> event)
	{
		Console.debug("- Enregistrement des blocks:");
		for (Block block : blocks)
		{
			event.getRegistry().registerAll(block);
			Console.debug("  - #6FF7D0" + block.getRegistryName());
		}
	}
	
	@SubscribeEvent
	protected static void registerItemBlocks(RegistryEvent.Register<Item> event)
	{
		for (Block block : blocks)
		{
			event.getRegistry().registerAll(new ItemBlock(block).setRegistryName(block.getRegistryName()));
		}
	}
	
	/**
	 * @apiNote Get json model
	 */
    @SubscribeEvent
    protected static void regsiterRenders(ModelRegistryEvent event)
    {
        // 1. On appelle ton RenderHandler spécial pour les fluides
        fr.draconium.core.handlers.RenderHandler.registerCustomMeshesAndStates();

        // 2. On boucle sur les blocs normaux
        for (Block block : blocks)
        {
            // IMPORTANT : Si c'est un fluide, on PASSE au suivant (continue)
            // On ne veut pas l'enregistrer comme un bloc normal
            if (block instanceof fr.draconium.core.blocks.BlockFluid) {
                continue;
            }

            registerRender(Item.getItemFromBlock(block));
        }
    }
	
	private static void registerRender(Item item)
	{
		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
	}
}
