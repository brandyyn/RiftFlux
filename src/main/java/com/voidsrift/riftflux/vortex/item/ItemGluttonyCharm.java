package com.voidsrift.riftflux.vortex.item;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import baubles.common.container.InventoryBaubles;
import baubles.common.lib.PlayerHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.FoodStats;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import com.voidsrift.riftflux.riftflux;
import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.vortex.lib.container.InventoryGluttonyCharm;
import com.voidsrift.riftflux.vortex.lib.helper.ContainerHelper;
import com.voidsrift.riftflux.vortex.lib.helper.ItemHelper;

public class ItemGluttonyCharm extends Item implements IBauble {
   public IIcon icon;

   public ItemGluttonyCharm() {
      this.setMaxStackSize(1);
      this.setCreativeTab(CreativeTabs.tabTools);
   }

   @SideOnly(Side.CLIENT)
   public void registerIcons(IIconRegister p_94581_1_) {
      this.icon = p_94581_1_.registerIcon("riftflux:gluttonycharm");
   }

   @SideOnly(Side.CLIENT)
   public IIcon getIconFromDamage(int par1) {
      return this.icon;
   }

   public int getMaxItemUseDuration(ItemStack p_77626_1_) {
      return ModConfig.GluttonyCharm ? 1 : super.getMaxItemUseDuration(p_77626_1_);
   }

   public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer player) {
      if (!world.isRemote) {
         if (ModConfig.GluttonyCharm) {
            player.openGui(riftflux.instance, 2, world, 0, 0, 0);
         } else {
            InventoryBaubles baubles = PlayerHandler.getPlayerBaubles(player);

            for(int i = 0; i < baubles.getSizeInventory(); ++i) {
               if (baubles.getStackInSlot(i) == null && baubles.isItemValidForSlot(i, itemstack)) {
                  baubles.setInventorySlotContents(i, itemstack.copy());
                  if (!player.capabilities.isCreativeMode) {
                     player.inventory.setInventorySlotContents(player.inventory.currentItem, (ItemStack)null);
                  }

                  this.onEquipped(itemstack, player);
                  break;
               }
            }
         }
      }

      return itemstack;
   }

   public BaubleType getBaubleType(ItemStack arg0) {
      return BaubleType.AMULET;
   }

   public void onWornTick(ItemStack itemStack, EntityLivingBase entityLivingBase) {
      if (!entityLivingBase.worldObj.isRemote && ModConfig.GluttonyCharm && entityLivingBase instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)entityLivingBase;
         if (player.ticksExisted % 60 == 0) {
            FoodStats foodStats = player.getFoodStats();
            if (foodStats.needFood()) {
               int needed = 20 - foodStats.getFoodLevel();
               InventoryGluttonyCharm inventoryGluttonyCharm = ContainerHelper.getGluttonyCharmInventory(itemStack);
               ItemStack foodStack = inventoryGluttonyCharm.getStackInSlot(0);
               if (foodStack != null && foodStack.getItem() instanceof ItemFood) {
                  ItemFood foodItem = (ItemFood)foodStack.getItem();
                  if (foodItem.func_150905_g(foodStack) <= needed) {
                     ItemStack returnStack = foodItem.onEaten(foodStack, player.worldObj, player);
                     returnStack = ForgeEventFactory.onItemUseFinish(player, foodStack, 0, returnStack);
                     if (!ItemHelper.areItemStacksEqualStackSizeUnaware(foodStack, returnStack) && !player.inventory.addItemStackToInventory(returnStack)) {
                        player.entityDropItem(returnStack, 0.0F);
                     }

                     inventoryGluttonyCharm.setInventorySlotContents(0, foodStack);
                  }
               }
            }
         }
      }

   }

   public void onEquipped(ItemStack arg0, EntityLivingBase entityLivingBase) {
   }

   public void onUnequipped(ItemStack arg0, EntityLivingBase arg1) {
   }

   public boolean canEquip(ItemStack arg0, EntityLivingBase arg1) {
      return true;
   }

   public boolean canUnequip(ItemStack arg0, EntityLivingBase arg1) {
      return true;
   }

   public EnumRarity getRarity(ItemStack p_77613_1_) {
      return EnumRarity.epic;
   }

   public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
      list.add("Gorge your face");
   }
}
