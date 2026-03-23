package com.voidsrift.riftflux.vortex.item;

import com.voidsrift.riftflux.util.ConfigResolver;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import de.rinonline.korinrpg.Helper.NBT.RINPlayer2;

public class ItemButterflyKnife extends ItemSword {
   public IIcon icon;
   public IIcon iconFlick;
   public IIcon iconAlt;
   private static final String TAG_FLICK_UNTIL = "rf_flick_until";
   private static final String TAG_FLICK_ALT = "rf_flick_alt";
   private static final String TAG_LAST_BACKSTAB = "rf_last_backstab";
   private static final String TAG_BACKSTAB_COUNT = "rf_backtrak";
   private static final int FLICK_TICKS = 12;

   public ItemButterflyKnife(ToolMaterial material) {
      super(material);
   }

   @SideOnly(Side.CLIENT)
   public void registerIcons(IIconRegister p_94581_1_) {
      this.icon = p_94581_1_.registerIcon("riftflux:butterflyknife");
      this.iconFlick = p_94581_1_.registerIcon("riftflux:butterflyknife_flick");
      this.iconAlt = p_94581_1_.registerIcon("riftflux:butterflyknifealt");
   }

   @SideOnly(Side.CLIENT)
   public IIcon getIconFromDamage(int par1) {
      return this.icon;
   }

   @SideOnly(Side.CLIENT)
   public IIcon getIconIndex(ItemStack stack) {
      // Keep GUI/inventory rendering on the base icon.
      return this.icon;
   }

   @SideOnly(Side.CLIENT)
   public IIcon getIcon(ItemStack stack, int pass) {
      if (this.iconFlick != null && isFlickActive(stack)) {
         return this.iconFlick;
      }
      if (this.iconAlt != null && isAltActiveForRender(stack) && isEquipped(stack)) {
         return this.iconAlt;
      }
      return this.icon;
   }

