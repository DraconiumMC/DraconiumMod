package fr.draconium.core.primal.abilities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class GhastAbility implements IPrimalAbility {

    private int sneakTimer = 0;

    @Override
    public boolean execute(EntityPlayerMP player, World world) {
        Vec3d look = player.getLook(1.0F);

        // 1. Position de spawn : 3 blocs devant pour ne pas exploser sur le joueur
        double x = player.posX + look.x * 3.0D;
        double y = player.posY + player.getEyeHeight() + look.y * 1.0D;
        double z = player.posZ + look.z * 3.0D;

        // 2. Création de la boule de feu
        EntityLargeFireball fireball = new EntityLargeFireball(world, player, look.x, look.y, look.z);
        fireball.explosionPower = 2;
        fireball.setPosition(x, y, z);

        // --- LE FIX DE PRÉCISION ---
        // On force les accélérations pour que la boule suive le vecteur du regard sans "flotter"
        fireball.accelerationX = look.x * 0.1D;
        fireball.accelerationY = look.y * 0.1D;
        fireball.accelerationZ = look.z * 0.1D;

        if (!world.isRemote) {
            world.spawnEntity(fireball);
        }

        world.playSound(null, player.posX, player.posY, player.posZ,
                SoundEvents.ENTITY_GHAST_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F);

        return true;
    }

    @Override
    public void onTick(EntityPlayerMP player, World world) {
        // --- LOGIQUE DE VOL ---
        if (!player.capabilities.allowFlying) {
            player.capabilities.allowFlying = true;
            player.sendPlayerAbilities();
        }

        // --- GESTION THERMIQUE & EFFETS ---
        handleEnvironmentEffects(player, world);

        // --- GESTION DU PASSAGER & RÉVOLTE ---
        handlePassengerLogic(player, world);
    }

    private void handleEnvironmentEffects(EntityPlayerMP player, World world) {
        if (player.isInLava()) {
            player.extinguish();
            player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 40, 1, false, false));
            if (player.ticksExisted % 20 == 0) player.heal(1.0F);
        } else if (player.isWet()) {
            player.motionY -= 0.05D;
            if (player.ticksExisted % 20 == 0) {
                player.attackEntityFrom(DamageSource.DROWN, 1.0F);
            }
        }

        if (player.ticksExisted % 100 == 0) {
            player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 300, 0, false, false));
        }
    }

    private void handlePassengerLogic(EntityPlayerMP player, World world) {
        if (player.isBeingRidden()) {
            Entity passenger = player.getPassengers().get(0);

            if (passenger instanceof EntityPlayer) {
                EntityPlayer rider = (EntityPlayer) passenger;

                // Contrôle avec la Carrot on a Stick
                if (rider.getHeldItemMainhand().getItem() == Items.CARROT_ON_A_STICK) {
                    handleAirSteering(player, rider);
                    player.capabilities.setFlySpeed(rider.isSwingInProgress ? 0.08F : 0.04F);
                } else {
                    player.capabilities.setFlySpeed(0.02F);
                }
            }

            // Système de révolte (Sneak 3s)
            if (player.isSneaking()) {
                sneakTimer++;
                if (sneakTimer >= 60) {
                    player.removePassengers();
                    player.sendMessage(new TextComponentString("§cVous avez expulsé votre cavalier !"));
                    world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_GHAST_SCREAM, SoundCategory.PLAYERS, 1.0F, 1.0F);
                    sneakTimer = 0;
                }
            } else {
                sneakTimer = 0;
            }

            // Stationnaire
            if (player.motionY < 0 && !player.isSneaking()) player.motionY *= 0.1D;

        } else {
            player.capabilities.setFlySpeed(0.02F);
        }
    }

    private void handleAirSteering(EntityPlayerMP mount, EntityPlayer rider) {
        mount.rotationYaw = rider.rotationYaw;
        mount.prevRotationYaw = mount.rotationYaw;
        mount.setRotationYawHead(mount.rotationYaw);

        if (rider.moveForward != 0 || rider.moveStrafing != 0) {
            double pitchRad = Math.toRadians(rider.rotationPitch);
            mount.motionY = -Math.sin(pitchRad) * 0.4D;
            mount.moveForward = rider.moveForward;
            mount.moveStrafing = rider.moveStrafing;
        }
        mount.velocityChanged = true;
    }
}