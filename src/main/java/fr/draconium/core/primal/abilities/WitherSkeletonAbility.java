package fr.draconium.core.primal.abilities;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.List;

public class WitherSkeletonAbility implements IPrimalAbility {

    @Override
    public boolean execute(EntityPlayerMP player, World world) {
        // --- CAPACITÉ ACTIVE (G) : Cri de Flétrissement ---
        AxisAlignedBB area = player.getEntityBoundingBox().grow(4.0D, 2.0D, 4.0D);
        List<EntityLivingBase> targets = world.getEntitiesWithinAABB(EntityLivingBase.class, area);

        boolean hit = false;
        for (EntityLivingBase target : targets) {
            if (target == player) continue;
            target.addPotionEffect(new PotionEffect(MobEffects.WITHER, 200, 1));
            hit = true;
        }

        if (hit) {
            world.playSound(null, player.posX, player.posY, player.posZ,
                    SoundEvents.ENTITY_WITHER_SKELETON_AMBIENT, SoundCategory.PLAYERS, 1.0F, 0.5F);
        }

        return hit;
    }

    @Override
    public void onTick(EntityPlayerMP player, World world) {
        // --- GESTION THERMIQUE (Eau vs Lave) ---

        if (player.isInLava()) {
            player.extinguish();
            // ATOUT : Puissance du Nether
            // Force II, Vitesse I et Régénération constante dans le magma
            player.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 40, 1, false, false));
            player.addPotionEffect(new PotionEffect(MobEffects.SPEED, 40, 0, false, false));
            if (player.ticksExisted % 20 == 0 && player.getHealth() < player.getMaxHealth()) {
                player.heal(1.0F);
            }
        } else if (player.isWet()) {
            // FAIBLESSE : L'eau est corrosive pour ses os calcinés
            if (player.ticksExisted % 25 == 0) {
                player.attackEntityFrom(DamageSource.DROWN, 1.0F);
                // Il coule aussi au fond
                player.motionY -= 0.05D;
            }
        } else {
            // --- ÉTAT NORMAL (Hors lave/eau) ---
            if (player.ticksExisted % 20 == 0) {
                player.addPotionEffect(new PotionEffect(MobEffects.SPEED, 40, 0, false, false));
            }
        }

        // --- VISION NOCTURNE ---
        if (player.ticksExisted % 100 == 0) {
            player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 300, 0, false, false));
        }

        // --- IMMUNITÉ FEU PASSIVE ---
        if (player.isBurning() && !player.isInLava()) {
            player.extinguish();
        }
    }
}