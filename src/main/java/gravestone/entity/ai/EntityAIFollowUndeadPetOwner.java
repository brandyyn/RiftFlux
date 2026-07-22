package gravestone.entity.ai;

import gravestone.entity.monster.EntityUndeadPet;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;

public class EntityAIFollowUndeadPetOwner extends EntityAIBase {
   private final EntityUndeadPet pet;
   private final double speed;
   private final float startDistanceSquared;
   private final float stopDistanceSquared;
   private EntityPlayer owner;
   private int pathUpdateTimer;

   public EntityAIFollowUndeadPetOwner(EntityUndeadPet pet, double speed, float startDistance, float stopDistance) {
      this.pet = pet;
      this.speed = speed;
      this.startDistanceSquared = startDistance * startDistance;
      this.stopDistanceSquared = stopDistance * stopDistance;
      this.setMutexBits(3);
   }

   public boolean shouldExecute() {
      if (!this.pet.isTameBehaviorActive()) {
         return false;
      }
      EntityPlayer possibleOwner = this.pet.getOwner();
      if (possibleOwner == null || possibleOwner.isDead
            || this.pet.getDistanceSqToEntity(possibleOwner) < (double)this.startDistanceSquared) {
         return false;
      }
      this.owner = possibleOwner;
      return true;
   }

   public boolean continueExecuting() {
      return this.owner != null && !this.owner.isDead && this.pet.isTameBehaviorActive()
            && !this.pet.getNavigator().noPath()
            && this.pet.getDistanceSqToEntity(this.owner) > (double)this.stopDistanceSquared;
   }

   public void startExecuting() {
      this.pathUpdateTimer = 0;
   }

   public void resetTask() {
      this.owner = null;
      this.pet.getNavigator().clearPathEntity();
   }

   public void updateTask() {
      this.pet.getLookHelper().setLookPositionWithEntity(this.owner, 10.0F, this.pet.getVerticalFaceSpeed());
      if (--this.pathUpdateTimer <= 0) {
         this.pathUpdateTimer = 10;
         this.pet.getNavigator().tryMoveToEntityLiving(this.owner, this.speed);
      }
   }
}
