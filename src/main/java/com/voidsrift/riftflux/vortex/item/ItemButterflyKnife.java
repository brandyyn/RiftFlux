package com.voidsrift.riftflux.vortex.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ItemButterflyKnife extends ItemSword {
   public IIcon icon;
   public IIcon iconFlick;
   private static final String TAG_FLICK_UNTIL = "rf_flick_until";
   private static final int FLICK_TICKS = 12;

   public ItemButterflyKnife(ToolMaterial material) {
      super(material);
   }

   @SideOnly(Side.CLIENT)
   public void registerIcons(IIconRegister p_94581_1_) {
      this.icon = p_94581_1_.registerIcon("riftflux:butterflyknife");
      this.iconFlick = p_94581_1_.registerIcon("riftflux:butterflyknife_flick");
   }

   @SideOnly(Side.CLIENT)
   public IIcon getIconFromDamage(int par1) {
      return this.icon;
   }

   @SideOnly(Side.CLIENT)
   public IIcon getIcon(ItemStack stack, int pass) {
      if (this.iconFlick != null && isFlickActive(stack)) {
         return this.iconFlick;
      }
      return this.icon;
   }

   public ItemStack onItemRightClick(ItemStack p_77659_1_, World p_77659_2_, EntityPlayer p_77659_3_) {
      if (p_77659_2_ != null) {
         long until = p_77659_2_.getTotalWorldTime() + FLICK_TICKS;
         net.minecraft.nbt.NBTTagCompound tag = p_77659_1_.getTagCompound();
         tag = (tag == null) ? new net.minecraft.nbt.NBTTagCompound() : (net.minecraft.nbt.NBTTagCompound) tag.copy();
         tag.setLong(TAG_FLICK_UNTIL, until);
         p_77659_1_.setTagCompound(tag);
         if (!p_77659_2_.isRemote) {
            p_77659_2_.playSoundAtEntity(p_77659_3_, "riftflux:butterflyknife_flick", 0.6F, 1.0F);
         }
      }
      return p_77659_1_;
   }

   public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isHeld) {
      if (stack != null && world != null) {
         clearFlickTagIfExpired(stack, world);
      }
      super.onUpdate(stack, world, entity, slot, isHeld);
   }

   public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
      if (player.isSneaking() && entity.isEntityAlive() && entity instanceof EntityLivingBase && !player.worldObj.isRemote && this.isBackstab(player, (EntityLivingBase)entity)) {
         float backstabDamage = com.voidsrift.riftflux.ModConfig.butterflyKnifeBackstabDamage;
         entity.attackEntityFrom(DamageSource.causePlayerDamage(player).setDamageBypassesArmor(), backstabDamage);
         player.onCriticalHit(entity);
         player.worldObj.playSoundAtEntity(entity, "riftflux:backstab", 0.4F, 1.0F);
         stack.damageItem(1, player);
         return true;
      } else {
         return super.onLeftClickEntity(stack, player, entity);
      }
   }

   
@Override
public boolean hitEntity(ItemStack stack, net.minecraft.entity.EntityLivingBase target, net.minecraft.entity.EntityLivingBase attacker) {
    // Consume durability on successful melee hit
    if (stack != null) {
        stack.damageItem(1, attacker);
    }
    return true;
}

private boolean isBackstab(EntityPlayer attacker, EntityLivingBase target) {
      float attackerAngle = 180.0F + MathHelper.wrapAngleTo180_float(attacker.rotationYawHead);
      float angle2 = (float)(Math.atan2(attacker.posX - target.posX, attacker.posZ - target.posZ) * 180.0D / 3.141592653589793D);
      if (angle2 >= 0.0F) {
         angle2 = 180.0F - angle2;
      } else {
         angle2 = -180.0F - angle2;
      }

      angle2 += 180.0F;
      float targetAngle = 180.0F + MathHelper.wrapAngleTo180_float(target.renderYawOffset);
      float difference = 180.0F - Math.abs(Math.abs(attackerAngle - targetAngle) - 180.0F);
      float difference2 = 180.0F - Math.abs(Math.abs(angle2 - targetAngle) - 180.0F);
      return difference < 75.0F && difference2 < 75.0F;
   }

   public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean p_77624_4_) {
      list.add("Backstabs while sneaking");
   }

   @SideOnly(Side.CLIENT)
   private static boolean isFlickActive(ItemStack stack) {
      if (stack == null) return false;
      net.minecraft.nbt.NBTTagCompound tag = stack.getTagCompound();
      if (tag == null || !tag.hasKey(TAG_FLICK_UNTIL, 4)) return false;
      if (Minecraft.getMinecraft() == null || Minecraft.getMinecraft().theWorld == null) return false;
      long now = Minecraft.getMinecraft().theWorld.getTotalWorldTime();
      if (now > tag.getLong(TAG_FLICK_UNTIL)) {
         net.minecraft.nbt.NBTTagCompound copy = (net.minecraft.nbt.NBTTagCompound) tag.copy();
         copy.removeTag(TAG_FLICK_UNTIL);
         if (copy.hasNoTags()) {
            stack.setTagCompound(null);
         } else {
            stack.setTagCompound(copy);
         }
         return false;
      }
      return true;
   }

   private static void clearFlickTagIfExpired(ItemStack stack, World world) {
      if (stack == null) return;
      net.minecraft.nbt.NBTTagCompound tag = stack.getTagCompound();
      if (tag == null || !tag.hasKey(TAG_FLICK_UNTIL, 4)) return;
      long now = world.getTotalWorldTime();
      if (now > tag.getLong(TAG_FLICK_UNTIL)) {
         net.minecraft.nbt.NBTTagCompound copy = (net.minecraft.nbt.NBTTagCompound) tag.copy();
         copy.removeTag(TAG_FLICK_UNTIL);
         if (copy.hasNoTags()) {
            stack.setTagCompound(null);
         } else {
            stack.setTagCompound(copy);
         }
      }
   }
}
