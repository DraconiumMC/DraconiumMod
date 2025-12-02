package fr.draconium.core.handlers;

import javax.annotation.Nullable;
import java.util.Map;

import fr.draconium.core.init.blocks.BlocksInit;
import fr.draconium.core.init.blocks.ores.BlocksOresInit;
import fr.draconium.core.init.items.armors.ArmorsInit;
import fr.draconium.core.init.items.ores.OresInit;
import fr.draconium.core.init.items.others.OthersInit;
import fr.draconium.core.init.items.swords.SwordsInit;
import fr.draconium.core.init.items.tools.ToolsInit;
import fr.draconium.core.recipes.DraconiumFurnaceRecipes;
import fr.draconium.core.references.Reference;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;

@Mod.EventBusSubscriber(modid = Reference.MODID)
public class RecipesHandler
{
    /**
     * Enregistre les recettes de CUISSON (Four Vanilla + Draconium Furnace)
     * À appeler depuis DraconiumCore.init()
     */
    public static void registerSmelting()
    {
        // Cuissons classiques
        GameRegistry.addSmelting(BlocksOresInit.PYRONITE_ORE, new ItemStack(OresInit.PYRONITE_INGOT, 1), 1.0F);
        GameRegistry.addSmelting(BlocksOresInit.NETHER_PYRONITE_ORE, new ItemStack(OresInit.PYRONITE_INGOT, 1), 1.0F);
        GameRegistry.addSmelting(BlocksOresInit.DRACONIUM_ORE, new ItemStack(OresInit.DRACONIUM_INGOT, 1), 2.0F);
        GameRegistry.addSmelting(BlocksOresInit.FINDIUM_ORE, new ItemStack(OresInit.FINDIUM_CRISTAL, 1), 2.0F);

        // Recyclage Draconium Furnace (Armures & Outils)
        DraconiumFurnaceRecipes.instance().addRecycling(ArmorsInit.DRACONIUM_HELMET, new ItemStack(OresInit.DRACONIUM_INGOT, 5));
        DraconiumFurnaceRecipes.instance().addRecycling(ArmorsInit.DRACONIUM_CHESTPLATE, new ItemStack(OresInit.DRACONIUM_INGOT, 8));
        DraconiumFurnaceRecipes.instance().addRecycling(ArmorsInit.DRACONIUM_LEGGINGS, new ItemStack(OresInit.DRACONIUM_INGOT, 7));
        DraconiumFurnaceRecipes.instance().addRecycling(ArmorsInit.DRACONIUM_BOOTS, new ItemStack(OresInit.DRACONIUM_INGOT, 4));

        DraconiumFurnaceRecipes.instance().addRecycling(ToolsInit.DRACONIUM_AXE, new ItemStack(OresInit.DRACONIUM_INGOT, 3));
        DraconiumFurnaceRecipes.instance().addRecycling(ToolsInit.DRACONIUM_HAMMER, new ItemStack(OresInit.DRACONIUM_INGOT, 6));
        DraconiumFurnaceRecipes.instance().addRecycling(ToolsInit.DRACONIUM_PICKAXE, new ItemStack(OresInit.DRACONIUM_INGOT, 3));
        DraconiumFurnaceRecipes.instance().addRecycling(ToolsInit.DRACONIUM_SHOVEL, new ItemStack(OresInit.DRACONIUM_INGOT, 1));
        DraconiumFurnaceRecipes.instance().addRecycling(SwordsInit.DRACONIUM_SWORD, new ItemStack(OresInit.DRACONIUM_INGOT, 2));

        DraconiumFurnaceRecipes.instance().addRecycling(ArmorsInit.PYRONITE_HELMET, new ItemStack(OresInit.PYRONITE_INGOT, 5));
        DraconiumFurnaceRecipes.instance().addRecycling(ArmorsInit.PYRONITE_CHESTPLATE, new ItemStack(OresInit.PYRONITE_INGOT, 8));
        DraconiumFurnaceRecipes.instance().addRecycling(ArmorsInit.PYRONITE_LEGGINGS, new ItemStack(OresInit.PYRONITE_INGOT, 7));
        DraconiumFurnaceRecipes.instance().addRecycling(ArmorsInit.PYRONITE_BOOTS, new ItemStack(OresInit.PYRONITE_INGOT, 4));

        DraconiumFurnaceRecipes.instance().addRecycling(ToolsInit.PYRONITE_AXE, new ItemStack(OresInit.PYRONITE_INGOT, 3));
        DraconiumFurnaceRecipes.instance().addRecycling(ToolsInit.PYRONITE_HAMMER, new ItemStack(OresInit.PYRONITE_INGOT, 6));
        DraconiumFurnaceRecipes.instance().addRecycling(ToolsInit.PYRONITE_PICKAXE, new ItemStack(OresInit.PYRONITE_INGOT, 3));
        DraconiumFurnaceRecipes.instance().addRecycling(ToolsInit.PYRONITE_SHOVEL, new ItemStack(OresInit.PYRONITE_INGOT, 1));
        DraconiumFurnaceRecipes.instance().addRecycling(SwordsInit.PYRONITE_SWORD, new ItemStack(OresInit.PYRONITE_INGOT, 2));
    }

