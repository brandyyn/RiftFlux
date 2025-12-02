package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

/**
 * Globally adjusts durability loss for "wrong use" cases.
 *
 * If ModConfig.wrongUseSingleDurability is true:
 *   any call to ItemStack.damageItem(2, ...) is treated as if it were damageItem(1, ...),
 *   regardless of item type.
 *
 * This effectively makes all "double durability" hits only cost 1 point instead.
 */
@Mixin(ItemStack.class)
public abstract class MixinItemStack_WrongUseDurability {

    @Shadow public abstract Item getItem();

    @Shadow public abstract boolean isItemStackDamageable();

    @Shadow protected abstract boolean attemptDamageItem(int amount, Random rand);

    @Shadow public int stackSize;

    @Inject(
            method = "damageItem(ILnet/minecraft/entity/EntityLivingBase;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void rf$adjustWrongUseDurability(int amount, EntityLivingBase entity, CallbackInfo ci) {
        // Only touch when feature is enabled
        if (!ModConfig.wrongUseSingleDurability) {
            return;
        }

        // Only care about the "2 durability" cases (vanilla wrong-use)
        if (amount != 2) {
            return;
        }

        // If not damageable or no entity context, let vanilla handle it
        if (!this.isItemStackDamageable() || entity == null) {
            return;
        }

        ItemStack self = (ItemStack) (Object) this;
        Item item = self.getItem(); // not strictly needed, but handy for logging if you ever want it

        // === Custom 1-durability logic ===
        // This mirrors vanilla ItemStack.damageItem, but with amount = 1 instead of 2.
        if (this.attemptDamageItem(1, entity.getRNG())) {
            entity.renderBrokenItemStack(self);
            --this.stackSize;
            if (this.stackSize < 0) {
                this.stackSize = 0;
            }
        }

        // We've handled it; cancel vanilla damageItem(2, ...)
        ci.cancel();
    }
}
