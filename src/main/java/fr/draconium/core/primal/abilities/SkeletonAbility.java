package fr.draconium.core.primal.abilities;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class SkeletonAbility implements IPrimalAbility {

    @Override
    public boolean execute(EntityPlayerMP player, World world) {
        // --- CAPACITÉ ACTIVE (G) : Tir de flèche rapide ---
        EntityArrow arrow = new EntityTippedArrow(world, player);
        arrow.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, 3.0F, 1.0F);

        world.spawnEntity(arrow);
        world.playSound(null, player.posX, player.posY, player.posZ,
                SoundEvents.ENTITY_SKELETON_SHOOT, player.getSoundCategory(), 1.0F, 1.0F);

        return true;
    }

    @Override
    public void onTick(EntityPlayerMP player, World world) {
        if (player.ticksExisted % 100 == 0) { // On rafraîchit l'effet régulièrement
            player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 300, 0, false, false));
        }
        // --- FAIBLESSE : Brûle au soleil ---
        if (world.isDaytime() && world.canSeeSky(player.getPosition()) && !world.isRainingAt(player.getPosition())) {
            // Si pas de casque, on brûle
            if (player.getItemStackFromSlot(net.minecraft.inventory.EntityEquipmentSlot.HEAD).isEmpty()) {
                if (player.ticksExisted % 20 == 0) player.setFire(8);
            }
        }

        // --- ATOUT : Maître de l'arc ---
        // On pourrait ajouter un effet ici si on veut que l'arc se charge plus vite
    }
}