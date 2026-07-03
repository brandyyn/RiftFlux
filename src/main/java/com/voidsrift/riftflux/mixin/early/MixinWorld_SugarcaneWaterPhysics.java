package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public abstract class MixinWorld_SugarcaneWaterPhysics {
    @Inject(method = "handleMaterialAcceleration", at = @At("RETURN"), cancellable = true)
    private void riftflux$handleSugarcaneWaterAcceleration(AxisAlignedBB box, Material material, Entity entity,
                                                           CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ() || !ModConfig.allowSugarcaneInWater || material != Material.water) {
            return;
        }

        World world = (World) (Object) this;
        Vec3 acceleration = Vec3.createVectorHelper(0.0D, 0.0D, 0.0D);
        boolean inWater = false;

        int minX = MathHelper.floor_double(box.minX);
        int maxX = MathHelper.floor_double(box.maxX + 1.0D);
        int minY = MathHelper.floor_double(box.minY);
        int maxY = MathHelper.floor_double(box.maxY + 1.0D);
        int minZ = MathHelper.floor_double(box.minZ);
        int maxZ = MathHelper.floor_double(box.maxZ + 1.0D);

        for (int x = minX; x < maxX; ++x) {
            for (int y = minY; y < maxY; ++y) {
                for (int z = minZ; z < maxZ; ++z) {
                    if (!this.riftflux$isWaterloggedSugarcane(world, x, y, z)) {
                        continue;
                    }
                    double waterTop = (double) (y + 1) - BlockLiquid.getLiquidHeightPercent(0);
                    if ((double) maxY >= waterTop) {
                        inWater = true;
                        Blocks.water.velocityToAddToEntity(world, x, y, z, entity, acceleration);
                    }
                }
            }
        }

        if (inWater) {
            double length = acceleration.lengthVector();
            if (length > 0.0D && entity.isPushedByWater()) {
                acceleration = acceleration.normalize();
                double strength = 0.014D;
                entity.motionX += acceleration.xCoord * strength;
                entity.motionY += acceleration.yCoord * strength;
                entity.motionZ += acceleration.zCoord * strength;
            }
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "isAABBInMaterial", at = @At("RETURN"), cancellable = true)
    private void riftflux$isAABBInSugarcaneWater(AxisAlignedBB box, Material material,
                                                 CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ()
                && ModConfig.allowSugarcaneInWater
                && material == Material.water
                && this.riftflux$hasWaterloggedSugarcane((World) (Object) this, box)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "isMaterialInBB", at = @At("RETURN"), cancellable = true)
    private void riftflux$isSugarcaneWaterInBB(AxisAlignedBB box, Material material,
                                               CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ()
                && ModConfig.allowSugarcaneInWater
                && material == Material.water
                && this.riftflux$hasWaterloggedSugarcane((World) (Object) this, box)) {
            cir.setReturnValue(true);
        }
    }

    @Unique
    private boolean riftflux$hasWaterloggedSugarcane(World world, AxisAlignedBB box) {
        int minX = MathHelper.floor_double(box.minX);
        int maxX = MathHelper.floor_double(box.maxX + 1.0D);
        int minY = MathHelper.floor_double(box.minY);
        int maxY = MathHelper.floor_double(box.maxY + 1.0D);
        int minZ = MathHelper.floor_double(box.minZ);
        int maxZ = MathHelper.floor_double(box.maxZ + 1.0D);

        for (int x = minX; x < maxX; ++x) {
            for (int y = minY; y < maxY; ++y) {
                for (int z = minZ; z < maxZ; ++z) {
                    if (this.riftflux$isWaterloggedSugarcane(world, x, y, z)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Unique
    private boolean riftflux$isWaterloggedSugarcane(World world, int x, int y, int z) {
        return world.getBlock(x, y, z) == Blocks.reeds
                && (this.riftflux$isWater(world.getBlock(x, y + 1, z))
                || this.riftflux$isWater(world.getBlock(x - 1, y, z))
                || this.riftflux$isWater(world.getBlock(x + 1, y, z))
                || this.riftflux$isWater(world.getBlock(x, y, z - 1))
                || this.riftflux$isWater(world.getBlock(x, y, z + 1)));
    }

    @Unique
    private boolean riftflux$isWater(Block block) {
        return block != null && block.getMaterial() == Material.water;
    }
}
