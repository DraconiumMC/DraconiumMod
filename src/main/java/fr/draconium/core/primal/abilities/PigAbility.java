package fr.draconium.core.primal.abilities;

import fr.draconium.core.primal.PrimalAvatarLogic;
import fr.draconium.core.primal.PrimalAvatarType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class PigAbility implements IPrimalAbility {

    private int sneakTimer = 0; // Compteur pour la "Révolte" (éjection forcée)

    @Override
    public boolean execute(EntityPlayerMP player, World world) {
        // --- 2.4 : CAPACITÉ ACTIVE (G) ---
        // Grognement et léger soin (fouille le sol)
        world.playSound(null, player.posX, player.posY, player.posZ,
                SoundEvents.ENTITY_PIG_AMBIENT, SoundCategory.PLAYERS, 1.0F, 1.0F);

        if (player.getHealth() < player.getMaxHealth()) {
            player.heal(1.0F); // Rend 0.5 coeur
            player.getServerWorld().spawnParticle(EnumParticleTypes.HEART,
                    player.posX, player.posY + 1.0, player.posZ, 2, 0.2, 0.2, 0.2, 0.0);
        }

        return true;
    }

    @Override
    public void onTick(EntityPlayerMP player, World world) {
        // --- 2.1 & 2.2 : GESTION DU PASSAGER ---
        if (player.isBeingRidden()) {
            Entity passenger = player.getPassengers().get(0);

            if (passenger instanceof EntityPlayer) {
                EntityPlayer rider = (EntityPlayer) passenger;

                // --- 2.3.B : MODE SOUMISSION (Bâton et Carotte) ---
                if (rider.getHeldItemMainhand().getItem() == Items.CARROT_ON_A_STICK) {
                    handleSteering(player, rider);

                    // --- BOOST DU BÂTON ---
                    // Si le cavalier utilise le bâton (clic droit), donne Speed II
                    if (rider.isSwingInProgress && player.ticksExisted % 20 == 0) {
                        player.addPotionEffect(new PotionEffect(MobEffects.SPEED, 100, 1));
                        // On consomme la durabilité du bâton
                        rider.getHeldItemMainhand().damageItem(1, rider);
                    }
                }
                // --- 2.3.A : MODE PARTENARIAT ---
                // Si pas d'item de contrôle, on ne fait rien ici (la monture garde le contrôle)
            }

            // --- 2.4 : RÉVOLTE DE LA MONTURE (Éjection forcée) ---
            // Si le joueur cochon maintient Sneak (Maj) pendant 3 secondes
            if (player.isSneaking()) {
                sneakTimer++;
                if (sneakTimer >= 60) { // 60 ticks = 3 secondes
                    ejectRider(player, world);
                    sneakTimer = 0;
                }
            } else {
                sneakTimer = 0;
            }

            // --- PROTECTION ---
            // On protège le duo des dégâts de chute
            if (player.fallDistance > 1.0F) {
                player.fallDistance = 0;
            }
        }
    }

    /**
     * Gère le transfert des contrôles du Cavalier vers la Monture
     */
    /**
     * Gère le transfert des contrôles du Cavalier vers la Monture
     */
    private void handleSteering(EntityPlayerMP mount, EntityPlayer rider) {
        // Transfert de la vue (Direction)
        mount.rotationYaw = rider.rotationYaw;
        mount.prevRotationYaw = mount.rotationYaw;
        mount.setRotationYawHead(mount.rotationYaw);

        // Transfert des déplacements (Z, Q, S, D)
        mount.moveForward = rider.moveForward;
        mount.moveStrafing = rider.moveStrafing;

        // --- CORRECTION DU SAUT ---
        // On utilise l'attribut motionY ou isJumping via réflexion/accesseur
        // Si le cavalier essaie de sauter, on fait sauter le cochon
        if (rider.rotationPitch < -45.0F && rider.moveForward > 0) {
            // Optionnel : Sauter si on regarde vers le haut
        }

        // La méthode la plus simple pour le saut sans erreur de compilation :
        if (rider.motionY > 0 && mount.onGround) {
            mount.jump();
        }
    }

    /**
     * Éjecte le passager avec un effet sonore
     */
    private void ejectRider(EntityPlayerMP mount, World world) {
        mount.removePassengers();
        world.playSound(null, mount.posX, mount.posY, mount.posZ,
                SoundEvents.ENTITY_PIG_HURT, SoundCategory.PLAYERS, 1.0F, 2.0F);

        mount.sendMessage(new TextComponentString("§cVous avez secoué votre cavalier pour l'éjecter !"));

        // On donne une petite impulsion pour séparer les joueurs
        mount.motionY = 0.3D;
        mount.velocityChanged = true;
    }
}