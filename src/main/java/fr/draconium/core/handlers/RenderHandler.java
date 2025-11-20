package fr.draconium.core.handlers;

import fr.draconium.core.entitys.EntityGrenade;
import fr.draconium.core.init.blocks.BlocksInit;
import fr.draconium.core.init.items.others.OthersInit;
import fr.draconium.core.references.Reference;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class RenderHandler
{
	public static void registerCustomMeshesAndStates()
	{
		ModelLoader.setCustomMeshDefinition(Item.getItemFromBlock(BlocksInit.FAKE_WATER_FLUID_BLOCK), new ItemMeshDefinition()
		{
			@Override
			public ModelResourceLocation getModelLocation(ItemStack stack)
			{
				return new ModelResourceLocation(Reference.MODID + ":fake_water_fluid", "fluid");
			}
		});
		
		ModelLoader.setCustomStateMapper(BlocksInit.FAKE_WATER_FLUID_BLOCK, new StateMapperBase()
		{
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState state)
			{
				return new ModelResourceLocation(Reference.MODID + ":fake_water_fluid", "fluid");
			}
		});
	}


    public static void registerEntityRenders() {
        RenderingRegistry.registerEntityRenderingHandler(EntityGrenade.class, new IRenderFactory<EntityGrenade>() {
            @Override
            public Render<? super EntityGrenade> createRenderFor(RenderManager manager) {
                return new RenderSnowball<>(manager, OthersInit.GRENADE, Minecraft.getMinecraft().getRenderItem());
            }
        });
    }
}
