package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.client.PostProcessRenderer;
import net.minecraft.client.renderer.RenderGlobal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderGlobal.class)
public abstract class MixinRenderGlobal_PostProcessSkyBloom {
    @Inject(method = "renderSky(F)V", at = @At("RETURN"), require = 0)
    private void riftflux$renderPostProcessCelestialBloomAfterSky(float partialTicks, CallbackInfo ci) {
        PostProcessRenderer.renderSkyBloomAfterSky(partialTicks);
    }
}
