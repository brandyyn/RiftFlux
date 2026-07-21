package gravestone.entity;

import gravestone.entity.ai.EntityAIBreakBlock;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIOpenDoor;
import net.minecraft.init.Blocks;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class EntityLostSoul extends EntityGhost {
   public EntityLostSoul(World world) {
      super(world);
      this.tasks.addTask(3, new EntityAIOpenDoor(this, true));
      this.tasks.addTask(4, new EntityAIBreakBlock(this, Blocks.torch));
   }

   public boolean attackEntityAsMob(Entity entity) {
      float f = (float)this.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
      int i = 0;
      if (entity instanceof EntityLivingBase) {
         float var10000 = f + EnchantmentHelper.getEnchantmentModifierLiving(this, (EntityLivingBase)entity);
         int var4 = i + EnchantmentHelper.getKnockbackModifier(this, (EntityLivingBase)entity);
         ((EntityLivingBase)entity).addPotionEffect(new PotionEffect(this.getPotionId(entity.worldObj.rand), 3));
      }

      return false;
   }

   protected void attackEntity(Entity par1Entity, float par2) {
      if (this.attackTime <= 0 && par2 < 2.0F && par1Entity.boundingBox.maxY > this.boundingBox.minY && par1Entity.boundingBox.minY < this.boundingBox.maxY) {
         this.attackTime = 20;
         this.attackEntityAsMob(par1Entity);
      }

   }
}
