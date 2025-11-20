package fr.draconium.core.capabilities.player;

import javax.annotation.Nullable;

import fr.draconium.core.references.Reference;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;

public class ExtendedPlayerData implements ICapabilitySerializable<NBTTagCompound>
{
	public static final ResourceLocation KEY = new ResourceLocation(Reference.MODID, "extended_player_data");
	
	@CapabilityInject(ExtendedPlayerData.class)
	public static Capability<ExtendedPlayerData> CAPABILITY = null;

	public final DraconiumArmorAbilities draconiumArmorAbilities = new DraconiumArmorAbilities();

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
	{
		return capability == CAPABILITY;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
	{
		if (CAPABILITY == null)
		{
			CrashReport crashReport = new CrashReport("Getting extended player data capability", new NullPointerException("Capability was not registered"));
			throw new ReportedException(crashReport);
		}
		return capability == CAPABILITY ? CAPABILITY.cast(this) : null;
	}

	@Override
	public NBTTagCompound serializeNBT()
	{
		NBTTagCompound compound = new NBTTagCompound();
		compound.setTag("draconiumArmorAbilities", this.draconiumArmorAbilities.serializeNBT());
		return compound;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt)
	{
		this.draconiumArmorAbilities.deserializeNBT(nbt.getCompoundTag("draconiumArmorAbilities"));
	}

	public void tick()
	{
		this.draconiumArmorAbilities.tick();
	}

	public static void register()
	{
		CapabilityManager.INSTANCE.register(ExtendedPlayerData.class, new Capability.IStorage<ExtendedPlayerData>()
		{
			@Override
			public NBTTagCompound writeNBT(Capability<ExtendedPlayerData> capability, ExtendedPlayerData instance, EnumFacing side)
			{
				return instance.serializeNBT();
			}

			@Override
			public void readNBT(Capability<ExtendedPlayerData> capability, ExtendedPlayerData instance, EnumFacing side, NBTBase nbt)
			{
				if (nbt instanceof NBTTagCompound)
				{
					instance.deserializeNBT((NBTTagCompound) nbt);
				}
			}
		}, ExtendedPlayerData::new);
	}

	public static ExtendedPlayerData get(EntityPlayer player)
	{
		ExtendedPlayerData capability = player.getCapability(CAPABILITY, null);
		if (capability == null)
		{
			CrashReport crashReport = new CrashReport("Getting extended player data", new NullPointerException("Player does not have extended data"));
			player.addEntityCrashInfo(crashReport.makeCategory("Player"));
			throw new ReportedException(crashReport);
		}
		return capability;
	}

	public static class DraconiumArmorAbilities implements INBTSerializable<NBTTagCompound>
	{
		private int teleportCooldown;

		private DraconiumArmorAbilities() {}

		@Override
		public NBTTagCompound serializeNBT()
		{
			NBTTagCompound compound = new NBTTagCompound();
			compound.setInteger("teleportCooldown", this.teleportCooldown);
			return compound;
		}

		public void deserializeNBT(NBTTagCompound nbt)
		{
			this.teleportCooldown = nbt.getInteger("teleportCooldown");
		}

		public int getTeleportCooldown()
		{
			return this.teleportCooldown;
		}

		public void setTeleportCooldown(int teleportCooldown)
		{
			this.teleportCooldown = teleportCooldown;
		}

		private void tick()
		{
			if (this.teleportCooldown > 0)
			{
				this.teleportCooldown--;
			}
		}
	}
}
