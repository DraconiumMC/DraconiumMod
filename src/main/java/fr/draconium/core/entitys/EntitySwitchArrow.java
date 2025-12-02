package fr.draconium.core.entitys;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntitySwitchArrow extends EntityTippedArrow {

    public EntitySwitchArrow(World worldIn) {
        super(worldIn);
    }

    public EntitySwitchArrow(World worldIn, EntityLivingBase shooter) {
        super(worldIn, shooter);
    }

    @Override
    protected void onHit(RayTraceResult raytraceResultIn) {
        // On appelle le super pour gérer les dégâts classiques de la flèche
        super.onHit(raytraceResultIn);

        // On vérifie qu'on est côté serveur et qu'on a touché une entité
        if (!world.isRemote && raytraceResultIn.entityHit != null) {

            // Sécurité 1 : Le tireur doit être vivant (pas null)
            if (this.shootingEntity instanceof EntityLivingBase) {

                EntityLivingBase shooter = (EntityLivingBase) this.shootingEntity;
                Entity targetEntity = raytraceResultIn.entityHit;

                // Sécurité 2 : La cible doit AUSSI être vivante (Joueur, Zombie, Vache...)
                // Sinon ça crash sur les Minecarts/Bateaux
                if (targetEntity instanceof EntityLivingBase) {

                    EntityLivingBase target = (EntityLivingBase) targetEntity;

                    // Stocke les positions
                    double shooterX = shooter.posX;
                    double shooterY = shooter.posY;
                    double shooterZ = shooter.posZ;

                    double targetX = target.posX;
                    double targetY = target.posY;
                    double targetZ = target.posZ;

                    // Échange les positions
                    // setPositionAndUpdate force la synchro client/serveur (évite les bugs visuels)
                    shooter.setPositionAndUpdate(targetX, targetY, targetZ);
                    target.setPositionAndUpdate(shooterX, shooterY, shooterZ);

                    // Effets Sonores (Style Enderman)
                    world.playSound(null, shooter.getPosition(), SoundEvents.ENTITY_ENDERPEARL_THROW, SoundCategory.PLAYERS, 1.0F, 1.0F);
                    world.playSound(null, target.getPosition(), SoundEvents.ENTITY_ENDERPEARL_THROW, SoundCategory.PLAYERS, 1.0F, 1.0F);
                }
            }
        }

        // Supprime la flèche après impact pour ne pas qu'on puisse la ramasser
        this.setDead();
    }
}