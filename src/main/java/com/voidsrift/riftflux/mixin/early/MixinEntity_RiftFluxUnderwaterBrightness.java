package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class MixinEntity_RiftFluxUnderwaterBrightness {
    @Shadow public World worldObj;
    @Shadow public double posX;
    @Shadow public double posY;
    @Shadow public double posZ;
    @Shadow public float yOffset;
    @Shadow public float height;
    @Shadow protected boolean inWater;

    @Inject(method = "getBrightnessForRender", at = @At("HEAD"), cancellable = true)
    private void riftflux$removeUnderwaterPackedDarkening(float partialTicks, CallbackInfoReturnable<Integer> cir) {
        if (!this.riftflux$shouldUseDryLightSample()) {
            return;
        }
        cir.setReturnValue(Integer.valueOf(this.riftflux$getPackedDryBrightness()));
    }

    @Inject(method = "getBrightness", at = @At("HEAD"), cancellable = true)
    private void riftflux$removeUnderwaterFloatDarkening(float partialTicks, CallbackInfoReturnable<Float> cir) {
        if (!this.riftflux$shouldUseDryLightSample()) {
            return;
        }
        cir.setReturnValue(Float.valueOf(this.riftflux$getDryBrightness()));
    }

    private boolean riftflux$shouldUseDryLightSample() {
        Entity entity = (Entity)(Object)this;
        return ModConfig.fixUnderwaterMobDarkening
                && entity instanceof EntityLivingBase
                && this.worldObj != null
                && this.worldObj.blockExists(MathHelper.floor_double(this.posX), 0, MathHelper.floor_double(this.posZ))
                && this.inWater;
    }

    private int riftflux$getPackedDryBrightness() {
        int x = MathHelper.floor_double(this.posX);
        int z = MathHelper.floor_double(this.posZ);
        int drySampleY = this.riftflux$getDrySampleY(x, z);
        return this.worldObj.getLightBrightnessForSkyBlocks(x, drySampleY, z, 0);
    }

    private float riftflux$getDryBrightness() {
        int x = MathHelper.floor_double(this.posX);
        int z = MathHelper.floor_double(this.posZ);
        int drySampleY = this.riftflux$getDrySampleY(x, z);
        return this.worldObj.getLightBrightness(x, drySampleY, z);
    }

    private int riftflux$getDrySampleY(int x, int z) {
        int topY = this.worldObj.getHeightValue(x, z);
        int vanillaY = this.riftflux$getVanillaBrightnessSampleY();
        return this.riftflux$clampSampleY(Math.max(vanillaY, topY));
    }

    private int riftflux$getVanillaBrightnessSampleY() {
        double sampleHeight = (double)this.height * 0.66D;
        return MathHelper.floor_double(this.posY - (double)this.yOffset + sampleHeight);
    }

    private int riftflux$clampSampleY(int y) {
        return Math.max(0, Math.min(y, Math.max(1, this.worldObj.getActualHeight() - 1)));
    }
}
