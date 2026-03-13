package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.client.sound.PlayerHurtSoundHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase_CustomPlayerHurtSound {
    @Shadow
    protected abstract float getSoundVolume();

    @Inject(method = "handleHealthUpdate", at = @At("HEAD"))
    private void riftflux$playLocalPlayerHurtSound(byte status, CallbackInfo ci) {
        if (status != 2 || !ModConfig.playerOnlyHurtSound) {
            return;
        }

        EntityLivingBase self = (EntityLivingBase) (Object) this;
        if (!(self instanceof EntityPlayer) || self.worldObj == null || !self.worldObj.isRemote) {
            return;
        }

        float pitch = (self.getRNG().nextFloat() - self.getRNG().nextFloat()) * 0.2F + 1.0F;
        PlayerHurtSoundHelper.playClientPlayerHurtSound((EntityPlayer) self, this.getSoundVolume(), pitch);
    }
}
