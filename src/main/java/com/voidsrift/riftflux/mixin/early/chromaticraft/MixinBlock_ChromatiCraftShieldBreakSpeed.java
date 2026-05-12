package com.voidsrift.riftflux.mixin.early.chromaticraft;

import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield;
import com.voidsrift.riftflux.ModConfig;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public abstract class MixinBlock_ChromatiCraftShieldBreakSpeed {

    @Inject(method = "getPlayerRelativeBlockHardness", at = @At("HEAD"), cancellable = true)
    private void riftflux$makeNetherShieldingStoneMineLikeObsidian(EntityPlayer player, World world, int x, int y, int z, CallbackInfoReturnable<Float> cir) {
        Block block = (Block) (Object) this;
        if (block instanceof BlockStructureShield && riftflux$isNetherShieldingStoneBreakable(world)) {
            cir.setReturnValue(ForgeHooks.blockStrength(Blocks.obsidian, player, world, x, y, z));
        }
    }

    private static boolean riftflux$isNetherShieldingStoneBreakable(World world) {
        return ModConfig.chromatiCraftNetherStructureShieldBreakableLikeObsidian
                && world != null
                && world.provider != null
                && world.provider.dimensionId == -1;
    }
}
