package com.excsi.riftfixes.net;

import com.excsi.riftfixes.client.PickupNotifierHud;
import com.excsi.riftfixes.client.PickupStarClientTracker;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class MsgPickup implements IMessage {
    private static final int MODE_STACK  = 0;
    private static final int MODE_LEGACY = 1;

    public ItemStack stack; // MODE_STACK
    public int count;

    private String regName; // MODE_LEGACY
    private int meta;
    private int mode = MODE_STACK;

    public MsgPickup() {}

    public MsgPickup(ItemStack stack, int count) {
        this.stack = stack == null ? null : stack.copy();
        this.count = Math.max(1, count);
        this.mode  = MODE_STACK;
    }

    public MsgPickup(String registryName, int meta, int count) {
        this.regName = registryName;
        this.meta    = meta;
        this.count   = Math.max(1, count);
        this.mode    = MODE_LEGACY;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(mode);
        buf.writeInt(count);
        if (mode == MODE_STACK) {
            ByteBufUtils.writeItemStack(buf, stack);
        } else {
            ByteBufUtils.writeUTF8String(buf, regName == null ? "" : regName);
            buf.writeInt(meta);
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        mode  = buf.readInt();
        count = buf.readInt();
        if (mode == MODE_STACK) {
            stack = ByteBufUtils.readItemStack(buf);
        } else {
            regName = ByteBufUtils.readUTF8String(buf);
            meta    = buf.readInt();
            Item it = (Item) Item.itemRegistry.getObject(regName);
            stack = it != null ? new ItemStack(it, 1, meta) : null;
        }
    }

    public static final class Handler implements IMessageHandler<MsgPickup, IMessage> {
        @Override
        public IMessage onMessage(MsgPickup msg, MessageContext ctx) {
            if (msg.count <= 0) return null;

            if (msg.stack != null) {
                PickupNotifierHud.enqueue(msg.stack, msg.count);
                PickupStarClientTracker.enqueue(msg.stack);   // ← make sure this happens
            } else if (msg.regName != null) {
                Item it = (Item) Item.itemRegistry.getObject(msg.regName);
                if (it != null) {
                    ItemStack st = new ItemStack(it, 1, msg.meta);
                    PickupNotifierHud.enqueue(st, msg.count);
                    PickupStarClientTracker.enqueue(st);      // ← also enqueue tracker here
                }
            }
            return null;
        }
    }
}
