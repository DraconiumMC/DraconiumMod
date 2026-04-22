package fr.draconium.core.items.armors;

import fr.draconium.core.DraconiumCore;
import fr.draconium.core.materials.ArmorsMaterial;
import fr.draconium.core.primal.PrimalAvatarType;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import javax.annotation.Nullable;

public class PrimalAvatarArmorItem extends ArmorBasic {

    private final PrimalAvatarType avatarType;

    public PrimalAvatarArmorItem(PrimalAvatarType avatarType, String registryName, EntityEquipmentSlot slot) {
        // On passe les paramètres à ArmorBasic (Super)
        super(registryName, ArmorsMaterial.MATERIAL_PRIMAL_AVATAR, 1, slot);
        this.avatarType = avatarType;

        // Nom de traduction interne
        this.setTranslationKey(registryName);

        // IMPORTANT : On définit l'onglet ici MAIS on le filtre plus bas
        this.setCreativeTab(null);
    }

    /**
     * Gère l'affichage dans l'inventaire créatif.
     * Cette méthode est le verrou qui empêche l'item de "baver" dans les autres onglets.
     */
    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
    }

    /**
     * Indique au moteur de recherche de Minecraft si l'item appartient à l'onglet consulté.
     */
    @Override
    public boolean isInCreativeTab(CreativeTabs targetTab) {
        // Autorise l'onglet de recherche global
        if (targetTab == CreativeTabs.SEARCH) return true;

        // Autorise uniquement l'onglet ARMORS de ton mod
        return targetTab == DraconiumCore.DRACONIUM_TAB_ARMORS;
    }

    /**
     * Retourne l'onglet officiel pour cet item.
     */
    @Nullable
    @Override
    public CreativeTabs getCreativeTab() {
        return DraconiumCore.DRACONIUM_TAB_ARMORS;
    }

    /**
     * Gère le rendu 3D de l'armure sur le corps du joueur.
     */
    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        // Layer 2 pour les jambes (leggings), Layer 1 pour le reste
        String layer = (slot == EntityEquipmentSlot.LEGS) ? "2" : "1";

        // On récupère le nom propre du mob depuis le nom d'enregistrement
        // Exemple : "creeper_helmet" devient "creeper"
        String path = this.getRegistryName().getPath();
        String mobName = path.substring(0, path.lastIndexOf('_'));

        // NOTE : Si tes fichiers s'appellent "irongolem" sans underscore,
        // décommente la ligne suivante :
        // mobName = mobName.replace("_", "");

        return "draconiumcore:textures/models/armor/" + mobName + "_layer_" + layer + ".png";
    }

    public PrimalAvatarType getAvatarType() {
        return avatarType;
    }
}