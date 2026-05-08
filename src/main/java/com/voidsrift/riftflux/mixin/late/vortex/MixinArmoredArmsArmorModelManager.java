package com.voidsrift.riftflux.mixin.late.vortex;

import com.voidsrift.riftflux.vortex.lib.helper.ArmoredArmsGlintHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Pseudo
@Mixin(targets = "com.artur114.armoredarms.client.modelrender.armor.ArmModelManagerArmor", remap = false)
public abstract class MixinArmoredArmsArmorModelManager {
    @Inject(method = "newTextureList", at = @At("HEAD"), cancellable = true, require = 0)
    private void riftflux$useCustomGlintTextureEligibility(CallbackInfoReturnable<List> cir) {
        List textures = ArmoredArmsGlintHelper.newArmorTextureListOrNull(this);
        if (textures != null) {
            cir.setReturnValue(textures);
        }
    }
}
