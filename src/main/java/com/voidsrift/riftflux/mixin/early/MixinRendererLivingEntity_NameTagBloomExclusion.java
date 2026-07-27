package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.client.EntityNameTagBloomExclusion;
import com.voidsrift.riftflux.client.EntityNameTagRenderBridge;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RendererLivingEntity.class)
public abstract class MixinRendererLivingEntity_NameTagBloomExclusion implements EntityNameTagRenderBridge {

    @Shadow
    protected abstract void passSpecialRender(
            EntityLivingBase entity,
            double x,
            double y,
            double z
    );

    @Inject(
            method = "passSpecialRender(Lnet/minecraft/entity/EntityLivingBase;DDD)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void riftflux$deferNameTagUntilAfterBloom(
            EntityLivingBase entity,
            double x,
            double y,
            double z,
            CallbackInfo ci
    ) {
        if (!EntityNameTagBloomExclusion.shouldDefer()) {
            return;
        }
        EntityNameTagBloomExclusion.defer(
                (RendererLivingEntity) (Object) this,
                entity,
                x,
                y,
                z
        );
        ci.cancel();
    }

    @Override
    public void riftflux$renderDeferredNameTag(
            EntityLivingBase entity,
            double x,
            double y,
            double z
    ) {
        this.passSpecialRender(entity, x, y, z);
    }
}
