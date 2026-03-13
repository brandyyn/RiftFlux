/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  baubles.api.BaublesApi
 *  cpw.mods.fml.common.Loader
 *  net.minecraft.block.Block
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.network.NetworkManager
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.server.S35PacketUpdateTileEntity
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.AxisAlignedBB
 *  net.minecraft.util.Vec3
 */
package net.nmccoy.legendgear.block;

import baubles.api.BaublesApi;
import cpw.mods.fml.common.Loader;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.nmccoy.legendgear.LegendGear2;
import net.nmccoy.legendgear.PlayerStarstatsExtension;

public class TileEntityStarwell
extends TileEntity {
    public static int FLIGHT_CHARGE_DURATION = 200;
    public float beamHeight;
    public Block blockmode;
    public int flightCharge = 0;

    public AxisAlignedBB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

    public boolean shouldRenderInPass(int pass) {
        return pass == 1;
    }

    public double getMaxRenderDistanceSquared() {
        return 65536.0;
    }

    public boolean canUpdate() {
        return true;
    }

    public boolean checkFrameAt(int x, int y, int z) {
        return this.worldObj.getBlock(this.xCoord + x, this.yCoord + y, this.zCoord + z) == LegendGear2.starwellFrameBlock;
    }

    public boolean checkFrame() {
        if (!this.checkFrameAt(-1, 1, -1)) {
            return false;
        }
        if (!this.checkFrameAt(0, 1, -1)) {
            return false;
        }
        if (!this.checkFrameAt(1, 1, -1)) {
            return false;
        }
        if (!this.checkFrameAt(-1, 1, 0)) {
            return false;
        }
        if (!this.checkFrameAt(1, 1, 0)) {
            return false;
        }
        if (!this.checkFrameAt(-1, 1, 1)) {
            return false;
        }
        if (!this.checkFrameAt(0, 1, 1)) {
            return false;
        }
        if (!this.checkFrameAt(1, 1, 1)) {
            return false;
        }
        if (!this.checkFrameAt(-1, 0, 0)) {
            return false;
        }
        if (!this.checkFrameAt(0, 0, -1)) {
            return false;
        }
        if (!this.checkFrameAt(1, 0, 0)) {
            return false;
        }
        return this.checkFrameAt(0, 0, 1);
    }

    public Packet getDescriptionPacket() {
        if (this.worldObj != null) {
            NBTTagCompound nbt = new NBTTagCompound();
            nbt.setInteger("flightCharge", this.flightCharge);
            return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 0, nbt);
        }
        return null;
    }

    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
        NBTTagCompound nbt = packet.func_148857_g();
        this.flightCharge = nbt.getInteger("flightCharge");
        this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
    }

    public void updateEntity() {
        boolean ok;
        if (this.getBlockMetadata() == 0) {
            return;
        }
        if (!this.worldObj.isRemote && this.worldObj.rand.nextInt(20) == 0 && !(ok = this.checkFrame())) {
            this.worldObj.setBlock(this.xCoord, this.yCoord, this.zCoord, (Block)LegendGear2.starwellBlock, 0, 3);
        }
        this.blockmode = this.worldObj.getBlock(this.xCoord, this.yCoord + 1, this.zCoord);
        if (this.blockmode == Blocks.air || this.blockmode == LegendGear2.starstoneBlock || this.blockmode == LegendGear2.infusedStarstoneBlock) {
            if (this.worldObj.getWorldTime() % 4L == 0L) {
                float moonAngle = (float)(-Math.cos(this.worldObj.getCelestialAngleRadians(0.0f)));
                float moonScale = 128.0f;
                if (this.blockmode == Blocks.air) {
                    moonScale = 10.0f;
                }
                if (this.worldObj.provider.getMoonPhase(this.worldObj.getWorldTime()) == 0) {
                    moonScale = 128.0f;
                }
                this.beamHeight = moonAngle > 0.0f ? moonAngle * moonScale : 0.0f;
                if (this.blockmode == LegendGear2.infusedStarstoneBlock) {
                    this.beamHeight = 128.0f;
                }
            }
        } else {
            this.beamHeight = 0.0f;
        }
        if (this.blockmode == LegendGear2.skylensBlock) {
            Block powerboost;
            float zoneHeight = 128.0f;
            float cushionHeight = 8.0f;
            float zoneRadius = 1.5f;
            float boostRadius = 1.0f;
            float force = 0.2f;
            float cushionforce = 0.2f;
            float cushionFloat = 0.05f;
            float highestV = 1.5f;
            float lowestV = -1.0f;
            float ceiling = 32.0f;
            --this.flightCharge;
            if (this.flightCharge == 0) {
                this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
            }
            if (this.flightCharge < 0) {
                this.flightCharge = 0;
            }
            if ((powerboost = this.worldObj.getBlock(this.xCoord, this.yCoord + 2, this.zCoord)) == LegendGear2.starstoneBlock) {
                ceiling = 48.0f;
            }
            if (powerboost == LegendGear2.infusedStarstoneBlock) {
                ceiling = 64.0f;
            }
            this.beamHeight = ceiling * 1.5f;
            AxisAlignedBB zone = AxisAlignedBB.getBoundingBox((double)((double)this.xCoord + 0.5 - (double)zoneRadius), (double)(this.yCoord + 2), (double)((double)this.zCoord + 0.5 - (double)zoneRadius), (double)((double)this.xCoord + 0.5 + (double)zoneRadius), (double)((float)(this.yCoord + 1) + zoneHeight), (double)((double)this.zCoord + 0.5 + (double)zoneRadius));
            List entities = this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, zone);
            for (Object obj : entities) {
                if (!(obj instanceof EntityPlayer)) continue;
                EntityPlayer entity = (EntityPlayer)obj;
                if (entity.onGround) continue;
                PlayerStarstatsExtension pse = PlayerStarstatsExtension.get(entity);
                boolean should_launch = false;
                float height = (float)(entity.posY - (double)entity.eyeHeight - zone.minY);
                if (!entity.worldObj.isRemote && height < ceiling + 2.0f && height > ceiling - 7.0f) {
                    ItemStack neckslot;
                    pse.skylensTagCharge = 3;
                    if (Loader.isModLoaded((String)"Baubles") && this.flightCharge > 0 && (neckslot = BaublesApi.getBaubles((EntityPlayer)entity).getStackInSlot(0)) != null && neckslot.getItem() == LegendGear2.charmPendant && (neckslot.getItemDamage() == 1 || neckslot.getItemDamage() == 2)) {
                        should_launch = true;
                    }
                }
                if (pse.getGlide() > 0.0f || should_launch) {
                    float oldGlide = pse.getGlide();
                    float newGlide = Math.max((float)this.yCoord + ceiling + 4.0f, oldGlide);
                    if (newGlide - oldGlide >= 4.0f) {
                        entity.worldObj.playSoundAtEntity((Entity)entity, "legendgear:whirlwind", 0.2f, 1.5f);
                    }
                    pse.setGlide(newGlide);
                    float boost = 0.25f;
                    Vec3 ahead = entity.getLookVec();
                    entity.motionX += ahead.xCoord * (double)boost;
                    entity.motionY += ahead.yCoord * (double)boost;
                    entity.motionZ += ahead.zCoord * (double)boost;
                }
                float verticalFalloff = 1.0f - height / zoneHeight;
                float cushionFalloff = 1.0f - height / cushionHeight;
                float xDist = (float)Math.abs((zone.minX + zone.maxX) / 2.0 - entity.posX);
                float zDist = (float)Math.abs((zone.minZ + zone.maxZ) / 2.0 - entity.posZ);
                float rectDist = Math.max(xDist, zDist);
                float cushionTarget = -0.2f;
                float steer = -entity.rotationPitch / 90.0f + 1.0f;
                float topDeadZone = 0.1f;
                float bottomDeadZone = 0.5f;
                float power = Math.min(steer * (1.0f + topDeadZone + bottomDeadZone) - bottomDeadZone, 1.0f);
                if (power < 0.0f) {
                    power = 0.0f;
                }
                float dig = 2.0f;
                float targetHeight = (ceiling + dig) * power - dig;
                float tension = (targetHeight - height) / zoneHeight;
                float drag = 0.05f;
                float buoyancy = 0.08f;
                entity.motionY += (double)(tension * 0.5f + buoyancy);
                entity.motionY -= (double)drag * entity.motionY;
                entity.fallDistance = 0.0f;
            }
        } else {
            this.flightCharge = 0;
        }
    }
}

