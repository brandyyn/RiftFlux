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
    public void fromBytes(ByteBuf buf) {
        this.windowId = buf.readInt();
        this.slotIndex = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.windowId);
        buf.writeInt(this.slotIndex);
    }

    public static class Handler implements IMessageHandler<MsgClearPickupTag, IMessage> {

        @Override
        public IMessage onMessage(MsgClearPickupTag msg, MessageContext ctx) {
            final EntityPlayerMP player = ctx.getServerHandler().playerEntity;

            // Sanity: window id must match
            if (player == null || player.openContainer == null) {
                return null;
            }

            final Container c = player.openContainer;
            if (c.windowId != msg.windowId) {
                return null;
            }

            // Sanity: slot index must be in bounds
            if (msg.slotIndex < 0 || msg.slotIndex >= c.inventorySlots.size()) {
                return null;
            }

            final Slot s = (Slot) c.inventorySlots.get(msg.slotIndex);
            if (s == null) {
                return null;
            }

            final ItemStack st = s.getStack();
            if (st == null) {
                return null;
            }

            NBTTagCompound tag = st.getTagCompound();
            // If there is no tag at all, don't create an empty one – that would leave
            // items as <item>.withTag({}), which breaks very strict NBT mods.
            if (tag == null) {
                return null;
            }

            tag.removeTag("riftflux_new");

            // If removing our key leaves the compound empty, strip NBT entirely.
            if (tag.hasNoTags()) {
                st.setTagCompound(null);
            } else {
                st.setTagCompound(tag);
            }

            s.onSlotChanged();
            c.detectAndSendChanges();
            return null;
        }
    }
}
