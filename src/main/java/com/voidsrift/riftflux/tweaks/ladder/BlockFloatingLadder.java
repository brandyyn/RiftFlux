package com.voidsrift.riftflux.tweaks.ladder;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.tweaks.ladder.client.RFRenderIds;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLadder;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.Random;

/**
 * A ladder variant that only requires another ladder directly above it.
 * This is used for the "extend ladders downward" tweak.
 */
public class BlockFloatingLadder extends BlockLadder {

    /**
     * Thickness of the physical ladder plane (in blocks).
     * Vanilla ladder visual bounds are 2/16 thick (0.125).
     */
    private static final float PLANE_THICKNESS = 0.125F; // 2/16

    public BlockFloatingLadder() {
        super();
        setHardness(0.4F);
        setStepSound(soundTypeLadder);

        // Give it a distinct unlocalized name, but reuse the vanilla ladder texture.
        setBlockName("riftflux.floating_ladder");
        setBlockTextureName("minecraft:ladder");

        // We don't want it to show up as a separate block in creative tabs.
        setCreativeTab(null);
    }

    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        // Only allow if there is a ladder above (vanilla or floating ladder).
        return world.getBlock(x, y + 1, z) instanceof BlockLadder;
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor) {
        // If the ladder above is gone, this one should drop and disappear.
        if (world.getBlock(x, y + 1, z) instanceof BlockLadder) {
            return;
        }
        dropBlockAsItem(world, x, y, z, 0, 0);
        world.setBlockToAir(x, y, z);
    }

    @Override
    public Item getItemDropped(int meta, Random rand, int fortune) {
        return Item.getItemFromBlock(Blocks.ladder);
    }

    @Override
    public int getRenderType() {
        // Server never renders; client will set the render id in RFRenderIds.
        // If the client doesn't enable the custom renderer, fall back to vanilla ladder rendering.
        if (RFRenderIds.doubleSidedLadderRenderId >= 0) {
            return RFRenderIds.doubleSidedLadderRenderId;
        }
        return super.getRenderType();
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        // Give the hanging ladder a real collision plane so you can't just walk through it in mid-air.
        // IMPORTANT: Keep the plane aligned with vanilla ladder bounds so it lines up with ladder columns.
        return getVanillaAlignedPlaneAABB(world, x, y, z);
    }

    @Override
    public void setBlockBoundsBasedOnState(net.minecraft.world.IBlockAccess world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        setVanillaAlignedBounds(meta);
    }

    @Override
    public void setBlockBoundsForItemRender() {
        // Render in inventory roughly like a normal ladder.
        setVanillaAlignedBounds(2);
    }

    private void setVanillaAlignedBounds(int meta) {
        // Match the same facing mapping as BlockLadder in 1.7.10:
        // 2=north (attached to south face, plane at z=1), 3=south (plane at z=0),
        // 4=west (plane at x=1), 5=east (plane at x=0)
        float t = PLANE_THICKNESS;
        if (meta == 2) {
            this.setBlockBounds(0.0F, 0.0F, 1.0F - t, 1.0F, 1.0F, 1.0F);
        } else if (meta == 3) {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, t);
        } else if (meta == 4) {
            this.setBlockBounds(1.0F - t, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        } else if (meta == 5) {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, t, 1.0F, 1.0F);
        } else {
            // Fallback: behave like meta 2
            this.setBlockBounds(0.0F, 0.0F, 1.0F - t, 1.0F, 1.0F, 1.0F);
        }
    }

    /**
     * World-space collision AABB for the vanilla-aligned ladder plane.
     */
    public AxisAlignedBB getVanillaAlignedPlaneAABB(World world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        float t = PLANE_THICKNESS;
        if (meta == 2) {
            return AxisAlignedBB.getBoundingBox(x, y, z + 1.0F - t, x + 1.0F, y + 1.0F, z + 1.0F);
        } else if (meta == 3) {
            return AxisAlignedBB.getBoundingBox(x, y, z, x + 1.0F, y + 1.0F, z + t);
        } else if (meta == 4) {
            return AxisAlignedBB.getBoundingBox(x + 1.0F - t, y, z, x + 1.0F, y + 1.0F, z + 1.0F);
        } else if (meta == 5) {
            return AxisAlignedBB.getBoundingBox(x, y, z, x + t, y + 1.0F, z + 1.0F);
        }
        return AxisAlignedBB.getBoundingBox(x, y, z + 1.0F - t, x + 1.0F, y + 1.0F, z + 1.0F);
    }
}
