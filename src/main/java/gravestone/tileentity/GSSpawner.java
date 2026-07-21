package gravestone.tileentity;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public abstract class GSSpawner {
   protected TileEntity tileEntity;
   protected int delay;
   protected Entity spawnedMob;

   public GSSpawner(TileEntity tileEntity, int delay) {
      this.tileEntity = tileEntity;
      this.delay = delay;
   }

   public void updateEntity() {
      if (this.canSpawnMobs(this.tileEntity.getWorldObj()) && !this.tileEntity.getWorldObj().difficultySetting.equals(EnumDifficulty.PEACEFUL) && this.anyPlayerInRange()) {
         if (this.tileEntity.getWorldObj().isRemote) {
            this.clientUpdateLogic();
         } else {
            this.serverUpdateLogic();
         }
      }

   }

   protected void updateDelay() {
      this.delay = this.getMinDelay() + this.tileEntity.getWorldObj().rand.nextInt(this.getMaxDelay() - this.getMinDelay());
   }

   protected void setMinDelay() {
      this.delay = this.getMinDelay();
   }

   protected int getNearbyMobsCount() {
      return this.tileEntity.getWorldObj().getEntitiesWithinAABB(this.spawnedMob.getClass(), AxisAlignedBB.getBoundingBox((double)this.tileEntity.xCoord, (double)this.tileEntity.yCoord, (double)this.tileEntity.zCoord, (double)(this.tileEntity.xCoord + 1), (double)(this.tileEntity.yCoord + 1), (double)(this.tileEntity.zCoord + 1)).expand(1.0D, 4.0D, (double)(this.getSpawnRange() * 2))).size();
   }

   protected boolean anyPlayerInRange() {
      return this.tileEntity.getWorldObj().getClosestPlayer((double)this.tileEntity.xCoord + 0.5D, (double)this.tileEntity.yCoord + 0.5D, (double)this.tileEntity.zCoord + 0.5D, (double)this.getPlayerRange()) != null;
   }

   protected abstract boolean canSpawnMobs(World var1);

   protected abstract int getPlayerRange();

   protected abstract int getSpawnRange();

   protected abstract int getMinDelay();

   protected abstract int getMaxDelay();

   protected abstract Entity getMob();

   protected abstract void clientUpdateLogic();

   protected abstract void serverUpdateLogic();
}
