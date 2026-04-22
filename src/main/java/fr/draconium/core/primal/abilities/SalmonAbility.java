package fr.draconium.core.primal.abilities;

import fr.draconium.core.worlds.ModConfig;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SalmonAbility implements IPrimalAbility {

    @Override
    public boolean execute(EntityPlayerMP player, World world) {
        // --- CAPACITÉ ACTIVE (G) : Le Saut du Saumon ---
        // Ne fonctionne que si le joueur est dans l'eau ou très proche de la surface
        if (player.isInWater() || world.isMaterialInBB(player.getEntityBoundingBox().grow(0.0D, -0.5D, 0.0D), net.minecraft.block.material.Material.WATER)) {

            Vec3d look = player.getLook(1.0F);
            // On propulse le joueur vers l'avant et vers le haut
            player.motionX = look.x * 1.5D;
            player.motionY = Math.max(0.6D, look.y * 1.2D + 0.4D);
            player.motionZ = look.z * 1.5D;

            player.velocityChanged = true;

            world.playSound(null, player.posX, player.posY, player.posZ,
                    SoundEvents.ENTITY_RABBIT_JUMP, player.getSoundCategory(), 1.0F, 1.0F);

            return true;
        }
        return false;
    }

    @Override
    public void onTick(EntityPlayerMP player, World world) {
        boolean inWater = player.isInWater();

        // --- ATOUT 1 : Vitesse de nage boostée ---
        if (inWater) {
            // On utilise ta config pour le multiplicateur de vitesse
            double boost = ModConfig.primalFishSwimBoost;

            player.motionX *= boost;
            player.motionY *= boost;
            player.motionZ *= boost;

            // On limite un peu pour éviter que le joueur ne s'envole hors de la map
            player.motionX = MathHelper.clamp(player.motionX, -1.5, 1.5);
            player.motionZ = MathHelper.clamp(player.motionZ, -1.5, 1.5);

            player.velocityChanged = true;

            // Respiration infinie
            player.setAir(300);
        }

        // --- FAIBLESSES : Poisson hors de l'eau ---
        if (!inWater && !player.isInLava()) {
            // Si le saumon n'est pas dans l'eau, il commence à s'asphyxier
            if (player.ticksExisted % 40 == 0) {
                player.attackEntityFrom(DamageSource.DROWN, 1.0F);
                world.playSound(null, player.posX, player.posY, player.posZ,
                        SoundEvents.ENTITY_PLAYER_BURP, player.getSoundCategory(), 0.8F, 1.2F);
            }
        }
    }
}