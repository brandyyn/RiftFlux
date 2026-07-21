package gravestone.entity.monster;

import gravestone.core.GSBlock;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.Facing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntitySkullCrawler extends EntityMob {
   protected int allySummonCooldown;
   protected int defaultSummonCooldown = 10;

   public EntitySkullCrawler(World world) {
      super(world);
      this.setSize(0.8F, 0.8F);
   }

   protected void entityInit() {
      super.entityInit();
      this.dataWatcher.addObject(16, new Byte((byte)0));
   }

   protected boolean canTriggerWalking() {
      return false;
   }

   public void onUpdate() {
      super.onUpdate();
      if (!this.worldObj.isRemote) {
         this.setBesideClimbableBlock(this.isCollidedHorizontally);
      }

   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(12.0D);
      this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.9D);
      this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(1.5D);
   }

   protected Entity findPlayerToAttack() {
      return this.worldObj.getClosestVulnerablePlayerToEntity(this, 10.0D);
   }

   protected String getLivingSound() {
      return "mob.skeleton.say";
   }

   protected String getHurtSound() {
      return "mob.skeleton.say";
   }

   protected String getDeathSound() {
      return "mob.skeleton.death";
   }

   protected void func_145780_a(int p_145780_1_, int p_145780_2_, int p_145780_3_, Block p_145780_4_) {
      this.playSound("mob.spider.step", 0.15F, 1.0F);
   }

   protected void attackEntity(Entity entity, float par2) {
      if (par2 > 2.0F && par2 < 6.0F && this.rand.nextInt(10) == 0) {
         if (this.onGround) {
            double d0 = entity.posX - this.posX;
            double d1 = entity.posZ - this.posZ;
            float f2 = MathHelper.sqrt_double(d0 * d0 + d1 * d1);
            this.motionX = d0 / (double)f2 * 0.5D * (double)0.8F + this.motionX * (double)0.2F;
            this.motionZ = d1 / (double)f2 * 0.5D * (double)0.8F + this.motionZ * (double)0.2F;
            this.motionY = (double)0.4F;
         }
      } else {
         super.attackEntity(entity, par2);
      }

   }

   public boolean attackEntityFrom(DamageSource source, float par2) {
      if (this.isEntityInvulnerable()) {
         return false;
      } else {
         if (this.allySummonCooldown <= 0 && (source instanceof EntityDamageSource || source == DamageSource.magic)) {
            this.allySummonCooldown = this.defaultSummonCooldown;
         }

         return super.attackEntityFrom(source, par2);
      }
   }

   public float getBlockPathWeight(int x, int y, int z) {
      return this.worldObj.getBlock(x, y - 1, z).equals(Blocks.stone) ? 10.0F : super.getBlockPathWeight(x, y, z);
   }

   protected Item getDropItem() {
      return Items.bone;
   }

   protected void dropRareDrop(int par1) {
      this.entityDropItem(new ItemStack(Items.skull, 1, 0), 0.0F);
   }

   public boolean isOnLadder() {
      return this.isBesideClimbableBlock();
   }

   public void setInWeb() {
   }

   public EnumCreatureAttribute getCreatureAttribute() {
      return EnumCreatureAttribute.UNDEAD;
   }

   public boolean isBesideClimbableBlock() {
      return (this.dataWatcher.getWatchableObjectByte(16) & 1) != 0;
   }

   public void setBesideClimbableBlock(boolean par1) {
      byte b0 = this.dataWatcher.getWatchableObjectByte(16);
      if (par1) {
         b0 = (byte)(b0 | 1);
      } else {
         b0 = (byte)(b0 & -2);
      }

      this.dataWatcher.updateObject(16, b0);
   }

   public boolean attackEntityAsMob(Entity entity) {
      if (super.attackEntityAsMob(entity)) {
         if (entity instanceof EntityLivingBase) {
            ((EntityLivingBase)entity).addPotionEffect(this.getPotionEffect());
         }

         return true;
      } else {
         return false;
      }
   }

   protected PotionEffect getPotionEffect() {
      return new PotionEffect(Potion.moveSlowdown.id, 200);
   }

   public void onLivingUpdate() {
      if (this.worldObj.isDaytime() && !this.worldObj.isRemote) {
         float f = this.getBrightness(1.0F);
         if (!this.isImmuneToFire && f > 0.5F && this.rand.nextFloat() * 30.0F < (f - 0.4F) * 2.0F && this.worldObj.canBlockSeeTheSky(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ))) {
            this.setFire(8);
         }
      }

      super.onLivingUpdate();
   }

   protected void updateEntityActionState() {
      super.updateEntityActionState();
      this.silverfishLikeBehaviour();
   }

   protected void silverfishLikeBehaviour() {
      if (!this.worldObj.isRemote) {
         int x = MathHelper.floor_double(this.posX);
         int y = MathHelper.floor_double(this.posY);
         int z = MathHelper.floor_double(this.posZ);
         if (this.allySummonCooldown > 0) {
            --this.allySummonCooldown;
            if (this.allySummonCooldown == 0) {
               boolean flag = false;

               for(int shiftY = 0; !flag && shiftY <= 5 && shiftY >= -5; shiftY = shiftY <= 0 ? 1 - shiftY : 0 - shiftY) {
                  for(int shiftX = 0; !flag && shiftX <= 10 && shiftX >= -10; shiftX = shiftX <= 0 ? 1 - shiftX : 0 - shiftX) {
                     for(int ShiftZ = 0; !flag && ShiftZ <= 10 && ShiftZ >= -10; ShiftZ = ShiftZ <= 0 ? 1 - ShiftZ : 0 - ShiftZ) {
                        Block block = this.worldObj.getBlock(x + shiftX, y + shiftY, z + ShiftZ);
                        int blockMeta = this.worldObj.getBlockMetadata(x + shiftX, y + shiftY, z + ShiftZ);
                        if (block.equals(GSBlock.boneBlock) && GSBlock.boneBlock.isSkullCrawlerBlock(blockMeta)) {
                           this.worldObj.func_147480_a(x + shiftX, y + shiftY, z + ShiftZ, false);
                           GSBlock.boneBlock.onBlockDestroyedByPlayer(this.worldObj, x + shiftX, y + shiftY, z + ShiftZ, blockMeta);
                           if (this.rand.nextBoolean()) {
                              flag = true;
                              break;
                           }
                        }
                     }
                  }
               }
            }
         }

         if (this.entityToAttack == null && !this.hasPath()) {
            int offset = this.rand.nextInt(6);
            x = x + Facing.offsetsXForSide[offset];
            y = MathHelper.floor_double(this.posY + 0.5D) + Facing.offsetsYForSide[offset];
            z = z + Facing.offsetsZForSide[offset];
            Block block = this.worldObj.getBlock(x, y, z);
            int metadata = this.worldObj.getBlockMetadata(x, y, z);
            if (GSBlock.boneBlock.equals(block) && !GSBlock.boneBlock.isSkullCrawlerBlock(metadata)) {
               this.worldObj.setBlock(x, y, z, GSBlock.boneBlock, metadata + 2, 3);
               this.spawnExplosionParticle();
               this.setDead();
            } else {
               this.updateWanderPath();
            }
         } else if (this.entityToAttack != null && !this.hasPath()) {
            this.entityToAttack = null;
         }
      }

   }

   public static enum SkullCrawlerType {
      skeleton,
      wither,
      zombie;
   }
}
