package gravestone.tileentity;

import gravestone.block.BlockGSSpawner;
import gravestone.block.enums.EnumSpawner;
import gravestone.core.GSMobSpawn;
import gravestone.core.logger.GSLogger;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class GSMobSpawner extends GSSpawner {
   private static final int BASE_DELAY = 60;
   private static final int MIN_DELAY = 600;
   private static final int MAX_DELAY = 800;
   private static final int BOSS_PLAYER_RANGE = 8;
   private static final int MOB_PLAYER_RANGE = 16;
   private static final int SPAWN_EFFECTS_DELAY = 20;
   private static final float MAX_LIGHT_VALUE = 0.46F;
   private EnumSpawner spawnerType = null;

   public GSMobSpawner(TileEntity tileEntity) {
      super(tileEntity, 60);
   }

   protected void clientUpdateLogic() {
   }

   protected void serverUpdateLogic() {
      --this.delay;
      if (this.delay <= 0) {
         EntityLiving entity = (EntityLiving)this.getMob();
         if (entity == null) {
            GSLogger.logError("Spanwer mob get 'null' as mob!!!");
         } else {
            double x = (double)this.tileEntity.xCoord + 0.5D;
            double y = (double)this.tileEntity.yCoord;
            double z = (double)this.tileEntity.zCoord + 0.5D;
            entity.setLocationAndAngles(x, y, z, this.tileEntity.getWorldObj().rand.nextFloat() * 360.0F, 0.0F);
            if (this.isBossSpawner()) {
               this.tileEntity.getWorldObj().removeTileEntity(this.tileEntity.xCoord, this.tileEntity.yCoord, this.tileEntity.zCoord);
               this.tileEntity.getWorldObj().setBlock(this.tileEntity.xCoord, this.tileEntity.yCoord, this.tileEntity.zCoord, Blocks.air);
               this.tileEntity.getWorldObj().spawnEntityInWorld(entity);
            } else if (this.tileEntity.getWorldObj().getLightBrightness(this.tileEntity.xCoord, this.tileEntity.yCoord, this.tileEntity.zCoord) <= 0.46F) {
               this.tileEntity.getWorldObj().spawnEntityInWorld(entity);
            }
         }

         this.updateDelay();
      }

   }

   private boolean isBossSpawner() {
      return BlockGSSpawner.BOSS_SPAWNERS.contains((byte)this.getSpawnerType().ordinal());
   }

   private EnumSpawner getSpawnerType() {
      if (this.spawnerType == null) {
         if (this.tileEntity.getWorldObj() == null) {
            GSLogger.logError("Spawner te worldobj is null !!!!!");
            return EnumSpawner.ZOMBIE_SPAWNER;
         } else {
            byte meta = (byte)this.tileEntity.getWorldObj().getBlockMetadata(this.tileEntity.xCoord, this.tileEntity.yCoord, this.tileEntity.zCoord);
            this.spawnerType = EnumSpawner.getById(meta);
            return this.spawnerType;
         }
      } else {
         return this.spawnerType;
      }
   }

   protected boolean canSpawnMobs(World world) {
      return true;
   }

   protected int getPlayerRange() {
      return this.isBossSpawner() ? 8 : 16;
   }

   protected Entity getMob() {
      return GSMobSpawn.getMobEntityForSpawner(this.tileEntity.getWorldObj(), this.getSpawnerType(), this.tileEntity.xCoord, this.tileEntity.yCoord, this.tileEntity.zCoord);
   }

   protected int getSpawnRange() {
      return 0;
   }

   protected int getMinDelay() {
      return 600;
   }

   protected int getMaxDelay() {
      return 800;
   }
}
