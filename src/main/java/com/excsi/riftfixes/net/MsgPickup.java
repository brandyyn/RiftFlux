package com.excsi.riftfixes.net;

import com.excsi.riftfixes.client.PickupNotifierHud;
import com.excsi.riftfixes.client.PickupStarClientTracker;
import cpw.mods.fml.common.network.simpleimpl.*;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class MsgPickup implements IMessage {
    public String itemName;
    public int meta;
    public int count;

    public MsgPickup() {}
    public MsgPickup(String name, int meta, int count) {
        this.itemName = name; this.meta = meta; this.count = count;
    }

    @Override public void fromBytes(ByteBuf buf) {
        int n = buf.readUnsignedShort();
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) sb.append((char) buf.readUnsignedByte());
        itemName = sb.toString();
        meta = buf.readShort();
        count = buf.readShort();
    }
    @Override public void toBytes(ByteBuf buf) {
        buf.writeShort(itemName.length());
        for (int i = 0; i < itemName.length(); i++) buf.writeByte((byte) itemName.charAt(i));
        buf.writeShort(meta);
        buf.writeShort(count);
    }

    /** Client handler: mark for star + show HUD popup. */
    public static class ClientHandler implements IMessageHandler<MsgPickup, IMessage> {
        @Override public IMessage onMessage(MsgPickup msg, MessageContext ctx) {
            // queue for star matching
            PickupStarClientTracker.enqueue(msg.itemName, msg.meta);

            // HUD popup
            Item it = (Item) Item.itemRegistry.getObject(msg.itemName);
            if (it != null) {
                ItemStack st = new ItemStack(it, Math.max(1, msg.count), msg.meta);
                PickupNotifierHud.enqueue(st, Math.max(1, st.stackSize));
            }
            return null;
        }
    }
}
