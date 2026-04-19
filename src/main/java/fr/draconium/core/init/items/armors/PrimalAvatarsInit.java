package fr.draconium.core.init.items.armors;

import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.PrintWriter;

import fr.draconium.core.items.armors.PrimalAvatarArmorItem;
import fr.draconium.core.primal.PrimalAvatarType;
import fr.draconium.core.references.Reference;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = Reference.MODID)
public class PrimalAvatarsInit {

    public static final List<Item> ITEMS = new ArrayList<>();

    public static void init() {
        // Enregistre ici tous tes mobs (Nom du dossier texture)
        registerSet(PrimalAvatarType.CREEPER, "creeper");
        registerSet(PrimalAvatarType.GHAST, "ghast");
        registerSet(PrimalAvatarType.ZOMBIE, "zombie");
        registerSet(PrimalAvatarType.ENDERMAN, "enderman");
        registerSet(PrimalAvatarType.CAVE_SPIDER, "cave_spider");
        registerSet(PrimalAvatarType.PIG_ZOMBIE, "pig_zombie");
        registerSet(PrimalAvatarType.BLAZE, "blaze");
        registerSet(PrimalAvatarType.SKELETON, "skeleton");
        registerSet(PrimalAvatarType.WITHER_SKELETON, "wither_skeleton");
        registerSet(PrimalAvatarType.SPIDER, "spider");
        registerSet(PrimalAvatarType.WITCH, "witch");
        registerSet(PrimalAvatarType.COW, "cow");
        registerSet(PrimalAvatarType.PIG, "pig");
        registerSet(PrimalAvatarType.SHEEP, "sheep");
        registerSet(PrimalAvatarType.CHICKEN, "chicken");
        registerSet(PrimalAvatarType.SQUID, "squid");
        registerSet(PrimalAvatarType.IRON_GOLEM, "iron_golem");
        registerSet(PrimalAvatarType.SNOW_GOLEM, "snow_golem");
        registerSet(PrimalAvatarType.SALMON, "salmon");
        registerSet(PrimalAvatarType.COD, "cod");
        registerSet(PrimalAvatarType.PUFFERFISH, "pufferfish");
    }

    private static void registerSet(PrimalAvatarType type, String nameEN) {
        // L'ordre ici est CRUCIAL pour l'affichage dans l'onglet créatif
        EntityEquipmentSlot[] slots = {
                EntityEquipmentSlot.HEAD,  // 1er : Casque
                EntityEquipmentSlot.CHEST, // 2ème : Plastron
                EntityEquipmentSlot.LEGS,  // 3ème : Jambières
                EntityEquipmentSlot.FEET   // 4ème : Bottes
        };

        String[] suffixes = {"_helmet", "_chestplate", "_leggings", "_boots"};

        for (int i = 0; i < 4; i++) {
            String registryName = nameEN + suffixes[i];

            // On crée l'item
            PrimalAvatarArmorItem item = new PrimalAvatarArmorItem(type, registryName, slots[i]);

            // On l'ajoute à la liste (L'ordre dans cette liste = l'ordre dans l'inventaire)
            ITEMS.add(item);

            // On génère le JSON avec ton chemin corrigé
            generateJson(nameEN, registryName);
        }
    }

    private static void generateJson(String mobFolder, String registryName) {
        try {
            // On remonte d'un dossier pour bien écrire dans le vrai src/main/resources (évite le dossier run/)
            java.io.File file = new java.io.File("../src/main/resources/assets/draconiumcore/models/item/" + registryName + ".json");

            if (!file.exists()) {
                file.getParentFile().mkdirs();
                java.io.PrintWriter writer = new java.io.PrintWriter(file);
                writer.println("{");
                writer.println("    \"parent\": \"item/generated\",");
                writer.println("    \"textures\": {");
                // Chemin mis à jour selon ta découverte : draconiumcore:items/armors/mobs/nom/item
                writer.println("        \"layer0\": \"draconiumcore:items/armors/" + mobFolder + "/" + registryName + "\"");
                writer.println("    }");
                writer.println("}");
                writer.close();
                System.out.println("[DRACONIUM] JSON généré avec succès : " + registryName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    public static void onRegisterItems(RegistryEvent.Register<Item> event) {
        for (Item item : ITEMS) {
            event.getRegistry().register(item);
        }
    }

    @SubscribeEvent
    public static void onModelRegister(ModelRegistryEvent event) {
        for (Item item : ITEMS) {
            ModelLoader.setCustomModelResourceLocation(item, 0,
                    new ModelResourceLocation(item.getRegistryName(), "inventory"));
        }
    }
}