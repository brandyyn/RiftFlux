package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.ForgeHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ForgeHooks.class)
public class MixinForgeHooks {

    @Inject(method = "getTotalArmorValue",at = @At("RETURN"),cancellable = true, remap = false)
    private static void inject(EntityPlayer player, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue((int)(cir.getReturnValue() * ModConfig.protectionMultiplier));
    }
}
