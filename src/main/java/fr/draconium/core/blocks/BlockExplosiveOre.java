package fr.draconium.core.blocks;

import java.util.Random;

import fr.draconium.core.DraconiumCore;
import fr.draconium.core.init.items.others.OthersInit;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class BlockExplosiveOre extends Block
{

	public BlockExplosiveOre(String name, Material materialIn)
	{
		super(materialIn);
		this.setUnlocalizedName(name);
		this.setRegistryName(name);
		this.setHardness(8.0F);
		this.setCreativeTab(DraconiumCore.DRACONIUM_TAB_BLOCK);
	}
	
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune)
	{
		return OthersInit.DEBRIS_GRENADE;
	}
	
	@Override
	public int quantityDroppedWithBonus(int fortune, Random random)
	{
		return random.nextFloat() < (0.6F + fortune * 0.06F) ? 1 : 0;
	}
	
	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player)
	{
		super.onBlockHarvested(worldIn, pos, state, player);
		if (!worldIn.isRemote)
		{
			double x = pos.getX() + 0.5, y = pos.getY() + 0.5, z = pos.getZ() + 0.5;
			player.attackEntityFrom(new DamageSource("explosion").setDifficultyScaled().setExplosion(), 2F);

			WorldServer worldServer = (WorldServer) worldIn;
			worldServer.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, x, y, z, 1, 0D, 0D, 0D, 1D);
			worldServer.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, x, y, z, 5, 1.5, 1.5, 1.5, 1);
			worldServer.playSound(null, x, y, z, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 4F, (1F + (worldIn.rand.nextFloat() - worldIn.rand.nextFloat()) * 0.2F) * 0.7F);
		}
	}
}
