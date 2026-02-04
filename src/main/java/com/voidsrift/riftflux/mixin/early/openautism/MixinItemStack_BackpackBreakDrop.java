package com.voidsrift.riftflux.mixin.early.vortex;

import com.voidsrift.riftflux.vortex.item.ModItems;
import com.voidsrift.riftflux.vortex.lib.container.InventoryBackpack;
import com.voidsrift.riftflux.vortex.lib.helper.ContainerHelper;
import com.voidsrift.riftflux.ModConfig;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public abstract class MixinItemStack_BackpackBreakDrop {
   private static final String TAG_DROPPED_ON_BREAK = "rf_bp_dropped";

   @Shadow
   public abstract Item getItem();

   @Shadow
   public abstract int getItemDamage();

   @Shadow
   public abstract int getMaxDamage();

   @Shadow
   public abstract NBTTagCompound getTagCompound();

   @Shadow
   public abstract void setTagCompound(NBTTagCompound tag);

   @Inject(method = "damageItem", at = @At("HEAD"))
   private void rf$dropBackpackContents(int amount, EntityLivingBase entity, CallbackInfo ci) {
      if (amount <= 0 || entity == null || entity.worldObj == null || entity.worldObj.isRemote) {
         return;
      }
      if (!ModConfig.backpackDurability) {
         return;
      }
      if (this.getItem() != ModItems.backpack) {
         return;
      }
      int max = this.getMaxDamage();
      if (max <= 0) {
         return;
      }
      if (this.getItemDamage() + amount <= max) {
         return;
      }
      NBTTagCompound tag = this.getTagCompound();
      if (tag != null && tag.getBoolean(TAG_DROPPED_ON_BREAK)) {
         return;
      }
      ItemStack stack = (ItemStack) (Object) this;
      InventoryBackpack backpack = ContainerHelper.getBackpackInventory(stack);
      for (int i = 0; i < backpack.getSizeInventory(); ++i) {
         ItemStack itemStack = backpack.getStackInSlot(i);
         if (itemStack != null) {
            entity.entityDropItem(itemStack.copy(), 0.0F);
            backpack.setInventorySlotContents(i, (ItemStack) null);
         }
      }
      entity.worldObj.playSoundAtEntity(entity, "random.break", 0.8F,
              0.8F + entity.worldObj.rand.nextFloat() * 0.4F);
      if (tag == null) {
         tag = new NBTTagCompound();
      } else {
         tag = (NBTTagCompound) tag.copy();
      }
      tag.setBoolean(TAG_DROPPED_ON_BREAK, true);
      this.setTagCompound(tag);
   }
}
