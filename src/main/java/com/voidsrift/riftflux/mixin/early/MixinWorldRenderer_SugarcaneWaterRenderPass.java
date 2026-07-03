package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.init.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WorldRenderer.class)
public abstract class MixinWorldRenderer_SugarcaneWaterRenderPass {
    @Redirect(
            method = "updateRenderer",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/Block;getRenderBlockPass()I"
            ),
            require = 0
    )
    private int riftflux$getSugarcaneRenderBlockPass(Block block) {
        if (ModConfig.allowSugarcaneInWater && block == Blocks.reeds) {
            return 0;
        }
        return block.getRenderBlockPass();
    }

    @Redirect(
            method = "updateRenderer",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/Block;canRenderInPass(I)Z",
                    remap = false
            ),
            require = 0
    )
    private boolean riftflux$canRenderSugarcaneInWaterPass(Block block, int pass) {
        if (ModConfig.allowSugarcaneInWater && block == Blocks.reeds) {
            return pass == 0 || pass == 1;
        }
        return block.canRenderInPass(pass);
    }
}
