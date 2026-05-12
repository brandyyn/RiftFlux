package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(Block.class)
public abstract class MixinBlock_NetherBedrockHardness {

    @Inject(method = "getBlockHardness", at = @At("HEAD"), cancellable = true)
    private void riftflux$makeNetherBedrockBreakLikeObsidian(World world, int x, int y, int z, CallbackInfoReturnable<Float> cir) {
        if (!ModConfig.chromatiCraftNetherBedrockBreakableLikeObsidian || world == null || world.provider == null) {
            return;
        }

        Block block = (Block) (Object) this;
        if (block == Blocks.bedrock && world.provider.dimensionId == -1) {
            cir.setReturnValue(Blocks.obsidian.getBlockHardness(world, x, y, z));
        }
    }

    @Inject(method = "getItemDropped", at = @At("HEAD"), cancellable = true)
    private void riftflux$preventBedrockItemDrop(int meta, Random random, int fortune, CallbackInfoReturnable<Item> cir) {
        Block block = (Block) (Object) this;
        if (block == Blocks.bedrock) {
            cir.setReturnValue(null);
        }
    }
}
