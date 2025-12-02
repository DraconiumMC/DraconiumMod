package fr.draconium.core.renders;

import fr.draconium.core.entitys.EntitySwitchArrow;
import fr.draconium.core.references.Reference;
import net.minecraft.client.renderer.entity.RenderArrow;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderSwitchArrow extends RenderArrow<EntitySwitchArrow>
{
    // On définit le chemin de la texture de l'entité (pas de l'item !)
    public static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MODID, "textures/entity/projectiles/switch_arrow.png");

    public RenderSwitchArrow(RenderManager manager)
    {
        super(manager);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntitySwitchArrow entity)
    {
        return TEXTURE;
    }
}