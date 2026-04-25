package com.voidsrift.riftflux.mixin.early.backhand;

import com.voidsrift.riftflux.avatar.glider.ItemGlider;
import com.voidsrift.riftflux.compat.BackhandCompat;
import com.voidsrift.riftflux.terramine.ItemIceRod;
import java.util.function.BooleanSupplier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xonin.backhand.api.core.BackhandUtils;

@Mixin(value = BackhandUtils.class, remap = false)
public abstract class MixinBackhandUtils_IceRodGliderMainhandOnly {
    @Inject(
            method = "useOffhandItem(Lnet/minecraft/entity/player/EntityPlayer;ZLjava/util/function/BooleanSupplier;)Z",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private static void riftflux$preferMainhandIceRodOrGlider(
            EntityPlayer player,
            boolean syncHeldItem,
            BooleanSupplier action,
            CallbackInfoReturnable<Boolean> cir
    ) {
        ItemStack mainhand = BackhandCompat.getMainhandItem(player);
        ItemStack offhand = BackhandCompat.getOffhandItem(player);
        if (riftflux$isIceRodOrGlider(mainhand) && riftflux$isIceRodOrGlider(offhand)) {
            cir.setReturnValue(false);
        }
    }

    private static boolean riftflux$isIceRodOrGlider(ItemStack stack) {
        if (stack == null) {
            return false;
        }
        Item item = stack.getItem();
        return item instanceof ItemIceRod || item instanceof ItemGlider;
    }
}
