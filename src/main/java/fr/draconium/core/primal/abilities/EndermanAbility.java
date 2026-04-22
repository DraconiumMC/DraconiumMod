package fr.draconium.core.primal.abilities;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EndermanAbility implements IPrimalAbility {

    @Override
    public boolean execute(EntityPlayerMP player, World world) {
        // --- CAPACITÉ ACTIVE (G) : Téléportation précise ---
        // Portée de 32 blocs comme dans tes notes
        double reach = 32.0D;
        Vec3d eyes = player.getPositionEyes(1.0F);
        Vec3d look = player.getLook(1.0F);
        Vec3d targetVec = eyes.add(look.x * reach, look.y * reach, look.z * reach);

        // Raytrace pour trouver où le joueur regarde
        RayTraceResult ray = world.rayTraceBlocks(eyes, targetVec, false, true, false);

        if (ray != null && ray.typeOfHit == RayTraceResult.Type.BLOCK) {
            BlockPos pos = ray.getBlockPos().up(); // On vise le bloc au-dessus

            // Vérification si l'endroit est sûr (2 blocs de haut pour passer)
            if (world.isAirBlock(pos) && world.isAirBlock(pos.up())) {

                // Son de téléportation au départ
                world.playSound(null, player.posX, player.posY, player.posZ,
                        SoundEvents.ENTITY_ENDERMEN_TELEPORT, player.getSoundCategory(), 1.0F, 1.0F);

                // Déplacement
                player.connection.setPlayerLocation(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, player.rotationYaw, player.rotationPitch);

                // Son de téléportation à l'arrivée
                world.playSound(null, player.posX, player.posY, player.posZ,
                        SoundEvents.ENTITY_ENDERMEN_TELEPORT, player.getSoundCategory(), 1.0F, 1.0F);

                return true;
            }
        }
        return false;
    }

    @Override
    public void onTick(EntityPlayerMP player, World world) {
        // --- FAIBLESSE : Sensibilité à l'eau & Pluie ---
        if (player.isWet()) {
            // Dégâts constants (1 point toutes les secondes)
            if (player.ticksExisted % 20 == 0) {
                player.attackEntityFrom(DamageSource.DROWN, 1.0F);
                world.playSound(null, player.posX, player.posY, player.posZ,
                        SoundEvents.ENTITY_ENDERMEN_HURT, player.getSoundCategory(), 1.0F, 1.0F);

                if (player.ticksExisted % 100 == 0) { // On rafraîchit l'effet régulièrement
                    player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 300, 0, false, false));
                }

                // Panique : Téléportation aléatoire si le joueur est DANS l'eau (pas juste la pluie)
                if (player.isInWater()) {
                    teleportRandomly(player, world);
                }
            }
        }

        // --- ATOUT : Pas de dégâts de chute (Optionnel, mais logique pour un Enderman) ---
        if (player.fallDistance > 2.0F) {
            player.fallDistance = 0;
        }
    }

    private void teleportRandomly(EntityPlayerMP player, World world) {
        double x = player.posX + (world.rand.nextDouble() - 0.5D) * 16.0D;
        double y = player.posY + (double)(world.rand.nextInt(16) - 8);
        double z = player.posZ + (world.rand.nextDouble() - 0.5D) * 16.0D;
        player.connection.setPlayerLocation(x, y, z, player.rotationYaw, player.rotationPitch);
    }
}