package com.voidsrift.riftflux.mixin.early.vortex;

import com.voidsrift.riftflux.vortex.lib.event.LivingDestroyArmorEvent;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.ISpecialArmor.ArmorProperties;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ArmorProperties.class})
public abstract class MixinArmorProperties {
   @Unique
   private static final ThreadLocal<ItemStack[]> riftflux$armorSnapshot = new ThreadLocal<ItemStack[]>();

   @Inject(
      method = {"ApplyArmor"},
      at = {@At("HEAD")},
      remap = false
   )
   private static void riftflux$captureArmorState(EntityLivingBase entity, ItemStack[] inventory, DamageSource source, double damage, CallbackInfoReturnable<Float> ci) {
      if (inventory == null) {
         riftflux$armorSnapshot.remove();
         return;
      }

      ItemStack[] snapshot = null;
      for (int i = 0; i < inventory.length; i++) {
         ItemStack stack = inventory[i];
         if (stack != null && stack.getMaxDamage() > 0) {
            if (snapshot == null) {
               snapshot = new ItemStack[inventory.length];
            }
            snapshot[i] = stack.copy();
         }
      }

      if (snapshot == null) {
         riftflux$armorSnapshot.remove();
      } else {
         riftflux$armorSnapshot.set(snapshot);
      }
   }

   @Inject(
      method = {"ApplyArmor"},
      at = {@At("RETURN")},
      remap = false
   )
   private static void riftflux$postDestroyArmorEvents(EntityLivingBase entity, ItemStack[] inventory, DamageSource source, double damage, CallbackInfoReturnable<Float> ci) {
      ItemStack[] snapshot = riftflux$armorSnapshot.get();
      riftflux$armorSnapshot.remove();
      if (snapshot == null || inventory == null) {
         return;
      }

      int size = Math.min(snapshot.length, inventory.length);
      for (int i = 0; i < size; i++) {
         ItemStack before = snapshot[i];
         if (before == null || before.getMaxDamage() <= 0) {
            continue;
         }
         if (inventory[i] == null) {
            MinecraftForge.EVENT_BUS.post(new LivingDestroyArmorEvent(entity, before));
         }
      }
   }
}
