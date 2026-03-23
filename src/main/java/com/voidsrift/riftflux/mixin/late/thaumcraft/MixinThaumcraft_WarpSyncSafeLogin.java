package com.voidsrift.riftflux.mixin.late.thaumcraft;

import com.voidsrift.riftflux.compat.thaumcraft.ThaumcraftWarpSyncCompat;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraft.entity.player.EntityPlayerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import thaumcraft.common.Thaumcraft;

@Mixin(value = Thaumcraft.class, remap = false)
public abstract class MixinThaumcraft_WarpSyncSafeLogin {
    @Redirect(
            method = {"addWarpToPlayer", "addStickyWarpToPlayer"},
            at = @At(
                    value = "INVOKE",
                    target = "Lcpw/mods/fml/common/network/simpleimpl/SimpleNetworkWrapper;sendTo(Lcpw/mods/fml/common/network/simpleimpl/IMessage;Lnet/minecraft/entity/player/EntityPlayerMP;)V",
                    remap = false
            ),
            remap = false
    )
    private static void riftflux$skipUnsafeEarlyWarpSync(SimpleNetworkWrapper network, IMessage message, EntityPlayerMP player) {
        if (player == null || player.playerNetServerHandler == null) {
            ThaumcraftWarpSyncCompat.markPending(player);
            return;
        }
        network.sendTo(message, player);
    }
}
