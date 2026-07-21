package gravestone.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.passive.EntityHorse;

public class EntityAIAttackLivingHorse extends EntityAIAttackOnCollide {
   protected EntityCreature attacker;

   public EntityAIAttackLivingHorse(EntityCreature creature, double speedTowardsTarget, boolean longMemory) {
      super(creature, EntityHorse.class, speedTowardsTarget, longMemory);
      this.attacker = creature;
   }

   public boolean shouldExecute() {
      EntityLivingBase entity = this.attacker.getAttackTarget();
      if (entity != null && entity instanceof EntityHorse) {
         EntityHorse horse = (EntityHorse)entity;
         if (horse.getHorseType() != 3 && horse.getHorseType() != 4) {
            return super.shouldExecute();
         }
      }

      return false;
   }
}
