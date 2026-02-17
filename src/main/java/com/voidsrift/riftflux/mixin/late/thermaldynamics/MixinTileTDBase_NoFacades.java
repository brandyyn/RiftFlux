package com.voidsrift.riftflux.mixin.late.thermaldynamics;

import cofh.thermaldynamics.block.TileTDBase;
import cofh.thermaldynamics.duct.attachments.cover.Cover;
import java.util.Arrays;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TileTDBase.class, remap = false)
public abstract class MixinTileTDBase_NoFacades {

    @Shadow public Cover[] covers;
    @Shadow public abstract void recalcFacadeMask();

    @Inject(method = "addFacade(Lcofh/thermaldynamics/duct/attachments/cover/Cover;)Z", at = @At("HEAD"), cancellable = true)
    private void riftflux$preventFacadeAttach(Cover cover, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }

    @Inject(method = "readFromNBT(Lnet/minecraft/nbt/NBTTagCompound;)V", at = @At("TAIL"))
    private void riftflux$clearLoadedFacades(NBTTagCompound tag, CallbackInfo ci) {
        riftflux$clearFacades();
    }

    @Inject(method = "handleTilePacket", at = @At("TAIL"))
    private void riftflux$clearSyncedFacades(CallbackInfo ci) {
        riftflux$clearFacades();
    }

    @Unique
    private void riftflux$clearFacades() {
        Arrays.fill(this.covers, null);
        this.recalcFacadeMask();
    }
}
