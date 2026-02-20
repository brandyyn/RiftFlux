package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.painting.CustomPaintingRegistry;
import net.minecraft.entity.item.EntityPainting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPainting.EnumArt.class)
public abstract class MixinEntityPainting_ExtraArt {

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void riftflux$registerExtraArt(CallbackInfo ci) {
        CustomPaintingRegistry.registerFromConfig();
    }
}
