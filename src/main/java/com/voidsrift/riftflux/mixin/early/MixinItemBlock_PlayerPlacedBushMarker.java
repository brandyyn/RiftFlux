package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.util.RFPlantContext;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemBlock.class)
public abstract class MixinItemBlock_PlayerPlacedBushMarker {

    @Inject(method = "placeBlockAt", at = @At("RETURN"), remap = false, require = 0)
    private void riftflux$markPlacedBush(ItemStack stack, EntityPlayer player, World world,
                                         int x, int y, int z, int side,
                                         float hitX, float hitY, float hitZ, int metadata,
                                         CallbackInfoReturnable<Boolean> cir) {
        if (!Boolean.TRUE.equals(cir.getReturnValue())) {
            return;
        }
        if (!ModConfig.allowPlantsOnAnyBlock && !ModConfig.directionalCrossedPlantRenderingByPlacement) {
            return;
        }

        Block block = world.getBlock(x, y, z);
        if (!(block instanceof BlockBush)) {
            return;
        }

        RFPlantContext.markPlayerPlaced(world, x, y, z);
        RFPlantContext.markCrossedPlantFacingFromPlacer(world, x, y, z, player);

        if (block instanceof BlockDoublePlant || world.getBlock(x, y + 1, z) == block) {
            RFPlantContext.markPlayerPlaced(world, x, y + 1, z);
            RFPlantContext.markCrossedPlantFacingFromPlacer(world, x, y + 1, z, player);
        }
    }
}
