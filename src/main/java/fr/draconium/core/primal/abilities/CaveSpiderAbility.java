package fr.draconium.core.primal.abilities;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.List;

public class CaveSpiderAbility implements IPrimalAbility {

    @Override
    public boolean execute(EntityPlayerMP player, World world) {
        // --- CAPACITÉ ACTIVE (G) : Morsure Venimeuse ---
        // Cherche les entités devant le joueur dans un petit rayon
        AxisAlignedBB targetBox = player.getEntityBoundingBox().grow(2.0D, 1.0D, 2.0D);
        List<EntityLivingBase> targets = world.getEntitiesWithinAABB(EntityLivingBase.class, targetBox);

        boolean hit = false;
        for (EntityLivingBase target : targets) {
            if (target == player) continue;

            // Applique le poison typique de la Cave Spider (7 secondes)
            target.addPotionEffect(new PotionEffect(MobEffects.POISON, 140, 0));

            // Un peu de dégâts immédiats
            target.attackEntityFrom(net.minecraft.util.DamageSource.causePlayerDamage(player), 2.0F);
            hit = true;
        }

        if (hit) {
            world.playSound(null, player.posX, player.posY, player.posZ,
                    SoundEvents.ENTITY_SPIDER_STEP, player.getSoundCategory(), 1.0F, 2.0F);
        }

        return hit;
    }

    @Override
    public void onTick(EntityPlayerMP player, World world) {
        // --- ATOUT 1 : Grimper aux murs ---
        if (player.collidedHorizontally) {
            player.motionY = 0.22D;
            player.fallDistance = 0.0F;
        }

        if (player.ticksExisted % 100 == 0) { // On rafraîchit l'effet régulièrement
            player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 300, 0, false, false));
        }

        // --- ATOUT 2 : Vision nocturne ---
        if (player.ticksExisted % 100 == 0) {
            player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 300, 0, false, false));
        }

        // --- ATOUT 3 : Agilité (Vitesse) ---
        // La Cave Spider est plus petite et plus vive
        if (player.ticksExisted % 20 == 0) {
            player.addPotionEffect(new PotionEffect(MobEffects.SPEED, 40, 0, false, false));
        }

        // --- ATOUT 4 : Immunité aux toiles (Cobwebs) ---
    }
}