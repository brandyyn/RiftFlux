package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.client.creative.CreativeModuleVisibility;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.List;

@Mixin(CreativeTabs.class)
public abstract class MixinCreativeTabs_DisabledModuleItems {
    @Redirect(
            method = "displayAllReleventItems(Ljava/util/List;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/Item;getSubItems(Lnet/minecraft/item/Item;Lnet/minecraft/creativetab/CreativeTabs;Ljava/util/List;)V"
            )
    )
    private void riftflux$skipDisabledModuleCreativeItems(Item item, Item sourceItem, CreativeTabs tab, List list) {
        if (CreativeModuleVisibility.shouldShowItem(item)) {
            item.getSubItems(sourceItem, tab, list);
        }
    }

    @Inject(method = "displayAllReleventItems(Ljava/util/List;)V", at = @At("TAIL"))
    private void riftflux$removeDisabledModuleCreativeStacks(List list, CallbackInfo ci) {
        if (list == null || list.isEmpty()) {
            return;
        }

        Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            Object value = iterator.next();
            if (value instanceof ItemStack && !CreativeModuleVisibility.shouldShowStack((ItemStack) value)) {
                iterator.remove();
            }
        }
    }
}
