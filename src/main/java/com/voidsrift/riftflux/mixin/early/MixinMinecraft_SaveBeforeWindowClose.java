package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.client.ClientWindowCloseSaveHandler;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft_SaveBeforeWindowClose {
    @Inject(
            method = "runGameLoop",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Minecraft;shutdown()V"
            )
    )
    private void riftflux$saveBeforeWindowClose(CallbackInfo ci) {
        ClientWindowCloseSaveHandler.saveIntegratedServerBeforeWindowClose((Minecraft)(Object)this);
    }
}
