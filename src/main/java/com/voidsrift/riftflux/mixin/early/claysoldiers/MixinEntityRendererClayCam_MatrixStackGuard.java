package com.voidsrift.riftflux.mixin.early.claysoldiers;

import de.sanandrew.mods.claysoldiers.client.render.EntityRendererClayCam;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Keeps Clay Cam from carrying leaked modelview pushes into later frames.
 */
@Mixin(value = EntityRendererClayCam.class, remap = false)
public abstract class MixinEntityRendererClayCam_MatrixStackGuard {
    @Unique
    private int riftflux$modelViewDepth;

    @Inject(method = "func_78480_b(F)V", at = @At("HEAD"), require = 1, remap = false)
    private void riftflux$captureModelViewDepth(float partialTicks, CallbackInfo ci) {
        this.riftflux$modelViewDepth = GL11.glGetInteger(GL11.GL_MODELVIEW_STACK_DEPTH);
    }

    @Inject(method = "func_78480_b(F)V", at = @At("RETURN"), require = 1, remap = false)
    private void riftflux$restoreModelViewDepth(float partialTicks, CallbackInfo ci) {
        int previousMatrixMode = GL11.glGetInteger(GL11.GL_MATRIX_MODE);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        int currentDepth = GL11.glGetInteger(GL11.GL_MODELVIEW_STACK_DEPTH);
        while (currentDepth > this.riftflux$modelViewDepth) {
            GL11.glPopMatrix();
            currentDepth--;
        }
        GL11.glMatrixMode(previousMatrixMode);
    }
}
