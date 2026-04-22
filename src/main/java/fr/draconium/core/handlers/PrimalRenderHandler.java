package fr.draconium.core.handlers;

import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PrimalRenderHandler {

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onPlayerRender(RenderPlayerEvent.Pre event) {
        EntityPlayer player = event.getEntityPlayer();

        if (isWearingDragonArmor(player) && player.capabilities.isFlying) {
            ModelPlayer model = event.getRenderer().getMainModel();

            // ageInTicks est géré par Minecraft pour les animations
            float speed = 0.5F; // Augmente pour battre plus vite
            float amplitude = 1.2F; // Augmente pour de plus grands mouvements

            // On utilise le sinus sur l'axe Z (monter/descendre sur les côtés)
            float flap = MathHelper.sin(player.ticksExisted * speed) * amplitude;

            // Force la rotation des bras (les ailes d'armures suivent les bras en ModelBiped)
            model.bipedRightArm.rotateAngleZ = 1.0F + flap;
            model.bipedLeftArm.rotateAngleZ = -1.0F - flap;

            // Optionnel : Incliner le corps vers l'avant pour faire "plongeon"
            model.bipedBody.rotateAngleX = 0.5F;
        }
    }

    private boolean isWearingDragonArmor(EntityPlayer player) {
        // On récupère le plastron (slot index 2 de l'armure)
        ItemStack chestStack = player.inventory.armorInventory.get(2);

        // Vérification : l'item existe-t-il et est-ce un item d'armure ?
        if (chestStack != null && !chestStack.isEmpty()) {

            // On récupère l'item technique
            net.minecraft.item.Item item = chestStack.getItem();

            if (item != null && item.getRegistryName() != null) {
                // Utiliser le RegistryName est BIEN PLUS FIABLE que le UnlocalizedName
                // Le RegistryName ressemble à "modid:armor_dragon_chest"
                String registryName = item.getRegistryName().toString().toLowerCase();

                return registryName.contains("dragon");
            }
        }
        return false;
    }
}