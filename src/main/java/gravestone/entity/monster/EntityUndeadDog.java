package gravestone.entity.monster;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.world.World;

public abstract class EntityUndeadDog extends EntityUndeadPet {
   protected float field_70926_e;
   protected float field_70924_f;

   public EntityUndeadDog(World world) {
      super(world);
      this.setSize(0.6F, 0.8F);
   }

   protected void updateAITick() {
      this.dataWatcher.updateObject(18, this.getHealth());
   }

   protected void entityInit() {
      super.entityInit();
      this.dataWatcher.addObject(18, new Float(this.getHealth()));
      this.dataWatcher.addObject(19, new Byte((byte)0));
   }

   public void onUpdate() {
      super.onUpdate();
      this.field_70924_f = this.field_70926_e;
      if (this.func_70922_bv()) {
         this.field_70926_e += (1.0F - this.field_70926_e) * 0.4F;
      } else {
         this.field_70926_e += (0.0F - this.field_70926_e) * 0.4F;
      }

      if (this.func_70922_bv()) {
         this.numTicksToChaseTarget = 10;
      }

   }

   @SideOnly(Side.CLIENT)
   public float getInterestedAngle(float par1) {
      return (this.field_70924_f + (this.field_70926_e - this.field_70924_f) * par1) * 0.15F * (float)Math.PI;
   }

   public float getEyeHeight() {
      return this.height * 0.8F;
   }

   @SideOnly(Side.CLIENT)
   public void handleHealthUpdate(byte par1) {
      super.handleHealthUpdate(par1);
   }

   @SideOnly(Side.CLIENT)
   public float getTailRotation() {
      return (0.55F - (20.0F - this.dataWatcher.getWatchableObjectFloat(18)) * 0.02F) * (float)Math.PI;
   }

   public void func_70918_i(boolean par1) {
      if (par1) {
         this.dataWatcher.updateObject(19, (byte)1);
      } else {
         this.dataWatcher.updateObject(19, (byte)0);
      }

   }

   public boolean func_70922_bv() {
      return this.dataWatcher.getWatchableObjectByte(19) == 1;
   }
}
