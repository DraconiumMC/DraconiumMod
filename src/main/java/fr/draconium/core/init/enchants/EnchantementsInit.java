package fr.draconium.core.init.enchants;

import fr.draconium.core.enchantments.EnchantRange;
import fr.draconium.core.messages.Console;
import fr.draconium.core.references.Reference;
import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = Reference.MODID)
public class EnchantementsInit {

    public static Enchantment RANGE_ENCHANT;

    @SubscribeEvent
    public static void registerEnchantments(RegistryEvent.Register<Enchantment> event) {
        Console.debug("- Enregistrement des enchantements:");
        RANGE_ENCHANT = new EnchantRange();
        event.getRegistry().register(RANGE_ENCHANT);
        Console.debug("  - " + RANGE_ENCHANT.getRegistryName());
    }
}
