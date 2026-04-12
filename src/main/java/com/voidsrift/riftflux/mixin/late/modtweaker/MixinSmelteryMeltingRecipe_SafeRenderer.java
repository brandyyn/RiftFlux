package com.voidsrift.riftflux.mixin.late.modtweaker;

import com.voidsrift.riftflux.compat.modtweaker.ModTweakerSmelteryCompat;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "modtweaker2.mods.tconstruct.handlers.Smeltery$MeltingRecipe", remap = false)
public abstract class MixinSmelteryMeltingRecipe_SafeRenderer {

    @Shadow
    @Final
    public ItemStack input;

    @Shadow
    @Final
    public ItemStack renderer;

    @Inject(method = "getRendererBlock", at = @At("HEAD"), cancellable = true, require = 0)
    private void riftflux$useSafeRendererBlock(CallbackInfoReturnable<Block> cir) {
        if (!ModTweakerSmelteryCompat.needsRendererFallback(renderer)) {
            return;
        }
        cir.setReturnValue(ModTweakerSmelteryCompat.resolveRendererBlock(input, renderer));
    }

    @Inject(method = "getRendererMeta", at = @At("HEAD"), cancellable = true, require = 0)
    private void riftflux$useSafeRendererMeta(CallbackInfoReturnable<Integer> cir) {
        if (!ModTweakerSmelteryCompat.needsRendererFallback(renderer)) {
            return;
        }
        cir.setReturnValue(ModTweakerSmelteryCompat.resolveRendererMeta(input, renderer));
    }
}
