package fr.draconium.core.network;

import fr.draconium.core.items.others.ItemVoidstone;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketVoidstoneHandler implements IMessageHandler<PacketVoidstone, IMessage> {
    @Override
    public IMessage onMessage(PacketVoidstone message, MessageContext ctx) {
        // On récupère le joueur sur le serveur
        EntityPlayerMP player = ctx.getServerHandler().player;

        // On exécute le code sur le thread principal du serveur (indispensable en 1.12.2)
        player.getServerWorld().addScheduledTask(() -> {
            ItemStack stack = player.getHeldItemMainhand(); // Ou ta logique de recherche
            if (!stack.isEmpty() && stack.getItem() instanceof ItemVoidstone) {
                ItemVoidstone item = (ItemVoidstone) stack.getItem();

                if (message.getButtonId() == 1) {
                    // Logique d'amélioration
                } else if (message.getButtonId() == 2) {
                    // Logique de concassage
                }
            }
        });
        return null;
    }
}