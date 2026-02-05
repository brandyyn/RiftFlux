package com.voidsrift.riftflux.mixin.early.satchels;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import makamys.satchels.Satchels;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.management.ItemInWorldManager;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;

@Mixin(ItemInWorldManager.class)
public abstract class MixinItemInWorldManager {
    
    @Inject(method = "setGameType", at = @At("RETURN"))
    private void postSetGameType(WorldSettings.GameType gameType, CallbackInfo ci) {
        Satchels.onGameTypeChanged(gameType, ((ItemInWorldManager)(Object)this).thisPlayerMP);
    }   

    @Inject(method = "tryUseItem(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true)
    private void riftflux$guardNullUseItem(EntityPlayer player, World world, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack == null) {
            cir.setReturnValue(false);
        }
    }
}    
