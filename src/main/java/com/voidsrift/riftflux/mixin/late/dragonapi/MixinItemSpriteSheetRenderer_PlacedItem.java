package com.voidsrift.riftflux.mixin.late.dragonapi;

import com.voidsrift.riftflux.placeditem.DragonApiPlacedItemRenderer;
import com.voidsrift.riftflux.placeditem.VanillaToolRenderContext;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "Reika.DragonAPI.Instantiable.Rendering.ItemSpriteSheetRenderer", remap = false)
public abstract class MixinItemSpriteSheetRenderer_PlacedItem {
    @Inject(method = "shouldUseRenderHelper", at = @At("HEAD"), cancellable = true, remap = false)
    private void riftflux$useFlatPlacedItemTransform(IItemRenderer.ItemRenderType type, ItemStack stack,
            IItemRenderer.ItemRendererHelper helper, CallbackInfoReturnable<Boolean> cir) {
        if (VanillaToolRenderContext.isActive()
                && type == IItemRenderer.ItemRenderType.ENTITY
                && helper == IItemRenderer.ItemRendererHelper.BLOCK_3D) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "renderItem", at = @At("HEAD"), cancellable = true, remap = false)
    private void riftflux$renderLikeVanillaTool(IItemRenderer.ItemRenderType type, ItemStack stack,
            Object[] data, CallbackInfo ci) {
        if (VanillaToolRenderContext.isActive()
                && type == IItemRenderer.ItemRenderType.ENTITY
                && DragonApiPlacedItemRenderer.render(stack)) {
            ci.cancel();
        }
    }
}
