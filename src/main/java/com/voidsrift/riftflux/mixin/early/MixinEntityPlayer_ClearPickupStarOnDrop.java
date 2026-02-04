package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityPlayer.class)
public abstract class MixinEntityPlayer_ClearPickupStarOnDrop {

    private static final String TAG_NEW = "riftflux_new";

    @Inject(
            method = "dropPlayerItemWithRandomChoice(Lnet/minecraft/item/ItemStack;Z)Lnet/minecraft/entity/item/EntityItem;",
            at = @At("HEAD")
    )
    private void riftflux$clearStarOnDrop(ItemStack stack, boolean random, CallbackInfoReturnable<EntityItem> cir) {
        if (!ModConfig.itemPickupStarClearOnLeaveInventory) return;
        EntityPlayer self = (EntityPlayer)(Object)this;
        if (self.worldObj != null && self.worldObj.isRemote) return;
        clearStarTag(stack);
    }

    private static void clearStarTag(ItemStack st) {
        if (st == null) return;
        NBTTagCompound tag = st.getTagCompound();
        if (tag == null || !tag.getBoolean(TAG_NEW)) return;
        tag.removeTag(TAG_NEW);
        if (tag.hasNoTags()) {
            st.setTagCompound(null);
        } else {
            st.setTagCompound(tag);
        }
    }
}
