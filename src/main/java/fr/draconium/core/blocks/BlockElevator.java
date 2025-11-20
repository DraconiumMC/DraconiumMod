package fr.draconium.core.blocks;

import java.util.HashSet;

import fr.draconium.core.DraconiumCore;
import fr.draconium.core.init.blocks.BlocksInit;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class BlockElevator extends Block
{

	private final HashSet<EntityPlayer> playersSneaking = new HashSet<>();

	public BlockElevator(String name, Material materialIn)
	{
		super(materialIn);
		this.setUnlocalizedName(name);
		this.setRegistryName(name);
		this.setHardness(8.0F);
		this.setCreativeTab(DraconiumCore.DRACONIUM_TAB_BLOCK);

		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent event)
	{
		EntityPlayer player = event.player;
		World world 		= player.world;
		
		if (!world.isRemote && event.phase == TickEvent.Phase.END)
		{
			if (player.isSneaking())
			{
				if (playersSneaking.add(player))
				{
					BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(MathHelper.floor(player.posX), MathHelper.floor(player.posY) - 1, MathHelper.floor(player.posZ));
					if (world.getBlockState(pos).getBlock() == BlocksInit.ELEVATOR)
					{
						for (int y = (int) player.posY - 2; y > -1; y--)
						{
							pos.setY(y);
							if (world.getBlockState(pos).getBlock() == BlocksInit.ELEVATOR)
							{
								player.attemptTeleport(player.posX, y + 1, player.posZ);
								break;
							}
						}
					}
				}
			}
			else
			{
				playersSneaking.remove(player);
			}
		}
	}

	@SubscribeEvent
	public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event)
	{
		playersSneaking.remove(event.player);
	}

	@SubscribeEvent
	public void onLivingJump(LivingEvent.LivingJumpEvent event)
	{
		EntityLivingBase entity = event.getEntityLiving();
		World world 			= entity.world;
		
		if (!world.isRemote && entity instanceof EntityPlayer)
		{
			BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(MathHelper.floor(entity.posX), MathHelper.floor(entity.posY) - 1, MathHelper.floor(entity.posZ));
			if (world.getBlockState(pos).getBlock() == BlocksInit.ELEVATOR)
			{
				for (int y = (int) entity.posY + 1; y < 256; y++)
				{
					pos.setY(y);
					if (world.getBlockState(pos).getBlock() == BlocksInit.ELEVATOR && entity.attemptTeleport(entity.posX, y + 1, entity.posZ))
					{
						entity.motionY = 0;
						entity.velocityChanged = true;
						break;
					}
				}
			}
		}
	}
}
