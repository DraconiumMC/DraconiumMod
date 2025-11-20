package fr.draconium.core.actions;

import java.util.Random;

import fr.draconium.core.capabilities.player.ExtendedPlayerData;
import fr.draconium.core.items.armors.DraconiqueArmor;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class KeyAction
{
	public static void teleportRandomly(EntityPlayer player)
	{
		if (player.world.isRemote) return;
		
		ExtendedPlayerData.DraconiumArmorAbilities armorAbilities = ExtendedPlayerData.get(player).draconiumArmorAbilities;
		if (armorAbilities.getTeleportCooldown() > 0)
		{
			player.sendMessage(new TextComponentString("Cette abilitée est en cooldown."));
			return;
		}
	
		final int TELEPORT_INTERVAL_TICKS = 84000; // 70 minutes en ticks
		
		armorAbilities.setTeleportCooldown(TELEPORT_INTERVAL_TICKS);

		// Code de la téléportation extrait du chorus fruit
		World world = player.world;
		Random random = player.getRNG();

		if (player.isRiding()) player.dismountRidingEntity();

		for (int i = 0; i < 16; i++)
		{
			final int TELEPORT_DISTANCE = 9;
			
			double x = player.posX + (random.nextDouble() - 0.5D) * TELEPORT_DISTANCE;
			double y = MathHelper.clamp(player.posY + (random.nextInt(TELEPORT_DISTANCE) - (double) TELEPORT_DISTANCE / 2), 0.0D, world.getActualHeight() - 1);
			double z = player.posZ + (random.nextDouble() - 0.5D) * TELEPORT_DISTANCE;

			if (player.attemptTeleport(x, y, z)) break;
		}
	}
	
	public static void applyEnergyShield(EntityPlayer player)
	{
		if (player.getRNG().nextDouble() < 0.3)
		{
			// 30% de chance d'activer le bouclier
			player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 100, 1)); // Résistance
			
		}
	}

	public static void spawnAllies(EntityPlayer player)
	{
		if (!player.world.isRemote)
		{
			World world 	= player.world;
			Random random 	= player.getRNG();

			// Déterminer une distance aléatoire entre 1 et 10 blocs
			double distance = 1.0D + random.nextDouble() * 9.0D;

			double x 		= player.posX + (random.nextDouble() - 0.5D) * 2.0D * distance;
			double y 		= player.posY;
			double z 		= player.posZ + (random.nextDouble() - 0.5D) * 2.0D * distance;

			// Assurez-vous que la position est valide avant de faire apparaître l'allié
			if (world.getCollisionBoxes(player, player.getEntityBoundingBox().offset(x - player.posX, y - player.posY, z - player.posZ)).isEmpty())
			{
				EntityWolf wolf = new EntityWolf(world);
				wolf.setPosition(x, y, z);
				wolf.setTamedBy(player);
				world.spawnEntity(wolf);
			}
		}
	}
}
