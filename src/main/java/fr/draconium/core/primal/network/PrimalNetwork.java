package fr.draconium.core.primal.network;

import fr.draconium.core.capabilities.player.ExtendedPlayerData;
import fr.draconium.core.primal.PrimalAvatarAbilities;
import fr.draconium.core.primal.client.PrimalAvatarClientCooldownStore;
import fr.draconium.core.proxy.packets.server.DraconiumCorePackets;
import fr.draconium.core.references.Reference;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class PrimalNetwork {

    // Utilisation directe du MODID pour éviter le NullPointerException au chargement
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MODID);

    public static void init() {
        int id = 0;
        // Enregistrement des paquets avec des IDs uniques
        INSTANCE.registerMessage(PacketRequest.Handler.class, PacketRequest.class, id++, Side.SERVER);
        INSTANCE.registerMessage(PacketCooldown.Handler.class, PacketCooldown.class, id++, Side.CLIENT);
    }

    public static void syncAbilityCooldown(EntityPlayerMP player) {
        long next = ExtendedPlayerData.get(player).primalAvatarAbility.getNextAbilityUseWorldTick();
        // Utilise l'instance locale pour l'envoi
        INSTANCE.sendTo(new PacketCooldown(player.getEntityId(), next), player);
    }

    // ==========================================
    // PAQUET : REQUÊTE CAPACITÉ (Client -> Serveur)
    // ==========================================
    public static class PacketRequest implements IMessage {
        public PacketRequest() {}

        @Override public void fromBytes(ByteBuf buf) {}
        @Override public void toBytes(ByteBuf buf) {}

        public static class Handler implements IMessageHandler<PacketRequest, IMessage> {
            @Override
            public IMessage onMessage(PacketRequest message, MessageContext ctx) {
                EntityPlayerMP player = ctx.getServerHandler().player;
                player.getServerWorld().addScheduledTask(() -> PrimalAvatarAbilities.tryActivate(player));
                return null;
            }
        }
    }

    // ==========================================
    // PAQUET : COOLDOWN (Serveur -> Client)
    // ==========================================
    public static class PacketCooldown implements IMessage {
        private int entityId;
        private long nextTick;

        public PacketCooldown() {}
        public PacketCooldown(int entityId, long nextTick) {
            this.entityId = entityId;
            this.nextTick = nextTick;
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            this.entityId = buf.readInt();
            this.nextTick = buf.readLong();
        }

        @Override
        public void toBytes(ByteBuf buf) {
            buf.writeInt(this.entityId);
            buf.writeLong(this.nextTick);
        }

        public static class Handler implements IMessageHandler<PacketCooldown, IMessage> {
            @Override
            public IMessage onMessage(PacketCooldown message, MessageContext ctx) {
                if (ctx.side.isClient()) {
                    Minecraft.getMinecraft().addScheduledTask(() -> handle(message));
                }
                return null;
            }

            @SideOnly(Side.CLIENT)
            private void handle(PacketCooldown message) {
                PrimalAvatarClientCooldownStore.set(message.entityId, message.nextTick);
            }
        }
    }
}