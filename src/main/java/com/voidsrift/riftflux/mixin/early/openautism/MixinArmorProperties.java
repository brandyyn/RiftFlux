package com.voidsrift.riftflux.mixin.early.vortex;

import java.util.ArrayList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ISpecialArmor.ArmorProperties;
import com.voidsrift.riftflux.vortex.lib.event.LivingDestroyArmorEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin({ArmorProperties.class})
public abstract class MixinArmorProperties {
   @Inject(
      method = {"ApplyArmor"},
      at = {@At(
   value = "FIELD",
   target = " net/minecraftforge/common/ISpecialArmor$ArmorProperties.Slot:I",
   opcode = 180,
   ordinal = 2
)},
      locals = LocalCapture.CAPTURE_FAILSOFT,
      remap = false
   )
   private static void onApplyArmor(EntityLivingBase entity, ItemStack[] inventory, DamageSource source, double damage, CallbackInfoReturnable<Float> ci, ArrayList dmgVals, ArmorProperties[] props, int level, double ratio, ArmorProperties[] var10, int var11, int var12, ArmorProperties prop, double absorb, ItemStack stack, int itemDamage) {
      if (stack != null) {
         int max = stack.getMaxDamage();
         if (max > 0 && itemDamage > 0 && stack.getItemDamage() + itemDamage >= max) {
            MinecraftForge.EVENT_BUS.post(new LivingDestroyArmorEvent(entity, stack));
         }
      }
   }
}
