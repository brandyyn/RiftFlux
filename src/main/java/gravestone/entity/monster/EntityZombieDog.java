package gravestone.entity.monster;

import gravestone.core.Resources;
import gravestone.config.GraveStoneConfig;
import gravestone.entity.ai.EntityAIAttackLivingHorse;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIMoveThroughVillage;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAITargetNonTamed;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityZombieDog extends EntityUndeadDog {
   protected boolean isGreen = false;

   public EntityZombieDog(World world) {
      this(world, world.rand.nextBoolean());
   }

   public EntityZombieDog(World world, boolean isGreen) {
      super(world);
      this.isGreen = isGreen;
      this.texture = isGreen ? Resources.GREEN_ZOMBIE_DOG : Resources.ZOMBIE_DOG;
      this.tasks.addTask(2, new EntityAIAttackOnCollide(this, EntityPlayer.class, 1.0D, false));
      this.tasks.addTask(4, new EntityAIMoveTowardsRestriction(this, 1.0D));
      this.tasks.addTask(6, new EntityAIWander(this, 1.0D));
      this.tasks.addTask(3, new EntityAIAttackOnCollide(this, EntityVillager.class, 1.0D, true));
      this.tasks.addTask(3, new EntityAIAttackOnCollide(this, EntityWolf.class, 1.0D, true));
      this.tasks.addTask(3, new EntityAIAttackOnCollide(this, EntityOcelot.class, 1.0D, true));
      this.tasks.addTask(4, new EntityAIAttackLivingHorse(this, 1.0D, false));
      this.tasks.addTask(4, new EntityAIAttackOnCollide(this, EntitySheep.class, 1.0D, false));
      this.tasks.addTask(5, new EntityAIMoveThroughVillage(this, 1.0D, false));
      this.targetTasks.addTask(4, new EntityAITargetNonTamed(this, EntityVillager.class, 0, false));
      this.targetTasks.addTask(4, new EntityAITargetNonTamed(this, EntityWolf.class, 0, false));
      this.targetTasks.addTask(4, new EntityAITargetNonTamed(this, EntityOcelot.class, 0, false));
      this.targetTasks.addTask(4, new EntityAITargetNonTamed(this, EntityHorse.class, 0, false));
      this.targetTasks.addTask(4, new EntityAITargetNonTamed(this, EntitySheep.class, 0, false));
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(20.0D);
      this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.3D);
      this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(3.0D);
   }

   protected String getLivingSound() {
      return this.rand.nextInt(3) == 0 ? "mob.wolf.bark" : "mob.wolf.growl";
   }

   protected String getHurtSound() {
      return "mob.wolf.hurt";
   }

   protected String getDeathSound() {
      return "mob.wolf.death";
   }

   protected void func_145780_a(int p_145780_1_, int p_145780_2_, int p_145780_3_, Block p_145780_4_) {
      this.playSound("mob.wolf.step", 0.15F, 1.0F);
   }

   protected float getSoundVolume() {
      return 0.4F;
   }

   protected Item getDropItem() {
      return Items.rotten_flesh;
   }

   public boolean interact(EntityPlayer player) {
      return this.tryTame(player, Items.bone) || super.interact(player);
   }

   protected boolean isTamingEnabled() {
      return GraveStoneConfig.enableZombiePetTaming;
   }

   public void onKillEntity(EntityLivingBase entityLiving) {
      super.onKillEntity(entityLiving);
      if (this.worldObj.difficultySetting == EnumDifficulty.NORMAL || this.worldObj.difficultySetting == EnumDifficulty.HARD) {
         this.spawnZombieMob(entityLiving);
      }

   }

   public void readEntityFromNBT(NBTTagCompound nbt) {
      super.readEntityFromNBT(nbt);
      if (nbt.hasKey("IsGreen")) {
         this.isGreen = nbt.getBoolean("IsGreen");
      }

   }

   public void writeEntityToNBT(NBTTagCompound nbt) {
      super.writeEntityToNBT(nbt);
      nbt.setBoolean("IsGreen", this.isGreen);
   }
}
