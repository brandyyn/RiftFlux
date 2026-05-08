package com.voidsrift.riftflux.mixin.late.vortex;

import com.voidsrift.riftflux.vortex.lib.helper.ArmoredArmsGlintHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Pseudo
@Mixin(targets = "com.artur114.armoredarms.client.util.TextureEnchant", remap = false)
public abstract class MixinArmoredArmsArmorGlintTexture {
    @Inject(method = "bind", at = @At("HEAD"), require = 0)
    private void riftflux$beginChestArmorGlintPass(CallbackInfo ci) {
        ArmoredArmsGlintHelper.beginArmorGlintPass();
    }

    @ModifyArgs(
            method = "bind",
            at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glColor4f(FFFF)V", ordinal = 1),
            require = 0
    )
    private void riftflux$colorChestArmorGlint(Args args) {
        ArmoredArmsGlintHelper.applyChestArmorGlintArgs(args);
    }

    @Inject(method = "postBind", at = @At("HEAD"), require = 0)
    private void riftflux$resetChestArmorGlintBlend(CallbackInfo ci) {
        ArmoredArmsGlintHelper.endArmorGlintPass();
    }
}
