package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 1.7.10 boats can get stuck underwater with modernized movement behavior (or from other mods).
 * This restores older-style buoyancy so boats "climb" back up toward the surface,
 * and prevents riders from being forcibly kicked off just because the boat is submerged.
 *
 * Targets vanilla EntityBoat, so modded boats that extend EntityBoat are covered automatically.
 */
@Mixin(EntityBoat.class)
public abstract class MixinEntityBoat_WaterClimb {

    @Unique
    private Entity riftflux$prevRider;

    // Vanilla 1.7.10 fallDistance is unreliable for boats while ridden (steering / motion updates reset it).
    // Track our own "downward travel" accumulator so fall-breaking works whether or not a rider is present.
    @Unique
    private float riftflux$fallAccum;

    @Inject(method = "onUpdate", at = @At("HEAD"))
    private void riftflux$capturePrevRider(CallbackInfo ci) {
        EntityBoat self = (EntityBoat) (Object) this;
        this.riftflux$prevRider = self.riddenByEntity;
    }

    @Inject(method = "onUpdate", at = @At("TAIL"))
    private void riftflux$boatWaterClimbAndNoUnderwaterEject(CallbackInfo ci) {
        EntityBoat self = (EntityBoat) (Object) this;

        final boolean climbEnabled = ModConfig.legacyBoatBuoyancy;

        // --- Buoyancy boost: makes boats climb back up when submerged ---
        if (climbEnabled && self.isInWater() && !self.onGround) {
            int x = MathHelper.floor_double(self.posX);
            int z = MathHelper.floor_double(self.posZ);
            // sample roughly around the middle of the boat to detect "submerged"
            int ySample = MathHelper.floor_double(self.posY + (double) (self.height * 0.5F));
            boolean submerged = self.worldObj.getBlock(x, ySample, z).getMaterial() == Material.water;

            // Base buoyancy values chosen to feel like "older" boats; amount configurable via legacyBoatBuoyancyStrength.
            final double mul = (double) ModConfig.legacyBoatBuoyancyStrength;
            double add = (submerged ? 0.08D : 0.04D) * mul;
            // If some other logic is pushing down, fight it a bit more.
            if (self.motionY < 0.2D) {
                self.motionY += add;
            }
            if (self.motionY > 0.2D) {
                self.motionY = 0.2D;
            }
        }

        // --- Prevent being kicked off just because the boat is underwater ---
        // Only fix on server side to avoid client/server desync.
        if (climbEnabled && !self.worldObj.isRemote && this.riftflux$prevRider != null && self.riddenByEntity == null) {
            Entity rider = this.riftflux$prevRider;
            // Allow intentional dismounts.
            if (!rider.isDead && !rider.isSneaking()) {
                // Only remount when the "kick" happened while both are in water.
                if (self.isInWater() && rider.isInWater()) {
                    double dx = rider.posX - self.posX;
                    double dy = rider.posY - self.posY;
                    double dz = rider.posZ - self.posZ;
                    double distSq = dx * dx + dy * dy + dz * dz;
                    if (distSq <= 4.0D) {
                        rider.mountEntity(self);
                    }
                }
            }
        }

        // --- Fall-breaking for boats (configurable, works while ridden) ---
        if (!self.worldObj.isRemote) {
            final float threshold = ModConfig.boatsFallBreakDistance;
            if (threshold <= 0.0F) {
                this.riftflux$fallAccum = 0.0F;
            } else if (self.isInWater()) {
                // Water should never accumulate fall breaking.
                this.riftflux$fallAccum = 0.0F;
            } else {
                // Accumulate downward motion while in air.
                if (!self.onGround && self.motionY < 0.0D) {
                    this.riftflux$fallAccum += (float) (-self.motionY);
                }

                // When we hit the ground, decide whether to break based on a "distance-ish" value.
                if (self.onGround) {
                    int x = MathHelper.floor_double(self.posX);
                    int y = MathHelper.floor_double(self.posY - 0.2D);
                    int z = MathHelper.floor_double(self.posZ);
                    Block under = self.worldObj.getBlock(x, y, z);

                    if (under != null && under.getMaterial().isSolid()) {
                        // Scale the accumulated downward motion into something comparable to fallDistance.
                        float impact = this.riftflux$fallAccum * 10.0F;
                        if (impact > threshold) {
                            // Dismount rider safely before breaking.
                            if (self.riddenByEntity != null) {
                                self.riddenByEntity.mountEntity(null);
                            }
                            // Let the boat's own damage/drop logic handle item drops.
                            self.attackEntityFrom(DamageSource.fall, 1000.0F);
                        }
                    }

                    this.riftflux$fallAccum = 0.0F;
                }
            }
        }

        // clear ref
        this.riftflux$prevRider = null;
    }
}
