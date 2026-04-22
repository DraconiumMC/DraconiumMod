package fr.draconium.core.primal.abilities;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BlazeAbility implements IPrimalAbility {

    @Override
    public boolean execute(EntityPlayerMP player, World world) {
        Vec3d look = player.getLook(1.0F);

        // --- TIR EN RAFALE (3 boules) ---
        for (int i = 0; i < 3; i++) {
            double accelX = look.x;
            double accelY = look.y;
            double accelZ = look.z;

            // Pour les boules 2 et 3 (index 1 et 2), on ajoute de l'aléatoire
            if (i > 0) {
                accelX += (world.rand.nextDouble() - 0.5D) * 0.2D;
                accelY += (world.rand.nextDouble() - 0.5D) * 0.2D;
                accelZ += (world.rand.nextDouble() - 0.5D) * 0.2D;
            }

            EntitySmallFireball fireball = new EntitySmallFireball(world, player, accelX, accelY, accelZ);

            // Positionnement devant le joueur
            fireball.setPosition(
                    player.posX + look.x * 1.5D,
                    player.posY + player.getEyeHeight() + look.y * 0.5D,
                    player.posZ + look.z * 1.5D
            );

            if (!world.isRemote) {
                world.spawnEntity(fireball);
            }
        }

        // Son de tir du Blaze
        world.playSound(null, player.posX, player.posY, player.posZ,
                SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F);

        return true;
    }

    @Override
    public void onTick(EntityPlayerMP player, World world) {
        if (!player.capabilities.allowFlying) {
            player.capabilities.allowFlying = true;
            player.sendPlayerAbilities();
        }

        // --- GESTION THERMIQUE ---
        if (player.isInLava()) {
            player.extinguish();
            // Régénération rapide et Force I
            if (player.ticksExisted % 15 == 0) player.heal(1.0F);
            player.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 40, 0, false, false));
            if (player.ticksExisted % 100 == 0) { // On rafraîchit l'effet régulièrement

                player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 300, 0, false, false));
            }
        } else if (player.isWet()) {
            // Dégâts d'eau (Le Blaze y est très sensible)
            if (player.ticksExisted % 10 == 0) {
                player.attackEntityFrom(DamageSource.DROWN, 2.0F);
                player.getServerWorld().spawnParticle(EnumParticleTypes.SMOKE_LARGE, player.posX, player.posY + 1, player.posZ, 3, 0.1, 0.1, 0.1, 0.0);
            }
        }

        if (player.isBurning() && !player.isInLava()) player.extinguish();
    }
}

