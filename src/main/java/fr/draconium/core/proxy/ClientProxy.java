package fr.draconium.core.proxy;

import fr.draconium.core.entitys.EntityGrenade;
import fr.draconium.core.handlers.GuiEventHandler; // 1. On importe ton handler
import fr.draconium.core.renders.RenderGrenade;
import net.minecraftforge.common.MinecraftForge; // 2. On importe MinecraftForge
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class ClientProxy extends ServerProxy
{
    @Override
    public void register()
    {
        // Ton code existant pour les grenades
        RenderingRegistry.registerEntityRenderingHandler(EntityGrenade.class, RenderGrenade::new);

        // ✅ 3. On ajoute l'enregistrement ICI
        // C'est sécurisé car seul le Client exécute ce fichier.
        MinecraftForge.EVENT_BUS.register(new GuiEventHandler());
    }
}