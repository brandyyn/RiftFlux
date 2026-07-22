package gravestone.entity.monster;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gravestone.config.GraveStoneConfig;
import gravestone.entity.ai.EntityAIFollowUndeadPetOwner;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public abstract class EntityUndeadPet extends EntityMob {
   private static final int TAMED_DATA_WATCHER = 20;
   private static final int OWNER_DATA_WATCHER = 21;
   protected ResourceLocation texture = null;

   public EntityUndeadPet(World world) {
      super(world);
      this.getNavigator().setAvoidsWater(true);
      this.tasks.addTask(1, new EntityAISwimming(this));
      this.tasks.addTask(5, new EntityAIFollowUndeadPetOwner(this, 1.1D, 4.0F, 2.0F));
      this.tasks.addTask(7, new EntityAILeapAtTarget(this, 0.3F));
      this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
      this.tasks.addTask(7, new EntityAILookIdle(this));
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
      this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));
   }

   protected void entityInit() {
      super.entityInit();
      this.dataWatcher.addObject(TAMED_DATA_WATCHER, (byte)0);
      this.dataWatcher.addObject(OWNER_DATA_WATCHER, "");
   }

   @SideOnly(Side.CLIENT)
   public ResourceLocation getTexture() {
      return this.texture;
   }

   protected boolean canDespawn() {
      return !this.isTamed();
   }

   public boolean isAIEnabled() {
      return true;
   }

   public boolean attackEntityAsMob(Entity entity) {
      if (this.isTameBehaviorActive() && entity instanceof EntityPlayer) {
         return false;
      }
      return entity.attackEntityFrom(DamageSource.causeMobDamage(this), 3.0F);
   }

   public void setAttackTarget(EntityLivingBase target) {
      if (this.isTameBehaviorActive() && target instanceof EntityPlayer) {
         return;
      }
      super.setAttackTarget(target);
   }

   protected void dropFewItems(boolean par1, int par2) {
   }

   public void onLivingUpdate() {
      if ((!this.isTameBehaviorActive()) && this.worldObj.isDaytime() && !this.worldObj.isRemote) {
         float f = this.getBrightness(1.0F);
         if (f > 0.5F && this.rand.nextFloat() * 30.0F < (f - 0.4F) * 2.0F && this.worldObj.canBlockSeeTheSky(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ))) {
            this.setFire(8);
         }
      }

      super.onLivingUpdate();
   }

   protected boolean tryTame(EntityPlayer player, Item tamingItem) {
      if (!GraveStoneConfig.enableSkeletonPetTaming || this.isTamed()) {
         return false;
      }

      ItemStack heldItem = player.inventory.getCurrentItem();
      if (heldItem == null || heldItem.getItem() != tamingItem) {
         return false;
      }

      if (!player.capabilities.isCreativeMode) {
         --heldItem.stackSize;
         if (heldItem.stackSize <= 0) {
            player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
         }
      }

      if (!this.worldObj.isRemote) {
         if (this.rand.nextInt(3) == 0) {
            this.setTamed(true);
            this.setOwnerName(player.getCommandSenderName());
            this.setAttackTarget(null);
            this.getNavigator().clearPathEntity();
            this.extinguish();
            this.setHealth(this.getMaxHealth());
            this.worldObj.setEntityState(this, (byte)7);
         } else {
            this.worldObj.setEntityState(this, (byte)6);
         }
      }
      return true;
   }

   public boolean isTamed() {
      return this.dataWatcher.getWatchableObjectByte(TAMED_DATA_WATCHER) != 0;
   }

   public void setTamed(boolean tamed) {
      this.dataWatcher.updateObject(TAMED_DATA_WATCHER, (byte)(tamed ? 1 : 0));
   }

   public String getOwnerName() {
      return this.dataWatcher.getWatchableObjectString(OWNER_DATA_WATCHER);
   }

   public void setOwnerName(String ownerName) {
      this.dataWatcher.updateObject(OWNER_DATA_WATCHER, ownerName == null ? "" : ownerName);
   }

   public EntityPlayer getOwner() {
      String ownerName = this.getOwnerName();
      return ownerName.isEmpty() ? null : this.worldObj.getPlayerEntityByName(ownerName);
   }

   public boolean isTameBehaviorActive() {
      return GraveStoneConfig.enableSkeletonPetTaming && this.isTamed();
   }

   public void writeEntityToNBT(NBTTagCompound nbt) {
      super.writeEntityToNBT(nbt);
      nbt.setBoolean("Tamed", this.isTamed());
      nbt.setString("Owner", this.getOwnerName());
   }

   public void readEntityFromNBT(NBTTagCompound nbt) {
      super.readEntityFromNBT(nbt);
      this.setTamed(nbt.getBoolean("Tamed"));
      this.setOwnerName(nbt.getString("Owner"));
   }

   @SideOnly(Side.CLIENT)
   public void handleHealthUpdate(byte state) {
      if (state == 7 || state == 6) {
         String particle = state == 7 ? "heart" : "smoke";
         for(int i = 0; i < 7; ++i) {
            double motionX = this.rand.nextGaussian() * 0.02D;
            double motionY = this.rand.nextGaussian() * 0.02D;
            double motionZ = this.rand.nextGaussian() * 0.02D;
            this.worldObj.spawnParticle(particle, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F)
                  - (double)this.width, this.posY + 0.5D + (double)(this.rand.nextFloat() * this.height),
                  this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width,
                  motionX, motionY, motionZ);
         }
      } else {
         super.handleHealthUpdate(state);
      }
   }

   public EnumCreatureAttribute getCreatureAttribute() {
      return EnumCreatureAttribute.UNDEAD;
   }

   protected void spawnZombieMob(EntityLivingBase entityLivingBase) {
      if (entityLivingBase instanceof EntityLiving) {
         EntityLiving entity = (EntityLiving)entityLivingBase;
         EntityLiving zombie = null;
         if (entity instanceof EntityVillager) {
            EntityZombie entityZombie = new EntityZombie(this.worldObj);
            entityZombie.copyLocationAndAnglesFrom(entity);
            this.worldObj.removeEntity(entity);
            entityZombie.onSpawnWithEgg((IEntityLivingData)null);
            entityZombie.setVillager(true);
            if (entity.isChild()) {
               entityZombie.setChild(true);
            }

            this.worldObj.spawnEntityInWorld(entityZombie);
            this.worldObj.playAuxSFXAtEntity((EntityPlayer)null, 1016, (int)this.posX, (int)this.posY, (int)this.posZ, 0);
            zombie = entityZombie;
         } else if (entity instanceof EntityWolf) {
            EntityZombieDog zombieDog = new EntityZombieDog(this.worldObj, false);
            zombieDog.copyLocationAndAnglesFrom(entity);
            this.worldObj.removeEntity(entity);
            this.worldObj.spawnEntityInWorld(zombieDog);
            this.worldObj.playAuxSFXAtEntity((EntityPlayer)null, 1016, (int)this.posX, (int)this.posY, (int)this.posZ, 0);
            zombie = zombieDog;
         } else if (entity instanceof EntityOcelot) {
            EntityZombieCat zombieCat = new EntityZombieCat(this.worldObj, false);
            zombieCat.copyLocationAndAnglesFrom(entity);
            if (((EntityOcelot)entity).isTamed()) {
               zombieCat.setSkin(((EntityOcelot)entity).getTameSkin());
            } else {
               zombieCat.setSkin(0);
            }

            this.worldObj.removeEntity(entity);
            zombieCat.onSpawnWithEgg((IEntityLivingData)null);
            this.worldObj.spawnEntityInWorld(zombieCat);
            this.worldObj.playAuxSFXAtEntity((EntityPlayer)null, 1016, (int)this.posX, (int)this.posY, (int)this.posZ, 0);
            zombie = zombieCat;
         } else if (entity instanceof EntityHorse) {
            EntityHorse zombieHorse = new EntityHorse(this.worldObj);
            zombieHorse.copyLocationAndAnglesFrom(entity);
            zombieHorse.setHorseType(3);
            zombieHorse.setGrowingAge(((EntityHorse)entity).getGrowingAge());
            this.worldObj.removeEntity(entity);
            this.worldObj.spawnEntityInWorld(zombieHorse);
            this.worldObj.playAuxSFXAtEntity((EntityPlayer)null, 1016, (int)this.posX, (int)this.posY, (int)this.posZ, 0);
            zombie = zombieHorse;
         }

         if (zombie != null && entity.hasCustomNameTag()) {
            zombie.setCustomNameTag(entity.getCustomNameTag());
         }
      }

   }
}
