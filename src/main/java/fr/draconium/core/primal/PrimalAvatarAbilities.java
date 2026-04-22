package fr.draconium.core.primal;

import fr.draconium.core.capabilities.player.ExtendedPlayerData;
import fr.draconium.core.primal.network.PrimalNetwork;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.server.permission.PermissionAPI;


public final class PrimalAvatarAbilities {

    private PrimalAvatarAbilities() {}

    public static void tryActivate(EntityPlayerMP player) {
        try {
            PrimalAvatarType type = PrimalAvatarLogic.getEquippedSetType(player);
            if (type == null || !type.isActive()) return;

            // --- SÉCURITÉ 1 : Permission pour la touche G ---
            String node = "primal.ability." + type.name().toLowerCase();
            if (!PermissionAPI.hasPermission(player, node)) {
                player.sendMessage(new TextComponentString("§c§lERREUR §8» §7Vous n'avez pas la permission LuckPerms : §e" + node));
                return;
            }

            World world = player.world;
            long now = world.getTotalWorldTime();
            ExtendedPlayerData.PrimalAvatarAbilityState st = ExtendedPlayerData.get(player).primalAvatarAbility;

            if (now < st.getNextAbilityUseWorldTick()) {
                int sec = (int) Math.ceil((st.getNextAbilityUseWorldTick() - now) / 20.0);
                player.sendMessage(new TextComponentString("§6Primal §8» §cRecharge : §e" + sec + "s"));
                return;
            }

            if (!consumePowerCost(player)) {
                player.sendMessage(new TextComponentString("§6Primal §8» §4Énergie insuffisante !"));
                return;
            }

            boolean success = type.getAbility().execute(player, world);

            if (success) {
                st.setNextAbilityUseWorldTick(now + type.defaultAbilityCooldownTicks);
                world.playSound(null, player.posX, player.posY, player.posZ,
                        SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, player.getSoundCategory(), 0.6F, 1.2F);
            }

        } finally {
            PrimalNetwork.syncAbilityCooldown(player);
        }
    }

    public static void tickPassiveServer(EntityPlayerMP player, PrimalAvatarType type) {
        // --- SÉCURITÉ 2 : Permission pour PORTER l'armure (Avatar) ---
        String node = "primal.avatar." + type.name().toLowerCase();
        boolean hasPermission = PermissionAPI.hasPermission(player, node);
        boolean hasFullSet = (PrimalAvatarLogic.getEquippedSetType(player) == type);

        // CAS 1 : Il a le set complet ET la permission -> TRANSFORMATION
        if (type.isActive() && hasFullSet && hasPermission && type.getAbility() != null) {

            if (type == PrimalAvatarType.ENDER_DRAGON) {
                if (player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getBaseValue() != 200.0D) {
                    player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(200.0D);
                    player.heal(200.0F);
                }
            }
            type.getAbility().onTick(player, player.world);

        }
        // CAS 2 : Il a le set complet mais PAS DE PERMISSION -> ÉJECTION
        else if (hasFullSet && !hasPermission) {
            ejectArmor(player);
            player.sendMessage(new TextComponentString("§4§lSÉCURITÉ §8» §cCette armure est trop puissante pour vous. (Manque: " + node + ")"));
            resetPlayerStats(player);
        }
        // CAS 3 : Set incomplet ou aucune armure
        else {
            resetPlayerStats(player);
        }
    }

    /**
     * Retire l'armure du corps et la remet dans l'inventaire (ou au sol si plein)
     */
    private static void ejectArmor(EntityPlayerMP player) {
        for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
            if (slot.getSlotType() == EntityEquipmentSlot.Type.ARMOR) {
                ItemStack stack = player.getItemStackFromSlot(slot);
                if (!stack.isEmpty()) {
                    // On essaie de mettre dans l'inventaire, sinon on drop au sol
                    if (!player.inventory.addItemStackToInventory(stack)) {
                        player.dropItem(stack, false);
                    }
                    player.setItemStackToSlot(slot, ItemStack.EMPTY);
                }
            }
        }
        player.world.playSound(null, player.posX, player.posY, player.posZ,
                SoundEvents.ENTITY_ITEM_BREAK, player.getSoundCategory(), 1.0F, 0.5F);
    }

    private static void resetPlayerStats(EntityPlayerMP player) {
        if (player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getBaseValue() != 20.0D) {
            player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);
            if (player.getHealth() > 20.0F) player.setHealth(20.0F);
        }
        if (!player.isCreative() && !player.isSpectator()) {
            if (player.capabilities.allowFlying) {
                player.capabilities.allowFlying = false;
                player.capabilities.isFlying = false;
                player.sendPlayerAbilities();
            }
        }
    }

    private static boolean consumePowerCost(EntityPlayerMP player) {
        if (player.getFoodStats().getFoodLevel() > 6) {
            player.addExhaustion(4.0F);
            return true;
        }
        for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
            if (slot.getSlotType() != EntityEquipmentSlot.Type.ARMOR) continue;
            ItemStack stack = player.getItemStackFromSlot(slot);
            if (!stack.isEmpty() && stack.isItemStackDamageable() && stack.getItemDamage() < stack.getMaxDamage() - 1) {
                stack.damageItem(2, player);
                return true;
            }
        }
        return false;
    }
}