package fr.draconium.core.entitys;

import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityGrenade extends EntityThrowable
{
	//private static final double HITBOX_SIZE = 0.5D;
	//private float rotationAngle;

	public EntityGrenade(World worldIn)
	{
		super(worldIn);
	}

	public EntityGrenade(World worldIn,EntityLivingBase throwerIn)
	{
		super(worldIn, throwerIn);
	}
	
	/*
	@Override
	protected void entityInit()
	{
		this.setSize((float) HITBOX_SIZE, (float) HITBOX_SIZE);
		this.updateBoundingBox();
	}
	
	private void updateBoundingBox()
	{
		this.setEntityBoundingBox(new AxisAlignedBB(this.posX - HITBOX_SIZE / 2, this.posY - HITBOX_SIZE / 2, this.posZ - HITBOX_SIZE / 2, this.posX + HITBOX_SIZE / 2, this.posY + HITBOX_SIZE / 2, this.posZ + HITBOX_SIZE / 2));
	}
	
	@Override
	public void onUpdate()
	{
		super.onUpdate();

		// Mettre à jour l'angle de rotation
		rotationAngle += 10.0F; // Ajustez la vitesse ici
		if (rotationAngle >= 360.0F)
		{
			rotationAngle -= 360.0F;
		}

		// Calculer la nouvelle position pour simuler un mouvement circulaire
		double radius = 0.1; // Ajustez la taille du cercle
		this.posX += Math.sin(Math.toRadians(rotationAngle)) * radius;
		this.posZ += Math.cos(Math.toRadians(rotationAngle)) * radius;

		// Mettre à jour la hitbox
		updateBoundingBox();
	}
	*/
	@Override
	protected void onImpact(RayTraceResult result)
	{
		if (!this.world.isRemote)
		{
			this.world.createExplosion(this, this.posX, this.posY, this.posZ, 4.0F, true);
			this.world.playSound(null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 1.0F, 1.0F);
			this.world.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, this.posX, this.posY, this.posZ, 1, 0, 0, 0, 0);
			this.destroyObsidianNearby();
			this.setDead();
		}
	}

	private void destroyObsidianNearby()
	{
		BlockPos explosionPos = new BlockPos(this.posX, this.posY, this.posZ);
		int explosionRadius = 2;

		for (int x = explosionPos.getX() - explosionRadius; x <= explosionPos.getX() + explosionRadius; x++)
		{
			for (int y = explosionPos.getY() - explosionRadius; y <= explosionPos.getY() + explosionRadius; y++)
			{
				for (int z = explosionPos.getZ() - explosionRadius; z <= explosionPos.getZ() + explosionRadius; z++)
				{
					BlockPos currentPos = new BlockPos(x, y, z);
					if (this.world.getBlockState(currentPos).getBlock() instanceof BlockObsidian && this.world.rand.nextFloat() < 0.4F)
					{
						this.world.destroyBlock(currentPos, true);
					}
				}
			}
		}
	}
}
