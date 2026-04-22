package fr.draconium.core.primal.client;

import java.util.EnumMap;
import java.util.Map;
import java.util.WeakHashMap;

import fr.draconium.core.primal.PrimalAvatarLogic;
import fr.draconium.core.primal.PrimalAvatarType;
import fr.draconium.core.references.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(modid = Reference.MODID, value = Side.CLIENT)
public final class PrimalAvatarMorphRender {

    private static final Map<World, EnumMap<PrimalAvatarType, Entity>> DUMMY_CACHE = new WeakHashMap<>();

    @SubscribeEvent
    public static void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
        // Si le joueur porte l'armure, on annule le rendu du skin humain
        if (PrimalAvatarLogic.getEquippedSetType(event.getEntityPlayer()).isActive()) {
            event.setCanceled(true);

            // On appelle manuellement le rendu du mob ici pour qu'il soit bien placé
            renderMorph(event);
        }
    }

    private static void renderMorph(RenderPlayerEvent.Pre event) {
        EntityPlayer player = event.getEntityPlayer();
        PrimalAvatarType type = PrimalAvatarLogic.getEquippedSetType(player);

        if (!type.isActive() || type.renderEntityClass == null) return;

        Entity dummy = getOrCreateDummy(player.world, type);
        if (dummy == null) return;

        copyPose(dummy, player);

        // On récupère le moteur de rendu du mob
        Render<Entity> render = Minecraft.getMinecraft().getRenderManager().getEntityRenderObject(dummy);
        if (render != null) {
            // On dessine le mob EXACTEMENT là où le joueur devrait être
            render.doRender(dummy, event.getX(), event.getY(), event.getZ(), player.rotationYaw, event.getPartialRenderTick());
        }
    }

    private static Entity getOrCreateDummy(World world, PrimalAvatarType type) {
        synchronized (DUMMY_CACHE) {
            EnumMap<PrimalAvatarType, Entity> map = DUMMY_CACHE.computeIfAbsent(world, w -> new EnumMap<>(PrimalAvatarType.class));
            Entity existing = map.get(type);
            if (existing != null && existing.world == world) return existing;

            try {
                Entity created = type.renderEntityClass.getConstructor(World.class).newInstance(world);

                // On vérifie si c'est un mob (EntityLiving) avant de couper l'IA
                if (created instanceof net.minecraft.entity.EntityLiving) {
                    ((net.minecraft.entity.EntityLiving) created).setNoAI(true);
                }

                // On le rend silencieux pour pas qu'il crie sans arrêt
                created.setSilent(true);

                map.put(type, created);
                return created;
            } catch (Exception e) {
                e.printStackTrace(); // Utile pour voir s'il y a un autre souci
                return null;
            }
        }
    }

    private static void copyPose(Entity dummy, EntityPlayer player) {
        dummy.ticksExisted = player.ticksExisted;
        dummy.setSneaking(player.isSneaking());

        if (dummy instanceof EntityLivingBase) {
            EntityLivingBase dLiving = (EntityLivingBase) dummy;
            dLiving.rotationYawHead = player.rotationYawHead;
            dLiving.prevRotationYawHead = player.prevRotationYawHead;
            dLiving.renderYawOffset = player.renderYawOffset;
            dLiving.prevRenderYawOffset = player.prevRenderYawOffset;
            dLiving.rotationYaw = player.rotationYaw;
            dLiving.prevRotationYaw = player.prevRotationYaw;
            dLiving.rotationPitch = player.rotationPitch;
            dLiving.prevRotationPitch = player.prevRotationPitch;

            dLiving.limbSwing = player.limbSwing;
            dLiving.limbSwingAmount = player.limbSwingAmount;
            dLiving.prevLimbSwingAmount = player.prevLimbSwingAmount;

            dLiving.swingProgress = player.swingProgress;
            dLiving.swingProgressInt = player.swingProgressInt;
            dLiving.isSwingInProgress = player.isSwingInProgress;
        }
    }
}