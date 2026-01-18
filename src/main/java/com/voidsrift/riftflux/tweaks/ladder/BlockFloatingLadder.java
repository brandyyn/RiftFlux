package com.voidsrift.riftflux.tweaks.ladder;

import com.voidsrift.riftflux.tweaks.ladder.client.RFRenderIds;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLadder;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.world.World;

import java.util.Random;

/**
 * A ladder variant that only requires another ladder directly above it.
 * This is used for the "extend ladders downward" tweak.
 */
public class BlockFloatingLadder extends BlockLadder {

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

    // IMPORTANT: We intentionally do NOT override any bounds/collision methods.
    // BlockFloatingLadder extends BlockLadder; by inheriting BlockLadder's bounds/collision behavior,
    // the Hanging Ladder's physical/selection hitbox exactly matches vanilla ladders.
}
