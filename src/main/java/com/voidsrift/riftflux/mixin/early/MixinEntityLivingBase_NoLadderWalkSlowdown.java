package com.voidsrift.riftflux.mixin.early;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase_NoLadderWalkSlowdown {
    
    @Overwrite
    public void moveEntityWithHeading(float strafe, float forward) {
        EntityLivingBase self = (EntityLivingBase)(Object)this;

        if (self.isInWater() && !self.isOnLadder()) {
            double d0 = self.posY;
            float f1 = 0.8F;
            float f2 = 0.02F;
            float f3 = (float) self.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue();

            if (!self.onGround) {
                f3 *= 0.5F;
            }

            if (f3 > 0.0F) {
                f1 += (0.54600006F - f1) * f3 / 0.02F;
                f2 += (self.jumpMovementFactor - f2) * f3 / 0.02F;
            }

            self.moveFlying(strafe, forward, f2);
            self.moveEntity(self.motionX, self.motionY, self.motionZ);
            self.motionX *= (double) f1;
            self.motionY *= 0.800000011920929D;
            self.motionZ *= (double) f1;
            self.motionY -= 0.02D;

            if (self.isCollidedHorizontally && self.isOffsetPositionInLiquid(self.motionX, self.motionY + 0.6000000238418579D - self.posY + d0, self.motionZ)) {
                self.motionY = 0.30000001192092896D;
            }
        }
        else if (self.handleLavaMovement() && !self.isOnLadder()) {
            double d1 = self.posY;
            self.moveFlying(strafe, forward, 0.02F);
            self.moveEntity(self.motionX, self.motionY, self.motionZ);
            self.motionX *= 0.5D;
            self.motionY *= 0.5D;
            self.motionZ *= 0.5D;
            self.motionY -= 0.02D;

            if (self.isCollidedHorizontally && self.isOffsetPositionInLiquid(self.motionX, self.motionY + 0.6000000238418579D - self.posY + d1, self.motionZ)) {
                self.motionY = 0.30000001192092896D;
            }
        }
        else {
            float f4 = 0.91F;

            if (self.onGround) {
                f4 = self.worldObj.getBlock(MathHelper.floor_double(self.posX), MathHelper.floor_double(self.boundingBox.minY) - 1, MathHelper.floor_double(self.posZ)).slipperiness * 0.91F;
            }

            float f5 = 0.16277136F / (f4 * f4 * f4);
            float f6;

            if (self.onGround) {
                f6 = (float) self.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue() * f5;
            } else {
                f6 = self.jumpMovementFactor;
            }

            self.moveFlying(strafe, forward, f6);
            f4 = 0.91F;

            if (self.onGround) {
                f4 = self.worldObj.getBlock(MathHelper.floor_double(self.posX), MathHelper.floor_double(self.boundingBox.minY) - 1, MathHelper.floor_double(self.posZ)).slipperiness * 0.91F;
            }

            // ===== Ladder handling (modified) =====
            if (self.isOnLadder()) {
                self.fallDistance = 0.0F;

                // keep the vanilla vertical cap
                if (self.motionY < -0.15D) {
                    self.motionY = -0.15D;
                }

                // sneaking: don't slide down
                if (self.isSneaking() && self.motionY < 0.0D) {
                    self.motionY = 0.0D;
                }

                // NOTE: removed horizontal clamp and damping here on purpose.
                // Vanilla would clamp to [-0.15, 0.15] and then multiply motionX/Z by 0.15.

                // vanilla "step up" when pushing into ladder
                if (self.isCollidedHorizontally) {
                    self.motionY = 0.2D;
                }
            }
            // =====================================

            self.moveEntity(self.motionX, self.motionY, self.motionZ);

            if (self.isCollidedHorizontally && self.isOnLadder()) {
                self.motionY = 0.2D;
            }

            if (self.worldObj.isRemote && (!self.worldObj.blockExists((int)self.posX, 0, (int)self.posZ) || !self.worldObj.getChunkFromBlockCoords((int)self.posX, (int)self.posZ).isChunkLoaded)) {
                if (self.posY > 0.0D) {
                    self.motionY = -0.1D;
                } else {
                    self.motionY = 0.0D;
                }
            } else {
                self.motionY -= 0.08D;
            }

            self.motionY *= 0.9800000190734863D;
            self.motionX *= (double) f4;
            self.motionZ *= (double) f4;
        }

        self.prevLimbSwingAmount = self.limbSwingAmount;
        double d2 = self.posX - self.prevPosX;
        double d3 = self.posZ - self.prevPosZ;
        float f7 = MathHelper.sqrt_double(d2 * d2 + d3 * d3) * 4.0F;

        if (f7 > 1.0F) {
            f7 = 1.0F;
        }

        self.limbSwingAmount += (f7 - self.limbSwingAmount) * 0.4F;
        self.limbSwing += self.limbSwingAmount;
    }
}
