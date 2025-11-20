package fr.draconium.core.renders;

import fr.draconium.core.entitys.EntityGrenade;
import fr.draconium.core.init.items.others.OthersInit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderGrenade extends RenderSnowball<EntityGrenade>
{

	public RenderGrenade(RenderManager renderManager)
	{
		super(renderManager, OthersInit.GRENADE, Minecraft.getMinecraft().getRenderItem());
	}
	
	public static class Factory implements IRenderFactory<EntityGrenade>
	{
		@Override
		public RenderGrenade createRenderFor(RenderManager manager)
		{
			return new RenderGrenade(manager);
		}
	}

	public static void register()
	{
		RenderingRegistry.registerEntityRenderingHandler(EntityGrenade.class, new Factory());
	}
}