package com.voidsrift.riftflux.mixin.early.angelica;

import com.voidsrift.riftflux.client.photomode.IsometricPhotoModeController;
import org.embeddedt.embeddium.impl.gl.shader.GlProgram;
import org.embeddedt.embeddium.impl.render.chunk.shader.ChunkFogMode;
import org.embeddedt.embeddium.impl.render.chunk.shader.ChunkShaderComponent;
import org.embeddedt.embeddium.impl.render.chunk.shader.ChunkShaderInterface;
import org.embeddedt.embeddium.impl.render.chunk.terrain.TerrainRenderPass;
import org.embeddedt.embeddium.impl.render.shader.ShaderLoader;
import org.lwjgl.opengl.GL20;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Pseudo
@Mixin(targets = "org.embeddedt.embeddium.impl.render.chunk.ShaderChunkRenderer", remap = false)
public abstract class MixinEmbeddiumShaderChunkRenderer_PhotoMode {

    @Shadow
    protected GlProgram<ChunkShaderInterface> activeProgram;

    @Unique
    private int riftflux$cutawayProgramHandle = -1;

    @Unique
    private int riftflux$cutawayRadiusUniform = -1;

    @Inject(method = "getShaderComponents", at = @At("HEAD"), cancellable = true, require = 0)
    private void riftflux$forceNoFogShaderComponent(CallbackInfoReturnable<List<ChunkShaderComponent.Factory<?>>> cir) {
        if (!IsometricPhotoModeController.instance().isActive()) {
            return;
        }

        List<ChunkShaderComponent.Factory<?>> components = new ArrayList<ChunkShaderComponent.Factory<?>>(1);
        components.add(ChunkFogMode.NONE);
        cir.setReturnValue(components);
    }

    @Redirect(
            method = "loadShader",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/embeddedt/embeddium/impl/render/shader/ShaderLoader;getShaderSource(Ljava/lang/String;)Ljava/lang/String;"
            ),
            require = 0
    )
    private String riftflux$loadCutawayCapableTerrainShader(String shaderName) {
        if (shaderName.endsWith(".vsh")) {
            return ShaderLoader.getShaderSource("riftflux:photo_mode_block_cutaway.vsh");
        }
        if (shaderName.endsWith(".fsh")) {
            return ShaderLoader.getShaderSource("riftflux:photo_mode_block_cutaway.fsh");
        }
        return ShaderLoader.getShaderSource(shaderName);
    }

    @Inject(method = "begin", at = @At("TAIL"), require = 0)
    private void riftflux$setPhotoModeCutawayRadius(TerrainRenderPass pass, CallbackInfo ci) {
        if (this.activeProgram == null) {
            return;
        }

        int programHandle = this.activeProgram.handle();
        if (this.riftflux$cutawayProgramHandle != programHandle) {
            this.riftflux$cutawayProgramHandle = programHandle;
            this.riftflux$cutawayRadiusUniform = GL20.glGetUniformLocation(programHandle, "u_RiftFluxCutawayRadius");
        }
        if (this.riftflux$cutawayRadiusUniform >= 0) {
            GL20.glUniform1f(
                    this.riftflux$cutawayRadiusUniform,
                    IsometricPhotoModeController.instance().getTerrainCutawayRadius()
            );
        }
    }
}
