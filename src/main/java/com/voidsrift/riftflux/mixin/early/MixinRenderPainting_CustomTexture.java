package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.painting.CustomPaintingRegistry;
import net.minecraft.client.renderer.entity.RenderPainting;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderPainting.class)
public abstract class MixinRenderPainting_CustomTexture {

    @Inject(
            method = "getEntityTexture(Lnet/minecraft/entity/item/EntityPainting;)Lnet/minecraft/util/ResourceLocation;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void riftflux$useCustomTexture(EntityPainting painting, CallbackInfoReturnable<ResourceLocation> cir) {
        if (painting == null || painting.art == null) {
            return;
        }

        ResourceLocation customTexture = CustomPaintingRegistry.getCustomTexture(painting.art);
        if (customTexture != null) {
            cir.setReturnValue(customTexture);
        }
    }
}
