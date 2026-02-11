package com.voidsrift.riftflux.placeditem;

import net.minecraft.init.Blocks;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TilePlacedItem extends TileEntity {
    public ItemStack stack;
    public float rotation = 0.0F;
    @SideOnly(Side.CLIENT)
    private transient EntityItem renderEntity;
    @SideOnly(Side.CLIENT)
    private transient ItemStack renderStack;

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
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
        clearRenderCache();
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
        clearRenderCache();
    }

    @Override
    public boolean canUpdate() {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public EntityItem getOrCreateRenderEntity() {
        if (worldObj == null || !worldObj.isRemote || stack == null) {
            return null;
        }
        if (renderStack == null || !sameItem(renderStack, stack)) {
            renderStack = stack.copy();
            renderStack.stackSize = 1;
            renderEntity = new EntityItem(worldObj, 0.0D, 0.0D, 0.0D, renderStack);
            renderEntity.hoverStart = 0.0F;
            renderEntity.age = 0;
            renderEntity.rotationYaw = 0.0F;
            renderEntity.rotationPitch = 0.0F;
            renderEntity.onGround = true;
        }
        return renderEntity;
    }

    @SideOnly(Side.CLIENT)
    private static boolean sameItem(ItemStack a, ItemStack b) {
        if (a == b) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        if (a.getItem() != b.getItem()) {
            return false;
        }
        if (a.getItemDamage() != b.getItemDamage()) {
            return false;
        }
        return ItemStack.areItemStackTagsEqual(a, b);
    }

    private void clearRenderCache() {
        renderEntity = null;
        renderStack = null;
    }
}
