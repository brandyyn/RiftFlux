package com.voidsrift.riftflux.mixin.early.vortex;

import com.voidsrift.riftflux.vortex.lib.helper.EnchantHelper;
import java.util.Random;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ChestGenHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChestGenHooks.class)
public abstract class MixinChestGenHooksRandomGlint {

    @Inject(method = "generateStacks", at = @At("RETURN"), remap = false)
    private static void riftflux$applyRandomGlintToGeneratedLoot(Random rand, ItemStack source, int min, int max,
                                                                 CallbackInfoReturnable<ItemStack[]> cir) {
        ItemStack[] stacks = cir.getReturnValue();
        if (stacks == null) {
            return;
        }

        for (ItemStack stack : stacks) {
            EnchantHelper.maybeApplyRandomGlint(stack, rand);
        }
    }
}
