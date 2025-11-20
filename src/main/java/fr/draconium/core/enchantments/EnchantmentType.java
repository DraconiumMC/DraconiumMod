package fr.draconium.core.enchantments;

import fr.draconium.core.items.others.ItemRadar;
import net.minecraft.item.Item;

public enum EnchantmentType
{
	RADAR
	{
        /**
         * Return true if the item passed can be enchanted by a enchantment of this type.
         */
        public boolean canEnchantItem(Item itemIn)
        {
            return itemIn instanceof ItemRadar;
        }
    }
}
