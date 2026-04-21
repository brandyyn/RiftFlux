package com.voidsrift.riftflux.duckling;

import com.voidsrift.riftflux.ModConfig;
import net.minecraft.block.material.Material;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

public class EntityAIQuacklingFishing extends EntityAIBase {
    private static final int SEARCH_RADIUS = 10;
    private static final double FISHING_SPOT_REACHED_DISTANCE_SQ = 0.36D;

    private final EntityQuackling quackling;
    private int targetX;
    private int targetY;
    private int targetZ;
    private int waterX;
    private int waterY;
    private int waterZ;
    private int repathTicks;
    private int catchTicks;
    private int targetCatches;
    private int catchesThisSession;
    private int lostTargetTicks;
    private boolean hasTarget;
    private boolean sessionDone;
    private boolean resumeSavedSession;

    public EntityAIQuacklingFishing(EntityQuackling quackling) {
        this.quackling = quackling;
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        if (this.quackling.shouldResumeFishingSession()) {
            this.resumeSavedSession = true;
            return true;
        }
        this.resumeSavedSession = false;
        if (!this.quackling.shouldStartFishingSessionNow()) {
            return false;
        }
        this.hasTarget = this.findFishingSpot();
        if (!this.hasTarget) {
            this.quackling.deferFishingStartCheck();
        }
        return this.hasTarget;
    }

    @Override
    public boolean continueExecuting() {
        return !this.sessionDone
                && this.quackling.isEntityAlive()
                && !this.quackling.isChild()
                && this.catchesThisSession < this.targetCatches;
    }

    @Override
    public void startExecuting() {
        this.repathTicks = 0;
        this.sessionDone = false;
        if (this.resumeSavedSession) {
            this.hasTarget = this.quackling.savedFishingSessionHasTarget();
            this.targetX = this.quackling.getSavedFishingTargetX();
            this.targetY = this.quackling.getSavedFishingTargetY();
            this.targetZ = this.quackling.getSavedFishingTargetZ();
            this.waterX = this.quackling.getSavedFishingWaterX();
            this.waterY = this.quackling.getSavedFishingWaterY();
            this.waterZ = this.quackling.getSavedFishingWaterZ();
            this.catchTicks = Math.max(1, this.quackling.getSavedFishingCatchTicks());
            this.targetCatches = Math.max(1, this.quackling.getSavedFishingTargetCatches());
            this.catchesThisSession = Math.max(0, this.quackling.getSavedFishingCatchesThisSession());
            this.lostTargetTicks = Math.max(0, this.quackling.getSavedFishingLostTargetTicks());
        } else {
            this.catchTicks = this.nextCatchDelay();
            this.targetCatches = this.quackling.getRandomFishingCatchTarget();
            this.catchesThisSession = 0;
            this.lostTargetTicks = 0;
            this.quackling.beginFishingSession();
        }
        this.persistSession();
        this.quackling.setFishingActive(false);
    }

    @Override
    public void resetTask() {
        if (this.sessionDone || !this.quackling.isEntityAlive() || this.catchesThisSession >= this.targetCatches) {
            this.quackling.clearFishingSession();
        } else {
            this.persistSession();
        }
        this.quackling.getNavigator().clearPathEntity();
        this.hasTarget = false;
        this.lostTargetTicks = 0;
        this.sessionDone = false;
        this.resumeSavedSession = false;
        this.quackling.setFishingActive(false);
    }

    @Override
    public void updateTask() {
        if (!this.hasTarget || !this.isValidFishingSpot(this.targetX, this.targetY, this.targetZ)) {
            this.hasTarget = this.findFishingSpot();
            this.repathTicks = 0;
            this.quackling.setFishingActive(false);
            this.persistSession();
        }

        if (!this.hasTarget) {
            this.quackling.motionX *= 0.8D;
            this.quackling.motionZ *= 0.8D;
            this.quackling.setFishingActive(false);
            if (++this.lostTargetTicks >= this.getBumpRecoveryTicks()) {
                this.sessionDone = true;
                this.quackling.clearFishingSession();
            } else {
                this.persistSession();
            }
            return;
        }
        this.lostTargetTicks = 0;

        double dx = this.quackling.posX - ((double)this.targetX + 0.5D);
        double dz = this.quackling.posZ - ((double)this.targetZ + 0.5D);
        if (dx * dx + dz * dz > FISHING_SPOT_REACHED_DISTANCE_SQ) {
            this.quackling.setFishingActive(false);
            if (--this.repathTicks <= 0) {
                this.repathTicks = 20 + this.quackling.getRNG().nextInt(20);
                this.quackling.getNavigator().tryMoveToXYZ((double)this.targetX + 0.5D, this.targetY, (double)this.targetZ + 0.5D, 1.0D);
            }
            this.persistSession();
            return;
        }

        this.quackling.getNavigator().clearPathEntity();
        this.quackling.motionX *= 0.5D;
        this.quackling.motionZ *= 0.5D;
        this.quackling.getLookHelper().setLookPosition((double)this.waterX + 0.5D, (double)this.waterY + 0.2D, (double)this.waterZ + 0.5D, 12.0F, 12.0F);
        this.quackling.setFishingActive(true);
        this.tryCatchFish();
        this.persistSession();
    }

