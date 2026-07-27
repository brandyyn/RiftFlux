package gravestone.entity.monster;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gravestone.core.Resources;
import gravestone.config.GraveStoneConfig;
import gravestone.entity.ai.EntityAIAttackLivingHorse;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIMoveThroughVillage;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAITargetNonTamed;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityZombieCat extends EntityUndeadCat {
   private static final byte CAT_TYPES = 4;
   protected boolean isGreen = false;

   public EntityZombieCat(World world) {
      this(world, world.rand.nextBoolean());
   }

   public EntityZombieCat(World world, boolean isGreen) {
      super(world);
      this.isGreen = isGreen;
      this.texture = isGreen ? Resources.GREEN_ZOMBIE_OZELOT : Resources.ZOMBIE_OZELOT;
      this.tasks.addTask(2, new EntityAIAttackOnCollide(this, EntityPlayer.class, 1.0D, false));
      this.tasks.addTask(4, new EntityAIMoveTowardsRestriction(this, 1.0D));
      this.tasks.addTask(6, new EntityAIWander(this, 1.0D));
      this.tasks.addTask(3, new EntityAIAttackOnCollide(this, EntityVillager.class, 1.0D, true));
      this.tasks.addTask(3, new EntityAIAttackOnCollide(this, EntityWolf.class, 1.0D, true));
      this.tasks.addTask(3, new EntityAIAttackOnCollide(this, EntityOcelot.class, 1.0D, true));
      this.tasks.addTask(4, new EntityAIAttackLivingHorse(this, 1.0D, false));
      this.tasks.addTask(5, new EntityAIMoveThroughVillage(this, 1.0D, false));
      this.targetTasks.addTask(4, new EntityAITargetNonTamed(this, EntityVillager.class, 0, false));
      this.targetTasks.addTask(4, new EntityAITargetNonTamed(this, EntityWolf.class, 0, false));
      this.targetTasks.addTask(4, new EntityAITargetNonTamed(this, EntityOcelot.class, 0, false));
      this.targetTasks.addTask(4, new EntityAITargetNonTamed(this, EntityChicken.class, 0, false));
      this.targetTasks.addTask(4, new EntityAITargetNonTamed(this, EntityHorse.class, 0, false));
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(10.0D);
      this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.5D);
   }

   @SideOnly(Side.CLIENT)
   public ResourceLocation getTexture() {
      if (this.isGreen) {
         switch(this.getSkin()) {
         case 0:
         default:
            return Resources.GREEN_ZOMBIE_OZELOT;
         case 1:
            return Resources.GREEN_ZOMBIE_CAT_BLACK;
         case 2:
            return Resources.GREEN_ZOMBIE_CAT_RED;
         case 3:
            return Resources.GREEN_ZOMBIE_CAT_SIAMESE;
         }
      } else {
         switch(this.getSkin()) {
         case 0:
         default:
            return Resources.ZOMBIE_OZELOT;
         case 1:
            return Resources.ZOMBIE_CAT_BLACK;
         case 2:
            return Resources.ZOMBIE_CAT_RED;
         case 3:
            return Resources.ZOMBIE_CAT_SIAMESE;
         }
      }
   }

   public void writeEntityToNBT(NBTTagCompound nbt) {
      super.writeEntityToNBT(nbt);
      nbt.setInteger("ZombieCatType", this.getSkin());
      nbt.setBoolean("IsGreen", this.isGreen);
   }

   public void readEntityFromNBT(NBTTagCompound nbt) {
      super.readEntityFromNBT(nbt);
      this.setSkin(nbt.getInteger("ZombieCatType"));
      if (nbt.hasKey("IsGreen")) {
         this.isGreen = nbt.getBoolean("IsGreen");
      }

   }

   protected String getLivingSound() {
      return this.rand.nextInt(4) == 0 ? "mob.cat.purreow" : "mob.cat.meow";
   }

   protected String getHurtSound() {
      return "mob.cat.hitt";
   }

   protected String getDeathSound() {
      return "mob.cat.hitt";
   }

   protected void func_145780_a(int p_145780_1_, int p_145780_2_, int p_145780_3_, Block p_145780_4_) {
   }

   protected float getSoundVolume() {
      return 0.4F;
   }

   protected Item getDropItem() {
      return Items.rotten_flesh;
   }

   public boolean interact(EntityPlayer player) {
      return this.tryTame(player, Items.fish) || super.interact(player);
   }

   protected boolean isTamingEnabled() {
      return GraveStoneConfig.enableZombiePetTaming;
   }

   public int getSkin() {
      return this.dataWatcher.getWatchableObjectByte(18);
   }

   public void setSkin(int par1) {
      this.dataWatcher.updateObject(18, (byte)par1);
   }

   public void onKillEntity(EntityLivingBase entityLiving) {
      super.onKillEntity(entityLiving);
      if (this.worldObj.difficultySetting == EnumDifficulty.NORMAL || this.worldObj.difficultySetting == EnumDifficulty.HARD) {
         this.spawnZombieMob(entityLiving);
      }

   }

   public IEntityLivingData onSpawnWithEgg(IEntityLivingData data) {
      this.setSkin((new Random()).nextInt(4));
      return super.onSpawnWithEgg(data);
   }
}
