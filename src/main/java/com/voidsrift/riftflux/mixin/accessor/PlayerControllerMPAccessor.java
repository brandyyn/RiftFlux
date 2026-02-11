package com.voidsrift.riftflux.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.multiplayer.PlayerControllerMP;

@Mixin(PlayerControllerMP.class)
public interface PlayerControllerMPAccessor {
    @Invoker("syncCurrentPlayItem")
    void riftflux$syncCurrentPlayItem();
}
