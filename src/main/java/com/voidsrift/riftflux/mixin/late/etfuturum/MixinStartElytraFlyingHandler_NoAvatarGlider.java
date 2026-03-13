package com.voidsrift.riftflux.mixin.late.etfuturum;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.avatar.glider.GliderItemHelper;
import com.voidsrift.riftflux.compat.OpenBlocksGliderCompat;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetHandlerPlayServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "ganymedes01.etfuturum.network.StartElytraFlyingHandler", remap = false)
public abstract class MixinStartElytraFlyingHandler_NoAvatarGlider {

    @Inject(
            method = "onMessage(Lganymedes01/etfuturum/network/StartElytraFlyingMessage;Lcpw/mods/fml/common/network/simpleimpl/MessageContext;)Lcpw/mods/fml/common/network/simpleimpl/IMessage;",
            at = @At("HEAD"),
            cancellable = true,
            remap = false,
            require = 0
    )
    private void riftflux$blockElytraWhenUsingAvatarGlider(@Coerce Object message,
                                                           MessageContext ctx,
                                                           CallbackInfoReturnable<IMessage> cir) {
        if (!ModConfig.blockEtFuturumElytraWhileAvatarGliding
                || ctx == null
                || !(ctx.netHandler instanceof NetHandlerPlayServer)) {
            return;
        }

        EntityPlayer player = ((NetHandlerPlayServer) ctx.netHandler).playerEntity;
        if (player == null) {
            return;
        }

        if (!GliderItemHelper.isGliderEnabled(player) && !OpenBlocksGliderCompat.isGliderActive(player)) {
            return;
        }

        cir.setReturnValue(null);
    }
}
