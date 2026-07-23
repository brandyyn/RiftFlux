package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import gravestone.block.BlockGSGraveStone;
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
        if (riftflux$usesWaterRenderPass(block)) {
            return 1;
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
        if (riftflux$usesWaterRenderPass(block)) {
            return pass == 0 || pass == 1;
        }
        return block.canRenderInPass(pass);
    }

    private static boolean riftflux$usesWaterRenderPass(Block block) {
        return ModConfig.allowSugarcaneInWater && block == Blocks.reeds
                || ModConfig.enableGravestoneModule
                && ModConfig.waterlogGravestones
                && block instanceof BlockGSGraveStone;
    }
}
