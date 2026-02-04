package com.voidsrift.riftflux.mixin;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Ensures items with our "customGlint" tag actually render an enchantment glint.
 * Many items are otherwise unenchanted, so RenderItem never draws the glint layer.
 */
@Mixin(ItemStack.class)
public abstract class MixinItemStackCustomGlint {

    @Inject(method = "func_77962_s", at = @At("HEAD"), cancellable = true)
    private void riftflux$forceGlintWhenCustom(CallbackInfoReturnable<Boolean> cir) {
        try {
            ItemStack self = (ItemStack) (Object) this;
            if (self.hasTagCompound() && self.getTagCompound().hasKey("customGlint", 3)) {
                cir.setReturnValue(true);
            }
        } catch (Throwable ignored) {
        }
    }
}
