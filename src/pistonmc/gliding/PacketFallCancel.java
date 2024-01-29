package pistonmc.gliding;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class PacketFallCancel implements IMessage {

    @Override
    public void fromBytes(ByteBuf buf) {
    }

    @Override
    public void toBytes(ByteBuf buf) {
    }

    public static class Handler implements IMessageHandler<PacketFallCancel, IMessage> {

        @Override
        public IMessage onMessage(PacketFallCancel message, MessageContext ctx) {
            ctx.getServerHandler().playerEntity.fallDistance = 0;
            return null;
        }
    }
}
