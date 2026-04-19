package fr.draconium.core.primal;

import fr.draconium.core.capabilities.player.ExtendedPlayerData;
import fr.draconium.core.primal.network.PrimalNetwork;
import fr.draconium.core.worlds.ModConfig;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public final class PrimalAvatarAbilities {

	private PrimalAvatarAbilities() {
	}

	public static void tryActivate(EntityPlayerMP player) {
		try {
			PrimalAvatarType type = PrimalAvatarLogic.getEquippedSetType(player);
			if (!type.isActive()) {
				return;
			}

			World world = player.world;
			long now = world.getTotalWorldTime();
			ExtendedPlayerData.PrimalAvatarAbilityState st = ExtendedPlayerData.get(player).primalAvatarAbility;
			if (now < st.getNextAbilityUseWorldTick()) {
				int sec = (int) Math.ceil((st.getNextAbilityUseWorldTick() - now) / 20.0);
				player.sendMessage(new TextComponentString("Recharge : " + sec + " s"));
				return;
			}

			if (!consumePowerCost(player)) {
				player.sendMessage(new TextComponentString("Vous n'avez pas assez de nourriture ou de durabilité d'armure."));
				return;
			}

			boolean ok = false;
			switch (type) {
				case PIG:
					ok = doPigGroan(player, world);
					break;
				case ZOMBIE:
					ok = doZombieSummon(player, world);
					break;
				case ENDERMAN:
					ok = doEndermanTeleport(player, world);
					break;
				case SALMON:
					ok = doSalmonLeap(player, world);
					break;
				case GHAST:
					ok = doGhastFireball(player, world);
					break;
				default:
					break;
			}

			if (ok) {
				st.setNextAbilityUseWorldTick(now + type.defaultAbilityCooldownTicks);
				player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, player.getSoundCategory(), 0.6F, 1.2F);
			}
		} finally {
			PrimalNetwork.syncAbilityCooldown(player);
		}
	}

	private static boolean consumePowerCost(EntityPlayerMP player) {
		if (player.getFoodStats().getFoodLevel() > 6) {
			player.addExhaustion(4.0F);
			return true;
		}
		for (net.minecraft.inventory.EntityEquipmentSlot slot : net.minecraft.inventory.EntityEquipmentSlot.values()) {
			if (slot.getSlotType() != net.minecraft.inventory.EntityEquipmentSlot.Type.ARMOR) {
				continue;
			}
			net.minecraft.item.ItemStack stack = player.getItemStackFromSlot(slot);
			if (!stack.isEmpty() && stack.isItemStackDamageable() && stack.getItemDamage() < stack.getMaxDamage() - 1) {
				stack.damageItem(2, player);
				return true;
			}
		}
		return false;
	}

	private static boolean doPigGroan(EntityPlayerMP player, World world) {
		AxisAlignedBB box = player.getEntityBoundingBox().grow(10.0D, 5.0D, 10.0D);
		for (EntityAnimal animal : world.getEntitiesWithinAABB(EntityAnimal.class, box)) {
			if (animal.equals(player) || animal.getLeashed() || animal.isDead) {
				continue;
			}
			Vec3d push = new Vec3d(player.posX - animal.posX, 0, player.posZ - animal.posZ).normalize().scale(0.35D);
			animal.motionX += push.x;
			animal.motionZ += push.z;
			animal.velocityChanged = true;
		}
		return true;
	}

	private static boolean doZombieSummon(EntityPlayerMP player, World world) {
		EntityZombie ally = new EntityZombie(world);
		ally.setChild(true);
		ally.setPosition(player.posX + (world.rand.nextDouble() - 0.5D) * 2.0D, player.posY, player.posZ + (world.rand.nextDouble() - 0.5D) * 2.0D);
		ally.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(ally)), null);
		world.spawnEntity(ally);
		return true;
	}

	private static boolean doEndermanTeleport(EntityPlayerMP player, World world) {
		double reach = 32.0D;
		Vec3d eyes = player.getPositionEyes(1.0F);
		Vec3d look = player.getLook(1.0F);
		Vec3d end = eyes.add(look.x * reach, look.y * reach, look.z * reach);
		RayTraceResult trace = world.rayTraceBlocks(eyes, end, false, true, false);
		BlockPos target = trace != null && trace.typeOfHit == RayTraceResult.Type.BLOCK ? trace.getBlockPos() : null;
		if (target == null) {
			return false;
		}
		BlockPos.MutableBlockPos step = new BlockPos.MutableBlockPos(target);
		for (int i = 0; i < 4; i++) {
			if (!world.getBlockState(step).causesSuffocation() && !world.getBlockState(step.up()).causesSuffocation()) {
				player.connection.setPlayerLocation(step.getX() + 0.5D, step.getY(), step.getZ() + 0.5D, player.rotationYaw, player.rotationPitch);
				return true;
			}
			step.move(EnumFacing.DOWN);
		}
		return false;
	}

	private static boolean doSalmonLeap(EntityPlayerMP player, World world) {
		Vec3d look = player.getLook(1.0F).normalize().scale(1.8D);
		player.motionX += look.x;
		player.motionY += Math.max(0.35D, look.y * 0.8D) + 0.2D;
		player.motionZ += look.z;
		player.velocityChanged = true;
		player.fallDistance = 0.0F;
		return true;
	}

	private static boolean doGhastFireball(EntityPlayerMP player, World world) {
		Vec3d look = player.getLook(1.0F);
		EntityLargeFireball ball = new EntityLargeFireball(world, player, look.x * 0.1D + (world.rand.nextDouble() - 0.5D) * 0.02D, look.y * 0.1D, look.z * 0.1D + (world.rand.nextDouble() - 0.5D) * 0.02D);
		ball.explosionPower = 1;
		ball.setPosition(player.posX + look.x * 2.0D, player.posY + player.getEyeHeight() + look.y * 1.2D, player.posZ + look.z * 2.0D);
		world.spawnEntity(ball);
		return true;
	}

	public static void tickPassiveServer(EntityPlayerMP player, PrimalAvatarType type) {
		World world = player.world;
		if (type == PrimalAvatarType.ZOMBIE && world.isDaytime() && world.canSeeSky(player.getPosition()) && !world.isRainingAt(player.getPosition()) && player.ticksExisted % 40 == 0) {
			ItemStack head = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
			if (head.isEmpty()) {
				player.setFire(2);
			}
		}
		if (type == PrimalAvatarType.ENDERMAN && player.isWet() && player.ticksExisted % 10 == 0) {
			player.attackEntityFrom(DamageSource.DROWN, 2.0F);
		}
		if (type == PrimalAvatarType.SALMON && type.fishBiology) {
			boolean inWater = player.isInsideOfMaterial(net.minecraft.block.material.Material.WATER) || player.isInWater();
			boolean rain = world.isRainingAt(player.getPosition()) && world.canSeeSky(player.getPosition());
			if (!inWater && !rain && player.ticksExisted % 30 == 0) {
				player.attackEntityFrom(DamageSource.DROWN, 1.0F);
			}
			if (inWater && player.ticksExisted % 5 == 0) {
				double f = ModConfig.primalFishSwimBoost;
				player.motionX = MathHelper.clamp(player.motionX * f, -1.8D, 1.8D);
				player.motionY = MathHelper.clamp(player.motionY * f, -1.2D, 1.2D);
				player.motionZ = MathHelper.clamp(player.motionZ * f, -1.8D, 1.8D);
				player.velocityChanged = true;
			}
			if (!inWater && player.onGround) {
				player.motionX *= 0.15D;
				player.motionZ *= 0.15D;
			}
		}
	}

}
