package fr.draconium.core.primal.abilities;

import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ZombieAbility implements IPrimalAbility {

    @Override
    public boolean execute(EntityPlayerMP player, World world) {
        // --- CAPACITÉ ACTIVE (G) : Invocation de renforts ---
        // On invoque un petit zombie allié (Bébé Zombie)
        EntityZombie minion = new EntityZombie(world);
        minion.setChild(true); // Atout : Les bébés zombies sont rapides et puissants

        // Positionnement aléatoire autour du joueur
        double spawnX = player.posX + (world.rand.nextDouble() - 0.5D) * 3.0D;
        double spawnZ = player.posZ + (world.rand.nextDouble() - 0.5D) * 3.0D;
        minion.setPosition(spawnX, player.posY, spawnZ);

        // Initialisation (pour qu'il ait parfois une armure ou un outil comme dans tes notes)
        minion.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(minion)), null);

        world.spawnEntity(minion);
        world.playSound(null, player.posX, player.posY, player.posZ,
                SoundEvents.ENTITY_ZOMBIE_AMBIENT, player.getSoundCategory(), 1.0F, 1.0F);

        return true;
    }

    @Override
    public void onTick(EntityPlayerMP player, World world) {
        // --- FAIBLESSE 1 : Brûle au soleil ---
        if (world.isDaytime() && world.canSeeSky(player.getPosition()) && !world.isRainingAt(player.getPosition())) {
            // Si le joueur n'a pas de casque (ou si le casque est cassé)
            if (player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).isEmpty()) {
                if (player.ticksExisted % 20 == 0) {
                    player.setFire(8);
                }
            }
        }

        // --- ATOUT : Attaque enflammée ---
        // Si le zombie brûle (à cause du soleil), ses attaques mettent le feu aux ennemis
        // (Note : C'est géré automatiquement par Minecraft si le joueur frappe en brûlant,
        // mais on peut renforcer l'effet ici si besoin).

        // --- FAIBLESSE 2 : Lenteur par défaut ---
        // Les zombies sont lents
        if (player.ticksExisted % 20 == 0) {
            player.addPotionEffect(new net.minecraft.potion.PotionEffect(net.minecraft.init.MobEffects.SLOWNESS, 40, 0, false, false));
        }

        if (player.ticksExisted % 100 == 0) { // On rafraîchit l'effet régulièrement
            player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 300, 0, false, false));
        }

        // --- FAIBLESSE 3 : Coule au fond de l'eau ---
        if (player.isInWater()) {
            player.motionY -= 0.03D; // Gravité augmentée dans l'eau
        }
    }
}