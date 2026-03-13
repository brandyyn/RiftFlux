package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.vortex.respawn.RespawnDelayHelper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.client.C16PacketClientStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetHandlerPlayServer.class)
public abstract class MixinNetHandlerPlayServer_RespawnDelay {
    @Shadow public EntityPlayerMP playerEntity;

    @Inject(
            method = "processClientStatus(Lnet/minecraft/network/play/client/C16PacketClientStatus;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void riftflux$blockRespawnWhileDelayActive(C16PacketClientStatus packet, CallbackInfo ci) {
        if (packet == null || packet.func_149435_c() != C16PacketClientStatus.EnumState.PERFORM_RESPAWN) {
            return;
        }
        if (!RespawnDelayHelper.shouldBlockRespawn(this.playerEntity)) {
            return;
        }
        RespawnDelayHelper.sync(this.playerEntity);
        ci.cancel();
    }
}
