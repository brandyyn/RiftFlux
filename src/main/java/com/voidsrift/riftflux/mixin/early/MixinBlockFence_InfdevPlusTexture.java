package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.fence.FenceOverrideClientState;
import com.voidsrift.riftflux.fence.FenceRenderSupport;
import com.voidsrift.riftflux.fence.client.FenceItemRenderHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderBlocks.class)
public abstract class MixinBlockFence_InfdevPlusTexture {
    @Shadow public IBlockAccess blockAccess;
    @Shadow public boolean field_152631_f;

    @Shadow public abstract void setRenderBounds(double minX, double minY, double minZ, double maxX, double maxY, double maxZ);
    @Shadow public abstract boolean renderStandardBlock(Block block, int x, int y, int z);
    @Shadow public abstract void setOverrideBlockTexture(IIcon icon);
    @Shadow public abstract void clearOverrideBlockTexture();
    @Shadow public abstract void renderFaceYNeg(Block block, double x, double y, double z, IIcon icon);
    @Shadow public abstract void renderFaceYPos(Block block, double x, double y, double z, IIcon icon);
    @Shadow public abstract void renderFaceZNeg(Block block, double x, double y, double z, IIcon icon);
    @Shadow public abstract void renderFaceZPos(Block block, double x, double y, double z, IIcon icon);
    @Shadow public abstract void renderFaceXNeg(Block block, double x, double y, double z, IIcon icon);
    @Shadow public abstract void renderFaceXPos(Block block, double x, double y, double z, IIcon icon);

    @Inject(method = "renderBlockFence", at = @At("HEAD"), cancellable = true)
    private void riftflux$renderOakFence(BlockFence block, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
        if (this.riftflux$shouldUseOriginalFenceTexture(x, y, z)) {
            return;
        }

        FenceRenderSupport.FenceRenderTextures textures = FenceRenderSupport.getWorldTextures(block, this.blockAccess, x, y, z);
        if (textures == null) {
            return;
        }

        IIcon railIcon = textures.railIcon;
        IIcon postIcon = textures.postIcon;
        boolean rendered = false;
        float min = 0.375F;
        float max = 0.625F;

        this.setOverrideBlockTexture(postIcon);
        this.setRenderBounds(min, 0.0D, min, max, 1.0D, max);
        rendered = this.renderStandardBlock(block, x, y, z);
        this.clearOverrideBlockTexture();

        boolean eastWest = block.canConnectFenceTo(this.blockAccess, x - 1, y, z) || block.canConnectFenceTo(this.blockAccess, x + 1, y, z);
        boolean northSouth = block.canConnectFenceTo(this.blockAccess, x, y, z - 1) || block.canConnectFenceTo(this.blockAccess, x, y, z + 1);
        boolean west = block.canConnectFenceTo(this.blockAccess, x - 1, y, z);
        boolean east = block.canConnectFenceTo(this.blockAccess, x + 1, y, z);
        boolean north = block.canConnectFenceTo(this.blockAccess, x, y, z - 1);
        boolean south = block.canConnectFenceTo(this.blockAccess, x, y, z + 1);

        if (!eastWest && !northSouth) {
            eastWest = true;
        }

        float railMin = 0.4375F;
        float railMax = 0.5625F;
        float topMin = 0.75F;
        float topMax = 0.9375F;
        float westMin = west ? 0.0F : railMin;
        float eastMax = east ? 1.0F : railMax;
        float northMin = north ? 0.0F : railMin;
        float southMax = south ? 1.0F : railMax;

        this.field_152631_f = true;
        this.setOverrideBlockTexture(railIcon);

        if (eastWest) {
            this.setRenderBounds(westMin, topMin, railMin, eastMax, topMax, railMax);
            rendered |= this.renderStandardBlock(block, x, y, z);
        }

        if (northSouth) {
            this.setRenderBounds(railMin, topMin, northMin, railMax, topMax, southMax);
            rendered |= this.renderStandardBlock(block, x, y, z);
        }

        topMin = 0.375F;
        topMax = 0.5625F;

        if (eastWest) {
            this.setRenderBounds(westMin, topMin, railMin, eastMax, topMax, railMax);
            rendered |= this.renderStandardBlock(block, x, y, z);
        }

        if (northSouth) {
            this.setRenderBounds(railMin, topMin, northMin, railMax, topMax, southMax);
            rendered |= this.renderStandardBlock(block, x, y, z);
        }

        this.clearOverrideBlockTexture();
        this.field_152631_f = false;
        block.setBlockBoundsBasedOnState(this.blockAccess, x, y, z);
        cir.setReturnValue(rendered);
    }

    @Inject(method = "renderBlockAsItem", at = @At("HEAD"), cancellable = true)
    private void riftflux$renderOakFenceItem(Block block, int metadata, float brightness, CallbackInfo ci) {
        if (!(block instanceof BlockFence)) {
            return;
        }

        if (!FenceItemRenderHelper.renderFenceItem((RenderBlocks) (Object) this, (BlockFence) block, metadata, 0.0F, 0.0F, 0.0F)) {
            return;
        }
        ci.cancel();
    }

    private boolean riftflux$shouldUseOriginalFenceTexture(int x, int y, int z) {
        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft == null || minecraft.theWorld == null || minecraft.theWorld.provider == null) {
            return false;
        }
        return FenceOverrideClientState.isOriginalFence(minecraft.theWorld.provider.dimensionId, x, y, z);
    }
}