   public ItemStack onItemRightClick(ItemStack p_77659_1_, World p_77659_2_, EntityPlayer p_77659_3_) {
      if (p_77659_2_ != null) {
         long until = p_77659_2_.getTotalWorldTime() + FLICK_TICKS;
         net.minecraft.nbt.NBTTagCompound tag = p_77659_1_.getTagCompound();
         tag = (tag == null) ? new net.minecraft.nbt.NBTTagCompound() : (net.minecraft.nbt.NBTTagCompound) tag.copy();
         tag.setLong(TAG_FLICK_UNTIL, until);
         boolean alt = tag.getBoolean(TAG_FLICK_ALT);
         if (alt) {
            tag.removeTag(TAG_FLICK_ALT);
         } else {
            tag.setBoolean(TAG_FLICK_ALT, true);
         }
         p_77659_1_.setTagCompound(tag);
         if (!p_77659_2_.isRemote) {
            p_77659_2_.playSoundAtEntity(p_77659_3_, "riftflux:butterflyknife_flick", 0.6F, 1.0F);
            maybeApplyBackstabBoost(p_77659_1_, p_77659_2_, p_77659_3_);
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
         markBackstabSuccess(stack, player.worldObj);
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
      list.add("Backstabs while sneaking, Right click to flip");
      list.add("Flipping after a successful backstab gives a boost");
      if (com.voidsrift.riftflux.ModConfig.butterflyKnifeShowBackstabCounter) {
         int count = getBackstabCount(stack);
         String label = com.voidsrift.riftflux.ModConfig.butterflyKnifeBackstabCounterLabel;
         if (label == null || label.trim().isEmpty()) {
            label = "BackTrak: %d";
         }
         list.add(String.format(label, count));
      }
   }

   @SideOnly(Side.CLIENT)
   private static boolean isFlickActive(ItemStack stack) {
      if (stack == null) return false;
      net.minecraft.nbt.NBTTagCompound tag = stack.getTagCompound();
      if (tag == null || !tag.hasKey(TAG_FLICK_UNTIL, 4)) return false;
      if (Minecraft.getMinecraft() == null || Minecraft.getMinecraft().theWorld == null) return false;
      long now = Minecraft.getMinecraft().theWorld.getTotalWorldTime();
      if (now > tag.getLong(TAG_FLICK_UNTIL)) {
         clearFlickTagIfExpired(stack, Minecraft.getMinecraft().theWorld);
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
         endFlick(stack, tag);
      }
   }

   @SideOnly(Side.CLIENT)
   private static boolean isEquipped(ItemStack stack) {
      Minecraft mc = Minecraft.getMinecraft();
      if (mc == null || mc.thePlayer == null) return false;
      return mc.thePlayer.getCurrentEquippedItem() == stack;
   }

   @SideOnly(Side.CLIENT)
   private static boolean isAltActiveForRender(ItemStack stack) {
      if (stack == null) return false;
      net.minecraft.nbt.NBTTagCompound tag = stack.getTagCompound();
      return tag != null && tag.getBoolean(TAG_FLICK_ALT);
   }

   private static void markBackstabSuccess(ItemStack stack, World world) {
      if (stack == null || world == null) return;
      net.minecraft.nbt.NBTTagCompound tag = stack.getTagCompound();
      if (tag == null) {
         tag = new net.minecraft.nbt.NBTTagCompound();
      }
      tag.setLong(TAG_LAST_BACKSTAB, world.getTotalWorldTime());
      int count = tag.getInteger(TAG_BACKSTAB_COUNT);
      tag.setInteger(TAG_BACKSTAB_COUNT, Math.max(0, count + 1));
      stack.setTagCompound(tag);
   }

   private static int getBackstabCount(ItemStack stack) {
      if (stack == null) return 0;
      net.minecraft.nbt.NBTTagCompound tag = stack.getTagCompound();
      return tag != null ? tag.getInteger(TAG_BACKSTAB_COUNT) : 0;
   }

   private static void maybeApplyBackstabBoost(ItemStack stack, World world, EntityPlayer player) {
      if (stack == null || world == null || player == null) return;
      if (com.voidsrift.riftflux.ModConfig.butterflyKnifeFlickBoostWindowTicks <= 0) return;
      net.minecraft.nbt.NBTTagCompound tag = stack.getTagCompound();
      if (tag == null || !tag.hasKey(TAG_LAST_BACKSTAB, 4)) return;
      long last = tag.getLong(TAG_LAST_BACKSTAB);
      if (world.getTotalWorldTime() - last > com.voidsrift.riftflux.ModConfig.butterflyKnifeFlickBoostWindowTicks) {
         return;
      }
      List<PotionEffect> effects = buildBoostEffects();
      for (PotionEffect effect : effects) {
         if (effect != null) {
            player.addPotionEffect(new PotionEffect(effect.getPotionID(), effect.getDuration(), effect.getAmplifier(), true));
         }
      }
      maybeApplyStaminaBoost(player);
      tag.removeTag(TAG_LAST_BACKSTAB);
      stack.setTagCompound(tag.hasNoTags() ? null : tag);
   }

   private static void maybeApplyStaminaBoost(EntityPlayer player) {
      if (player == null) return;
      if (!com.voidsrift.riftflux.ModConfig.dssEnabled) return;
      if (!com.voidsrift.riftflux.ModConfig.butterflyKnifeStaminaBoost) return;
      float amount = com.voidsrift.riftflux.ModConfig.butterflyKnifeStaminaBoostAmount;
      if (amount <= 0.0F) return;
      RINPlayer2 props = RINPlayer2.get(player);
      if (props == null) return;
      props.restoreStamina(amount);
   }

   private static List<PotionEffect> buildBoostEffects() {
      String[] entries = com.voidsrift.riftflux.ModConfig.butterflyKnifeFlickBoostEffects;
      if (entries == null || entries.length == 0) {
         return Collections.emptyList();
      }
      List<PotionEffect> effects = new ArrayList<>();
       for (String entry : entries) {
          if (entry == null) continue;
          String raw = entry.trim();
          if (raw.isEmpty()) continue;
          String[] parts = raw.split("\\s*,\\s*");
          if (parts.length < 3) continue;
          int potionId = parsePotionId(parts[0]);
          if (potionId < 0 || potionId >= Potion.potionTypes.length || Potion.potionTypes[potionId] == null) {
             continue;
          }
          int amp = parseIntSafe(parts[1], 0);
          int durationTicks = parseDurationTicks(parts[2]);
          if (durationTicks <= 0) continue;
          effects.add(new PotionEffect(potionId, durationTicks, amp, true));
       }
       return effects;
    }

    private static int parsePotionId(String token) {
       if (token == null) return -1;
       String trimmed = token.trim();
       if (trimmed.isEmpty()) return -1;
       try {
          return Integer.parseInt(trimmed);
       } catch (NumberFormatException ignored) {
       }
       String normalized = trimmed.toLowerCase(Locale.ROOT);
       if (normalized.startsWith("potion.")) {
          normalized = normalized.substring("potion.".length());
       }
       if (normalized.equals("speed")) normalized = "movespeed";
       if (normalized.equals("slowness")) normalized = "moveslowdown";
       if (normalized.equals("haste")) normalized = "digspeed";
       if (normalized.equals("miningfatigue")) normalized = "digslowdown";
       if (normalized.equals("strength")) normalized = "damageboost";
       if (normalized.equals("jump")) normalized = "jump";
       if (normalized.equals("regen")) normalized = "regeneration";
       for (Potion potion : Potion.potionTypes) {
          if (potion == null) continue;
          String name = potion.getName();
          if (name == null) continue;
          String simple = name.toLowerCase(Locale.ROOT);
          if (simple.startsWith("potion.")) {
             simple = simple.substring("potion.".length());
          }
          if (simple.equals(normalized)) {
             return potion.id;
          }
       }
       return -1;
    }
 
    private static int parseDurationTicks(String token) {
       if (token == null) return 0;
       String trimmed = token.trim().toLowerCase(Locale.ROOT);
      boolean seconds = trimmed.contains(".") || trimmed.endsWith("s");
      if (trimmed.endsWith("s")) {
         trimmed = trimmed.substring(0, trimmed.length() - 1);
      }
      float value;
      try {
         value = Float.parseFloat(trimmed);
      } catch (NumberFormatException ignored) {
         return 0;
      }
      if (seconds) {
         return Math.max(1, Math.round(value * 20.0F));
      }
      return Math.max(1, (int) value);
   }

   private static int parseIntSafe(String token, int fallback) {
      if (token == null) return fallback;
      try {
         return Integer.parseInt(token.trim());
      } catch (NumberFormatException ignored) {
         return fallback;
      }
   }

   @Override
   public Set getToolClasses(ItemStack stack) {
      return Collections.singleton("sword");
   }

   private static void endFlick(ItemStack stack, net.minecraft.nbt.NBTTagCompound tag) {
      if (stack == null || tag == null) return;
      net.minecraft.nbt.NBTTagCompound copy = (net.minecraft.nbt.NBTTagCompound) tag.copy();
      copy.removeTag(TAG_FLICK_UNTIL);
      if (copy.hasNoTags()) {
         stack.setTagCompound(null);
      } else {
         stack.setTagCompound(copy);
      }
   }
}
