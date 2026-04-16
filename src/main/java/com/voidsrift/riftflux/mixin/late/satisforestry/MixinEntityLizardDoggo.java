package com.voidsrift.riftflux.mixin.late.satisforestry;

import com.voidsrift.riftflux.ModConfig;
import net.minecraft.entity.EntityLiving;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "Reika.Satisforestry.Entity.EntityLizardDoggo", remap = false)
public abstract class MixinEntityLizardDoggo {

    @Inject(method = "getCommandSenderName", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void riftflux$useCustomNameTag(CallbackInfoReturnable<String> cir) {
        if (!ModConfig.satisforestryLizardDoggoAllowNametagRename) {
            return;
        }

        EntityLiving self = (EntityLiving) (Object) this;
        if (self.hasCustomNameTag()) {
            cir.setReturnValue(self.getCustomNameTag());
        }
    }

    @Inject(method = "generateItem", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void riftflux$disableRandomItemFinding(CallbackInfo ci) {
        if (ModConfig.satisforestryLizardDoggoDisableRandomItemFinding) {
            ci.cancel();
        }
    }
}
