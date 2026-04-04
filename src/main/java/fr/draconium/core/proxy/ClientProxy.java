package fr.draconium.core.proxy;

import fr.draconium.core.blocks.tileentity.TileEntityDisenchanter; // Import Disenchanter
import fr.draconium.core.client.render.RenderDisenchanter;       // Import RenderDisenchanter
import fr.draconium.core.entitys.EntityGrenade;
import fr.draconium.core.handlers.GuiEventHandler;
import fr.draconium.core.init.items.ores.OresInit;
import fr.draconium.core.init.items.others.OthersInit;
import fr.draconium.core.renders.RenderGrenade;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class ClientProxy extends ServerProxy
{
    /**
     * ✅ CORRECTION DU PROBLÈME @OVERRIDE :
     * Cette méthode surcharge la méthode vide déclarée dans ServerProxy.
     */
    @Override
    public void registerClientOnlyRenders()
    {
        // 1. Enregistrement des Entités (Grenade)
        RenderingRegistry.registerEntityRenderingHandler(EntityGrenade.class, RenderGrenade::new);

        // 2. Enregistrement du TESR (Désenchanteur - Le livre)
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDisenchanter.class, new RenderDisenchanter());
        registerModel(OthersInit.VOIDSTONE);

        // 3. Enregistrement des Événements Client (GUI)
        MinecraftForge.EVENT_BUS.register(new GuiEventHandler());
    }

    public void registerModel(Item item) {
        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
    }

    // Laissez les méthodes register() et registerRenderers() vides si tu ne peux pas les supprimer de ton code !
}