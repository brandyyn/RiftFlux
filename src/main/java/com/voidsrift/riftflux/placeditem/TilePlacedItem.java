package com.voidsrift.riftflux.placeditem;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class TilePlacedItem extends TileEntity {
    public ItemStack stack;
    public float rotation = 0.0F;
    private boolean hasUpdated = false;

    @Override
    public void updateEntity() {
        if (!hasUpdated && stack != null) {
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
            hasUpdated = true;
        }
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tag = new NBTTagCompound();
        writeToNBT(tag);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        readFromNBT(pkt.func_148857_g());
    }

    public void setStack(ItemStack stack) {
        this.stack = stack;
        if (worldObj != null) {
            worldObj.scheduleBlockUpdate(xCoord, yCoord, zCoord, PlacedItemContent.placedItemBlock, 20);
        }
    }

    public ItemStack getStack() {
        return stack;
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        NBTTagCompound itemTag = new NBTTagCompound();
        if (stack != null) {
            stack.writeToNBT(itemTag);
        }
        compound.setTag("Item0", itemTag);
        compound.setFloat("Rotation", rotation);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        NBTTagCompound itemTag = compound.getCompoundTag("Item0");
        stack = ItemStack.loadItemStackFromNBT(itemTag);
        if (stack == null) {
            stack = new ItemStack(Blocks.stone);
            invalidate();
        }
        rotation = compound.getFloat("Rotation");
    }
}
