package gravestone.entity.monster;

import gravestone.core.Resources;
import gravestone.config.GraveStoneConfig;
import net.minecraft.block.Block;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAIOcelotAttack;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.world.World;

public class EntitySkeletonCat extends EntityUndeadCat {
   public EntitySkeletonCat(World world) {
      super(world);
      this.texture = Resources.SKELETON_CAT;
      this.tasks.addTask(2, new EntityAIAttackOnCollide(this, EntityPlayer.class, 1.0D, false));
      this.tasks.addTask(4, new EntityAIMoveTowardsRestriction(this, 1.0D));
      this.tasks.addTask(6, new EntityAIWander(this, 1.0D));
      this.tasks.addTask(8, new EntityAIOcelotAttack(this));
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(5.0D);
      this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.7D);
      this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(1.0D);
   }

   protected String getLivingSound() {
      return "mob.skeleton.say";
   }

   protected String getHurtSound() {
      return "mob.skeleton.hurt";
   }

   protected String getDeathSound() {
      return "mob.skeleton.death";
   }

   protected void func_145780_a(int p_145780_1_, int p_145780_2_, int p_145780_3_, Block p_145780_4_) {
   }

   protected Item getDropItem() {
      return Items.bone;
   }

   public boolean interact(EntityPlayer player) {
      return this.tryTame(player, Items.fish) || super.interact(player);
   }

   protected boolean isTamingEnabled() {
      return GraveStoneConfig.enableSkeletonPetTaming;
   }
}
