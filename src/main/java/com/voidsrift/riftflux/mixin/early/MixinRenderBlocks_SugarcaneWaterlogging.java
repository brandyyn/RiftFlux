package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.client.render.FullWaterBlockRenderer;
import com.voidsrift.riftflux.client.render.WaterloggingRenderDebug;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.MinecraftForgeClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderBlocks.class)
public abstract class MixinRenderBlocks_SugarcaneWaterlogging {
    @Shadow
    public IBlockAccess blockAccess;

    @Unique
    private IBlockAccess riftflux$previousLiquidBlockAccess;

    @Inject(method = "renderBlockByRenderType", at = @At("HEAD"), cancellable = true)
    private void riftflux$renderSugarcaneWater(Block block, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
        if (!ModConfig.allowSugarcaneInWater || block != Blocks.reeds || this.blockAccess == null) {
            return;
        }
        int worldPass = ForgeHooksClient.getWorldRenderPass();
        int renderPass = MinecraftForgeClient.getRenderPass();
        if (worldPass != 1 && renderPass != 1) {
            return;
        }
        SugarcaneWaterAccess waterAccess = new SugarcaneWaterAccess(this.blockAccess);
        boolean waterlogged = waterAccess.isWaterloggedAt(x, y, z);
        WaterloggingRenderDebug.sugarcaneRenderHook(x, y, z, worldPass, waterlogged);
        if (waterlogged) {
            cir.setReturnValue(FullWaterBlockRenderer.renderStillSurface(waterAccess, x, y, z));
        } else {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "renderBlockLiquid", at = @At("HEAD"))
    private void riftflux$wrapSugarcaneWaterloggedNeighbors(Block block, int x, int y, int z,
                                                            CallbackInfoReturnable<Boolean> cir) {
        if (!ModConfig.allowSugarcaneInWater
                || block == null
                || block.getMaterial() != Material.water
                || this.blockAccess == null
                || this.blockAccess instanceof SugarcaneWaterAccess) {
            return;
        }

        this.riftflux$previousLiquidBlockAccess = this.blockAccess;
        this.blockAccess = new SugarcaneWaterAccess(this.blockAccess);
    }

    @Inject(method = "renderBlockLiquid", at = @At("RETURN"))
    private void riftflux$restoreSugarcaneWaterloggedNeighbors(Block block, int x, int y, int z,
                                                               CallbackInfoReturnable<Boolean> cir) {
        if (this.riftflux$previousLiquidBlockAccess != null) {
            this.blockAccess = this.riftflux$previousLiquidBlockAccess;
            this.riftflux$previousLiquidBlockAccess = null;
        }
    }

    @Unique
    private static class SugarcaneWaterAccess extends FullWaterBlockRenderer.WaterloggedBlockAccess {
        private SugarcaneWaterAccess(IBlockAccess delegate) {
            super(delegate);
        }

        protected boolean isWaterloggedAt(int x, int y, int z) {
            return this.delegate.getBlock(x, y, z) == Blocks.reeds
                    && this.riftflux$hasWaterloggingSource(x, y, z);
        }

        private boolean riftflux$hasWaterloggingSource(int x, int y, int z) {
            return this.isRealWaterAt(x, y + 1, z)
                    || this.isRealWaterAt(x - 1, y, z)
                    || this.isRealWaterAt(x + 1, y, z)
                    || this.isRealWaterAt(x, y, z - 1)
                    || this.isRealWaterAt(x, y, z + 1);
        }
    }
}
