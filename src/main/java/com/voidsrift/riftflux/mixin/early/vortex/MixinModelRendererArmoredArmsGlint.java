package com.voidsrift.riftflux.mixin.early.vortex;

import com.voidsrift.riftflux.vortex.lib.helper.ArmoredArmsGlintHelper;
import net.minecraft.client.model.ModelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelRenderer.class)
public abstract class MixinModelRendererArmoredArmsGlint {
    @Inject(method = "render(F)V", at = @At("HEAD"))
    private void riftflux$restoreArmoredArmsArmorGlintColor(float scale, CallbackInfo ci) {
        ArmoredArmsGlintHelper.applyArmorModelGlintColorIfActive();
    }
}
