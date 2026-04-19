package fr.draconium.core.items.armors;

import fr.draconium.core.materials.ArmorsMaterial;
import fr.draconium.core.primal.PrimalAvatarType;
import fr.draconium.core.references.Reference;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

public class PrimalAvatarArmorItem extends ArmorBasic {

    private final PrimalAvatarType avatarType;

    public PrimalAvatarArmorItem(PrimalAvatarType avatarType, String registryName, EntityEquipmentSlot slot) {
        // On passe le registryName au super (ArmorBasic)
        super(registryName, ArmorsMaterial.MATERIAL_PRIMAL_AVATAR, 1, slot);
        this.avatarType = avatarType;

        // On définit explicitement les noms ici pour éviter les erreurs ailleurs
        this.setTranslationKey(registryName);
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        // Détermine le layer (1 pour casque/plastron/bottes, 2 pour jambières)
        String layer = (slot == EntityEquipmentSlot.LEGS) ? "2" : "1";

        // Récupère le nom du mob depuis le nom de l'item (ex: "creeper_helmet" -> "creeper")
        String registryPath = this.getRegistryName().getPath();
        String mobName = registryPath.substring(0, registryPath.lastIndexOf('_'));

        // On enlève les underscores pour coller à tes noms de fichiers (ex: iron_golem -> irongolem)
        // SI tes fichiers PNG s'appellent "iron_golem_layer_1", enlève le .replace("_", "")
        mobName = mobName.replace("_", "");

        return "draconiumcore:textures/models/armor/" + mobName + "_layer_" + layer + ".png";
    }

    public PrimalAvatarType getAvatarType() {
        return avatarType;
    }
}