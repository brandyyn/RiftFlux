package com.excsi.riftfixes.mixin.early;

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
 * Launch entities standing on a chest when it opens, in the lid's opening direction (toward the back/hinge).
 * Uses ModConfig.enableChestLaunch, ModConfig.chestLaunchHorizontal, ModConfig.chestLaunchUpward.
 */
@Mixin(BlockChest.class)
public abstract class MixinBlockChestLaunch {

    @Inject(method = "onBlockActivated", at = @At("RETURN"))
    private void launchOnOpen(World world, int x, int y, int z,
                              EntityPlayer player, int side, float hitX, float hitY, float hitZ,
                              CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) return;       // only if it actually opened
        if (world.isRemote) return;               // server-side only
        if (!ModConfig.enableChestLaunch) return; // feature toggle

        // Vanilla chest facing meta: 2=N(-Z front), 3=S(+Z front), 4=W(-X front), 5=E(+X front).
        // Lid opens TOWARD THE BACK (opposite of front). So invert each axis.
        int meta = world.getBlockMetadata(x, y, z);
        double ax = 0D, az = 0D;
        switch (meta) {
            case 2: az =  1D; break; // front -Z => push +Z (toward back)
            case 3: az = -1D; break; // front +Z => push -Z
            case 4: ax =  1D; break; // front -X => push +X
            case 5: ax = -1D; break; // front +X => push -X
            default:
                // Fallback: push AWAY from the opener (invert sign)
                double dx = player.posX - (x + 0.5D);
                double dz = player.posZ - (z + 0.5D);
                if (Math.abs(dx) > Math.abs(dz)) {
                    ax = dx > 0 ? -1D : 1D;
                } else {
                    az = dz > 0 ? -1D : 1D;
                }
                break;
        }

        AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(x, y + 1, z, x + 1, y + 2, z + 1);

        @SuppressWarnings("unchecked")
        List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, aabb);
        if (entities == null || entities.isEmpty()) return;

        final double h = ModConfig.chestLaunchHorizontal;
        final double u = ModConfig.chestLaunchUpward;

        for (Entity e : entities) {
            e.addVelocity(ax * h, u, az * h);
            e.velocityChanged = true;
        }
    }
}