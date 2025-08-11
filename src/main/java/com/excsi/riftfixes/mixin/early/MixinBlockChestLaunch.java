package com.excsi.riftfixes.mixin.early;

import com.excsi.riftfixes.ModConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/**
 * Launch entities standing directly on top of a chest when it actually opens.
 * Triggers when numPlayersUsing goes 0 -> >0. “On top” is: the block directly
 * under the entity’s feet equals this chest’s position.
 */
@Mixin(TileEntityChest.class)
public abstract class MixinBlockChestLaunch {

    @Unique private boolean riftfixes$wasOpen = false;

    // MCP name + descriptor so it resolves in dev
    @Inject(method = "updateEntity()V", at = @At("TAIL"))
    private void riftfixes$onChestTick(CallbackInfo ci) {
        if (!ModConfig.enableChestLaunch) return;

        TileEntityChest te = (TileEntityChest)(Object)this;
        World world = te.getWorldObj();
        if (world == null || world.isRemote) return;

        final boolean isOpen = te.numPlayersUsing > 0;
        if (isOpen && !riftfixes$wasOpen) {
            riftfixes$launchOnOpen(world, te.xCoord, te.yCoord, te.zCoord);
        }
        riftfixes$wasOpen = isOpen;
    }

    @Unique
    private void riftfixes$launchOnOpen(World world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        if (!(block instanceof BlockChest)) return;

        // Push toward the hinge (opposite chest "front")
        int meta = world.getBlockMetadata(x, y, z);
        double ax = 0D, az = 0D;
        switch (meta) {
            case 2: az =  1D; break; // front -Z -> push +Z
            case 3: az = -1D; break; // front +Z -> push -Z
            case 4: ax =  1D; break; // front -X -> push +X
            case 5: ax = -1D; break; // front +X -> push -X
            default: az = 1D; break;
        }

        // Slightly thicker slab above the top face to gather candidates
        AxisAlignedBB box = AxisAlignedBB.getBoundingBox(
                x + 0.001D, y + 1.00D, z + 0.001D,
                x + 0.999D, y + 1.40D, z + 0.999D
        );

        @SuppressWarnings("unchecked")
        List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, box);
        if (entities == null || entities.isEmpty()) return;

        final double h = ModConfig.chestLaunchHorizontal;
        final double u = ModConfig.chestLaunchUpward;

        for (Entity e : entities) {
            if (!riftfixes$isStandingDirectlyOnTop(x, y, z, e)) continue;
            e.addVelocity(ax * h, u, az * h);
            e.velocityChanged = true;
        }
    }

    /** True only if the block directly under the entity's feet is this chest. */
    @Unique
    private static boolean riftfixes$isStandingDirectlyOnTop(int x, int y, int z, Entity e) {
        if (e == null || e.boundingBox == null) return false;

        int bx = MathHelper.floor_double(e.posX);
        int by = MathHelper.floor_double(e.boundingBox.minY - 1.0E-3D);
        int bz = MathHelper.floor_double(e.posZ);

        if (bx != x || by != y || bz != z) return false;

        // Optional stability: require grounded or near-zero vertical motion
        return e.onGround || Math.abs(e.motionY) < 0.05D;
    }
}