package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityLiving.class)
public abstract class MixinEntityLiving_JackOLanternArmorPosition {
    @Inject(method = "getArmorPosition", at = @At("HEAD"), cancellable = true)
    private static void riftflux$treatJackOLanternAsHelmet(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if (!ModConfig.enableJackOLanternHelmet || stack == null) {
            return;
        }
        if (stack.getItem() == Item.getItemFromBlock(Blocks.lit_pumpkin)) {
            cir.setReturnValue(4);
        }
    }
}
