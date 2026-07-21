package gravestone.entity.ai;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.MathHelper;

public class EntityAIBlockInteract extends EntityAIBase {
   protected EntityLiving theEntity;
   protected int entityPosX;
   protected int entityPosY;
   protected int entityPosZ;
   protected Block targetBlock;
   protected Block block;
   boolean hasStoppedBlockInteraction;
   float entityPositionX;
   float entityPositionZ;

   public EntityAIBlockInteract(EntityLiving entity, Block block) {
      this.theEntity = entity;
      this.block = block;
   }

   public boolean shouldExecute() {
      PathNavigate pathnavigate = this.theEntity.getNavigator();
      PathEntity pathentity = pathnavigate.getPath();
      if (pathentity != null && !pathentity.isFinished()) {
         for(int i = 0; i < Math.min(pathentity.getCurrentPathIndex() + 2, pathentity.getCurrentPathLength()); ++i) {
            PathPoint pathpoint = pathentity.getPathPointFromIndex(i);
            this.entityPosX = pathpoint.xCoord;
            this.entityPosY = pathpoint.yCoord;
            this.entityPosZ = pathpoint.zCoord;
            if (this.theEntity.getDistanceSq((double)this.entityPosX, this.theEntity.posY, (double)this.entityPosZ) <= 2.25D) {
               this.targetBlock = this.findBlock(this.entityPosX, this.entityPosY, this.entityPosZ);
               if (this.targetBlock != null) {
                  return true;
               }
            }
         }

         this.entityPosX = MathHelper.floor_double(this.theEntity.posX);
         this.entityPosY = MathHelper.floor_double(this.theEntity.posY);
         this.entityPosZ = MathHelper.floor_double(this.theEntity.posZ);
         this.targetBlock = this.findBlock(this.entityPosX, this.entityPosY, this.entityPosZ);
         return this.targetBlock != null;
      } else {
         return false;
      }
   }

   public boolean continueExecuting() {
      return !this.hasStoppedBlockInteraction;
   }

   public void startExecuting() {
      this.hasStoppedBlockInteraction = false;
      this.entityPositionX = (float)((double)((float)this.entityPosX + 0.5F) - this.theEntity.posX);
      this.entityPositionZ = (float)((double)((float)this.entityPosZ + 0.5F) - this.theEntity.posZ);
   }

   public void updateTask() {
      float f = (float)((double)((float)this.entityPosX + 0.5F) - this.theEntity.posX);
      float f1 = (float)((double)((float)this.entityPosZ + 0.5F) - this.theEntity.posZ);
      float f2 = this.entityPositionX * f + this.entityPositionZ * f1;
      if (f2 < 0.0F) {
         this.hasStoppedBlockInteraction = true;
      }

   }

   private Block findBlock(int x, int y, int z) {
      return this.theEntity.worldObj.getBlock(x, y, z);
   }
}
