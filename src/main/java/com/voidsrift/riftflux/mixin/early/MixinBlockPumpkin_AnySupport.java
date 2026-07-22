package com.voidsrift.riftflux.mixin.early;

import net.minecraft.block.BlockPumpkin;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = BlockPumpkin.class, priority = 1100)
public abstract class MixinBlockPumpkin_AnySupport {

    /**
     * @author RiftFlux
     * @reason Allow any non-air support while winning conflicts with BugTorch's placement overwrite.
     */
    @Overwrite
    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        return world.getBlock(x, y, z).getMaterial().isReplaceable()
                && world.getBlock(x, y - 1, z).getMaterial() != Material.air;
    }
}
