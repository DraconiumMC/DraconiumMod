package fr.draconium.core.blocks;

import java.util.Random;

import fr.draconium.core.DraconiumCore;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockPyroniteOre extends Block {

    public BlockPyroniteOre(String name, Material materialIn) {
        super(materialIn);
        this.setTranslationKey(name);
        this.setRegistryName(name);
        this.setCreativeTab(DraconiumCore.DRACONIUM_TAB_BLOCK);
        this.setHardness(8.0F);
        this.setResistance(8.0F);
        this.setLightLevel(0.5F); // bloc lumineux
        this.setHarvestLevel("pickaxe", 3);
    }

    @Override
    public void randomDisplayTick(IBlockState state, World worldIn, BlockPos pos, Random rand) {
        if (!worldIn.isRemote) return;

        // petites particules de lave / braises
        if (rand.nextFloat() < 0.3F) {
            double x = pos.getX() + 0.5 + (rand.nextDouble() - 0.5) * 0.4;
            double y = pos.getY() + 0.5 + (rand.nextDouble() - 0.5) * 0.4;
            double z = pos.getZ() + 0.5 + (rand.nextDouble() - 0.5) * 0.4;
            worldIn.spawnParticle(EnumParticleTypes.LAVA, x, y, z, 0, 0.01, 0);
        }

        // son de lave de temps en temps
        if (rand.nextInt(100) == 0) {
            double x = pos.getX() + 0.5;
            double y = pos.getY() + 0.5;
            double z = pos.getZ() + 0.5;

            worldIn.playSound(
                    x, y, z,
                    SoundEvents.BLOCK_LAVA_POP,
                    SoundCategory.BLOCKS,
                    0.3F + rand.nextFloat() * 0.3F,
                    0.8F + rand.nextFloat() * 0.4F,
                    false
            );
        }
    }

    @Override
    public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
        super.onEntityWalk(worldIn, pos, entityIn);
        if (!worldIn.isRemote) {
            entityIn.attackEntityFrom(DamageSource.HOT_FLOOR, 1.0F);
        }
    }
}
