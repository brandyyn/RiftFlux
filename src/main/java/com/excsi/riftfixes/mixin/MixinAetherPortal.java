package com.excsi.riftfixes.mixin;

import net.aetherteam.aether.blocks.BlockAetherPortal;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BlockAetherPortal.class,remap = false)
public class MixinAetherPortal {

    @Inject(method = "func_150000_e", at = @At("HEAD"), cancellable = true)
    public void inject(World world, int x, int y, int z, CallbackInfoReturnable<Boolean> cir){
        cir.setReturnValue(false);
    }
}