    private void tryCatchFish() {
        if (this.quackling.worldObj.isRemote) {
            return;
        }
        if (--this.catchTicks > 0) {
            return;
        }

        this.catchTicks = this.nextCatchDelay();
        ItemStack fish = this.createCaughtFish();
        this.spawnCaughtFish(fish);
        ++this.catchesThisSession;
        this.quackling.playSound("random.splash", 0.4F, 1.0F + (this.quackling.getRNG().nextFloat() - this.quackling.getRNG().nextFloat()) * 0.2F);
        this.quackling.worldObj.setEntityState(this.quackling, (byte)14);
        if (this.catchesThisSession >= this.targetCatches) {
            this.sessionDone = true;
            this.quackling.setFishingActive(false);
            this.quackling.clearFishingSession();
        }
    }

    private int nextCatchDelay() {
        int min = Math.max(1, ModConfig.getQuacklingFishingMinCatchDelayTicks());
        int max = Math.max(min, ModConfig.getQuacklingFishingMaxCatchDelayTicks());
        return min + this.quackling.getRNG().nextInt(max - min + 1);
    }

    private int getBumpRecoveryTicks() {
        return Math.max(20, ModConfig.ducklingQuacklingFishingBumpRecoveryTicks);
    }

    private void persistSession() {
        if (!this.quackling.worldObj.isRemote && !this.sessionDone && this.targetCatches > this.catchesThisSession) {
            this.quackling.saveFishingSession(this.hasTarget, this.targetX, this.targetY, this.targetZ,
                    this.waterX, this.waterY, this.waterZ, this.catchTicks, this.targetCatches,
                    this.catchesThisSession, this.lostTargetTicks);
        }
    }

    private ItemStack createCaughtFish() {
        int roll = this.quackling.getRNG().nextInt(100);
        int meta = roll < 85 ? 0 : roll < 95 ? 1 : roll < 98 ? 2 : 3;
        return new ItemStack(Items.fish, 1, meta);
    }

    private void spawnCaughtFish(ItemStack fish) {
        EntityItem item = new EntityItem(this.quackling.worldObj,
                (double)this.waterX + 0.5D,
                (double)this.waterY + 0.8D,
                (double)this.waterZ + 0.5D,
                fish);
        item.motionX = (this.quackling.posX - item.posX) * 0.08D;
        item.motionY = 0.25D;
        item.motionZ = (this.quackling.posZ - item.posZ) * 0.08D;
        this.quackling.worldObj.spawnEntityInWorld(item);
    }

    private boolean findFishingSpot() {
        int baseX = MathHelper.floor_double(this.quackling.posX);
        int baseY = MathHelper.floor_double(this.quackling.boundingBox.minY);
        int baseZ = MathHelper.floor_double(this.quackling.posZ);
        double bestDistance = Double.MAX_VALUE;
        boolean found = false;

        for (int radius = 0; radius <= SEARCH_RADIUS; radius++) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (Math.abs(dx) != radius && Math.abs(dz) != radius) {
                        continue;
                    }
                    for (int dy = -3; dy <= 3; dy++) {
                        int x = baseX + dx;
                        int y = baseY + dy;
                        int z = baseZ + dz;
                        if (!this.isValidFishingSpot(x, y, z)) {
                            continue;
                        }
                        double distance = this.quackling.getDistanceSq((double)x + 0.5D, y, (double)z + 0.5D);
                        if (distance < bestDistance) {
                            bestDistance = distance;
                            this.targetX = x;
                            this.targetY = y;
                            this.targetZ = z;
                            this.cacheAdjacentWater(x, y, z);
                            found = true;
                        }
                    }
                }
            }
            if (found) {
                return true;
            }
        }
        return false;
    }

    private boolean isValidFishingSpot(int x, int y, int z) {
        Material feet = this.quackling.worldObj.getBlock(x, y, z).getMaterial();
        Material head = this.quackling.worldObj.getBlock(x, y + 1, z).getMaterial();
        Material floor = this.quackling.worldObj.getBlock(x, y - 1, z).getMaterial();
        return !feet.blocksMovement()
                && !feet.isLiquid()
                && !head.blocksMovement()
                && !head.isLiquid()
                && floor.blocksMovement()
                && !floor.isLiquid()
                && this.cacheAdjacentWater(x, y, z);
    }

    private boolean cacheAdjacentWater(int x, int y, int z) {
        int[][] offsets = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        for (int i = 0; i < offsets.length; i++) {
            int wx = x + offsets[i][0];
            int wz = z + offsets[i][1];
            if (this.isWater(wx, y, wz)) {
                this.waterX = wx;
                this.waterY = y;
                this.waterZ = wz;
                return true;
            }
            if (this.isWater(wx, y - 1, wz)) {
                this.waterX = wx;
                this.waterY = y - 1;
                this.waterZ = wz;
                return true;
            }
        }
        return false;
    }

    private boolean isWater(int x, int y, int z) {
        return this.quackling.worldObj.getBlock(x, y, z).getMaterial() == Material.water;
    }
}
