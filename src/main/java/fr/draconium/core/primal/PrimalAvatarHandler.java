package fr.draconium.core.primal;

import java.util.Iterator;
import fr.draconium.core.capabilities.player.ExtendedPlayerData;
import fr.draconium.core.primal.network.PrimalNetwork;
import fr.draconium.core.references.Reference;
import fr.draconium.core.worlds.ModConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod.EventBusSubscriber(modid = Reference.MODID)
public class PrimalAvatarHandler {

    @SubscribeEvent
    public static void onEquipmentChange(LivingEquipmentChangeEvent event) {
        if (!(event.getEntityLiving() instanceof EntityPlayer)) return;
        applyMorphPhysics((EntityPlayer) event.getEntityLiving());
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player instanceof EntityPlayer) {
            applyMorphPhysics(event.player);
        }
    }

    public static void applyMorphPhysics(EntityPlayer player) {
        PrimalAvatarType newType = PrimalAvatarLogic.getEquippedSetType(player);
        ExtendedPlayerData data = ExtendedPlayerData.get(player);
        ExtendedPlayerData.PrimalAvatarAbilityState st = data.primalAvatarAbility;
        PrimalAvatarType oldType = PrimalAvatarType.byOrdinal(st.getLastMorphOrdinal());

        // Logique de suffocation pour les poissons hors de l'eau
        if (!player.world.isRemote && oldType.fishBiology && !newType.isActive()) {
            AxisAlignedBB box = player.getEntityBoundingBox();
            if (!player.world.getCollisionBoxes(player, box).isEmpty()) {
                player.attackEntityFrom(DamageSource.IN_WALL, 50.0F);
            }
        }

        st.setLastMorphOrdinal(newType.ordinal());

        if (newType.isActive()) {
            // Utilisation de la réflexion pour contourner l'accès 'protected'
            reflectionSetSize(player, newType.width, newType.height);

            if (!player.world.isRemote && player instanceof EntityPlayerMP) {
                EntityPlayerMP mp = (EntityPlayerMP) player;
                if (newType.grantsFlight && !mp.isCreative()) {
                    mp.capabilities.allowFlying = true;
                    mp.sendPlayerAbilities();
                }
            }
        } else {
            // Taille par défaut (Humain)
            reflectionSetSize(player, 0.6F, 1.8F);

            if (!player.world.isRemote && player instanceof EntityPlayerMP) {
                EntityPlayerMP mp = (EntityPlayerMP) player;
                if (!mp.isCreative()) {
                    mp.capabilities.allowFlying = false;
                    mp.capabilities.isFlying = false;
                    mp.sendPlayerAbilities();
                }
            }
        }

        if (player instanceof EntityPlayerMP) {
            PrimalNetwork.syncAbilityCooldown((EntityPlayerMP) player);
        }
    }

    // Méthode de réflexion placée HORS de applyMorphPhysics
    private static void reflectionSetSize(EntityPlayer player, float width, float height) {
        try {
            // func_70105_a est le nom SRG de setSize en 1.12.2
            java.lang.reflect.Method m = Entity.class.getDeclaredMethod("func_70105_a", float.class, float.class);
            m.setAccessible(true);
            m.invoke(player, width, height);
        } catch (Exception e) {
            // Si func_70105_a échoue (environnement dev), on tente le nom direct
            try {
                java.lang.reflect.Method m = Entity.class.getDeclaredMethod("setSize", float.class, float.class);
                m.setAccessible(true);
                m.invoke(player, width, height);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTickServer(TickEvent.PlayerTickEvent event) {
        if (event.player.world.isRemote || event.phase != TickEvent.Phase.END) return;
        if (!(event.player instanceof EntityPlayerMP)) return;

        EntityPlayerMP mp = (EntityPlayerMP) event.player;
        PrimalAvatarType type = PrimalAvatarLogic.getEquippedSetType(mp);
        if (type.isActive()) {
            PrimalAvatarAbilities.tickPassiveServer(mp, type);
        }
    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        World world = event.getWorld();
        if (world.isRemote || !(event.getTarget() instanceof EntityPlayer) || !(event.getEntityPlayer() instanceof EntityPlayerMP)) return;

        EntityPlayer mount = (EntityPlayer) event.getTarget();
        EntityPlayerMP rider = (EntityPlayerMP) event.getEntityPlayer();
        ItemStack held = rider.getHeldItem(event.getHand());

        if (held.getItem() != Items.SADDLE) return;

        PrimalAvatarType t = PrimalAvatarLogic.getEquippedSetType(mount);
        if (!t.mountableBySaddle) return;

        if (mount == rider || mount.isBeingRidden() || rider.isRiding()) return;

        rider.startRiding(mount, true);
        event.setCanceled(true);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPlayerDeathKeepInventoryPrimal(LivingDeathEvent event) {
        if (!(event.getEntityLiving() instanceof EntityPlayerMP)) return;
        EntityPlayerMP player = (EntityPlayerMP) event.getEntityLiving();
        World world = player.world;

        if (!world.getGameRules().getBoolean("keepInventory")) return;

        for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
            if (slot.getSlotType() != EntityEquipmentSlot.Type.ARMOR) continue;

            ItemStack stack = player.getItemStackFromSlot(slot);
            if (!PrimalAvatarLogic.isPrimalArmor(stack)) continue;

            if (rollDrop(world)) {
                world.spawnEntity(new EntityItem(world, player.posX, player.posY, player.posZ, stack.copy()));
                player.setItemStackToSlot(slot, ItemStack.EMPTY);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onLivingDrops(LivingDropsEvent event) {
        if (!(event.getEntityLiving() instanceof EntityPlayerMP)) return;
        EntityPlayerMP player = (EntityPlayerMP) event.getEntityLiving();
        World world = player.world;

        if (world.getGameRules().getBoolean("keepInventory")) return;

        Iterator<EntityItem> iterator = event.getDrops().iterator();
        while (iterator.hasNext()) {
            EntityItem drop = iterator.next();
            ItemStack stack = drop.getItem();
            if (PrimalAvatarLogic.isPrimalArmor(stack) && !rollDrop(world)) {
                iterator.remove();
            }
        }
    }

    private static boolean rollDrop(World world) {
        float span = ModConfig.primalArmorDropMaxChance - ModConfig.primalArmorDropMinChance;
        float p = ModConfig.primalArmorDropMinChance + world.rand.nextFloat() * span;
        return world.rand.nextFloat() < p;
    }
}