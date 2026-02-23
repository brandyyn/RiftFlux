/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.network.PacketBuffer
 */
package de.rinonline.korinrpg.Helper.Network;

import cpw.mods.fml.relauncher.Side;
import de.rinonline.korinrpg.Helper.NBT.RINPlayer2;
import de.rinonline.korinrpg.Helper.Network.AbstractMessage;
import java.io.IOException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;

public class SyncNewPlayerPropsMessage
extends AbstractMessage.AbstractClientMessage<SyncNewPlayerPropsMessage> {
    private NBTTagCompound data3;

    public SyncNewPlayerPropsMessage() {
    }

    public SyncNewPlayerPropsMessage(EntityPlayer player) {
        this.data3 = new NBTTagCompound();
        RINPlayer2.get(player).saveNBTData(this.data3);
    }

    @Override
    protected void read(PacketBuffer buffer) throws IOException {
        this.data3 = buffer.readNBTTagCompoundFromBuffer();
    }

    @Override
    protected void write(PacketBuffer buffer) throws IOException {
        buffer.writeNBTTagCompoundToBuffer(this.data3);
    }

    @Override
    public void process(EntityPlayer player, Side side) {
        RINPlayer2.get(player).loadNBTData(this.data3);
    }
}

