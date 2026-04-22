package fr.draconium.core.primal.abilities;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import java.util.List;

public class PigZombieAbility implements IPrimalAbility {

    @Override
    public boolean execute(EntityPlayerMP player, World world) {
        // --- CAPACITÉ ACTIVE (G) : Cri de Ralliement ---
        // Alerte tous les Pigmen autour pour attaquer la cible que le joueur regarde
        world.playSound(null, player.posX, player.posY, player.posZ,
                SoundEvents.ENTITY_ZOMBIE_PIG_ANGRY, player.getSoundCategory(), 1.0F, 1.0F);

        // Effet visuel de colère
        player.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 200, 0)); // Force I pendant 10s

        // Atout : Les Pigmen alliés proches deviennent agressifs envers les ennemis du joueur
        AxisAlignedBB area = player.getEntityBoundingBox().grow(16.0D);
        List<EntityPigZombie> allies = world.getEntitiesWithinAABB(EntityPigZombie.class, area);

        for (EntityPigZombie pigman : allies) {
            // Simule l'alerte de groupe vanilla
            pigman.setRevengeTarget(player.getLastAttackedEntity());
        }

        return true;
    }

    @Override
    public void onTick(EntityPlayerMP player, World world) {
        // --- ATOUT 1 : Immunité au Feu ---
        // C'est un habitant du Nether, il ne brûle pas.
        if (player.isBurning()) {
            player.extinguish();
        }

        // --- ATOUT 2 : Constitution Putréfiée ---
        // Il a plus de résistance que le cochon normal
        if (player.ticksExisted % 40 == 0) {
            player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 60, 0, false, false));
        }
        if (player.ticksExisted % 100 == 0) { // On rafraîchit l'effet régulièrement
            player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 300, 0, false, false));
        }
        // --- FAIBLESSE : L'Eau ---
        // Comme le Zombie, il est lourd et coule au fond de l'eau.
        if (player.isInWater()) {
            player.motionY -= 0.03D;
        }

        // --- PASSIF : Vengeance ---
        // Si le joueur est frappé, il gagne un court bonus de vitesse (Speed I)
        if (player.hurtTime > 0) {
            player.addPotionEffect(new PotionEffect(MobEffects.SPEED, 60, 0, false, false));
        }
    }
}