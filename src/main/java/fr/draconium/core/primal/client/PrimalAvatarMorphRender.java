package fr.draconium.core.primal.client;

import java.util.EnumMap;
import java.util.Map;
import java.util.WeakHashMap;

import fr.draconium.core.primal.PrimalAvatarLogic;
import fr.draconium.core.primal.PrimalAvatarType;
import fr.draconium.core.references.Reference;
import net.minecraft.client.Minecraft;
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
        if (PrimalAvatarLogic.getEquippedSetType(event.getEntityPlayer()).isActive()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRenderPlayerPost(RenderPlayerEvent.Post event) {
        EntityPlayer player = event.getEntityPlayer();
        PrimalAvatarType type = PrimalAvatarLogic.getEquippedSetType(player);
        if (!type.isActive() || type.renderEntityClass == null) return;

        Entity dummy = getOrCreateDummy(player.world, type);
        if (dummy == null) return;

        copyPose(dummy, player);

        // Correction doRenderEntity -> doRender
        // Correction getPartialRenderTicks -> getPartialRenderTick (sans le 's')
        Minecraft.getMinecraft().getRenderManager().renderEntityStatic(dummy, event.getPartialRenderTick(), false);
    }

    private static Entity getOrCreateDummy(World world, PrimalAvatarType type) {
        synchronized (DUMMY_CACHE) {
            EnumMap<PrimalAvatarType, Entity> map = DUMMY_CACHE.computeIfAbsent(world, w -> new EnumMap<>(PrimalAvatarType.class));
            Entity existing = map.get(type);
            if (existing != null) return existing;
            try {
                Entity created = type.renderEntityClass.getConstructor(World.class).newInstance(world);
                map.put(type, created);
                return created;
            } catch (Exception e) { return null; }
        }
    }

    private static void copyPose(Entity dummy, EntityPlayer player) {
        dummy.posX = player.posX;
        dummy.posY = player.posY;
        dummy.posZ = player.posZ;
        dummy.prevPosX = player.prevPosX;
        dummy.prevPosY = player.prevPosY;
        dummy.prevPosZ = player.prevPosZ;
        dummy.rotationYaw = player.rotationYaw;
        dummy.prevRotationYaw = player.prevRotationYaw;
        dummy.rotationPitch = player.rotationPitch;
        dummy.prevRotationPitch = player.prevRotationPitch;

        if (dummy instanceof EntityLivingBase) {
            EntityLivingBase dLiving = (EntityLivingBase) dummy;
            // On utilise les méthodes setter pour éviter les erreurs de compilation sur les champs directs
            dLiving.rotationYawHead = player.rotationYawHead;
            dLiving.prevRotationYawHead = player.prevRotationYawHead;
            dLiving.renderYawOffset = player.renderYawOffset;
            dLiving.prevRenderYawOffset = player.prevRenderYawOffset;

            dLiving.limbSwing = player.limbSwing;
            dLiving.limbSwingAmount = player.limbSwingAmount;
            dLiving.prevLimbSwingAmount = player.prevLimbSwingAmount;

            dLiving.setSneaking(player.isSneaking());
            dLiving.setSprinting(player.isSprinting());
        }
    }
}
