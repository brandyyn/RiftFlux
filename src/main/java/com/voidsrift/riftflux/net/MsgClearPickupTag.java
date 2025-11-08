package com.voidsrift.riftflux.net;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/** Client->Server: clear riftflux_new on exact slot and mark riftflux_seen. */
public class MsgClearPickupTag implements IMessage {

    private int windowId;
    private int slotIndex;

    public MsgClearPickupTag() {}
    public MsgClearPickupTag(int windowId, int slotIndex) {
        this.windowId = windowId;
        this.slotIndex = slotIndex;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(windowId);
        buf.writeInt(slotIndex);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        windowId = buf.readInt();
        slotIndex = buf.readInt();
    }

    public static class Handler implements IMessageHandler<MsgClearPickupTag, IMessage> {
        @Override
        public IMessage onMessage(MsgClearPickupTag msg, MessageContext ctx) {
            final EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            if (player == null) return null;

            // Prefer the container the client referenced, but if it changed
            // (common race), fall back to whatever is currently open or the player inv.
            Container c = player.openContainer;
            if (c == null || c.windowId != msg.windowId) {
                // Try player inventory container (windowId 0 in SP; safe fallback)
                c = player.inventoryContainer != null ? player.inventoryContainer : player.openContainer;
            }
            if (c == null || c.inventorySlots == null) return null;

            if (msg.slotIndex < 0 || msg.slotIndex >= c.inventorySlots.size()) return null;

            final Slot s = (Slot) c.inventorySlots.get(msg.slotIndex);
            final ItemStack st = s.getStack();
            if (st == null) return null;

            NBTTagCompound tag = st.getTagCompound();
            if (tag == null) tag = new NBTTagCompound();
            tag.removeTag("riftflux_new");
            
            st.setTagCompound(tag);

            s.onSlotChanged();
            c.detectAndSendChanges();
            return null;
        }
    }
}
