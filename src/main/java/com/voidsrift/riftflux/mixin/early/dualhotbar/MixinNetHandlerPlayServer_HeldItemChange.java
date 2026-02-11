package com.voidsrift.riftflux.mixin.early.dualhotbar;

import net.minecraft.network.NetHandlerPlayServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import com.voidsrift.riftflux.dualhotbar.DualHotbarState;

@Mixin(NetHandlerPlayServer.class)
public class MixinNetHandlerPlayServer_HeldItemChange {
    @ModifyConstant(
            method = "processHeldItemChange(Lnet/minecraft/network/play/client/C09PacketHeldItemChange;)V",
            constant = @Constant(intValue = 9),
            require = 0
    )
    private int riftflux$expandHotbarLimit(int original) {
        return DualHotbarState.hotbarSize;
    }

    @Redirect(
            method = "processHeldItemChange(Lnet/minecraft/network/play/client/C09PacketHeldItemChange;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/InventoryPlayer;getHotbarSize()I"
            ),
            require = 0
    )
    private int riftflux$expandHotbarLimitCall() {
        return DualHotbarState.hotbarSize;
    }
}
