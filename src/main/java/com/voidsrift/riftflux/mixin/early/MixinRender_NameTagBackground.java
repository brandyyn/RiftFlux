package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Render.class)
public abstract class MixinRender_NameTagBackground {

    @Redirect(
            method = "func_147906_a(Lnet/minecraft/entity/Entity;Ljava/lang/String;DDDI)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/Tessellator;setColorRGBA_F(FFFF)V"
            )
    )
    private void riftflux$configureNameTagBackground(
            Tessellator tessellator,
            float red,
            float green,
            float blue,
            float alpha
    ) {
        tessellator.setColorRGBA_F(
                red,
                green,
                blue,
                ModConfig.showEntityNameTagBackground ? alpha : 0.0F
        );
    }
}
