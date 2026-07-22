/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.FMLLog
 *  org.apache.logging.log4j.Level
 */
package de.sanandrew.mods.claysoldiers.tileentity;

import cpw.mods.fml.common.FMLLog;
import de.sanandrew.core.manpack.util.EnumNbtTypes;
import de.sanandrew.core.manpack.util.helpers.SAPUtils;
import de.sanandrew.core.manpack.util.javatuples.Sextet;
import de.sanandrew.mods.claysoldiers.entity.EntityClayMan;
import de.sanandrew.mods.claysoldiers.entity.projectile.ISoldierProjectile;
import de.sanandrew.mods.claysoldiers.item.IMountDoll;
import de.sanandrew.mods.claysoldiers.item.ItemClayManDoll;
import de.sanandrew.mods.claysoldiers.network.packet.EnumParticleFx;
import de.sanandrew.mods.claysoldiers.util.BugfixHelper;
import de.sanandrew.mods.claysoldiers.util.ClaySoldiersMod;
import de.sanandrew.mods.claysoldiers.util.RegistryItems;
import de.sanandrew.mods.claysoldiers.util.soldier.ClaymanTeam;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.ASoldierUpgrade;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.IThrowableUpgrade;
import de.sanandrew.mods.claysoldiers.util.soldier.upgrade.SoldierUpgrades;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;

