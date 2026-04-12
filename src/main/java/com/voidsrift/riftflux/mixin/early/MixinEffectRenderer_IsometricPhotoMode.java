package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.client.photomode.IsometricPhotoModeController;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EffectRenderer.class)
public abstract class MixinEffectRenderer_IsometricPhotoMode {

    @Inject(
            method = "renderParticles",
            at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glColor4f(FFFF)V", remap = false),
            require = 0
    )
    private void riftflux$syncParticleCamera(Entity entity, float partialTicks, CallbackInfo ci) {
        this.riftflux$applyParticleInterpolation(partialTicks);
    }

    @Inject(method = "renderLitParticles", at = @At("HEAD"))
    private void riftflux$syncLitParticleCamera(Entity entity, float partialTicks, CallbackInfo ci) {
        this.riftflux$applyParticleInterpolation(partialTicks);
    }

    @Unique
    private void riftflux$applyParticleInterpolation(float partialTicks) {
        if (!IsometricPhotoModeController.instance().isActive()) {
            return;
        }

        Entity viewEntity = Minecraft.getMinecraft().renderViewEntity;
        if (viewEntity == null) {
            return;
        }

        EntityFX.interpPosX = viewEntity.lastTickPosX + (viewEntity.posX - viewEntity.lastTickPosX) * (double) partialTicks;
        EntityFX.interpPosY = viewEntity.lastTickPosY + (viewEntity.posY - viewEntity.lastTickPosY) * (double) partialTicks;
        EntityFX.interpPosZ = viewEntity.lastTickPosZ + (viewEntity.posZ - viewEntity.lastTickPosZ) * (double) partialTicks;
    }
}
