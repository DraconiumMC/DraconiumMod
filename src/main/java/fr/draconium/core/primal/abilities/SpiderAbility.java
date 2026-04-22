package fr.draconium.core.primal.abilities;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import java.util.List;

public class SpiderAbility implements IPrimalAbility {

    @Override
    public boolean execute(EntityPlayerMP player, World world) {
        // --- CAPACITÉ ACTIVE (G) : Bond de l'araignée ---
        if (player.onGround) {
            double speed = 1.2D;
            player.motionX = player.getLookVec().x * speed;
            player.motionY = 0.5D; // Un petit saut vers le haut
            player.motionZ = player.getLookVec().z * speed;
            player.velocityChanged = true;
            return true;
        }
        return false;
    }

    @Override
    public void onTick(EntityPlayerMP player, World world) {
        // --- ATOUT 1 : Grimper aux murs ---
        // Si le joueur touche un mur (collidedHorizontally), on le fait monter
        if (player.collidedHorizontally) {
            player.motionY = 0.22D; // Vitesse de grimpe vanilla
            player.fallDistance = 0.0F; // Pas de dégâts de chute en grimpant
        }

        // --- ATOUT 2 : Vision nocturne ---
        // Les yeux de l'araignée voient dans le noir
        if (player.ticksExisted % 100 == 0) { // On rafraîchit l'effet régulièrement
            player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 300, 0, false, false));
        }

        // --- ATOUT 3 : Toile d'araignée (Passif) ---
        // Immunité au ralentissement des toiles d'araignées (Cobwebs)

        // --- FAIBLESSE : Taille ---
        // L'araignée fait 2 blocs de large, elle ne passe pas partout !
        // (Déjà géré par la hitbox dans ton Enum)
    }
}