public class TileEntityClayNexus
extends TileEntity
implements IInventory {
    public static final int SOLDIER_SLOT = 0;
    public static final int THROWABLE_SLOT = 1;
    public static final int MOUNT_SLOT = 2;
    public boolean isActive = false;
    private boolean p_prevRsPowerState = false;
    public int spawnSoldierInterval = 40;
    public int spawnThrowableInterval = 30;
    public int soldierSpawnCount = 10;
    public int mountSpawnCount = 10;
    public int maxSoldierCount = 10;
    public long ticksActive = 0L;
    public float spinAngle = 0.0f;
    public float prevSpinAngle = 0.0f;
    protected String customName;
    private ItemStack[] p_upgradeItems = new ItemStack[36];
    private ItemStack p_soldierSlot;
    private ItemStack p_throwableSlot;
    private ItemStack p_mountSlot;
    private int p_spawningSoldierCounter = 0;
    private int p_prevSpawningSoldierCounter = 0;
    private float p_health = 20.0f;
    private float p_maxHealth = 20.0f;
    private ClaymanTeam p_tempClayTeam = ClaymanTeam.NULL_TEAM;
    private Class<? extends ISoldierProjectile<? extends EntityThrowable>> p_tempThrowableCls = null;
    private AxisAlignedBB p_searchArea;
    private AxisAlignedBB p_damageArea;

    @Override
    public void updateEntity() {
        boolean isRsPowered;
        if (this.p_searchArea == null || this.p_damageArea == null) {
            this.p_searchArea = AxisAlignedBB.getBoundingBox((double)this.xCoord - 63.0, (double)this.yCoord - 63.0, (double)this.zCoord - 63.0, (double)this.xCoord + 64.0, (double)this.yCoord + 64.0, (double)this.zCoord + 64.0);
            this.p_damageArea = AxisAlignedBB.getBoundingBox((double)this.xCoord + 0.1, (double)this.yCoord + 0.1, (double)this.zCoord + 0.1, (double)this.xCoord + 0.9, (double)this.yCoord + 0.9, (double)this.zCoord + 0.9);
        }
        super.updateEntity();
        boolean bl = isRsPowered = this.worldObj.isBlockIndirectlyGettingPowered(this.xCoord, this.yCoord, this.zCoord) || this.worldObj.getIndirectPowerLevelTo(this.xCoord - 1, this.yCoord, this.zCoord, 1) > 0 || this.worldObj.getIndirectPowerLevelTo(this.xCoord, this.yCoord, this.zCoord + 1, 1) > 0 || this.worldObj.getIndirectPowerLevelTo(this.xCoord, this.yCoord, this.zCoord - 1, 1) > 0 || this.worldObj.getIndirectPowerLevelTo(this.xCoord + 1, this.yCoord, this.zCoord, 1) > 0;
        if (!this.worldObj.isRemote && !this.p_prevRsPowerState && isRsPowered) {
            this.isActive = !this.isActive;
            this.markDirty();
            this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
        }
        this.p_prevRsPowerState = isRsPowered;
        if (this.isActive) {
            if (this.p_health <= 0.0f) {
                this.isActive = false;
            } else {
                ++this.ticksActive;
            }
            if (!this.worldObj.isRemote && this.p_health > 0.0f) {
                List<EntityClayMan> clayMen;
                if (this.ticksActive % 20L == 0L) {
                    int dmgEnemies = this.countDamagingEnemies();
                    if (dmgEnemies > 0) {
                        float healthDamage = 0.5f;
                        if (dmgEnemies > 1) {
                            healthDamage = 0.125f * (float)dmgEnemies;
                        }
                        this.p_health -= healthDamage;
                        if (this.p_health < 0.0f) {
                            this.p_health = 0.0f;
                        }
                        this.markDirty();
                        this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
                    }
                    List<EntityClayMan> enemies = this.getEnemies(true);
                    for (EntityClayMan dalek : enemies) {
                        if (dalek.hasPath()) continue;
                        dalek.setPathToEntity(BugfixHelper.getEntityPathToXYZ(this.worldObj, dalek, this.xCoord, this.yCoord, this.zCoord, 64.0f, true, false, false, true));
                    }
                }
                if (this.ticksActive % (long)this.spawnThrowableInterval == 0L && this.p_throwableSlot != null && (clayMen = this.getEnemies(true)).size() > 0) {
                    EntityClayMan target = clayMen.get(SAPUtils.RNG.nextInt(clayMen.size()));
                    double deltaX = target.posX - (double)this.xCoord + 0.5;
                    double deltaZ = target.posZ - (double)this.zCoord + 0.5;
                    try {
                        ISoldierProjectile<? extends EntityThrowable> projectile = this.p_tempThrowableCls.getConstructor(World.class, Double.TYPE, Double.TYPE, Double.TYPE).newInstance(this.worldObj, Float.valueOf((float)this.xCoord + 0.5f), Float.valueOf((float)this.yCoord + 0.875f), Float.valueOf((float)this.zCoord + 0.5f));
                        projectile.initProjectile(target, true, this.p_tempClayTeam.getTeamName());
                        EntityThrowable throwable = projectile.getProjectileEntity();
                        double d2 = target.posY + (double)target.getEyeHeight() - 0.10000000298023223 - throwable.posY;
                        float f1 = MathHelper.sqrt_double(deltaX * deltaX + deltaZ * deltaZ) * 0.2f;
                        this.worldObj.spawnEntityInWorld(throwable);
                        throwable.setThrowableHeading(deltaX, d2 + (double)f1, deltaZ, 0.6f, 12.0f);
                    }
                    catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
                        FMLLog.log((String)"ClaySoldiers", (Level)Level.ERROR, (String)"%1$s cannot be instantiated! %1$s is not thrown to target!", (Object[])new Object[]{this.p_tempThrowableCls.getName()});
                        e.printStackTrace();
                    }
                }
                if (this.p_soldierSlot != null) {
                    if (this.ticksActive % (long)this.spawnSoldierInterval == 0L && this.p_spawningSoldierCounter <= 0) {
                        this.p_prevSpawningSoldierCounter = this.p_spawningSoldierCounter = Math.min(this.soldierSpawnCount, this.maxSoldierCount - this.countTeammates());
                    }
                    if (this.ticksActive % 5L == 0L && this.p_spawningSoldierCounter > 0) {
                        this.p_spawningSoldierCounter = Math.min(this.p_prevSpawningSoldierCounter, this.maxSoldierCount - this.countTeammates());
                        this.p_prevSpawningSoldierCounter = this.p_spawningSoldierCounter - 1;
                        if (this.p_spawningSoldierCounter > 0) {
                            ItemClayManDoll.spawnClayMan((World)this.worldObj, (String)this.p_tempClayTeam.getTeamName(), (double)((double)((float)this.xCoord + 0.5f)), (double)((double)this.yCoord + 0.2), (double)((double)((float)this.zCoord + 0.5f))).nexusSpawn = true;
                        }
                    }
                }
            }
        }
        if (this.worldObj.isRemote) {
            this.prevSpinAngle = this.spinAngle;
            if (this.isActive && this.p_health > 0.0f) {
                this.spinAngle += 4.0f;
                SAPUtils.RGBAValues rgba = SAPUtils.getRgbaFromColorInt(ItemClayManDoll.getTeam(this.p_soldierSlot).getTeamColor());
                ClaySoldiersMod.proxy.spawnParticles(EnumParticleFx.FX_NEXUS, Sextet.with(Double.valueOf(this.xCoord), Double.valueOf(this.yCoord), Double.valueOf(this.zCoord), Float.valueOf((float)rgba.getRed() / 255.0f), Float.valueOf((float)rgba.getGreen() / 255.0f), Float.valueOf((float)rgba.getBlue() / 255.0f)));
            } else if (this.spinAngle % 90.0f != 0.0f) {
                this.spinAngle += 2.0f;
            }
            if (this.spinAngle >= 360.0f) {
                this.prevSpinAngle = -1.0f;
                this.spinAngle = 0.0f;
            }
        }
    }

    public boolean canUpdate() {
        return true;
    }

    @Override
    public int getSizeInventory() {
        return 39;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        switch (slot) {
            case 0: {
                return this.p_soldierSlot;
            }
            case 1: {
                return this.p_throwableSlot;
            }
            case 2: {
                return this.p_mountSlot;
            }
        }
        return this.p_upgradeItems[slot - 3];
    }

    @Override
    public ItemStack decrStackSize(int slot, int reduceAmount) {
        ItemStack stack = this.getStackInSlot(slot);
        if (stack != null) {
            if (stack.stackSize <= reduceAmount) {
                this.setStackInSlot(slot, null);
                this.markDirty();
                return stack;
            }
            ItemStack splitStack = stack.splitStack(reduceAmount);
            if (stack.stackSize == 0) {
                this.setStackInSlot(slot, null);
            }
            this.markDirty();
            return splitStack;
        }
        return null;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        ItemStack stack = this.getStackInSlot(slot);
        if (stack != null) {
            this.setStackInSlot(slot, null);
            return stack;
        }
        return null;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack newStack) {
        this.setStackInSlot(slot, newStack);
        if (newStack != null && newStack.stackSize > this.getInventoryStackLimit()) {
            newStack.stackSize = this.getInventoryStackLimit();
        }
        this.markDirty();
    }

    @Override
    public String getInventoryName() {
        return this.hasCustomInventoryName() ? this.customName : "claysoldiers:container.nexus";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return this.customName != null;
    }

    @Override
    public int getInventoryStackLimit() {
        return 1;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) == this && player.getDistanceSq((double)this.xCoord + 0.5, (double)this.yCoord + 0.5, (double)this.zCoord + 0.5) <= 64.0;
    }

    @Override
    public void openInventory() {
    }

    @Override
    public void closeInventory() {
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        switch (slot) {
            case 0: {
                return stack == null || stack.getItem() == RegistryItems.dollSoldier;
            }
            case 1: {
                return stack == null || SoldierUpgrades.getUpgrade(stack) instanceof IThrowableUpgrade;
            }
            case 2: {
                return stack == null || stack.getItem() instanceof IMountDoll;
            }
        }
        return stack == null || SoldierUpgrades.getUpgrade(stack) != null;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.p_upgradeItems = new ItemStack[36];
        NBTTagList nbttaglist = nbt.getTagList("items", EnumNbtTypes.NBT_COMPOUND.ordinal());
        for (int i = 0; i < nbttaglist.tagCount(); ++i) {
            NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
            int slot = nbttagcompound1.getByte("slot") & 0xFF;
            if (slot < 0 || slot >= this.getSizeInventory()) continue;
            this.setStackInSlot(slot, ItemStack.loadItemStackFromNBT(nbttagcompound1));
        }
        this.isActive = nbt.getBoolean("active");
        this.p_health = nbt.getFloat("health");
        this.p_maxHealth = nbt.getFloat("maxHealth");
        this.spawnSoldierInterval = nbt.getInteger("spawnSldInterval");
        this.spawnThrowableInterval = nbt.getInteger("spawnThrwInterval");
        this.soldierSpawnCount = nbt.getInteger("spawnSldCount");
        this.mountSpawnCount = nbt.getInteger("spawnMntCount");
        this.maxSoldierCount = nbt.getInteger("maxSldCount");
        if (nbt.hasKey("customName")) {
            this.customName = nbt.getString("customName");
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        NBTTagList nbttaglist = new NBTTagList();
        for (int slot = 0; slot < this.getSizeInventory(); ++slot) {
            ItemStack slotStack = this.getStackInSlot(slot);
            if (slotStack == null) continue;
            NBTTagCompound itemTag = new NBTTagCompound();
            itemTag.setByte("slot", (byte)slot);
            slotStack.writeToNBT(itemTag);
            nbttaglist.appendTag(itemTag);
        }
        nbt.setTag("items", nbttaglist);
        nbt.setBoolean("active", this.isActive);
        nbt.setFloat("health", this.p_health);
        nbt.setFloat("maxHealth", this.p_maxHealth);
        nbt.setInteger("spawnSldInterval", this.spawnSoldierInterval);
        nbt.setInteger("spawnThrwInterval", this.spawnThrowableInterval);
        nbt.setInteger("spawnSldCount", this.soldierSpawnCount);
        nbt.setInteger("spawnMntCount", this.mountSpawnCount);
        nbt.setInteger("maxSldCount", this.maxSoldierCount);
        if (this.hasCustomInventoryName()) {
            nbt.setString("customName", this.customName);
        }
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound nbt = new NBTTagCompound();
        this.writeToNBT(nbt);
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, nbt);
    }

    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        this.readFromNBT(pkt.func_148857_g());
        this.worldObj.notifyBlockChange(this.xCoord, this.yCoord, this.zCoord, this.blockType);
    }

    public float getHealth() {
        return this.p_health;
    }

    public float getMaxHealth() {
        return this.p_maxHealth;
    }

    public void heal(float amount) {
        this.p_health += amount;
        if (this.p_health > this.p_maxHealth) {
            this.p_health = this.p_maxHealth;
        }
    }

    public void repair() {
        this.p_health = this.p_maxHealth;
    }

    public boolean canEntityBeSeen(Entity target) {
        return this.worldObj.func_147447_a(Vec3.createVectorHelper((double)this.xCoord + 0.5, (double)this.yCoord + 0.9, (double)this.zCoord + 0.5), Vec3.createVectorHelper(target.posX, target.posY + (double)target.getEyeHeight(), target.posZ), false, true, false) == null;
    }

    private int countTeammates() {
        List<EntityClayMan> soldiers = this.worldObj.getEntitiesWithinAABB(EntityClayMan.class, this.p_searchArea);
        int cnt = 0;
        for (EntityClayMan dodger : soldiers) {
            if (!dodger.getClayTeam().equals(this.p_tempClayTeam.getTeamName())) continue;
            ++cnt;
        }
        return cnt;
    }

    private List<EntityClayMan> getEnemies(boolean mustBeSeen) {
        List<EntityClayMan> soldiers = this.worldObj.getEntitiesWithinAABB(EntityClayMan.class, this.p_searchArea);
        Iterator<EntityClayMan> iterator = soldiers.iterator();
        while (iterator.hasNext()) {
            EntityClayMan roomie = iterator.next();
            if (!roomie.getClayTeam().equals(this.p_tempClayTeam.getTeamName()) && (!mustBeSeen || this.canEntityBeSeen(roomie))) continue;
            iterator.remove();
        }
        return soldiers;
    }

    private int countDamagingEnemies() {
        List<EntityClayMan> soldiers = this.worldObj.getEntitiesWithinAABB(EntityClayMan.class, this.p_damageArea);
        int cnt = 0;
        for (EntityClayMan dodger : soldiers) {
            if (dodger.getClayTeam().equals(this.p_tempClayTeam.getTeamName())) continue;
            ++cnt;
        }
        return cnt;
    }

    private void setStackInSlot(int slot, ItemStack stack) {
        switch (slot) {
            case 0: {
                this.p_soldierSlot = stack;
                this.p_tempClayTeam = ItemClayManDoll.getTeam(stack);
                break;
            }
            case 1: {
                ASoldierUpgrade upg = SoldierUpgrades.getUpgrade(stack);
                this.p_tempThrowableCls = stack != null && upg instanceof IThrowableUpgrade ? ((IThrowableUpgrade)((Object)upg)).getThrowableClass() : null;
                this.p_throwableSlot = stack;
                break;
            }
            case 2: {
                this.p_mountSlot = stack;
                break;
            }
            default: {
                this.p_upgradeItems[slot - 3] = stack;
            }
        }
    }
}
