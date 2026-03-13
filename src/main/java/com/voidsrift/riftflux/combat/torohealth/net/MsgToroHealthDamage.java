package com.voidsrift.riftflux.combat.torohealth.net;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.riftflux;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class MsgToroHealthDamage implements IMessage {

    public int entityId;
    public int damage;

    public MsgToroHealthDamage() {
    }

    public MsgToroHealthDamage(int entityId, int damage) {
        this.entityId = entityId;
        this.damage = damage;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.entityId = buf.readInt();
        this.damage = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.entityId);
        buf.writeInt(this.damage);
    }

    public static class Handler implements IMessageHandler<MsgToroHealthDamage, IMessage> {
        @Override
        public IMessage onMessage(MsgToroHealthDamage message, MessageContext ctx) {
            if (!ModConfig.enableToroHealthModule || !ModConfig.toroHealthShowDamageParticles || message.damage <= 0) {
                return null;
            }
            if (riftflux.proxy != null) {
                riftflux.proxy.applyToroHealthDamage(message.entityId, message.damage);
            }
            return null;
        }
    }
}
