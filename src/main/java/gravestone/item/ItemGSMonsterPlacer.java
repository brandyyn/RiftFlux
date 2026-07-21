package gravestone.item;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Facing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;

public class ItemGSMonsterPlacer extends ItemMonsterPlacer {
   public static String[] eggs = new String[]{"GSZombieDog", "GSZombieCat", "GSSkeletonDog", "GSSkeletonCat", "GSSkullCrawler", "GSWitherSkullCrawler", "GSZombieSkullCrawler"};
   public static int[][] eggColor = new int[][]{{14144467, 7969893}, {15720061, 7969893}, {14144467, 4802889}, {15720061, 4802889}, {0, 11013646}, {12698049, 11013646}, {44975, 11013646}};

   public ItemGSMonsterPlacer() {
      this.setHasSubtypes(true);
      this.setCreativeTab(CreativeTabs.tabMisc);
      this.setUnlocalizedName("monsterPlacer");
      this.iconString = "spawn_egg";
   }

   public String getItemStackDisplayName(ItemStack p_77653_1_) {
      String s = ("" + StatCollector.translateToLocal(this.getUnlocalizedName() + ".name")).trim();
      String s1 = eggs[p_77653_1_.getItemDamage()];
      if (s1 != null) {
         s = s + " " + StatCollector.translateToLocal("entity." + s1 + ".name");
      }

      return s;
   }

   public int getColorFromItemStack(ItemStack item, int colorID) {
      int itemDamage = item.getItemDamage();
      return itemDamage >= 0 && itemDamage < eggColor.length ? eggColor[itemDamage][colorID & 1] : 16777215;
   }

   public boolean onItemUse(ItemStack item, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
      if (world.isRemote) {
         return true;
      } else {
         Block block = world.getBlock(x, y, z);
         x = x + Facing.offsetsXForSide[side];
         y = y + Facing.offsetsYForSide[side];
         z = z + Facing.offsetsZForSide[side];
         double d0 = 0.0D;
         if (side == 1 && block.getRenderType() == 11) {
            d0 = 0.5D;
         }

         Entity entity = spawnCreature(world, item.getItemDamage(), (double)x + 0.5D, (double)y + d0, (double)z + 0.5D);
         if (entity != null) {
            if (entity instanceof EntityLivingBase && item.hasDisplayName()) {
               ((EntityLiving)entity).setCustomNameTag(item.getDisplayName());
            }

            if (!player.capabilities.isCreativeMode) {
               --item.stackSize;
            }
         }

         return true;
      }
   }

   public ItemStack onItemRightClick(ItemStack item, World world, EntityPlayer player) {
      if (world.isRemote) {
         return item;
      } else {
         MovingObjectPosition movingobjectposition = this.getMovingObjectPositionFromPlayer(world, player, true);
         if (movingobjectposition == null) {
            return item;
         } else {
            if (movingobjectposition.typeOfHit == MovingObjectType.BLOCK) {
               int i = movingobjectposition.blockX;
               int j = movingobjectposition.blockY;
               int k = movingobjectposition.blockZ;
               if (!world.canMineBlock(player, i, j, k)) {
                  return item;
               }

               if (!player.canPlayerEdit(i, j, k, movingobjectposition.sideHit, item)) {
                  return item;
               }

               if (world.getBlock(i, j, k) instanceof BlockLiquid) {
                  Entity entity = spawnCreature(world, item.getItemDamage(), (double)i, (double)j, (double)k);
                  if (entity != null) {
                     if (entity instanceof EntityLivingBase && item.hasDisplayName()) {
                        ((EntityLiving)entity).setCustomNameTag(item.getDisplayName());
                     }

                     if (!player.capabilities.isCreativeMode) {
                        --item.stackSize;
                     }
                  }
               }
            }

            return item;
         }
      }
   }

   public static Entity spawnCreature(World world, int damageValue, double x, double y, double z) {
      if (!world.isRemote && damageValue >= 0 && damageValue < eggs.length) {
         String fullEntityName = String.format("%s.%s", "riftflux", eggs[damageValue]);
         Entity entity = EntityList.createEntityByName(fullEntityName, world);
         if (entity != null && entity instanceof EntityLivingBase) {
            EntityLiving entityliving = (EntityLiving)entity;
            entity.setLocationAndAngles(x, y, z, MathHelper.wrapAngleTo180_float(world.rand.nextFloat() * 360.0F), 0.0F);
            entityliving.rotationYawHead = entityliving.rotationYaw;
            entityliving.renderYawOffset = entityliving.rotationYaw;
            entityliving.onSpawnWithEgg((IEntityLivingData)null);
            world.spawnEntityInWorld(entity);
            entityliving.playLivingSound();
         }

         return entity;
      } else {
         return null;
      }
   }

   public void getSubItems(Item item, CreativeTabs tabs, List subitems) {
      for(int i = 0; i < eggs.length; ++i) {
         subitems.add(new ItemStack(item, 1, i));
      }

   }
}
