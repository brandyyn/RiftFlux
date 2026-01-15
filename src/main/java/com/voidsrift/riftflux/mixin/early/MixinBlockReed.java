package com.voidsrift.riftflux.mixin.early;

import net.minecraft.block.BlockReed;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(BlockReed.class)
public abstract class MixinBlockReed {

    /**
     * Remove biome tinting from sugar cane.
     */
    @Overwrite
    public int colorMultiplier(IBlockAccess world, int x, int y, int z) {
        return 0xFFFFFF; // always render white
    }
}
