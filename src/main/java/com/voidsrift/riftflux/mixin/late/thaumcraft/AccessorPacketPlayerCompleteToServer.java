package com.voidsrift.riftflux.mixin.late.thaumcraft;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import thaumcraft.common.lib.network.playerdata.PacketPlayerCompleteToServer;

@Mixin(value = PacketPlayerCompleteToServer.class, remap = false)
public interface AccessorPacketPlayerCompleteToServer {
    @Accessor("key")
    String riftflux$getKey();

    @Accessor("dim")
    int riftflux$getDim();

    @Accessor("username")
    String riftflux$getUsername();

    @Accessor("type")
    byte riftflux$getType();
}
