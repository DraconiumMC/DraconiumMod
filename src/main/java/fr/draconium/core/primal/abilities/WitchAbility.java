package fr.draconium.core.primal.abilities;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.PotionTypes;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class WitchAbility implements IPrimalAbility {

    @Override
    public boolean execute(EntityPlayerMP player, World world) {
        // --- CAPACITÉ ACTIVE (G) : Jet de Potion de Dégâts ---
        Vec3d look = player.getLook(1.0F);

        // Création d'une potion jetable de Dégâts Instantanés (Harming)
        ItemStack potionStack = new ItemStack(Items.SPLASH_POTION);
        PotionUtils.addPotionToItemStack(potionStack, PotionTypes.HARMING);

        EntityPotion entityPotion = new EntityPotion(world, player, potionStack);

        // On règle la trajectoire (vitesse 0.5, imprécision 1.0 comme la vraie sorcière)
        entityPotion.shoot(player, player.rotationPitch, player.rotationYaw, -20.0F, 0.5F, 1.0F);

        world.spawnEntity(entityPotion);
        world.playSound(null, player.posX, player.posY, player.posZ,
                SoundEvents.ENTITY_WITCH_THROW, player.getSoundCategory(), 1.0F, 1.0F);

        return true;
    }

    @Override
    public void onTick(EntityPlayerMP player, World world) {
        // --- ATOUT 1 : Auto-soin (Boire une potion) ---
        // Si la sorcière a peu de vie, elle gagne Régénération (simule le fait de boire)
        if (player.getHealth() < player.getMaxHealth() && player.ticksExisted % 100 == 0) {
            player.addPotionEffect(new net.minecraft.potion.PotionEffect(MobEffects.REGENERATION, 100, 0));
            world.playSound(null, player.posX, player.posY, player.posZ,
                    SoundEvents.ENTITY_WITCH_DRINK, player.getSoundCategory(), 1.0F, 1.0F);
        }

        // --- ATOUT 2 : Résistance au feu (Potion de protection) ---
        // Si elle brûle, elle active automatiquement la résistance au feu
        if (player.isBurning()) {
            player.addPotionEffect(new net.minecraft.potion.PotionEffect(MobEffects.FIRE_RESISTANCE, 200, 0));
        }
        if (player.ticksExisted % 100 == 0) { // On rafraîchit l'effet régulièrement
            player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 300, 0, false, false));
        }
        // --- ATOUT 3 : Résistance magique ---
        // La sorcière subit moins les effets négatifs (on réduit la durée si active)
        if (player.isPotionActive(MobEffects.POISON) || player.isPotionActive(MobEffects.WITHER)) {
            // On réduit la force des effets ou on les enlève plus vite
            if (player.ticksExisted % 20 == 0) {
                player.removePotionEffect(MobEffects.POISON);
            }
        }

        // --- FAIBLESSE : Pas d'attaque de mêlée ---
        // La sorcière est faite pour le combat à distance
    }
}