package gravestone.tileentity;

import gravestone.block.enums.EnumGraves;
import gravestone.config.GraveStoneConfig;
import gravestone.core.GSMobSpawn;
import gravestone.core.TimeHelper;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class GSGraveStoneSpawn extends GSSpawner {
   private static final int BASE_DELAY = 600;
   private static final int PLAYER_RANGE = 35;
   private static final int MIN_DELAY = 500;
   private boolean getNewMob = true;
   private static final int MAX_NEARBY_ENTITIES = 3;
   private static final int SPAWN_RANGE = 1;

   public GSGraveStoneSpawn(TileEntity tileEntity) {
      super(tileEntity, 600);
   }

   protected void clientUpdateLogic() {
   }

   protected void serverUpdateLogic() {
      if (this.delay == -1) {
         this.updateDelay();
      }

      if (this.delay > 0) {
         --this.delay;
      } else {
         if (this.getNewMob) {
            this.spawnedMob = GSMobSpawn.getMobEntity(this.tileEntity.getWorldObj(), EnumGraves.getByID(((TileEntityGSGraveStone)this.tileEntity).graveType), this.tileEntity.xCoord, this.tileEntity.yCoord, this.tileEntity.zCoord);
            if (this.spawnedMob == null) {
               return;
            }

            this.getNewMob = false;
         }

         int nearbyEntitiesCount = this.tileEntity.getWorldObj().getEntitiesWithinAABB(this.spawnedMob.getClass(), AxisAlignedBB.getBoundingBox((double)this.tileEntity.xCoord, (double)this.tileEntity.yCoord, (double)this.tileEntity.zCoord, (double)(this.tileEntity.xCoord + 1), (double)(this.tileEntity.yCoord + 1), (double)(this.tileEntity.zCoord + 1)).expand(1.0D, 4.0D, 2.0D)).size();
         if (nearbyEntitiesCount >= 3) {
            this.updateDelay();
         } else {
            if (GSMobSpawn.checkChance(this.tileEntity.getWorldObj().rand) && GSMobSpawn.spawnMob(this.tileEntity.getWorldObj(), this.spawnedMob, (double)this.tileEntity.xCoord, (double)this.tileEntity.yCoord, (double)this.tileEntity.zCoord, true)) {
               this.getNewMob = true;
               ((TileEntityGSGraveStone)this.tileEntity).markMobSpawnedThisNight();
            }

            this.updateDelay();
         }
      }
   }

   protected boolean canSpawnMobs(World world) {
      TileEntityGSGraveStone grave = (TileEntityGSGraveStone)this.tileEntity;
      return !grave.isPlayerPlaced() && !grave.hasSpawnedMobThisNight() && TimeHelper.isGraveSpawnTime();
   }

   protected int getPlayerRange() {
      return 35;
   }

   protected int getSpawnRange() {
      return 1;
   }

   protected int getMinDelay() {
      return 500;
   }

   protected int getMaxDelay() {
      return GraveStoneConfig.graveSpawnRate;
   }

   protected Entity getMob() {
      return GSMobSpawn.getMobEntity(this.tileEntity.getWorldObj(), EnumGraves.getByID(((TileEntityGSGraveStone)this.tileEntity).graveType), this.tileEntity.xCoord, this.tileEntity.yCoord, this.tileEntity.zCoord);
   }
}
