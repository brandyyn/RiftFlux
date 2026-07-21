package gravestone.entity;

import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.potion.Potion;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityGhost extends EntityFlying implements IMob {
   public EntityGhost(World world) {
      super(world);
   }

   protected boolean canDespawn() {
      return true;
   }

   public boolean isAIEnabled() {
      return true;
   }

   public boolean attackEntityAsMob(Entity entity) {
      return entity.attackEntityFrom(DamageSource.causeMobDamage(this), 3.0F);
   }

   protected void dropFewItems(boolean par1, int par2) {
   }

   public void onLivingUpdate() {
      if (this.worldObj.isDaytime() && !this.worldObj.isRemote) {
         float f = this.getBrightness(1.0F);
         if (f > 0.5F && this.rand.nextFloat() * 30.0F < (f - 0.4F) * 2.0F && this.worldObj.canBlockSeeTheSky(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ))) {
            this.setFire(8);
         }
      }

      super.onLivingUpdate();
   }

   public EnumCreatureAttribute getCreatureAttribute() {
      return EnumCreatureAttribute.UNDEAD;
   }

   protected int getPotionId(Random random) {
      switch(random.nextInt(3)) {
      case 0:
      default:
         return Potion.moveSlowdown.getId();
      case 1:
         return Potion.weakness.getId();
      case 2:
         return Potion.hunger.getId();
      }
   }
}
