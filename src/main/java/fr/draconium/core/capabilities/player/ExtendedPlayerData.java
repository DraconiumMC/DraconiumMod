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

public class ExtendedPlayerData implements ICapabilitySerializable<NBTTagCompound> {
    public static final ResourceLocation KEY = new ResourceLocation(Reference.MODID, "extended_player_data");

    @CapabilityInject(ExtendedPlayerData.class)
    public static Capability<ExtendedPlayerData> CAPABILITY = null;

    public final DraconiumArmorAbilities draconiumArmorAbilities = new DraconiumArmorAbilities();

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CAPABILITY;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (CAPABILITY == null) {
            CrashReport crashReport = new CrashReport("Getting extended player data capability", new NullPointerException("Capability was not registered"));
            throw new ReportedException(crashReport);
        }
        return capability == CAPABILITY ? CAPABILITY.cast(this) : null;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setTag("draconiumArmorAbilities", this.draconiumArmorAbilities.serializeNBT());
        return compound;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        this.draconiumArmorAbilities.deserializeNBT(nbt.getCompoundTag("draconiumArmorAbilities"));
    }

    public void tick() {
        // Le corps doit être vide ou ne contenir que des logiques non liées aux cooldowns persistants.
        // Laissez-le tel quel pour cette version :
    }

    public static void register() {
        CapabilityManager.INSTANCE.register(ExtendedPlayerData.class, new Capability.IStorage<ExtendedPlayerData>() {
            @Override
            public NBTTagCompound writeNBT(Capability<ExtendedPlayerData> capability, ExtendedPlayerData instance, EnumFacing side) {
                return instance.serializeNBT();
            }

            @Override
            public void readNBT(Capability<ExtendedPlayerData> capability, ExtendedPlayerData instance, EnumFacing side, NBTBase nbt) {
                if (nbt instanceof NBTTagCompound) {
                    instance.deserializeNBT((NBTTagCompound) nbt);
                }
            }
        }, ExtendedPlayerData::new);
    }

    public static ExtendedPlayerData get(EntityPlayer player) {
        ExtendedPlayerData capability = player.getCapability(CAPABILITY, null);
        if (capability == null) {
            CrashReport crashReport = new CrashReport("Getting extended player data", new NullPointerException("Player does not have extended data"));
            player.addEntityCrashInfo(crashReport.makeCategory("Player"));
            throw new ReportedException(crashReport);
        }
        return capability;
    }

    public static class DraconiumArmorAbilities implements INBTSerializable<NBTTagCompound> {
        // CHANGEMENT : On stocke l'heure d'expiration du cooldown (en ticks du monde)
        private long teleportCooldownExpiry;
        private long shieldCooldownExpiry;
        private long wolfCooldownExpiry;

        private DraconiumArmorAbilities() {
        }

        @Override
        public NBTTagCompound serializeNBT() {
            NBTTagCompound compound = new NBTTagCompound();
            // Sauvegarde en LONG
            compound.setLong("teleportCooldownExpiry", this.teleportCooldownExpiry);
            compound.setLong("shieldCooldownExpiry", this.shieldCooldownExpiry);
            compound.setLong("wolfCooldownExpiry", this.wolfCooldownExpiry);
            return compound;
        }

        public void deserializeNBT(NBTTagCompound nbt) {
            // Chargement en LONG
            this.teleportCooldownExpiry = nbt.getLong("teleportCooldownExpiry");
            this.shieldCooldownExpiry = nbt.getLong("shieldCooldownExpiry");
            this.wolfCooldownExpiry = nbt.getLong("wolfCooldownExpiry");
        }

        // --- Getters et Setters ---

        // Les getters/setters utilisent maintenant le mot Expiry
        public long getTeleportCooldownExpiry() {
            return this.teleportCooldownExpiry;
        }

        public void setTeleportCooldownExpiry(long time) {
            this.teleportCooldownExpiry = time;
        }

        public long getShieldCooldownExpiry() {
            return this.shieldCooldownExpiry;
        }

        public void setShieldCooldownExpiry(long time) {
            this.shieldCooldownExpiry = time;
        }

        public long getWolfCooldownExpiry() {
            return this.wolfCooldownExpiry;
        }

        public void setWolfCooldownExpiry(long time) {
            this.wolfCooldownExpiry = time;
        }

        // --- Ticking (Décompte - VIDE pour ces champs) ---
        // Le décompte ne se fait PLUS ici, car on utilise l'heure du monde.
        private void tick() {
            // Seule la variable non persistante (si tu en as d'autres) devrait être décrémentée ici.
            // On laisse le corps de la méthode vide, car le décompte est fait par comparaison dans le Packet Handler.
        }
    }
}