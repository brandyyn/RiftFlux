package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.placeditem.BlockPlacedItem;
import com.voidsrift.riftflux.placeditem.TilePlacedItem;
import net.minecraft.block.Block;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldClient.class)
public abstract class MixinWorldClient_NoPlacedItemParticles {

    private static boolean rf$redirecting;

    @Inject(
            method = "spawnParticle(Ljava/lang/String;DDDDDD)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void rf$skipPlacedItemParticlesClient(String name,
                                                  double x, double y, double z,
                                                  double motionX, double motionY, double motionZ,
                                                  CallbackInfo ci) {
        if (name == null || (!name.startsWith("blockcrack") && !name.startsWith("blockdust"))) {
            return;
        }
        if (rf$redirecting) {
            return;
        }
        WorldClient world = (WorldClient) (Object) this;
        int bx = MathHelper.floor_double(x);
        int by = MathHelper.floor_double(y - 0.2D);
        int bz = MathHelper.floor_double(z);
        Block block = world.getBlock(bx, by, bz);
        if (name.startsWith("blockdust") && block instanceof BlockPlacedItem) {
            ci.cancel();
            return;
        }
        Block belowBlock = world.getBlock(bx, by - 1, bz);
        if (name.startsWith("blockdust") && belowBlock instanceof BlockPlacedItem) {
            ci.cancel();
            return;
        }
        ItemStack stack = null;
        if (block instanceof BlockPlacedItem) {
            stack = getPlacedStack(world, bx, by, bz);
        } else {
            Block below = belowBlock;
            if (below instanceof BlockPlacedItem) {
                stack = getPlacedStack(world, bx, by - 1, bz);
            }
        }
        if (stack != null) {
            if ((name.startsWith("blockdust") || name.startsWith("blockcrack"))
                    && !(stack.getItem() instanceof ItemBlock)) {
                ci.cancel();
                return;
            }
            String prefix = name.startsWith("blockdust") ? "blockdust_" : "blockcrack_";
            String particleName = buildParticleName(prefix, stack);
            if (particleName != null) {
                rf$redirecting = true;
                try {
                    world.spawnParticle(particleName, x, y, z, motionX, motionY, motionZ);
                } finally {
                    rf$redirecting = false;
                }
                ci.cancel();
            }
        }
    }

    private static ItemStack getPlacedStack(WorldClient world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TilePlacedItem) {
            return ((TilePlacedItem) te).getStack();
        }
        return null;
    }

    private static String buildParticleName(String prefix, ItemStack stack) {
        if (stack.getItem() instanceof ItemBlock) {
            Block stackBlock = Block.getBlockFromItem(stack.getItem());
            if (stackBlock == null) {
                return null;
            }
            int blockId = Block.getIdFromBlock(stackBlock);
            int meta = stack.getItemDamage();
            return prefix + blockId + "_" + meta;
        }
        int itemId = Item.getIdFromItem(stack.getItem());
        if (itemId < 0) {
            return null;
        }
        return "iconcrack_" + itemId + "_" + stack.getItemDamage();
    }
}
