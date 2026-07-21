package gravestone.entity.monster;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
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
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public abstract class EntityUndeadPet extends EntityMob {
   protected ResourceLocation texture = null;

   public EntityUndeadPet(World world) {
      super(world);
      this.getNavigator().setAvoidsWater(true);
      this.tasks.addTask(1, new EntityAISwimming(this));
      this.tasks.addTask(7, new EntityAILeapAtTarget(this, 0.3F));
      this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
      this.tasks.addTask(7, new EntityAILookIdle(this));
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
      this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));
   }

   @SideOnly(Side.CLIENT)
   public ResourceLocation getTexture() {
      return this.texture;
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
