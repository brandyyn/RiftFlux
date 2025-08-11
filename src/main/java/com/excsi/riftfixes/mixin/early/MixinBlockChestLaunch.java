package com.excsi.riftfixes.mixin.early;

import com.excsi.riftfixes.ModConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/**
 * Launch entities standing directly on top of a chest as soon as it’s activated,
 * i.e., before the GUI opens. No “must be still / onGround” requirement.
 */
@Mixin(BlockChest.class)
public abstract class MixinBlockChestLaunch {

    @Inject(
            method = "onBlockActivated(Lnet/minecraft/world/World;IIILnet/minecraft/entity/player/EntityPlayer;IFFF)Z",
            at = @At("HEAD")
    )
    private void riftfixes$launchOnActivate(World world, int x, int y, int z,
                                            EntityPlayer player, int side, float hitX, float hitY, float hitZ,
                                            CallbackInfoReturnable<Boolean> cir) {
        if (!ModConfig.enableChestLaunch) return;
        if (world == null || world.isRemote) return;

        final Block b = world.getBlock(x, y, z);
        if (!(b instanceof BlockChest)) return;

        // Determine push direction from chest facing (2/3/4/5)
        final int meta = world.getBlockMetadata(x, y, z);
        double ax = 0D, az = 0D;
        switch (meta) {
            case 2: az =  1D; break; // chest front faces -Z → push +Z (lid “opens” toward -Z, hinge at +Z)
            case 3: az = -1D; break; // front +Z → push -Z
            case 4: ax =  1D; break; // front -X → push +X
            case 5: ax = -1D; break; // front +X → push -X
            default: az = 1D;  break;
        }

        // Thin slab above the top face; captures feet over THIS block only
        AxisAlignedBB box = AxisAlignedBB.getBoundingBox(
                x + 0.001D, y + 1.00D, z + 0.001D,
                x + 0.999D, y + 1.40D, z + 0.999D
        );

        @SuppressWarnings("unchecked")
        List<Entity> list = world.getEntitiesWithinAABB(Entity.class, box);
        if (list == null || list.isEmpty()) return;

        final double h = ModConfig.chestLaunchHorizontal;
        final double u = ModConfig.chestLaunchUpward;

        for (Entity e : list) {
            if (!riftfixes$isStandingDirectlyOnTop(x, y, z, e)) continue;
            e.addVelocity(ax * h, u, az * h);
            e.velocityChanged = true;
        }
    }

    /** True only if the block directly under the entity's feet is exactly (x,y,z). */
    @Unique
    private static boolean riftfixes$isStandingDirectlyOnTop(int x, int y, int z, Entity e) {
        if (e == null || e.boundingBox == null) return false;
        int bx = MathHelper.floor_double(e.posX);
        int by = MathHelper.floor_double(e.boundingBox.minY - 1.0E-3D);
        int bz = MathHelper.floor_double(e.posZ);
        return bx == x && by == y && bz == z;
    }
}