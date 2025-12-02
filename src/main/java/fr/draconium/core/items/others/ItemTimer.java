package fr.draconium.core.items.others;

import fr.draconium.core.DraconiumCore;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class ItemTimer extends Item
{
	public static ItemTimer instance;

	private final int time = 1800;
	private final int tickInterval = 30;

	public ItemTimer(String name)
	{
		instance = this;

		this.setTranslationKey(name);
		this.setRegistryName(name);
		this.setMaxStackSize(1);
		this.setCreativeTab(DraconiumCore.DRACONIUM_TAB_OTHERS);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
	{
		ItemStack stack = player.getHeldItem(hand);

		if (world.isRemote)
		{
			NBTTagCompound tag = stack.getOrCreateSubCompound("timer");

			// Premier lancement
			if (!tag.hasKey("enable"))
			{
				int startTime = this.time * this.tickInterval;
				tag.setBoolean("enable", true);
				tag.setInteger("time", startTime);

				player.sendMessage(new TextComponentString(
						"Timer de " + this.formatTimeLeft(startTime) + " secondes lancé !"));
			}
			else if (!tag.getBoolean("enable"))
			{
				int timeLeft = tag.getInteger("time");
				tag.setBoolean("enable", true);

				player.sendMessage(new TextComponentString(
						"Temps restant : " + this.formatTimeLeft(timeLeft) + " secondes"));
			}
		}

		return new ActionResult<>(EnumActionResult.SUCCESS, stack);
	}

	private String formatTimeLeft(int seconds)
	{
		seconds = seconds / this.tickInterval;

		int hours = seconds / 3600;
		int minutes = (seconds % 3600) / 60;
		int secs = seconds % 60;

		return String.format("%02d:%02d:%02d", hours, minutes, secs);
	}
}
