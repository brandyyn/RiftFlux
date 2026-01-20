package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(EntityPlayer.class)
public class MixinEntityPlayer_FistStickDamage {

    @ModifyVariable(
            method = "attackTargetEntityWithCurrentItem(Lnet/minecraft/entity/Entity;)V",
            at = @At(value = "STORE"),
            ordinal = 0
    )
    private float riftflux$adjustBaseAttackDamage(float baseDamage, Entity target) {
        final EntityPlayer self = (EntityPlayer) (Object) this;
        
        if (ModConfig.enableStickDamageBonus) {
            final ItemStack held = self.getHeldItem();
            if (held != null && held.getItem() == Items.stick) {
                final float bonus = ModConfig.stickDamageBonus;
                if (bonus != 0.0F) {
                    baseDamage += bonus;
                }
            }
        }

        if (ModConfig.enableFistDamageBoost) {
            final ItemStack held = self.getHeldItem();
            if (held == null) {
                final float min = ModConfig.fistDamageAmount;
                if (baseDamage < min) {
                    baseDamage = min;
                }
            }
        }

        return baseDamage;
    }
}
