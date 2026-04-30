package com.voidsrift.riftflux.mixin.early;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.handshake.NetworkDispatcher;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = FMLProxyPacket.class, remap = false)
public abstract class MixinFMLProxyPacket_NullDispatcherReject {
    @Redirect(
            method = "func_148833_a",
            at = @At(
                    value = "INVOKE",
                    target = "Lcpw/mods/fml/common/network/handshake/NetworkDispatcher;rejectHandshake(Ljava/lang/String;)V",
                    ordinal = 0
            )
    )
    private void riftflux$rejectNetworkExceptionIfPossible(NetworkDispatcher dispatcher, String result) {
        rejectIfPossible(dispatcher, result);
    }

    @Redirect(
            method = "func_148833_a",
            at = @At(
                    value = "INVOKE",
                    target = "Lcpw/mods/fml/common/network/handshake/NetworkDispatcher;rejectHandshake(Ljava/lang/String;)V",
                    ordinal = 1
            )
    )
    private void riftflux$rejectCriticalExceptionIfPossible(NetworkDispatcher dispatcher, String result) {
        rejectIfPossible(dispatcher, result);
    }

    private static void rejectIfPossible(NetworkDispatcher dispatcher, String result) {
        if (dispatcher != null) {
            dispatcher.rejectHandshake(result);
            return;
        }
        FMLLog.warning("RiftFlux prevented a secondary Forge crash while rejecting a failed FML packet: %s", result);
    }
}
