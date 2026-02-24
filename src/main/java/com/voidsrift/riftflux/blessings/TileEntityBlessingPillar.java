package com.voidsrift.riftflux.blessings;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

import java.util.Random;

public class TileEntityBlessingPillar extends TileEntity {
    private String blessing;
    private boolean active;

    public String getBlessing() {
        if (blessing == null) {
            if (worldObj != null && worldObj.isRemote) {
                return null;
            }
            Random rand = worldObj != null ? worldObj.rand : new Random();
            blessing = BlessingHelper.getRandomBlessing(rand, true);
        }
        if (blessing != null && !BlessingHelper.isBlessingEnabled(blessing)) {
            Random rand = worldObj != null ? worldObj.rand : new Random();
            blessing = BlessingHelper.getRandomBlessing(rand, true);
        }
        return blessing;
    }

    public void setBlessing(String blessing) {
        this.blessing = blessing;
        if (worldObj != null) {
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        if (this.active == active) {
            return;
        }
        this.active = active;
        if (worldObj != null) {
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
            markDirty();
        }
    }

    @Override
    public boolean canUpdate() {
        return false;
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        if (worldObj == null) {
            return;
        }
        if (worldObj.isRemote) {
            spawnParticles();
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        if (blessing == null) {
            Random rand = worldObj != null ? worldObj.rand : new Random();
            blessing = BlessingHelper.getRandomBlessing(rand, true);
        }
        tag.setString(BlessingHelper.NBT_BLESSING, blessing);
        tag.setBoolean("Active", active);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        if (tag.hasKey(BlessingHelper.NBT_BLESSING)) {
            blessing = tag.getString(BlessingHelper.NBT_BLESSING);
            if ("Loner".equals(blessing)) {
                blessing = "Rogue";
            }
            if (blessing != null && !BlessingHelper.isBlessingEnabled(blessing)) {
                if (worldObj != null && !worldObj.isRemote) {
                    Random rand = worldObj.rand;
                    blessing = BlessingHelper.getRandomBlessing(rand, true);
                } else {
                    blessing = null;
                }
            }
        } else {
            if (worldObj != null && !worldObj.isRemote) {
                Random rand = worldObj.rand;
                blessing = BlessingHelper.getRandomBlessing(rand, true);
            }
        }
        active = tag.getBoolean("Active");
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
        if (worldObj != null && worldObj.isRemote) {
            BlockBlessingPillar.updateSourceLight(worldObj, xCoord, yCoord, zCoord);
        }
    }

    @SideOnly(Side.CLIENT)
    private void spawnParticles() {
        if (!active) {
            return;
        }
        for (int i = 0; i < 2; i++) {
            worldObj.spawnParticle(
                    "portal",
                    xCoord + worldObj.rand.nextDouble(),
                    yCoord + worldObj.rand.nextDouble() * 2.0,
                    zCoord + worldObj.rand.nextDouble(),
                    (worldObj.rand.nextDouble() - 0.5) * 2.0,
                    -worldObj.rand.nextDouble(),
                    (worldObj.rand.nextDouble() - 0.5) * 2.0
            );
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }
}
