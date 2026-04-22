package fr.draconium.core.init.items.armors;

import java.util.ArrayList;
import java.util.List;

import fr.draconium.core.items.armors.*;
import fr.draconium.core.materials.ArmorsMaterial;
import fr.draconium.core.messages.Console;
import fr.draconium.core.references.Reference;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

@Mod.EventBusSubscriber(modid = Reference.MODID)
public class ArmorsInit
{

    private static List<Item> armors = new ArrayList<>();

    public static Item PYRONITE_HELMET;
    public static Item PYRONITE_CHESTPLATE;
    public static Item PYRONITE_LEGGINGS;
    public static Item PYRONITE_BOOTS;

    public static Item DRACONIUM_HELMET;
    public static Item DRACONIUM_CHESTPLATE;
    public static Item DRACONIUM_LEGGINGS;
    public static Item DRACONIUM_BOOTS;

    public static Item DRACONIQUE_HELMET;
    public static Item DRACONIQUE_CHESTPLATE;
    public static Item DRACONIQUE_LEGGINGS;
    public static Item DRACONIQUE_BOOTS;

    public static Item AQUATIQUE_HELMET;

    public static void init()
    {
        armors.add(PYRONITE_HELMET 			= new ArmorBasic("pyronite_helmet", ArmorsMaterial.MATERIAL_PYRONITE, 1, EntityEquipmentSlot.HEAD));
        armors.add(PYRONITE_CHESTPLATE 		= new ArmorBasic("pyronite_chestplate", ArmorsMaterial.MATERIAL_PYRONITE, 1, EntityEquipmentSlot.CHEST));
        armors.add(PYRONITE_LEGGINGS 		= new ArmorBasic("pyronite_leggings", ArmorsMaterial.MATERIAL_PYRONITE, 2, EntityEquipmentSlot.LEGS));
        armors.add(PYRONITE_BOOTS 			= new ArmorBasic("pyronite_boots", ArmorsMaterial.MATERIAL_PYRONITE, 1, EntityEquipmentSlot.FEET));

        armors.add(DRACONIUM_HELMET 		= new DraconiumArmor("draconium_helmet", ArmorsMaterial.MATERIAL_DRACONIUM, 1, EntityEquipmentSlot.HEAD));
        armors.add(DRACONIUM_CHESTPLATE 	= new DraconiumArmor("draconium_chestplate",ArmorsMaterial.MATERIAL_DRACONIUM, 1, EntityEquipmentSlot.CHEST));
        armors.add(DRACONIUM_LEGGINGS 		= new DraconiumArmor("draconium_leggings", ArmorsMaterial.MATERIAL_DRACONIUM, 2, EntityEquipmentSlot.LEGS));
        armors.add(DRACONIUM_BOOTS 			= new DraconiumArmor("draconium_boots", ArmorsMaterial.MATERIAL_DRACONIUM, 1, EntityEquipmentSlot.FEET));

        armors.add(DRACONIQUE_HELMET 		= new DraconiqueArmor("draconique_helmet", ArmorsMaterial.MATERIAL_DRACONIQUE, 1, EntityEquipmentSlot.HEAD));
        armors.add(DRACONIQUE_CHESTPLATE 	= new DraconiqueArmor("draconique_chestplate", ArmorsMaterial.MATERIAL_DRACONIQUE, 1, EntityEquipmentSlot.CHEST));
        armors.add(DRACONIQUE_LEGGINGS 		= new DraconiqueArmor("draconique_leggings", ArmorsMaterial.MATERIAL_DRACONIQUE, 2, EntityEquipmentSlot.LEGS));
        armors.add(DRACONIQUE_BOOTS 		= new DraconiqueArmor("draconique_boots", ArmorsMaterial.MATERIAL_DRACONIQUE, 1, EntityEquipmentSlot.FEET));

        armors.add(AQUATIQUE_HELMET 		= new AquatiqueArmor("aquatique_helmet", ArmorsMaterial.MATERIAL_AQUATIQUE, 1, EntityEquipmentSlot.HEAD));
    }

    @SubscribeEvent
    protected static void registerItems(RegistryEvent.Register<Item> event)
    {
        Console.debug("- Enregistrement des armures:");
        for (Item armor : armors)
        {
            event.getRegistry().registerAll(armor);
            Console.debug("  - #6FF7D0" + armor.getRegistryName());
        }
    }

    @SubscribeEvent
    protected static void registerRenders(ModelRegistryEvent event)
    {
        Console.debug("- Enregistrement du rendu des armures:");
        for (Item armor : armors)
        {
            registerRender(armor);
            Console.debug("  - #6FF794" + armor.getRegistryName());
        }
    }

    private static void registerRender(Item item)
    {
        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
    }
}