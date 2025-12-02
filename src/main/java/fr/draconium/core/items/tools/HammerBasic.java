package fr.draconium.core.items.tools;

import fr.draconium.core.DraconiumCore;
import fr.draconium.core.init.blocks.ores.BlocksOresInit;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class HammerBasic extends ItemPickaxe
{

	public HammerBasic(String name, ToolMaterial material)
	{
		super(material);
		this.setTranslationKey(name);
		this.setRegistryName(name);
		this.setMaxStackSize(1);
		this.setCreativeTab(DraconiumCore.DRACONIUM_TAB_TOOLS);
	}
	
	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment)
	{
		if (enchantment == Enchantments.MENDING)
		{
			return false;
		}
		return super.canApplyAtEnchantingTable(stack, enchantment);
	}
	
	public RayTraceResult rayTrace(double blockReachDistance, float partialTicks, World world, EntityLivingBase entity)
	{
		Vec3d vec3d 	= entity.getPositionEyes(partialTicks);
		Vec3d vec3d1 	= entity.getLook(partialTicks);
		Vec3d vec3d2 	= vec3d.add(vec3d1.x * blockReachDistance, vec3d1.y * blockReachDistance, vec3d1.z * blockReachDistance);
		return world.rayTraceBlocks(vec3d, vec3d2, false, false, true);
	}
	
	@Override
	public boolean onBlockDestroyed(ItemStack breaker, World world, IBlockState state, BlockPos position, EntityLivingBase entity)
	{
		if (entity instanceof EntityPlayer && !world.isRemote)
		{
			// Vérifie si le bloc est un minerai, si oui, retourne false sans miner
			if (state.getBlock() == Blocks.COAL_ORE 
					|| state.getBlock() == Blocks.IRON_ORE 
					|| state.getBlock() == Blocks.GOLD_ORE 
					|| state.getBlock() == Blocks.DIAMOND_ORE 
					|| state.getBlock() == Blocks.REDSTONE_ORE 
					|| state.getBlock() == Blocks.LAPIS_ORE 
					|| state.getBlock() == Blocks.EMERALD_ORE 
					|| state.getBlock() == Blocks.QUARTZ_ORE 
					|| state.getBlock() == BlocksOresInit.PYRONITE_ORE
					|| state.getBlock() == BlocksOresInit.DRACONIUM_ORE 
					|| state.getBlock() == BlocksOresInit.FINDIUM_ORE)
			{
				return false; // Ne pas miner les minerais
			}

			EntityPlayer player = (EntityPlayer) entity;
			RayTraceResult result = this.rayTrace(5.0D, 0.0f, world, entity);

			if (result.typeOfHit == RayTraceResult.Type.BLOCK)
			{
				int x = position.getX();
				int y = position.getY();
				int z = position.getZ();

				EnumFacing side = result.sideHit;

				if (side == EnumFacing.DOWN || side == EnumFacing.UP)
				{
					this.destroyAndDropBlock(world, player, breaker, x + 1, y, z - 1);
					this.destroyAndDropBlock(world, player, breaker, x + 1, y, z);
					this.destroyAndDropBlock(world, player, breaker, x + 1, y, z + 1);
					this.destroyAndDropBlock(world, player, breaker, x, y, z - 1);
					// Middle block
					this.destroyAndDropBlock(world, player, breaker, x, y, z + 1);
					this.destroyAndDropBlock(world, player, breaker, x - 1, y, z - 1);
					this.destroyAndDropBlock(world, player, breaker, x - 1, y, z);
					this.destroyAndDropBlock(world, player, breaker, x - 1, y, z + 1);
				}
				// Z
				// NORTH - SOUTH
				else if (side == EnumFacing.NORTH || side == EnumFacing.SOUTH)
				{
					this.destroyAndDropBlock(world, player, breaker, x + 1, y + 1, z);
					this.destroyAndDropBlock(world, player, breaker, x, y + 1, z);
					this.destroyAndDropBlock(world, player, breaker, x - 1, y + 1, z);
					this.destroyAndDropBlock(world, player, breaker, x + 1, y, z);
					// Middle block
					this.destroyAndDropBlock(world, player, breaker, x - 1, y, z);
					this.destroyAndDropBlock(world, player, breaker, x + 1, y - 1, z);
					this.destroyAndDropBlock(world, player, breaker, x, y - 1, z);
					this.destroyAndDropBlock(world, player, breaker, x - 1, y - 1, z);
				}
				// X
				// EAST - WEST
				else if (side == EnumFacing.EAST || side == EnumFacing.WEST)
				{
					this.destroyAndDropBlock(world, player, breaker, x, y + 1, z + 1);
					this.destroyAndDropBlock(world, player, breaker, x, y + 1, z);
					this.destroyAndDropBlock(world, player, breaker, x, y + 1, z - 1);
					this.destroyAndDropBlock(world, player, breaker, x, y, z + 1);
					// Middle block
					this.destroyAndDropBlock(world, player, breaker, x, y, z - 1);
					this.destroyAndDropBlock(world, player, breaker, x, y - 1, z + 1);
					this.destroyAndDropBlock(world, player, breaker, x, y - 1, z);
					this.destroyAndDropBlock(world, player, breaker, x, y - 1, z - 1);
				}
				return true;
			}
		}

		breaker.damageItem(1, entity);
		return super.onBlockDestroyed(breaker, world, state, position, entity);
	}
	
	private void destroyAndDropBlock(World world, EntityPlayer player, ItemStack breaker, int x, int y, int z)
	{
		BlockPos position = new BlockPos(x, y, z);

		if (world.getBlockState(position).getBlockHardness(world, position) >= 0 && world.getBlockState(position).getBlock().getMaterial(world.getBlockState(position).getBlock().getDefaultState()) == Material.ROCK)
		{
			world.getBlockState(position).getBlock().harvestBlock(world, player, position, world.getBlockState(position), world.getTileEntity(position), breaker);
			world.setBlockToAir(position);
		}
	}
}
