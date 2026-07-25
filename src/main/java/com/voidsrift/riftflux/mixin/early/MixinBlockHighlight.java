package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.client.blockhighlight.BlockHighlightRenderer;
import net.nmccoy.legendgear.block.CaltropsBlock;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Angelica/Iris wraps drawSelectionBox in a balanced outline pass. Keep the
 * vanilla method running, render RiftFlux's replacement, and suppress only
 * vanilla's outlined AABB call.
 */
@Mixin(value = RenderGlobal.class, priority = 2000)
public abstract class MixinBlockHighlight {

    @Inject(method = "drawSelectionBox", at = @At("HEAD"))
    private void riftflux$renderBlockHighlight(
            EntityPlayer player,
            MovingObjectPosition hit,
            int renderPass,
            float partialTicks,
            CallbackInfo ci
    ) {
        BlockHighlightRenderer.renderOrDefer(player, hit, renderPass, partialTicks);
    }

    @Redirect(
            method = "drawSelectionBox",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/RenderGlobal;drawOutlinedBoundingBox(Lnet/minecraft/util/AxisAlignedBB;I)V"
            ),
            require = 0
    )
    private void riftflux$skipVanillaOutlinedAabb(AxisAlignedBB box, int color) {
    }

    @Redirect(
            method = "drawBlockDamageTexture(Lnet/minecraft/client/renderer/Tessellator;Lnet/minecraft/entity/EntityLivingBase;F)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/RenderBlocks;renderBlockUsingTexture(Lnet/minecraft/block/Block;IIILnet/minecraft/util/IIcon;)V"
            )
    )
    private void riftflux$renderCaltropsDamageBox(
            RenderBlocks renderer,
            Block block,
            int x,
            int y,
            int z,
            IIcon icon
    ) {
        if (!(block instanceof CaltropsBlock)) {
            renderer.renderBlockUsingTexture(block, x, y, z, icon);
            return;
        }

        renderer.setOverrideBlockTexture(icon);
        renderer.setRenderBounds(
                CaltropsBlock.HITBOX_MIN,
                0.0D,
                CaltropsBlock.HITBOX_MIN,
                CaltropsBlock.HITBOX_MAX,
                CaltropsBlock.HITBOX_HEIGHT,
                CaltropsBlock.HITBOX_MAX
        );
        renderer.renderStandardBlock(block, x, y, z);
        renderer.clearOverrideBlockTexture();
        block.setBlockBoundsForItemRender();
    }
}
