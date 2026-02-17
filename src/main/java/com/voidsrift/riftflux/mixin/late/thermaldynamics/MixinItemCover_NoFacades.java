package com.voidsrift.riftflux.mixin.late.thermaldynamics;

import cofh.thermaldynamics.block.Attachment;
import cofh.thermaldynamics.block.TileTDBase;
import cofh.thermaldynamics.item.ItemCover;
import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ItemCover.class, remap = false)
public class MixinItemCover_NoFacades {

    @Inject(
            method = "getAttachment(ILnet/minecraft/item/ItemStack;Lcofh/thermaldynamics/block/TileTDBase;)Lcofh/thermaldynamics/block/Attachment;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void riftflux$disableFacadePlacement(int side, ItemStack stack, TileTDBase tile, CallbackInfoReturnable<Attachment> cir) {
        cir.setReturnValue(null);
    }

    @Inject(method = "getSubItems(Lnet/minecraft/item/Item;Lnet/minecraft/creativetab/CreativeTabs;Ljava/util/List;)V", at = @At("HEAD"), cancellable = true)
    private void riftflux$hideFacadeItems(Item item, CreativeTabs tab, List<?> items, CallbackInfo ci) {
        ci.cancel();
    }
}
