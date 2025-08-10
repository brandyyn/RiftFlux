package com.excsi.riftfixes.mixin.late;

import java.util.List;

import net.minecraft.block.BlockChest;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import com.excsi.riftfixes.ModConfig;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 1.7.10: Launch any entity standing on a chest when it opens.
 * Uses values from ModConfig (EnableChestLaunch, ChestLaunchHorizontal, ChestLaunchUpward).
 */
@Mixin(BlockChest.class)
public abstract class MixinBlockChestLaunch {

    @Inject(method = "onBlockActivated", at = @At("RETURN"))
    private void launchOnOpen(World world, int x, int y, int z,
                              EntityPlayer player, int side, float hitX, float hitY, float hitZ,
                              CallbackInfoReturnable<Boolean> cir) {
        // Only if it actually opened and feature is enabled
        if (!cir.getReturnValue()) return;
        if (world.isRemote) return;
        if (!ModConfig.enableChestLaunch) return;

        // Determine facing from metadata: 2=N(-Z), 3=S(+Z), 4=W(-X), 5=E(+X)
        int meta = world.getBlockMetadata(x, y, z);
        double ax = 0D, az = 0D;
        switch (meta) {
            case 2: az = -1D; break; // north
            case 3: az =  1D; break; // south
            case 4: ax = -1D; break; // west
            case 5: ax =  1D; break; // east
            default:
                // Fallback: push away from center based on opener position
                double dx = player.posX - (x + 0.5D);
                double dz = player.posZ - (z + 0.5D);
                if (Math.abs(dx) > Math.abs(dz)) ax = dx > 0 ? 1D : -1D;
                else                             az = dz > 0 ? 1D : -1D;
        }

        // AABB one block above the chest (entities "standing on top")
        AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(x, y + 1, z, x + 1, y + 2, z + 1);

        @SuppressWarnings("unchecked")
        List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, aabb);
        if (entities == null || entities.isEmpty()) return;

        final double h = ModConfig.chestLaunchHorizontal;
        final double u = ModConfig.chestLaunchUpward;

        for (Entity e : entities) {
            e.addVelocity(ax * h, u, az * h);
            e.velocityChanged = true; // sync to clients
        }
    }
}