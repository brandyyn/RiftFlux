package com.voidsrift.riftflux.net;

import com.voidsrift.riftflux.client.PickupNotifierHud;
import com.voidsrift.riftflux.client.PickupStarClientTracker;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;

/**
 * Send the full ItemStack for pickups so damage/NBT variants tag correctly.
 * (On 1.7.10 the client handler runs on the client thread already.)
 */
public final class MsgPickup implements IMessage {

    public ItemStack stack;
    public int count;

    public MsgPickup() {}

    public MsgPickup(ItemStack stack, int count){
        this.stack = stack == null ? null : stack.copy();
        this.count = Math.max(1, count);
        if (this.stack != null) this.stack.stackSize = this.count; // for HUD text
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeItemStack(buf, stack);
        buf.writeInt(count);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        stack = ByteBufUtils.readItemStack(buf);
        count = buf.readInt();
    }

    public static final class ClientHandler implements IMessageHandler<MsgPickup, IMessage> {
        @Override
        public IMessage onMessage(MsgPickup msg, MessageContext ctx) {
            if (msg.stack != null) {
                PickupStarClientTracker.enqueue(msg.stack);       // star (handles damageables)
                PickupNotifierHud.enqueue(msg.stack, msg.count);  // HUD entry
            }
            return null;
        }
    }
}
