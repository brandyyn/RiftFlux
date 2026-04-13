package com.voidsrift.riftflux.mixin.early.angelica;

import com.voidsrift.riftflux.client.photomode.IsometricPhotoModeController;
import org.embeddedt.embeddium.impl.render.chunk.shader.ChunkFogMode;
import org.embeddedt.embeddium.impl.render.chunk.shader.ChunkShaderComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Pseudo
@Mixin(targets = "org.embeddedt.embeddium.impl.render.chunk.ShaderChunkRenderer", remap = false)
public abstract class MixinEmbeddiumShaderChunkRenderer_PhotoMode {

    @Inject(method = "getShaderComponents", at = @At("HEAD"), cancellable = true, require = 0)
    private void riftflux$forceNoFogShaderComponent(CallbackInfoReturnable<List<ChunkShaderComponent.Factory<?>>> cir) {
        if (!IsometricPhotoModeController.instance().isActive()) {
            return;
        }

        List<ChunkShaderComponent.Factory<?>> components = new ArrayList<ChunkShaderComponent.Factory<?>>(1);
        components.add(ChunkFogMode.NONE);
        cir.setReturnValue(components);
    }
}
