package fr.draconium.core.handlers;

import fr.draconium.core.init.sounds.BackgroundInit;
import fr.draconium.core.primal.PrimalAvatarLogic;
import fr.draconium.core.primal.PrimalAvatarType;
import fr.draconium.core.references.Reference;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

@Mod.EventBusSubscriber(modid = Reference.MODID)
public class PlayerJoinHandler
{
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event)
    {
        EntityPlayer player = event.player;
        World world = player.world;
        if (!world.isRemote && BackgroundInit.BACKGROUND_MUSIC != null)
        {
            world.playSound(null, player.getPosition(), BackgroundInit.BACKGROUND_MUSIC, SoundCategory.MUSIC, 1.0F, 1.0F);
        }
    }

    @SubscribeEvent
    public void onWitherImpact(LivingHurtEvent event) {
        // 1. On vérifie si la source des dégâts est un projectile
        if (event.getSource().getImmediateSource() instanceof EntityWitherSkull) {
            EntityWitherSkull skull = (EntityWitherSkull) event.getSource().getImmediateSource();

            // 2. On vérifie si c'est NOTRE crâne boosté (grâce au tag)
            if (skull.getTags().contains("primal_wither_skull")) {

                // 3. On applique l'effet Wither II (Amplifier 1 = niveau 2)
                event.getEntityLiving().addPotionEffect(new PotionEffect(MobEffects.WITHER, 300, 1));

                // 4. BONUS : Augmenter les dégâts d'impact direct (le choc du crâne)
                // On ajoute par exemple 10 points de dégâts supplémentaires (5 cœurs)
                float nouveauxDegats = event.getAmount() + 10.0F;
                event.setAmount(nouveauxDegats);
            }
        }
    }

    /**
     * Gère l'interaction de la selle entre deux joueurs (Partie 2.2 du Game Design)
     */
    @SubscribeEvent
    public static void onPlayerInteract(PlayerInteractEvent.EntityInteract event)
    {
        // On vérifie qu'on est côté serveur et que la cible est un joueur
        if (event.getWorld().isRemote || !(event.getTarget() instanceof EntityPlayerMP)) return;

        EntityPlayerMP mount = (EntityPlayerMP) event.getTarget();
        EntityPlayerMP rider = (EntityPlayerMP) event.getEntityPlayer();

        // Récupération du type d'avatar du joueur ciblé
        PrimalAvatarType type = PrimalAvatarLogic.getEquippedSetType(mount);

        // Vérification : Seuls le Cochon et le Ghast peuvent être montés
        if (type == PrimalAvatarType.PIG || type == PrimalAvatarType.GHAST) {

            // Le cavalier doit tenir une selle
            if (rider.getHeldItemMainhand().getItem() == Items.SADDLE) {

                // Si personne ne monte déjà le joueur
                if (!mount.isBeingRidden()) {

                    // On fait monter le cavalier
                    rider.startRiding(mount, true);

                    // Effets sonores et messages
                    mount.world.playSound(null, mount.posX, mount.posY, mount.posZ,
                            SoundEvents.ENTITY_HORSE_SADDLE, SoundCategory.PLAYERS, 1.0F, 1.0F);

                    mount.sendMessage(new TextComponentString("§6Vous servez de monture à §e" + rider.getName()));
                    rider.sendMessage(new TextComponentString("§aVous montez sur §e" + mount.getName()));

                } else {
                    rider.sendMessage(new TextComponentString("§cCe joueur est déjà occupé !"));
                }
            }
        }
    }
}