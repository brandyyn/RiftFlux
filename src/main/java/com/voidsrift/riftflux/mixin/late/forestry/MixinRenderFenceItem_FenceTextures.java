package com.voidsrift.riftflux.mixin.late.forestry;

import com.voidsrift.riftflux.fence.client.FenceItemRenderHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "forestry.arboriculture.render.RenderFenceItem", remap = false)
public abstract class MixinRenderFenceItem_FenceTextures {
    @Inject(method = "renderItem", at = @At("HEAD"), cancellable = true, remap = false)
    private void riftflux$renderCustomFenceItem(IItemRenderer.ItemRenderType type, ItemStack stack, Object[] data, CallbackInfo ci) {
        if (stack == null || data == null || data.length == 0 || !(data[0] instanceof RenderBlocks)) {
            return;
        }

        Block block = Block.getBlockFromItem(stack.getItem());
        if (!(block instanceof BlockFence)) {
            return;
        }

        float x = 0.0F;
        float y = 0.0F;
        float z = 0.0F;
        if (type == IItemRenderer.ItemRenderType.EQUIPPED || type == IItemRenderer.ItemRenderType.EQUIPPED_FIRST_PERSON) {
            x = 0.5F;
            y = 0.5F;
            z = 0.5F;
        }

        if (FenceItemRenderHelper.renderFenceItem((RenderBlocks) data[0], (BlockFence) block, stack.getItemDamage(), x, y, z)) {
            ci.cancel();
        }
    }
}
