package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityEnderman.class)
public abstract class MixinEntityEnderman_JackOLanternNoAggro {
    @Inject(method = "shouldAttackPlayer", at = @At("HEAD"), cancellable = true)
    private void riftflux$ignoreJackOLanternWearers(EntityPlayer player, CallbackInfoReturnable<Boolean> cir) {
        if (!ModConfig.enableJackOLanternHelmet || player == null) {
            return;
        }

        ItemStack helmet = player.inventory.armorInventory[3];
        if (helmet != null && helmet.getItem() == Item.getItemFromBlock(Blocks.lit_pumpkin)) {
            cir.setReturnValue(false);
        }
    }
}
