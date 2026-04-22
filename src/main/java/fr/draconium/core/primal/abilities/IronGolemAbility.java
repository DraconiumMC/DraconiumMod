package fr.draconium.core.primal.abilities;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.List;

public class IronGolemAbility implements IPrimalAbility {

    @Override
    public boolean execute(EntityPlayerMP player, World world) {
        // --- CAPACITÉ ACTIVE (G) : Uppercut du Golem ---
        // Cherche les cibles dans un rayon de 3 blocs devant le joueur
        AxisAlignedBB hitBox = player.getEntityBoundingBox().grow(3.0D, 1.0D, 3.0D);
        List<EntityLivingBase> targets = world.getEntitiesWithinAABB(EntityLivingBase.class, hitBox);

        boolean hitSomething = false;
        for (EntityLivingBase target : targets) {
            if (target == player) continue;

            // Dégâts massifs (simule le coup du Golem)
            target.attackEntityFrom(DamageSource.causePlayerDamage(player), 15.0F);

            // Projection vers le haut (l'uppercut classique)
            target.motionY += 0.6D;
            target.velocityChanged = true;

            hitSomething = true;
        }

        if (hitSomething) {
            world.playSound(null, player.posX, player.posY, player.posZ,
                    SoundEvents.ENTITY_IRONGOLEM_ATTACK, player.getSoundCategory(), 1.0F, 1.0F);
        }

        return hitSomething;
    }

    @Override
    public void onTick(EntityPlayerMP player, World world) {
        // --- ATOUT 1 : Résistance aux dégâts (Armure naturelle) ---
        if (player.ticksExisted % 40 == 0) {
            player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 60, 1, false, false));
        }

        // --- ATOUT 2 : Immunité aux dégâts de chute ---
        // Le Golem est fait de fer, il ne prend pas de dégâts en tombant
        if (player.fallDistance > 0) {
            player.fallDistance = 0;
        }

        // --- ATOUT 3 : Résistance au recul (Knockback) ---
        // Difficile de faire bouger un Golem !
        if (player.hurtTime > 0) {
            player.motionX *= 0.5D;
            player.motionZ *= 0.5D;
        }

        // --- FAIBLESSE : Lenteur et Taille ---
        // Il est très lourd et grand (2,7 blocs de haut)
        if (player.ticksExisted % 20 == 0) {
            player.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 40, 1, false, false));
        }

        // Atout : Respiration aquatique (Il est en fer, il ne respire pas vraiment)
        player.setAir(300);
    }
}