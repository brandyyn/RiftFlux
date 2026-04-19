/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.channel.ChannelHandlerContext
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 */
package zairus.worldexplorer.core.util.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import zairus.worldexplorer.core.items.WorldExplorerItems;
import zairus.worldexplorer.core.player.CorePlayerManager;
import zairus.worldexplorer.core.util.network.AbstractPacket;

public class JournalPacket
extends AbstractPacket {
    @Override
    public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
    }

    @Override
    public void handleClientSide(EntityPlayer player) {
    }

    @Override
    public void handleServerSide(EntityPlayer player) {
        this.updateJournalTitle(player);
        this.playerInit(player);
    }

    private void playerInit(EntityPlayer player) {
        CorePlayerManager.checkInitialize(player);
    }

    private void updateJournalTitle(EntityPlayer player) {
        String name = player.getDisplayName() + "'s Journal";
        ItemStack journal = player.getHeldItem();
        if (journal == null) {
            return;
        }
        if (journal.getItem() != WorldExplorerItems.journal) {
            return;
        }
        if (!journal.hasTagCompound()) {
            journal.setTagCompound(new NBTTagCompound());
        }
        journal.getTagCompound().setString("JournalTitle", name);
    }
}

