package fr.draconium.core.primal.abilities;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EnderDragonAbility implements IPrimalAbility {

    @Override
    public boolean execute(EntityPlayerMP player, World world) {
        Vec3d look = player.getLook(1.0F);
        double x = player.posX + look.x * 2.0D;
        double y = player.posY + player.getEyeHeight() + look.y * 0.5D;
        double z = player.posZ + look.z * 2.0D;

        EntityLargeFireball fireball = new EntityLargeFireball(world, player, look.x, look.y, look.z);
        fireball.explosionPower = 10;
        fireball.setPosition(x, y, z);
        fireball.accelerationX = look.x * 0.1D;
        fireball.accelerationY = look.y * 0.1D;
        fireball.accelerationZ = look.z * 0.1D;

        if (!world.isRemote) {
            fireball.addTag("admin_dragon_nuke");
            world.spawnEntity(fireball);
        }

        world.playSound(null, player.posX, player.posY, player.posZ,
                SoundEvents.ENTITY_ENDERDRAGON_GROWL, SoundCategory.PLAYERS, 2.0F, 0.5F);

        return true;
    }

    // --- 💥 IMPACT AU SOL : SPAWN DU SOUFFLE (Dragon Breath) ---
    @SubscribeEvent
    public void onProjectileImpact(ProjectileImpactEvent event) {
        if (event.getEntity() instanceof EntityLargeFireball) {
            EntityLargeFireball fireball = (EntityLargeFireball) event.getEntity();

            if (fireball.getTags().contains("admin_dragon_nuke") && !fireball.world.isRemote) {
                World world = fireball.world;

                // Nuage de souffle
                EntityAreaEffectCloud cloud = new EntityAreaEffectCloud(world, fireball.posX, fireball.posY, fireball.posZ);
                if (fireball.shootingEntity instanceof EntityLivingBase) {
                    cloud.setOwner((EntityLivingBase) fireball.shootingEntity);
                }

                cloud.setParticle(EnumParticleTypes.DRAGON_BREATH);
                cloud.setRadius(5.0F);
                cloud.setDuration(200);
                cloud.setWaitTime(0);
                cloud.addEffect(new PotionEffect(MobEffects.WITHER, 100, 1));
                cloud.addEffect(new PotionEffect(MobEffects.INSTANT_DAMAGE, 1, 0));

                world.spawnEntity(cloud);

                // Son d'explosion corrigé (FIREBALL_EXPLODE)
                world.playSound(null, fireball.posX, fireball.posY, fireball.posZ,
                        SoundEvents.ENTITY_ENDERDRAGON_FIREBALL_EPLD, SoundCategory.BLOCKS, 4.0F, 1.0F);
            }
        }
    }

    // --- 🎯 IMPACT SUR JOUEUR : DEGATS ---
    @SubscribeEvent
    public void onAdminProjectileHit(LivingHurtEvent event) {
        if (event.getSource().getImmediateSource() instanceof EntityLargeFireball) {
            EntityLargeFireball fireball = (EntityLargeFireball) event.getSource().getImmediateSource();
            if (fireball.getTags().contains("admin_dragon_nuke")) {
                event.setAmount(50.0F);
                event.getEntityLiving().addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 300, 0));
            }
        }
    }

    // --- 🐉 ANIMATION DES AILES (CÔTÉ CLIENT UNIQUEMENT) ---
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onPlayerRender(RenderPlayerEvent.Pre event) {
        EntityPlayer player = event.getEntityPlayer();

        // On vérifie si le joueur vole (et on peut ajouter ici une check d'armure)
        if (player.capabilities.isFlying) {
            ModelPlayer model = event.getRenderer().getMainModel();

            float speed = 0.15F;
            float degree = 0.5F;
            float flap = MathHelper.cos((player.ticksExisted + event.getPartialRenderTick()) * speed) * degree;

            // Mouvement des bras (porteurs des ailes)
            model.bipedRightArm.rotateAngleZ = 0.2F + flap;
            model.bipedLeftArm.rotateAngleZ = -0.2F - flap;

            model.bipedRightArm.rotateAngleY = -0.3F;
            model.bipedLeftArm.rotateAngleY = 0.3F;

            model.bipedBody.rotateAngleX = 0.25F;
        }
    }

    @Override
    public void onTick(EntityPlayerMP player, World world) {
        if (!player.capabilities.allowFlying || !player.capabilities.isFlying) {
            player.capabilities.allowFlying = true;
            player.capabilities.isFlying = true;
            player.sendPlayerAbilities();
        }

        if (player.capabilities.getFlySpeed() != 0.08F) {
            player.capabilities.setFlySpeed(0.08F);
            player.sendPlayerAbilities();
        }

        if (player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getBaseValue() != 200.0D) {
            player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(200.0D);
            player.heal(200.0F);
        }

        if (player.capabilities.isFlying && !player.isCreative()) {
            destroyBlocksMassive(player, world);
        }

        if (player.ticksExisted % 20 == 0) {
            world.playSound(null, player.posX, player.posY, player.posZ,
                    SoundEvents.ENTITY_ENDERDRAGON_FLAP, SoundCategory.PLAYERS, 1.5F, 1.0F);
            player.heal(2.0F);
        }
    }

    private void destroyBlocksMassive(EntityPlayerMP player, World world) {
        AxisAlignedBB box = player.getEntityBoundingBox().grow(2.0D, 1.5D, 2.0D);
        for (BlockPos pos : BlockPos.getAllInBox(new BlockPos(box.minX, box.minY, box.minZ), new BlockPos(box.maxX, box.maxY, box.maxZ))) {
            IBlockState state = world.getBlockState(pos);
            if (!world.isAirBlock(pos) && state.getBlockHardness(world, pos) >= 0 && !state.getMaterial().isLiquid()) {
                world.destroyBlock(pos, false);
                if (world.rand.nextInt(10) == 0 && world instanceof WorldServer) {
                    ((WorldServer)world).spawnParticle(EnumParticleTypes.DRAGON_BREATH, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 1, 0.0D, 0.0D, 0.0D, 0.0D);
                }
            }
        }
    }

    public void onRemoved(EntityPlayerMP player) {
        player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);
        if (!player.isCreative()) {
            player.capabilities.allowFlying = false;
            player.capabilities.isFlying = false;
            player.sendPlayerAbilities();
        }
    }
}