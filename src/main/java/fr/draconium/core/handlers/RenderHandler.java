package fr.draconium.core.handlers;

import fr.draconium.core.entitys.EntityGrenade;
import fr.draconium.core.entitys.EntitySwitchArrow;
import fr.draconium.core.init.blocks.BlocksInit;
import fr.draconium.core.init.items.others.OthersInit;
import fr.draconium.core.references.Reference;
import fr.draconium.core.renders.RenderSwitchArrow;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class RenderHandler
{
    public static void registerCustomMeshesAndStates()
    {
        Block block = BlocksInit.FAKE_WATER_FLUID_BLOCK;
        Item item = Item.getItemFromBlock(block);

        // On crée le StateMap qui dit "Ignore le niveau du liquide"
        // Cela permet d'avoir la même texture peu importe la hauteur de l'eau
        ModelLoader.setCustomStateMapper(block, new StateMap.Builder().ignore(BlockFluidClassic.LEVEL).build());

        // On lie l'item (le seau/bloc en main) au fichier blockstate
        if (item != null) {
            ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(Reference.MODID + ":fake_water_fluid", "fluid"));
        }
    }


    public static void registerEntityRenders() {
        RenderingRegistry.registerEntityRenderingHandler(EntityGrenade.class, new IRenderFactory<EntityGrenade>() {
            @Override
            public Render<? super EntityGrenade> createRenderFor(RenderManager manager) {
                return new RenderSnowball<>(manager, OthersInit.GRENADE, Minecraft.getMinecraft().getRenderItem());
            }
        });


        // Switch Arrow
        RenderingRegistry.registerEntityRenderingHandler(EntitySwitchArrow.class, new IRenderFactory<EntitySwitchArrow>() {
            @Override
            public Render<? super EntitySwitchArrow> createRenderFor(RenderManager manager) {
                // On utilise notre nouvelle classe de rendu 3D.
                return new RenderSwitchArrow(manager);
            }
        });
    }
}
