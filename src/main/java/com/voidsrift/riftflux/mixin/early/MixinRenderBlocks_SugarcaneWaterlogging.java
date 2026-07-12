package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.client.render.FullWaterBlockRenderer;
import com.voidsrift.riftflux.client.render.WaterloggedBlockRenderAccess;
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
        if (!ModConfig.allowSugarcaneInWater || block != Blocks.reeds || this.blockAccess == null) {
            return;
        }
        int worldPass = ForgeHooksClient.getWorldRenderPass();
        if (worldPass != 1) {
            return;
        }
        WaterloggedBlockRenderAccess waterAccess = new WaterloggedBlockRenderAccess(this.blockAccess);
        boolean waterlogged = waterAccess.isWaterloggedAt(x, y, z);
        if (waterlogged) {
            cir.setReturnValue(FullWaterBlockRenderer.render((RenderBlocks) (Object) this, waterAccess, x, y, z));
        } else {
            cir.setReturnValue(false);
        }
    }
}
