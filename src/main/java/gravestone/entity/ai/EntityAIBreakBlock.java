package gravestone.entity.ai;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.world.EnumDifficulty;

public class EntityAIBreakBlock extends EntityAIBlockInteract {
   private boolean isBlockBroken = false;

   public EntityAIBreakBlock(EntityLiving entity, Block block) {
      super(entity, block);
   }

   public boolean shouldExecute() {
      return super.shouldExecute() && this.theEntity.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing");
   }

   public void startExecuting() {
      super.startExecuting();
   }

   public boolean continueExecuting() {
      double d0 = this.theEntity.getDistanceSq((double)this.entityPosX, (double)this.entityPosY, (double)this.entityPosZ);
      return d0 < 4.0D;
   }

   public void resetTask() {
      super.resetTask();
   }

   public void updateTask() {
      super.updateTask();
      if (!this.isBlockBroken && this.theEntity.worldObj.difficultySetting == EnumDifficulty.NORMAL || this.theEntity.worldObj.difficultySetting == EnumDifficulty.HARD) {
         this.isBlockBroken = true;
         this.targetBlock.dropBlockAsItem(this.theEntity.worldObj, this.entityPosX, this.entityPosY, this.entityPosZ, 0, 0);
         this.theEntity.worldObj.setBlockToAir(this.entityPosX, this.entityPosY, this.entityPosZ);
         this.theEntity.worldObj.playAuxSFX(1012, this.entityPosX, this.entityPosY, this.entityPosZ, 0);
         this.theEntity.worldObj.playAuxSFX(2001, this.entityPosX, this.entityPosY, this.entityPosZ, 0);
      }

   }
}
