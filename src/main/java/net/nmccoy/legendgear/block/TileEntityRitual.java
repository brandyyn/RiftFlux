/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.network.NetworkManager
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.server.S35PacketUpdateTileEntity
 *  net.minecraft.stats.StatBase
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.AxisAlignedBB
 */
package net.nmccoy.legendgear.block;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.stats.StatBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.nmccoy.legendgear.LegendGear2;
import net.nmccoy.legendgear.ritual.Edge;
import net.nmccoy.legendgear.ritual.RecipeComponent;
import net.nmccoy.legendgear.ritual.RitualGrid;
import net.nmccoy.legendgear.ritual.RitualRecipe;

public class TileEntityRitual
extends TileEntity {
    public boolean active;
    public boolean stable;
    public RitualGrid grid;
    public int[] ticksSinceEmpty = new int[8];
    public int awakeTicks;
    public long[] pulseStartTime = new long[8];
    public boolean dirty;
    public static int PULSE_SEPARATION = 3;
    private boolean gridIsBogus = true;
    public int successEffectTimer = 0;
    public static int SUCCESS_EFFECT_DURATION = 30;
    public boolean successGoing = false;

    public boolean canUpdate() {
        return true;
    }

    public <entity extends Entity> List<entity> targetsInRitual(Class<entity> type) {
        AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox((double)this.xCoord, (double)(this.yCoord + 1), (double)this.zCoord, (double)(this.xCoord + 1), (double)(this.yCoord + 2), (double)(this.zCoord + 1));
        bounds = bounds.expand(0.5, 0.5, 0.5);
        List entities = this.worldObj.getEntitiesWithinAABB(type, bounds);
        return entities;
    }

    public List<EntityItem> itemsInRitual() {
        AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox((double)this.xCoord, (double)(this.yCoord + 1), (double)this.zCoord, (double)(this.xCoord + 1), (double)(this.yCoord + 2), (double)(this.zCoord + 1));
        bounds = bounds.expand(0.5, 0.5, 0.5);
        List entities = this.worldObj.getEntitiesWithinAABB(EntityItem.class, bounds);
        ArrayList<EntityItem> items = new ArrayList<EntityItem>();
        for (Object obj : entities) {
            if (!(obj instanceof EntityItem)) continue;
            EntityItem item = (EntityItem)obj;
            items.add(item);
        }
        return items;
    }

    public void tuning() {
        if (!this.active) {
            return;
        }
        this.worldObj.playSoundEffect((double)this.xCoord + 0.5, (double)this.yCoord + 0.5, (double)this.zCoord + 0.5, "legendgear:fork", 0.5f, 1.0f);
        if (!this.dirty) {
            int edgeNumber = this.grid.inhabitedPoints(this.worldObj).size();
            if (edgeNumber < 8) {
                this.worldObj.playSoundEffect((double)this.xCoord + 0.5, (double)this.yCoord + 0.5, (double)this.zCoord + 0.5, "legendgear:chime." + this.grid.offsets[edgeNumber], 1.0f, 0.5f);
            }
        } else {
            this.worldObj.playSoundEffect((double)this.xCoord + 0.5, (double)this.yCoord + 0.5, (double)this.zCoord + 0.5, "legendgear:chimeSad", 1.0f, 0.5f);
        }
    }

    public AxisAlignedBB getRenderBoundingBox() {
        return AxisAlignedBB.getBoundingBox((double)(this.xCoord - 5), (double)(this.yCoord - 5), (double)(this.zCoord - 5), (double)(this.xCoord + 5), (double)(this.yCoord + 5), (double)(this.zCoord + 5));
    }

    public boolean tryInvoke(EntityPlayer player) {
        if (!this.active) {
            return false;
        }
        this.stable = this.grid.isGridStable(this.worldObj);
        if (this.dirty || !this.stable) {
            return false;
        }
        RitualRecipe ingredients = this.getIngredients();
        boolean success = LegendGear2.ritualManager.attemptInvocation(ingredients, this, player);
        System.out.println("Attempted ritual with:\n" + ingredients + "\nSuccess: " + success);
        if (success) {
            this.worldObj.playSoundEffect((double)this.xCoord + 0.5, (double)this.yCoord + 0.5, (double)this.zCoord + 0.5, "legendgear:ritualSuccess", 1.0f, 1.0f);
            this.successGoing = true;
            this.worldObj.playSoundEffect((double)this.xCoord + 0.5, (double)this.yCoord + 0.5, (double)this.zCoord + 0.5, "legendgear:laser", 0.15f, 1.0f);
            player.addStat((StatBase)LegendGear2.achievementRitualist, 1);
            if (player.worldObj.getWorldTime() % 24000L < 12000L) {
                player.addStat((StatBase)LegendGear2.achievementDayRitual, 1);
            }
        } else {
            this.worldObj.playSoundEffect((double)this.xCoord + 0.5, (double)this.yCoord + 0.5, (double)this.zCoord + 0.5, "legendgear:ritualFail", 1.0f, 1.0f);
            this.worldObj.playSoundEffect((double)this.xCoord + 0.5, (double)this.yCoord + 0.5, (double)this.zCoord + 0.5, "random.fizz", 0.2f, 1.0f);
        }
        this.dirty = true;
        this.grid.edges.clear();
        this.stable = false;
        this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
        return success;
    }

    public void updateEntity() {
        super.updateEntity();
        if (this.grid == null) {
            this.grid = new RitualGrid(this.xCoord, this.yCoord + 1, this.zCoord);
        }
        if (this.awakeTicks == 0 && !this.worldObj.isRemote) {
            if (this.grid.inhabitedPoints(this.worldObj).size() != 0) {
                this.dirty = true;
            }
            this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
        }
        if (this.successGoing) {
            ++this.successEffectTimer;
            if (this.successEffectTimer >= SUCCESS_EFFECT_DURATION) {
                this.successGoing = false;
                this.successEffectTimer = 0;
            }
        }
        if (this.worldObj.getBlock(this.xCoord, this.yCoord - 1, this.zCoord) == LegendGear2.starwellBlock && this.worldObj.getBlockMetadata(this.xCoord, this.yCoord - 1, this.zCoord) == 1) {
            this.active = true;
        } else {
            this.active = false;
            this.awakeTicks = 0;
        }
        if (this.active) {
            for (int i = 0; i < 8; ++i) {
                if (this.grid.blockOnPoint(i, this.worldObj).equals(Blocks.air)) {
                    if (this.ticksSinceEmpty[i] != 0) {
                        this.onGridChange(i, false);
                    }
                    this.ticksSinceEmpty[i] = 0;
                    continue;
                }
                if (this.ticksSinceEmpty[i] == 0) {
                    this.onGridChange(i, true);
                }
                int n = i;
                this.ticksSinceEmpty[n] = this.ticksSinceEmpty[n] + 1;
            }
            ++this.awakeTicks;
        }
    }

    private void onGridChange(int pointChanged, boolean placed) {
        this.stable = this.grid.isGridStable(this.worldObj);
        String blockName = this.grid.blockOnPoint(pointChanged, this.worldObj).getUnlocalizedName();
        if (placed && this.awakeTicks > 1) {
            this.doPlacement(pointChanged);
        }
        int blockCount = this.grid.inhabitedPoints(this.worldObj).size();
        if (this.stable && !this.dirty) {
            this.worldObj.playSoundEffect((double)this.xCoord + 0.5, (double)this.yCoord + 0.5, (double)this.zCoord + 0.5, "legendgear:ritualReady", 1.0f, 1.0f);
        }
        if (!placed) {
            if (blockCount == 0) {
                this.dirty = false;
            } else {
                if (!this.dirty) {
                    this.worldObj.playSoundEffect((double)this.xCoord + 0.5, (double)this.yCoord + 0.5, (double)this.zCoord + 0.5, "legendgear:chimeSad", 0.7f, 1.0f);
                }
                this.dirty = true;
            }
            this.grid.edges.clear();
        }
    }

    public RitualRecipe getIngredients() {
        RitualRecipe recipe = new RitualRecipe();
        if (!this.stable) {
            return recipe;
        }
        HashSet<Integer> counted = new HashSet<Integer>();
        for (Edge e : this.grid.edges) {
            Block block1 = this.grid.blockOnPoint(e.first, this.worldObj);
            int meta1 = this.grid.metaOnPoint(e.first, this.worldObj);
            Block block2 = this.grid.blockOnPoint(e.second, this.worldObj);
            int meta2 = this.grid.metaOnPoint(e.second, this.worldObj);
            if (block1 != Blocks.air && block2 != Blocks.air) {
                RecipeComponent ingredient = new RecipeComponent(block1, meta1, block2, meta2);
                recipe.add(ingredient);
            }
            counted.add(e.first);
            counted.add(e.second);
        }
        for (int i = 0; i < 8; ++i) {
            if (counted.contains(i)) continue;
            Block block = this.grid.blockOnPoint(i, this.worldObj);
            int meta = this.grid.metaOnPoint(i, this.worldObj);
            if (block == Blocks.air) continue;
            recipe.add(new RecipeComponent(block, meta));
        }
        return recipe;
    }

    public Block focusBlock() {
        return this.worldObj.getBlock(this.xCoord, this.yCoord + 1, this.zCoord);
    }

    public void clearFocusBlock() {
        this.worldObj.setBlockToAir(this.xCoord, this.yCoord + 1, this.zCoord);
    }

    public void doPlacement(int point) {
        float x = (float)this.grid.places[point].chunkPosX + 0.5f;
        float y = (float)this.grid.places[point].chunkPosY + 0.5f;
        float z = (float)this.grid.places[point].chunkPosZ + 0.5f;
        if (!this.dirty) {
            this.pulseStartTime[point] = this.worldObj.getTotalWorldTime();
            int edgeNumber = this.grid.inhabitedPoints(this.worldObj).size() - 1;
            this.worldObj.playSoundEffect((double)x + 0.5, (double)y + 0.5, (double)z + 0.5, "legendgear:chime." + this.grid.offsets[edgeNumber], 0.5f, 1.0f);
            int destination = this.grid.makeEdgeFrom(point, edgeNumber);
            this.pulseStartTime[destination] = this.worldObj.getTotalWorldTime() + (long)PULSE_SEPARATION;
        }
        this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
    }

    public Packet getDescriptionPacket() {
        if (this.worldObj != null) {
            NBTTagCompound nbt = new NBTTagCompound();
            this.writeEdgesToTag(nbt);
            nbt.setBoolean("dirty", this.dirty);
            nbt.setBoolean("successGoing", this.successGoing);
            return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 0, nbt);
        }
        return null;
    }

    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
        NBTTagCompound nbt = packet.func_148857_g();
        this.readEdgesFromTag(nbt);
        this.dirty = nbt.getBoolean("dirty");
        this.successGoing = nbt.getBoolean("successGoing");
        this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
    }

    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setIntArray("ticksSinceEmpty", this.ticksSinceEmpty);
        tag.setInteger("awakeTicks", this.awakeTicks);
        tag.setBoolean("dirty", this.dirty);
        if (this.grid != null) {
            this.writeEdgesToTag(tag);
        }
    }

    private void writeEdgesToTag(NBTTagCompound tag) {
        if (this.grid == null) {
            return;
        }
        ArrayList<Integer> edgeList = new ArrayList<Integer>();
        for (Edge edge : this.grid.edges) {
            edgeList.add(edge.first);
            edgeList.add(edge.second);
        }
        int[] edgeInts = new int[edgeList.size()];
        for (int i = 0; i < edgeInts.length; ++i) {
            edgeInts[i] = (Integer)edgeList.get(i);
        }
        tag.setIntArray("edges", edgeInts);
    }

    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.ticksSinceEmpty = tag.getIntArray("ticksSinceEmpty");
        if (this.ticksSinceEmpty.length == 0) {
            this.ticksSinceEmpty = new int[8];
        }
        this.awakeTicks = tag.getInteger("awakeTicks");
        this.dirty = tag.getBoolean("dirty");
        this.readEdgesFromTag(tag);
    }

    private void readEdgesFromTag(NBTTagCompound tag) {
        int[] edgeInts = tag.getIntArray("edges");
        if (this.grid == null) {
            this.grid = new RitualGrid(this.xCoord, this.yCoord + 1, this.zCoord);
        }
        this.grid.clearEdges();
        for (int i = 0; i < edgeInts.length; i += 2) {
            this.grid.edges.add(new Edge(edgeInts[i], edgeInts[i + 1]));
        }
    }
}

