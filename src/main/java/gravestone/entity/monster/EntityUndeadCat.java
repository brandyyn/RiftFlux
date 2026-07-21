package gravestone.entity.monster;

import net.minecraft.world.World;

public abstract class EntityUndeadCat extends EntityUndeadPet {
   public EntityUndeadCat(World world) {
      super(world);
      this.setSize(0.6F, 0.8F);
   }

   protected void entityInit() {
      super.entityInit();
      this.dataWatcher.addObject(18, (byte)0);
   }

   public void updateAITick() {
      if (this.getMoveHelper().isUpdating()) {
         double f = this.getMoveHelper().getSpeed();
         if (f == (double)0.18F) {
            this.setSneaking(true);
            this.setSprinting(false);
         } else if (f == (double)0.4F) {
            this.setSneaking(false);
            this.setSprinting(true);
         } else {
            this.setSneaking(false);
            this.setSprinting(false);
         }
      } else {
         this.setSneaking(false);
         this.setSprinting(false);
      }

   }

   protected void fall(float par1) {
   }
}
