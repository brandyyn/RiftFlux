package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.client.render.FullWaterBlockRenderer;
import com.voidsrift.riftflux.client.render.WaterloggedBlockRenderAccess;
import com.voidsrift.riftflux.waterlogging.RiftFluxFluidloggedLookup;
import gravestone.block.BlockGSGraveStone;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.ForgeHooksClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderBlocks.class)
public abstract class MixinRenderBlocks_SugarcaneWaterlogging {
    @Shadow
    public IBlockAccess blockAccess;

    @Inject(method = "renderBlockByRenderType", at = @At("HEAD"), cancellable = true)
    private void riftflux$renderSugarcaneWater(Block block, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
        if (!this.riftflux$usesWaterRenderPass(block) || this.blockAccess == null) {
            return;
        }
        int worldPass = ForgeHooksClient.getWorldRenderPass();
        Block fluid = RiftFluxFluidloggedLookup.getSupportedFluidBlock(this.blockAccess, x, y, z);
        if (RiftFluxFluidloggedLookup.hasStoredFluidBlock(this.blockAccess, x, y, z)) {
            if (worldPass == 1) {
                cir.setReturnValue(false);
            }
            return;
        }
        if (fluid == null || !fluid.canRenderInPass(worldPass)) {
            if (worldPass == 1) {
                cir.setReturnValue(false);
            }
            return;
        }
        WaterloggedBlockRenderAccess waterAccess = new WaterloggedBlockRenderAccess(this.blockAccess);
        boolean rendered = FullWaterBlockRenderer.render((RenderBlocks) (Object) this, waterAccess, x, y, z);
        if (worldPass == 1) {
            cir.setReturnValue(rendered);
        }
    }

    private boolean riftflux$usesWaterRenderPass(Block block) {
        return ModConfig.allowSugarcaneInWater && block == Blocks.reeds
                || ModConfig.enableGravestoneModule
                && ModConfig.waterlogGravestones
                && block instanceof BlockGSGraveStone;
    }
}
