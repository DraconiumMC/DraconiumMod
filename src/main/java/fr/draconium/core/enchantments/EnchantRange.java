package fr.draconium.core.enchantments;

import fr.draconium.core.init.items.others.OthersInit;
import fr.draconium.core.references.Reference;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class EnchantRange extends Enchantment {

    public EnchantRange() {
        super(
                Rarity.RARE,                         // un peu spécial
                EnumEnchantmentType.ALL,             // on filtre dans canApply
                new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND}
        );
        setRegistryName(new ResourceLocation(Reference.MODID, "range_enchant"));
        setName(Reference.MODID + ".range_enchant");
    }

    @Override
    public boolean canApply(ItemStack stack) {
        // Autoriser sur le radar uniquement (et sur les livres via super)
        return stack.getItem() == OthersInit.RADAR || super.canApply(stack);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        // Seul l'enchant Range passe par la table sur le Radar
        return stack.getItem() == OthersInit.RADAR;

    }

    @Override
    public boolean canApplyTogether(Enchantment enchant) {
        // OK avec Unbreaking, mais pas empilable avec lui-même
        if (enchant == this) return false;
        if (enchant == Enchantments.UNBREAKING) return true;
        return super.canApplyTogether(enchant);
    }

    @Override
    public int getMinLevel() {
        return 1;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public int getMinEnchantability(int level) {
        return 10 + level * 8;
    }

    @Override
    public int getMaxEnchantability(int level) {
        return getMinEnchantability(level) + 20;
    }
}
