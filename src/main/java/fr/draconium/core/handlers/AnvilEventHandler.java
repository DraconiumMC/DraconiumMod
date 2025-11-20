package fr.draconium.core.handlers;

import fr.draconium.core.init.enchants.EnchantementsInit;
import fr.draconium.core.init.items.armors.ArmorsInit;
import fr.draconium.core.init.items.others.OthersInit;
import fr.draconium.core.references.Reference;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.*;

@Mod.EventBusSubscriber(modid = Reference.MODID)
public class AnvilEventHandler {

    /** Liste des casques autorisés à être fusionnés avec le radar */
    private static final Set<Item> ALLOWED_HELMETS = new HashSet<>(Arrays.asList(
            Items.CHAINMAIL_HELMET,
            Items.IRON_HELMET,
            Items.GOLDEN_HELMET,
            Items.DIAMOND_HELMET,
            ArmorsInit.PYRONITE_HELMET,
            ArmorsInit.DRACONIUM_HELMET,
            ArmorsInit.DRACONIQUE_HELMET
    ));

    /** Vérifie si un ItemStack est un casque compatible */
    private static boolean isValidHelmet(ItemStack stack) {
        return ALLOWED_HELMETS.contains(stack.getItem());
    }


    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        ItemStack left  = event.getLeft();
        ItemStack right = event.getRight();

        if (left.isEmpty() || right.isEmpty())
            return;

        // Cas 1 : Radar + livre d’enchantement
        if (left.getItem() == OthersInit.RADAR && right.getItem() == Items.ENCHANTED_BOOK) {
            handleRadarBookCombine(event, left, right);
            return;
        }

        // Cas 2 : Fusion casque + radar (UNIQUEMENT Range III)
        if (isValidHelmet(left) && right.getItem() == OthersInit.RADAR) {
            handleHelmetFusion(event, left, right);
            return;
        }

        // Cas 3 : Radar dans le slot gauche, casque dans le slot droit (si l’ordre est inversé)
        if (isValidHelmet(right) && left.getItem() == OthersInit.RADAR) {
            handleHelmetFusion(event, right, left);
        }
    }



    /* --------------------------------------------------------- */
    /* ------------------ RADAR + LIVRE ------------------------ */
    /* --------------------------------------------------------- */

    private static void handleRadarBookCombine(AnvilUpdateEvent event, ItemStack radar, ItemStack book) {

        int rangeLevel = EnchantmentHelper.getEnchantmentLevel(EnchantementsInit.RANGE_ENCHANT, radar);

        // Si Range < 3 → impossible de mettre Mending / Unbreaking
        if (rangeLevel < 3)
            return;

        Map<Enchantment, Integer> bookEnchants = EnchantmentHelper.getEnchantments(book);
        if (bookEnchants.isEmpty())
            return;

        Map<Enchantment, Integer> toAdd = new HashMap<>();

        for (Map.Entry<Enchantment, Integer> entry : bookEnchants.entrySet()) {

            Enchantment ench = entry.getKey();
            int lvl = entry.getValue();

            // Seuls ces deux enchants sont autorisés sur le radar
            if (ench == Enchantments.MENDING || ench == Enchantments.UNBREAKING) {
                toAdd.put(ench, lvl);
            } else {
                // Livre invalide → on bloque la recette
                return;
            }
        }

        if (toAdd.isEmpty())
            return;

        ItemStack output = radar.copy();
        Map<Enchantment, Integer> current = EnchantmentHelper.getEnchantments(output);

        for (Map.Entry<Enchantment, Integer> entry : toAdd.entrySet()) {

            Enchantment ench = entry.getKey();
            int lvl = entry.getValue();

            int existing = current.getOrDefault(ench, 0);

            int finalLvl = Math.max(existing, lvl);

            // Cap d'équilibrage
            if (ench == Enchantments.UNBREAKING)
                finalLvl = Math.min(finalLvl, 3);

            current.put(ench, finalLvl);
        }

        EnchantmentHelper.setEnchantments(current, output);
        event.setOutput(output);
        event.setCost(8);
    }



    /* --------------------------------------------------------- */
    /* ---------------- FUSION CASQUE + RADAR ------------------ */
    /* --------------------------------------------------------- */

    private static void handleHelmetFusion(AnvilUpdateEvent event, ItemStack helmet, ItemStack radar) {

        int rangeLevel = EnchantmentHelper.getEnchantmentLevel(EnchantementsInit.RANGE_ENCHANT, radar);

        // Fusion uniquement si radar = Range III
        if (rangeLevel < 3)
            return;

        ItemStack output = helmet.copy();
        NBTTagCompound tag = output.getOrCreateSubCompound("RadarData");

        tag.setBoolean("HasRadar", true); // ce tag sera lu par le radar

        event.setOutput(output);
        event.setCost(10);
    }
}
