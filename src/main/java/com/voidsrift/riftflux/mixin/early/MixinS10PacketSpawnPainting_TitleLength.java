package com.voidsrift.riftflux.mixin.early;

import net.minecraft.network.play.server.S10PacketSpawnPainting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(S10PacketSpawnPainting.class)
public abstract class MixinS10PacketSpawnPainting_TitleLength {

    @ModifyArg(
            method = "readPacketData",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/PacketBuffer;readStringFromBuffer(I)Ljava/lang/String;"
            ),
            index = 0
    )
    private int riftflux$increaseMotiveNameLimit(int vanillaLimit) {
        return Math.max(vanillaLimit, 256);
    }
}
