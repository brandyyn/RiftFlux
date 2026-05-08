package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.jukebox.JukeboxLoopHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockJukebox;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockJukebox.class)
public abstract class MixinBlockJukebox_RedstoneRestart {
    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor) {
        JukeboxLoopHelper.handleRedstoneChange(world, x, y, z);
    }
}
