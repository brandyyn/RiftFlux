package com.voidsrift.riftflux.mixin.accessor.angelica;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Accessor;

@Pseudo
@Mixin(targets = "com.gtnewhorizons.angelica.rendering.celeritas.world.WorldSlice", remap = false)
public interface WorldSliceAccessor {

    @Accessor("world")
    WorldClient riftflux$getWorld();

    @Accessor("volume")
    StructureBoundingBox riftflux$getVolume();
}
