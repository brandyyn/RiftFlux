package com.excsi.riftfixes.mixin.early;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Make stacks still merge even if the only NBT differences are our
 * marker keys: "riftfixes_new" / "riftfixes_seen".
 */
@Mixin(ItemStack.class)
public abstract class MixinItemStackTagEqual {

    private static final String TAG_NEW  = "riftfixes_new";
    private static final String TAG_SEEN = "riftfixes_seen";

    @Inject(
            method = "areItemStackTagsEqual(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void riftfixes$ignoreMarkerKeys(ItemStack a, ItemStack b, CallbackInfoReturnable<Boolean> cir) {
        NBTTagCompound ta = (a != null) ? a.getTagCompound() : null;
        NBTTagCompound tb = (b != null) ? b.getTagCompound() : null;

        // Fast path: same ref (covers both null or same object)
        if (ta == tb) return;

        NBTTagCompound na = normalize(ta);
        NBTTagCompound nb = normalize(tb);

        // If equal after removing our keys, treat as equal for stacking
        if ((na == null && nb == null) || (na != null && na.equals(nb))) {
            cir.setReturnValue(true);
        }
    }

    private static NBTTagCompound normalize(NBTTagCompound tag) {
        if (tag == null) return null;
        NBTTagCompound c = (NBTTagCompound) tag.copy();
        c.removeTag(TAG_NEW);
        c.removeTag(TAG_SEEN);
        return c.hasNoTags() ? null : c;
    }
}
