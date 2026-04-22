package fr.draconium.core.primal.abilities;

import fr.draconium.core.primal.PrimalAvatarLogic;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import java.util.List;

public class CreeperAbility implements IPrimalAbility {

    private final boolean isCharged;

    public CreeperAbility(boolean isCharged) {
        this.isCharged = isCharged;
    }

    @Override
    public boolean execute(EntityPlayerMP player, World world) {
        // Son d'amorçage
        world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_CREEPER_PRIMED, player.getSoundCategory(), 1.2F, 0.5F);

        player.getServer().addScheduledTask(() -> {
            try {
                Thread.sleep(1500);
                player.getServer().addScheduledTask(() -> {
                    // On vérifie si le joueur est toujours un Creeper (Normal ou Chargé)
                    if (player.isDead || !PrimalAvatarLogic.getEquippedSetType(player).name().contains("CREEPER")) return;

                    // Puissance : 6.0 si chargé, 3.0 si normal
                    float power = isCharged ? 6.0F : 3.0F;
                    world.createExplosion(player, player.posX, player.posY, player.posZ, power, true);
                });
            } catch (InterruptedException ignored) {}
        });
        return true;
    }

    @Override
    public void onTick(EntityPlayerMP player, World world) {
        // --- 1. ATOUT VISUEL (Seulement si chargé) ---
        if (isCharged && player.ticksExisted % 5 == 0) {
            // Particules bleues de "magie critique" pour simuler l'aura électrique
            player.getServerWorld().spawnParticle(EnumParticleTypes.CRIT_MAGIC, player.posX, player.posY + 1.0, player.posZ, 4, 0.3, 0.5, 0.3, 0.02);
        }

        if (player.ticksExisted % 100 == 0) { // On rafraîchit l'effet régulièrement
            player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 300, 0, false, false));
        }
        // --- 2. FAIBLESSE : Peur des chats ---
        List<net.minecraft.entity.passive.EntityOcelot> cats = world.getEntitiesWithinAABB(net.minecraft.entity.passive.EntityOcelot.class, player.getEntityBoundingBox().grow(6.0D));
        if (!cats.isEmpty()) {
            net.minecraft.entity.passive.EntityOcelot cat = cats.get(0);
            Vec3d flee = new Vec3d(player.posX - cat.posX, 0, player.posZ - cat.posZ).normalize().scale(0.4D);
            player.motionX = flee.x;
            player.motionZ = flee.z;
            player.velocityChanged = true;

            if (player.ticksExisted % 20 == 0) {
                world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_CREEPER_HURT, player.getSoundCategory(), 0.5F, 2.0F);
            }
        }
    }
}