package fr.draconium.core.primal.abilities;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class WitherAbility implements IPrimalAbility {

    @Override
    public boolean execute(EntityPlayerMP player, World world) {
        Vec3d look = player.getLook(1.0F); // Direction exacte du regard

        // Spawn légèrement devant le joueur pour éviter les collisions avec sa propre hitbox
        double x = player.posX + look.x * 1.5D;
        double y = player.posY + player.getEyeHeight() + look.y * 0.5D;
        double z = player.posZ + look.z * 1.5D;

        // On crée le crâne avec une accélération initiale basée sur le curseur
        EntityWitherSkull skull = new EntityWitherSkull(world, player, look.x, look.y, look.z);
        skull.setPosition(x, y, z);

        // --- LE FIX POUR LA PRÉCISION ---
        // On force les vecteurs d'accélération pour éviter le "zig-zag"
        skull.accelerationX = look.x * 0.1D;
        skull.accelerationY = look.y * 0.1D;
        skull.accelerationZ = look.z * 0.1D;

        if (world.rand.nextFloat() < 0.3F) { // 30% de chance
            skull.setInvulnerable(true);
        }
        // Dans ta méthode execute de WitherAbility.java
        if (!world.isRemote) {
            // On ajoute un tag personnalisé à l'entité
            skull.addTag("primal_wither_skull");
            world.spawnEntity(skull);
        }if (!world.isRemote) {
            world.spawnEntity(skull);
        }

        world.playSound(null, player.posX, player.posY, player.posZ,
                SoundEvents.ENTITY_WITHER_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F);

        return true;
    }



    @Override
    public void onTick(EntityPlayerMP player, World world) {
        // --- VOL & CAPACITÉS DE BASE ---
        if (!player.capabilities.allowFlying || !player.capabilities.isFlying) {
            player.capabilities.allowFlying = true;
            player.capabilities.isFlying = true;
            player.sendPlayerAbilities();
        }

        // --- GESTION THERMIQUE (Eau vs Lave) ---

        // 1. FAIBLESSE : Dégâts dans l'eau
        if (player.isInWater() || (player.isWet() && !player.isInLava())) {
            if (player.ticksExisted % 20 == 0) {
                // Dégâts de type "Noyade" (ou n'importe quel type qui ignore l'armure)
                player.attackEntityFrom(net.minecraft.util.DamageSource.DROWN, 2.0F);

                // Effet sonore de blessure du Wither
                world.playSound(null, player.posX, player.posY, player.posZ,
                        SoundEvents.ENTITY_WITHER_HURT, net.minecraft.util.SoundCategory.PLAYERS, 1.0F, 1.0F);

                // Particules de fumée pour simuler l'évaporation/réaction négative
                player.getServerWorld().spawnParticle(net.minecraft.util.EnumParticleTypes.SMOKE_NORMAL,
                        player.posX, player.posY + 1.5, player.posZ, 5, 0.2, 0.2, 0.2, 0.0);
            }
        }

        if (player.ticksExisted % 100 == 0) { // On rafraîchit l'effet régulièrement
            player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 300, 0, false, false));
        }

        // 2. ATOUT : Puissance dans la Lave
        if (player.isInLava()) {
            // Régénération rapide (2 points de vie toutes les secondes)
            if (player.ticksExisted % 10 == 0 && player.getHealth() < player.getMaxHealth()) {
                player.heal(1.0F);
            }

            // Boost de Puissance : Force II tant qu'il est dans la lave
            player.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 40, 1, false, false));

            // On éteint le feu visuel pour qu'il ne cache pas la vue du joueur
            player.extinguish();
        } else {
            // Régénération normale hors lave (Atout 2 original)
            if (player.ticksExisted % 40 == 0 && player.getHealth() < player.getMaxHealth()) {
                player.heal(1.0F);
            }
            if (player.isBurning()) player.extinguish();
        }

        // --- ARMURE & DESTRUCTION (Le reste du code ne change pas) ---
        handleWitherArmor(player, world);

        // --- DESTRUCTION STYLE BOSS (MALUS : PAS DE DROP) ---
        if (!player.isCreative() && player.ticksExisted % 5 == 0) {

            // 1. Lister les pièces portées
            List<EntityEquipmentSlot> slots = new ArrayList<>();
            for (EntityEquipmentSlot slot : new EntityEquipmentSlot[]{EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET}) {
                if (!player.getItemStackFromSlot(slot).isEmpty()) slots.add(slot);
            }

            // On ne détruit que si on a de l'armure
            if (!slots.isEmpty()) {
                if (destroyBlocksAround(player, world)) {
                    // Consommation d'une pièce au hasard (Roulette Russe)
                    EntityEquipmentSlot randomSlot = slots.get(world.rand.nextInt(slots.size()));
                    player.getItemStackFromSlot(randomSlot).damageItem(4, player);
                }
            }
        }

        if (player.ticksExisted % 5 == 0) {
            destroyBlocksAround(player, world);
        }

    }



    private void handleWitherArmor(EntityPlayerMP player, World world) {
        if (player.getHealth() / player.getMaxHealth() <= 0.5F) {
            if (player.ticksExisted % 5 == 0) {
                player.getServerWorld().spawnParticle(EnumParticleTypes.SMOKE_LARGE, player.posX, player.posY + 1, player.posZ, 3, 0.5, 1.0, 0.5, 0.02);
            }
            if (player.ticksExisted % 20 == 0) {
                player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 40, 1, false, false));
            }

            // Bouclier anti-projectiles
            AxisAlignedBB shieldBox = player.getEntityBoundingBox().grow(2.5D);
            List<Entity> projectiles = world.getEntitiesWithinAABB(Entity.class, shieldBox, e -> e instanceof IProjectile);

            for (Entity proj : projectiles) {
                proj.setDead();
                world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_WITHER_BREAK_BLOCK, SoundCategory.PLAYERS, 0.5F, 2.0F);
            }
        }
    }

    private boolean destroyBlocksAround(EntityPlayerMP player, World world) {
        // Zone autour du joueur
        AxisAlignedBB box = player.getEntityBoundingBox().grow(1.0D, 0.5D, 1.0D);
        boolean hasBrokenAnything = false; // On crée un marqueur

        int minX = (int) Math.floor(box.minX);
        int minY = (int) Math.floor(box.minY);
        int minZ = (int) Math.floor(box.minZ);
        int maxX = (int) Math.floor(box.maxX);
        int maxY = (int) Math.floor(box.maxY);
        int maxZ = (int) Math.floor(box.maxZ);

        for (int x = minX; x <= maxX; ++x) {
            for (int y = minY; y <= maxY; ++y) {
                for (int z = minZ; z <= maxZ; ++z) {
                    BlockPos pos = new BlockPos(x, y, z);
                    IBlockState state = world.getBlockState(pos);

                    if (!world.isAirBlock(pos)
                            && state.getBlockHardness(world, pos) != -1.0F
                            && !state.getMaterial().isLiquid()) {

                        // MALUS : false = pas de loot
                        world.destroyBlock(pos, false);
                        world.playSound(null, pos, SoundEvents.ENTITY_WITHER_BREAK_BLOCK, SoundCategory.BLOCKS, 0.5F, 1.0F);

                        hasBrokenAnything = true; // On a cassé au moins un bloc !
                    }
                }
            }
        }

        return hasBrokenAnything; // On renvoie le résultat au "if"
    }
}