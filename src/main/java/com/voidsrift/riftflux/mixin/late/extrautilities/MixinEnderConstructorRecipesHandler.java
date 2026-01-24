package com.voidsrift.riftflux.mixin.late.extrautilities;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.rwtema.extrautils.tileentity.enderconstructor.EnderConstructorRecipesHandler;

@Mixin(value = EnderConstructorRecipesHandler.class, remap = false)
public class MixinEnderConstructorRecipesHandler {
    /**
     * @author Charsy89
     * @reason Extra Utilities complains about ItemStacks that have null base
     *         items in the log. It doesn't tell us anything beyond "Oh no, I
     *         found an ItemStack with a null item", so it's just useless spam.
     *         Here, we make that specific call to printStackTrace() of
     *         RuntimeException into a no-op, so nothing is logged.
     */
    @Redirect(
        method = "postInit()V",
        at = @At(value = "INVOKE", target = "Ljava/lang/RuntimeException;printStackTrace()V", ordinal = 0, remap = false),
        remap = false
    )
    private static void riftflux$shutUp(RuntimeException self) {
    }
}
