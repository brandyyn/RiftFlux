package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import net.minecraft.entity.projectile.EntityArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(EntityArrow.class)
public class MixinEntityArrow_NoRandomSpread {

    @ModifyVariable(
            method = "setThrowableHeading(DDDFF)V",
            at = @At("HEAD"),
            ordinal = 1,
            argsOnly = true
    )
    private float riftflux$removeInaccuracy(float inaccuracy) {
        return ModConfig.vanillaArrowInaccuracy;
    }
}
