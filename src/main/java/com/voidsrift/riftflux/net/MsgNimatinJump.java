package com.voidsrift.riftflux.net;

import com.voidsrift.riftflux.palaria.entity.EntityNimatin;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

public class MsgNimatinJump implements IMessage {
    private int jumpCharge;

    public MsgNimatinJump() {
    }

    public MsgNimatinJump(int jumpCharge) {
        this.jumpCharge = jumpCharge;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.jumpCharge = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.jumpCharge);
    }

    public static class Handler implements IMessageHandler<MsgNimatinJump, IMessage> {
        @Override
        public IMessage onMessage(MsgNimatinJump msg, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            if (player == null || !(player.ridingEntity instanceof EntityNimatin)) {
                return null;
            }
            EntityNimatin nimatin = (EntityNimatin) player.ridingEntity;
            if (nimatin.riddenByEntity == player) {
                nimatin.setJumpPower(msg.jumpCharge);
            }
            return null;
        }
    }
}
