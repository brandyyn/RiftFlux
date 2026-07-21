package gravestone.entity;

import java.util.Random;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class EntityVengefulSpirit extends EntityGhost {
   public EntityVengefulSpirit(World world) {
      super(world);
   }

   public boolean attackEntityAsMob(Entity entity) {
      float f = (float)this.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
      int i = 0;
      if (entity instanceof EntityLivingBase) {
         float var10000 = f + EnchantmentHelper.getEnchantmentModifierLiving(this, (EntityLivingBase)entity);
         int var4 = i + EnchantmentHelper.getKnockbackModifier(this, (EntityLivingBase)entity);
         ((EntityLivingBase)entity).addPotionEffect(new PotionEffect(this.getPotionId(entity.worldObj.rand), 7));
         ((EntityLivingBase)entity).addPotionEffect(new PotionEffect(this.getAdditionalPotionId(entity.worldObj.rand), 3));
      }

      return true;
   }

   private int getAdditionalPotionId(Random random) {
      switch(random.nextInt(3)) {
      case 0:
      default:
         return Potion.wither.getId();
      case 1:
         return Potion.blindness.getId();
      case 2:
         return Potion.poison.getId();
      }
   }
}
