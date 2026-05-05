package com.voidsrift.riftflux.mixin.late.thermaldynamics;

import cofh.thermaldynamics.gui.TDCreativeTabCovers;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TDCreativeTabCovers.class, remap = false)
public class MixinTDCreativeTabCovers_NoFacades {

    @Shadow int iconIndex;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void riftflux$removeFacadeCreativeTab(CallbackInfo ci) {
        int index = ((CreativeTabs) (Object) this).getTabIndex();
        if (index >= 0 && index < CreativeTabs.creativeTabArray.length
                && CreativeTabs.creativeTabArray[index] == (Object) this) {
            CreativeTabs.creativeTabArray[index] = null;
        }
    }

    @Inject(method = "getIconItemStack()Lnet/minecraft/item/ItemStack;", at = @At("HEAD"), cancellable = true)
    private void riftflux$stableEmptyFacadeTab(CallbackInfoReturnable<ItemStack> cir) {
        this.iconIndex = 0;
        cir.setReturnValue(new ItemStack(Blocks.stone));
    }

    @Inject(method = "updateIcon()V", at = @At("HEAD"), cancellable = true)
    private void riftflux$skipEmptyFacadeIconRotation(CallbackInfo ci) {
        this.iconIndex = 0;
        ci.cancel();
    }
}
