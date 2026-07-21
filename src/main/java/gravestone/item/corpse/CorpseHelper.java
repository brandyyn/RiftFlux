package gravestone.item.corpse;

import gravestone.ModGraveStone;
import gravestone.core.GSItem;
import gravestone.item.enums.EnumCorpse;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import gravestone.core.logger.GSLogger;

public abstract class CorpseHelper {
   protected static void setMobName(EntityLiving entity, NBTTagCompound nbtTag) {
      if (nbtTag.hasKey("Name") && nbtTag.getString("Name").length() != 0) {
         entity.setCustomNameTag(nbtTag.getString("Name"));
      }

   }

   protected static void setName(EntityLiving entity, NBTTagCompound nbtTag) {
      if (entity.hasCustomNameTag()) {
         nbtTag.setString("Name", entity.getCustomNameTag());
      }

   }

   protected static void spawnMob(EntityLiving entity, World world, int x, int y, int z) {
      entity.setPosition((double)x + 0.5D, (double)(y + 1), (double)z + 0.5D);
      world.spawnEntityInWorld(entity);
      entity.addPotionEffect(new PotionEffect(Potion.regeneration.getId(), 300));
   }

   protected static void addNameInfo(List list, NBTTagCompound nbtTag) {
      if (nbtTag.hasKey("Name") && nbtTag.getString("Name").length() != 0) {
         list.add(ModGraveStone.proxy.getLocalizedString("item.corpse.entity_name") + " " + nbtTag.getString("Name"));
      }

   }

   public static void addInfo(int corpseType, List list, NBTTagCompound nbtTag) {
      switch(EnumCorpse.values()[corpseType]) {
      case VILLAGER:
         VillagerCorpseHelper.addInfo(list, nbtTag);
         break;
      case HORSE:
         HorseCorpseHelper.addInfo(list, nbtTag);
         break;
      case DOG:
         DogCorpseHelper.addInfo(list, nbtTag);
         break;
      case CAT:
         CatCorpseHelper.addInfo(list, nbtTag);
      }

   }

   public static List<ItemStack> getDefaultCorpse(Item item, int corpseType) {
      switch(EnumCorpse.values()[corpseType]) {
      case VILLAGER:
      default:
         return VillagerCorpseHelper.getDefaultCorpses(item, corpseType);
      case HORSE:
         return HorseCorpseHelper.getDefaultCorpses(item, corpseType);
      case DOG:
         return DogCorpseHelper.getDefaultCorpses(item, corpseType);
      case CAT:
         return CatCorpseHelper.getDefaultCorpses(item, corpseType);
      }
   }

   public static List<ItemStack> getCorpse(Entity entity, EnumCorpse type) {
      NBTTagCompound nbtTag = new NBTTagCompound();
      switch(type) {
      case VILLAGER:
         VillagerCorpseHelper.setNbt((EntityVillager)entity, nbtTag);
         break;
      case HORSE:
         HorseCorpseHelper.setNbt((EntityHorse)entity, nbtTag);
         break;
      case DOG:
         DogCorpseHelper.setNbt((EntityWolf)entity, nbtTag);
         break;
      case CAT:
         CatCorpseHelper.setNbt((EntityOcelot)entity, nbtTag);
      }

      NBTTagCompound entityData = new NBTTagCompound();
      String entityId = EntityList.getEntityString(entity);
      if (entityId != null) {
         entityData.setString("id", entityId);
         entity.writeToNBT(entityData);
         // Horse equipment and cargo are already moved into the grave separately. Keeping
         // those inventory tags in the corpse would duplicate them during resurrection.
         if (entity instanceof EntityHorse) {
            entityData.removeTag("Items");
            entityData.removeTag("ArmorItem");
            entityData.removeTag("SaddleItem");
            entityData.setBoolean("ChestedHorse", false);
         }
         nbtTag.setTag("EntityData", entityData);
      }

      List<ItemStack> corpse = new ArrayList<>();
      ItemStack stack = new ItemStack(GSItem.corpse, 1, type.ordinal());
      stack.setTagCompound(nbtTag);
      corpse.add(stack);
      return corpse;
   }

   public static boolean spawnMob(int type, World world, int x, int y, int z, NBTTagCompound nbtTag, EntityPlayer player) {
      if (!world.isRemote) {
         if (nbtTag != null && nbtTag.hasKey("EntityData")) {
            try {
               NBTTagCompound entityData = (NBTTagCompound)nbtTag.getCompoundTag("EntityData").copy();
               Entity savedEntity = EntityList.createEntityFromNBT(entityData, world);
               if (savedEntity instanceof EntityLiving) {
                  EntityLiving living = (EntityLiving)savedEntity;
                  living.isDead = false;
                  living.deathTime = 0;
                  living.hurtTime = 0;
                  living.fallDistance = 0.0F;
                  living.motionX = 0.0D;
                  living.motionY = 0.0D;
                  living.motionZ = 0.0D;
                  living.extinguish();
                  living.setHealth(living.getMaxHealth());
                  living.setPosition((double)x + 0.5D, (double)(y + 1), (double)z + 0.5D);
                  if (world.spawnEntityInWorld(living)) {
                     living.addPotionEffect(new PotionEffect(Potion.regeneration.getId(), 300));
                     return true;
                  }
               }
            } catch (Throwable error) {
               GSLogger.logError("Could not restore complete corpse entity data for " + nbtTag.getCompoundTag("EntityData").getString("id") + ". Falling back to legacy pet restoration.");
               error.printStackTrace();
            }
         }

         switch(EnumCorpse.values()[type]) {
         case VILLAGER:
            VillagerCorpseHelper.spawnVillager(world, x, y, z, nbtTag);
            break;
         case HORSE:
            HorseCorpseHelper.spawnHorse(world, x, y, z, nbtTag, player);
            break;
         case DOG:
            DogCorpseHelper.spawnDog(world, x, y, z, nbtTag, player);
            break;
         case CAT:
            CatCorpseHelper.spawnCat(world, x, y, z, nbtTag, player);
         }
         return true;
      }
      return false;
   }

   public static boolean canSpawnMob(EntityPlayer player, int damage) {
      if (player.capabilities.isCreativeMode) {
         return true;
      } else {
         return player.experienceLevel >= getRequiredLevel(damage);
      }
   }

   public static boolean tryTakeExperience(EntityPlayer player, int damage) {
      synchronized(player) {
         if (player.capabilities.isCreativeMode) {
            return true;
         }

         int requiredLevel = getRequiredLevel(damage);
         if (player.experienceLevel < requiredLevel) {
            return false;
         }

         player.addExperienceLevel(-requiredLevel);
         return true;
      }
   }

   public static void refundExperience(EntityPlayer player, int damage) {
      if (!player.capabilities.isCreativeMode) {
         player.addExperienceLevel(getRequiredLevel(damage));
      }
   }

   public static void getExperience(EntityPlayer player, int damage) {
      if (!player.capabilities.isCreativeMode) {
         player.addExperienceLevel(-getRequiredLevel(damage));
      }
   }

   public static int getRequiredLevel(int damage) {
      switch(EnumCorpse.getById((byte)damage)) {
      case VILLAGER:
         return 20;
      case HORSE:
         return 15;
      case DOG:
      case CAT:
         return 7;
      default:
         return 0;
      }
   }

   public static int getRequiredLevel(ItemStack itemStack) {
      return itemStack != null ? getRequiredLevel(itemStack.getItemDamage()) : 0;
   }
}