    /**
     * Recettes de TABLE DE CRAFT (Event automatique)
     */
    @SubscribeEvent
    public static void registerRecipes(RegistryEvent.Register<IRecipe> event)
    {
        // --- 1. AMÉLIORATION VITESSE (EFFICACITÉ V) ---
        // On utilise notre ingrédient intelligent
        Ingredient bookSpeed = new IngredientEnchantment(Enchantments.EFFICIENCY, 5);

        addShapedRecipe(event, "upgrade_speed", new ItemStack(OthersInit.UPGRADE_SPEED),
                "RDR",
                "DBD",
                "RDR",
                'R', Blocks.REDSTONE_BLOCK,
                'D', BlocksInit.DRACONIUM_BLOCK, // Vérifie si c'est BlocksInit ou BlocksOresInit selon ton code
                'B', bookSpeed
        );

        // --- 2. AMÉLIORATION FORTUNE (FORTUNE III) ---
        Ingredient bookFortune = new IngredientEnchantment(Enchantments.FORTUNE, 3);

        addShapedRecipe(event, "upgrade_fortune", new ItemStack(OthersInit.UPGRADE_FORTUNE),
                "EDE",
                "DBD",
                "EDE",
                'E', Blocks.EMERALD_BLOCK,
                'D', BlocksInit.DRACONIUM_BLOCK,
                'B', bookFortune
        );
    }

    // Méthode utilitaire pour enregistrer proprement une recette Shaped
    private static void addShapedRecipe(RegistryEvent.Register<IRecipe> event, String name, ItemStack output, Object... params) {
        ShapedOreRecipe recipe = new ShapedOreRecipe(new ResourceLocation(Reference.MODID, name), output, params);
        recipe.setRegistryName(new ResourceLocation(Reference.MODID, name));
        event.getRegistry().register(recipe);
    }

    // =========================================================
    // CLASSE MAGIQUE POUR VALIDER LES ENCHANTEMENTS
    // =========================================================
    public static class IngredientEnchantment extends Ingredient {
        private final Enchantment requiredEnchant;
        private final int requiredLevel;

        public IngredientEnchantment(Enchantment ench, int level) {
            // On génère un "vrai" livre enchanté pour l'affichage dans le livre de recettes (JEI)
            super(getEnchantedBookStack(ench, level));
            this.requiredEnchant = ench;
            this.requiredLevel = level;
        }

        @Override
        public boolean apply(@Nullable ItemStack stack) {
            if (stack == null || stack.isEmpty()) return false;

            // On vérifie que c'est bien un livre enchanté
            if (stack.getItem() != Items.ENCHANTED_BOOK) return false;

            // On récupère les enchantements du livre
            Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(stack);

            // On vérifie si l'enchantement requis est présent et au bon niveau
            if (enchants.containsKey(this.requiredEnchant)) {
                return enchants.get(this.requiredEnchant) >= this.requiredLevel;
            }
            return false;
        }

        // Helper pour créer l'item visuel
        private static ItemStack getEnchantedBookStack(Enchantment ench, int level) {
            ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
            ItemEnchantedBook.addEnchantment(book, new EnchantmentData(ench, level));
            return book;
        }
    }
}