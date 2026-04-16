package com.voidsrift.riftflux.mixin.early;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(World.class)
public abstract class MixinWorld_ExplosionDensityIgnoresPlants {
    private static final double RF_EXPLOSION_TRACE_EPSILON = 1.0E-4D;

    @Redirect(
            method = "getBlockDensity",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;rayTraceBlocks(Lnet/minecraft/util/Vec3;Lnet/minecraft/util/Vec3;)Lnet/minecraft/util/MovingObjectPosition;"
            )
    )
    private MovingObjectPosition riftflux$ignoreThinPlantsForExplosionExposure(World self, Vec3 from, Vec3 to) {
        Vec3 currentStart = Vec3.createVectorHelper(from.xCoord, from.yCoord, from.zCoord);

        for (int attempts = 0; attempts < 16; attempts++) {
            MovingObjectPosition hit = self.rayTraceBlocks(currentStart, to);
            if (hit == null || !riftflux$shouldIgnoreForExplosionDensity(self, hit)) {
                return hit;
            }

            double dx = to.xCoord - currentStart.xCoord;
            double dy = to.yCoord - currentStart.yCoord;
            double dz = to.zCoord - currentStart.zCoord;
            double length = Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (length <= RF_EXPLOSION_TRACE_EPSILON) {
                return null;
            }

            currentStart = Vec3.createVectorHelper(
                    hit.hitVec.xCoord + dx / length * RF_EXPLOSION_TRACE_EPSILON,
                    hit.hitVec.yCoord + dy / length * RF_EXPLOSION_TRACE_EPSILON,
                    hit.hitVec.zCoord + dz / length * RF_EXPLOSION_TRACE_EPSILON
            );
        }

        return null;
    }

    private static boolean riftflux$shouldIgnoreForExplosionDensity(World world, MovingObjectPosition hit) {
        if (hit == null || hit.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) {
            return false;
        }

        Block block = world.getBlock(hit.blockX, hit.blockY, hit.blockZ);
        if (block == null) {
            return false;
        }

        Material material = block.getMaterial();
        if (material == null || material == Material.air || material.isLiquid() || material.blocksMovement()) {
            return false;
        }

        AxisAlignedBB collisionBox = block.getCollisionBoundingBoxFromPool(world, hit.blockX, hit.blockY, hit.blockZ);
        return collisionBox == null;
    }
}
