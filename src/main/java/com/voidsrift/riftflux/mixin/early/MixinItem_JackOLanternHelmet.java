package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class MixinItem_JackOLanternHelmet {
    @Inject(method = "isValidArmor", at = @At("HEAD"), cancellable = true, remap = false)
    private void riftflux$allowJackOLanternHelmet(ItemStack stack, int armorType, net.minecraft.entity.Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (!ModConfig.enableJackOLanternHelmet || stack == null || armorType != 0) {
            return;
        }
        if ((Object) this == Item.getItemFromBlock(Blocks.lit_pumpkin)) {
            cir.setReturnValue(true);
        }
    }
}
