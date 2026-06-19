package com.voidsrift.riftflux.mixin.early;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntityNote;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TileEntityNote.class)
public abstract class MixinTileEntityNote_UncoveredSound {

    @Redirect(
            method = "triggerNote",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;getBlock(III)Lnet/minecraft/block/Block;",
                    ordinal = 0
            )
    )
    private Block riftflux$treatCoveredNoteBlockAsUncovered(World world, int x, int y, int z) {
        return Blocks.air;
    }
}
