package fr.draconium.core.primal.abilities;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class SnowGolemAbility implements IPrimalAbility {

    @Override
    public boolean execute(EntityPlayerMP player, World world) {
        // On tire 3 boules de neige à la suite vers le curseur
        for (int i = 0; i < 3; i++) {
            // Dans SnowGolemAbility.java
            EntitySnowball snowball = new EntitySnowball(world, player);
// Paramètres : (lanceur, pitch, yaw, roll, vitesse, imprécision)
// On met 0.0F à la fin pour une précision de 100%
            snowball.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, 1.5F, 0.0F);

            if (!world.isRemote) world.spawnEntity(snowball);
        }

        world.playSound(null, player.posX, player.posY, player.posZ,
                SoundEvents.ENTITY_SNOWMAN_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F);

        return true;
    }

    @Override
    public void onTick(EntityPlayerMP player, World world) {
        // --- ATOUT 1 : Trace de neige ---
        // Comme le vrai golem, il dépose de la neige là où il marche
        if (!world.isRemote) {
            for (int i = 0; i < 4; ++i) {
                int x = MathHelper.floor(player.posX + (double)((float)(i % 2 * 2 - 1) * 0.25F));
                int y = MathHelper.floor(player.posY);
                int z = MathHelper.floor(player.posZ + (double)((float)(i / 2 % 2 * 2 - 1) * 0.25F));
                BlockPos blockpos = new BlockPos(x, y, z);

                // Si c'est de l'air et qu'on peut poser de la neige
                if (world.isAirBlock(blockpos) && world.getBiome(blockpos).getTemperature(blockpos) < 0.8F && Blocks.SNOW_LAYER.canPlaceBlockAt(world, blockpos)) {
                    world.setBlockState(blockpos, Blocks.SNOW_LAYER.getDefaultState());
                }
            }
        }

        // --- FAIBLESSE 1 : Fond à la chaleur ---
        // Si le biome est trop chaud (désert, nether) ou s'il pleut
        BlockPos pos = new BlockPos(player.posX, player.posY + player.getEyeHeight(), player.posZ);
        if (world.getBiome(pos).getTemperature(pos) > 1.0F || world.isRainingAt(pos)) {
            if (player.ticksExisted % 20 == 0) {
                player.attackEntityFrom(DamageSource.HOT_FLOOR, 1.0F);
                world.playSound(null, player.posX, player.posY, player.posZ,
                        SoundEvents.ENTITY_SNOWMAN_HURT, player.getSoundCategory(), 1.0F, 1.0F);
            }
        }

        // --- FAIBLESSE 2 : L'eau (Lethal) ---
        if (player.isWet()) {
            if (player.ticksExisted % 10 == 0) {
                player.attackEntityFrom(DamageSource.DROWN, 1.0F);
            }
        }
    }
}