package fr.draconium.core.primal;

import fr.draconium.core.items.armors.PrimalAvatarArmorItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

public final class PrimalAvatarLogic {

	private PrimalAvatarLogic() {
	}

	public static PrimalAvatarType getEquippedSetType(EntityPlayer player) {
		ItemStack head = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
		ItemStack chest = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
		ItemStack legs = player.getItemStackFromSlot(EntityEquipmentSlot.LEGS);
		ItemStack feet = player.getItemStackFromSlot(EntityEquipmentSlot.FEET);
		if (head.isEmpty() || chest.isEmpty() || legs.isEmpty() || feet.isEmpty()) {
			return PrimalAvatarType.NONE;
		}
		PrimalAvatarType tHead = typeFromStack(head);
		if (tHead == PrimalAvatarType.NONE) {
			return PrimalAvatarType.NONE;
		}
		if (typeFromStack(chest) != tHead || typeFromStack(legs) != tHead || typeFromStack(feet) != tHead) {
			return PrimalAvatarType.NONE;
		}
		return tHead;
	}

	private static PrimalAvatarType typeFromStack(ItemStack stack) {
		if (stack.getItem() instanceof PrimalAvatarArmorItem) {
			return ((PrimalAvatarArmorItem) stack.getItem()).getAvatarType();
		}
		return PrimalAvatarType.NONE;
	}

	public static boolean isPrimalArmor(ItemStack stack) {
		return !stack.isEmpty() && stack.getItem() instanceof PrimalAvatarArmorItem;
	}
}
