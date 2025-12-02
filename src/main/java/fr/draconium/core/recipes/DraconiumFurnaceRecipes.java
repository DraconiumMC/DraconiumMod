package fr.draconium.core.recipes;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class DraconiumFurnaceRecipes
{
    private static final DraconiumFurnaceRecipes INSTANCE = new DraconiumFurnaceRecipes();

    // Map : L'item en entrée -> L'itemStack en sortie (Item + Nombre)
    private final Map<Item, ItemStack> smeltingList = new HashMap<>();

    public static DraconiumFurnaceRecipes instance() {
        return INSTANCE;
    }

    private DraconiumFurnaceRecipes() {
    }

    // Méthode pour ajouter une recette
    public void addRecycling(Item input, ItemStack output) {
        this.smeltingList.put(input, output);
    }

    // Récupère le résultat pour un item donné
    public ItemStack getSmeltingResult(ItemStack stack)
    {
        for (Entry<Item, ItemStack> entry : this.smeltingList.entrySet())
        {
            if (stack.getItem() == entry.getKey())
            {
                return entry.getValue();
            }
        }
        return ItemStack.EMPTY;
    }
}