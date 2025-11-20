package fr.draconium.core.proxy;

import fr.draconium.core.entitys.EntityGrenade;
import fr.draconium.core.renders.RenderGrenade;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class ClientProxy extends ServerProxy
{
	@Override
	public void register()
	{
        RenderingRegistry.registerEntityRenderingHandler(EntityGrenade.class, RenderGrenade::new);
	}

}