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

@Pseudo
@Mixin(targets = "Reika.DragonAPI.Instantiable.Rendering.MultiSheetItemRenderer", remap = false)
public abstract class MixinMultiSheetItemRenderer_PlacedItem {
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
