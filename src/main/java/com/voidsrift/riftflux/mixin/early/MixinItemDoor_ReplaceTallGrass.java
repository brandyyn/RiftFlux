package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemDoor.class)
public abstract class MixinItemDoor_ReplaceTallGrass {

    @Shadow private Material doorMaterial;

    @Shadow
    public static void placeDoorBlock(World world, int x, int y, int z, int facing, Block doorBlock) {
        throw new IllegalStateException("Mixin failed to shadow placeDoorBlock");
    }

    @Inject(method = "onItemUse(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/world/World;IIIIFFF)Z",
            at = @At("HEAD"),
            cancellable = true)
    private void riftflux$replaceTallGrass(ItemStack stack, EntityPlayer player, World world, int x, int y, int z,
                                           int side, float hitX, float hitY, float hitZ,
                                           CallbackInfoReturnable<Boolean> cir) {
        if (!ModConfig.enableDoorAirPlacement) return;
        if (side != 1) {
            cir.setReturnValue(false);
            return;
        }

        Block target = world.getBlock(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);
        boolean replaceable = target == Blocks.snow_layer && (meta & 7) < 1;
        if (!replaceable) {
            replaceable = target == Blocks.vine
                    || target == Blocks.tallgrass
                    || target == Blocks.deadbush
                    || target.isReplaceable(world, x, y, z);
        }

        int placeY = replaceable ? y : y + 1;
        Block doorBlock = (this.doorMaterial == Material.wood) ? Blocks.wooden_door : Blocks.iron_door;

        if (!player.canPlayerEdit(x, placeY, z, side, stack)
                || !player.canPlayerEdit(x, placeY + 1, z, side, stack)) {
            cir.setReturnValue(false);
            return;
        }

        if (!doorBlock.canPlaceBlockAt(world, x, placeY, z)) {
            cir.setReturnValue(false);
            return;
        }

        int facing = MathHelper.floor_double((double)((player.rotationYaw + 180.0F) * 4.0F / 360.0F) - 0.5D) & 3;
        placeDoorBlock(world, x, placeY, z, facing, doorBlock);
        --stack.stackSize;
        cir.setReturnValue(true);
    }
}
