package fr.draconium.core.actions;

import java.util.Random;

import fr.draconium.core.capabilities.player.ExtendedPlayerData;
import fr.draconium.core.proxy.packets.server.EnergyShieldPacket; // Import des constantes de durée
import fr.draconium.core.proxy.packets.server.SpawnWolfPacket;   // Import des constantes de durée
import fr.draconium.core.proxy.packets.server.TeleportPacket;    // Import des constantes de durée
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class KeyAction
{
    // --- Logique de la Téléportation Aléatoire ---
    public static void teleportRandomly(EntityPlayer player)
    {
        if (player.world.isRemote) return;

        ExtendedPlayerData.DraconiumArmorAbilities armorAbilities = ExtendedPlayerData.get(player).draconiumArmorAbilities;
        long worldTime = player.world.getTotalWorldTime();
        long expiryTime = armorAbilities.getTeleportCooldownExpiry();

        // 1. VÉRIFICATION DU COOLDOWN
        if (worldTime < expiryTime)
        {
            long remainingTicks = expiryTime - worldTime;
            // Le formatage complet est géré par les Packet Handlers, ici on simplifie l'affichage
            player.sendMessage(new TextComponentString("Cette abilitée est en cooldown. Temps restant: " + remainingTicks / 20 + "s"));
            return;
        }

        // 2. COOLDOWN EXPIRÉ : Appliquer la nouvelle expiration
        armorAbilities.setTeleportCooldownExpiry(worldTime + TeleportPacket.TELEPORT_COOLDOWN_DURATION);

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

    // --- Logique du Bouclier Énergétique ---
    public static void applyEnergyShield(EntityPlayer player)
    {
        if (player.world.isRemote) return;

        ExtendedPlayerData.DraconiumArmorAbilities armorAbilities = ExtendedPlayerData.get(player).draconiumArmorAbilities;
        long worldTime = player.world.getTotalWorldTime();

        if (worldTime < armorAbilities.getShieldCooldownExpiry())
        {
            long remainingTicks = armorAbilities.getShieldCooldownExpiry() - worldTime;
            player.sendMessage(new TextComponentString("Le bouclier est en cooldown. Temps restant: " + remainingTicks / 20 + "s"));
            return;
        }

        // Appliquer le nouveau cooldown
        armorAbilities.setShieldCooldownExpiry(worldTime + EnergyShieldPacket.SHIELD_COOLDOWN_DURATION);

        if (player.getRNG().nextDouble() < 0.3)
        {
            // 30% de chance d'activer le bouclier
            player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 100, 1)); // Résistance
        }
    }

    // --- Logique d'Invocation des Alliés (Loups) ---
    public static void spawnAllies(EntityPlayer player)
    {
        if (player.world.isRemote) return;

        ExtendedPlayerData.DraconiumArmorAbilities armorAbilities = ExtendedPlayerData.get(player).draconiumArmorAbilities;
        long worldTime = player.world.getTotalWorldTime();

        if (worldTime < armorAbilities.getWolfCooldownExpiry())
        {
            long remainingTicks = armorAbilities.getWolfCooldownExpiry() - worldTime;
            player.sendMessage(new TextComponentString("L'invocation est en cooldown. Temps restant: " + remainingTicks / 20 + "s"));
            return;
        }

        // Appliquer le nouveau cooldown
        armorAbilities.setWolfCooldownExpiry(worldTime + SpawnWolfPacket.WOLF_COOLDOWN_DURATION);

        if (!player.world.isRemote)
        {
            World world    = player.world;
            Random random  = player.getRNG();

            // Déterminer une distance aléatoire entre 1 et 10 blocs
            double distance = 1.0D + random.nextDouble() * 9.0D;

            double x      = player.posX + (random.nextDouble() - 0.5D) * 2.0D * distance;
            double y      = player.posY;
            double z      = player.posZ + (random.nextDouble() - 0.5D) * 2.0D * distance;

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