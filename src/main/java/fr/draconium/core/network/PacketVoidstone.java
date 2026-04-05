package fr.draconium.core.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketVoidstone implements IMessage {
    private int buttonId;

    public PacketVoidstone() {} // Obligatoire

    public PacketVoidstone(int buttonId) {
        this.buttonId = buttonId;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.buttonId = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.buttonId);
    }

    public int getButtonId() { return buttonId; }
}