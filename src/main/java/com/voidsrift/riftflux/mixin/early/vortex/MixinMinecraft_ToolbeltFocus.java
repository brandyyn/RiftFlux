package com.voidsrift.riftflux.mixin.early.vortex;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.voidsrift.riftflux.vortex.event.KeyEventHandler;
import com.voidsrift.riftflux.vortex.item.ModItems;
import com.voidsrift.riftflux.vortex.lib.helper.ItemHelper;
import com.voidsrift.riftflux.vortex.lib.helper.ToolbeltState;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft_ToolbeltFocus {
    private static boolean riftflux$toolbeltRadialActive() {
        if (ToolbeltState.isRadialActive()) {
            return true;
        }
        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.thePlayer == null) {
            return false;
        }
        if (!KeyEventHandler.isToolbeltKeyDown()) {
            return false;
        }
        return ItemHelper.hasBauble(mc.thePlayer, ModItems.toolbelt);
    }

    @Inject(method = "setIngameFocus", at = @At("HEAD"), cancellable = true)
    private void riftflux$blockToolbeltFocus(CallbackInfo ci) {
        if (riftflux$toolbeltRadialActive()) {
            ci.cancel();
        }
    }

    @Inject(method = "func_147116_af", at = @At("HEAD"), cancellable = true)
    private void riftflux$blockToolbeltLeftClick(CallbackInfo ci) {
        if (riftflux$toolbeltRadialActive()) {
            ci.cancel();
        }
    }

    @Inject(method = "func_147121_ag", at = @At("HEAD"), cancellable = true)
    private void riftflux$blockToolbeltRightClick(CallbackInfo ci) {
        if (riftflux$toolbeltRadialActive()) {
            ci.cancel();
        }
    }
}
