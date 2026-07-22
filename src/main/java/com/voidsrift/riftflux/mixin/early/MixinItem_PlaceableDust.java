package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.glowstonedust.BlockGlowstoneDust;
import com.voidsrift.riftflux.placeablegunpowder.BlockGunpowder;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class MixinItem_PlaceableDust {

    @Inject(method = "onItemUse", at = @At("HEAD"), cancellable = true)
    private void riftflux$placeDustAfterBlockInteraction(
            ItemStack stack,
            EntityPlayer player,
            World world,
            int x,
            int y,
            int z,
            int side,
            float hitX,
            float hitY,
            float hitZ,
            CallbackInfoReturnable<Boolean> cir) {
        Block dustBlock = riftflux$getDustBlock();
        if (dustBlock == null) {
            return;
        }

        Block clickedBlock = world.getBlock(x, y, z);
        if (!clickedBlock.isReplaceable(world, x, y, z)) {
            if (side == 0) {
                --y;
            } else if (side == 1) {
                ++y;
            } else if (side == 2) {
                --z;
            } else if (side == 3) {
                ++z;
            } else if (side == 4) {
                --x;
            } else if (side == 5) {
                ++x;
            }

            Block targetBlock = world.getBlock(x, y, z);
            if (!targetBlock.isAir(world, x, y, z) && !targetBlock.isReplaceable(world, x, y, z)) {
                cir.setReturnValue(false);
                return;
            }
        }

        if (!player.canPlayerEdit(x, y, z, side, stack) || !dustBlock.canPlaceBlockAt(world, x, y, z)) {
            cir.setReturnValue(false);
            return;
        }

        if (!world.setBlock(x, y, z, dustBlock)) {
            cir.setReturnValue(false);
            return;
        }

        Block.SoundType sound = dustBlock.stepSound;
        world.playSoundEffect(
                x + 0.5D,
                y + 0.5D,
                z + 0.5D,
                sound.func_150496_b(),
                (sound.getVolume() + 1.0F) / 2.0F,
                sound.getPitch() * 0.8F);

        if (!player.capabilities.isCreativeMode) {
            --stack.stackSize;
        }
        cir.setReturnValue(true);
    }

    private Block riftflux$getDustBlock() {
        if ((Object) this == Items.gunpowder && ModConfig.enablePlaceableGunpowder) {
            return BlockGunpowder.instance;
        }
        if ((Object) this == Items.glowstone_dust && ModConfig.enableGlowstoneDust) {
            return BlockGlowstoneDust.instance;
        }
        return null;
    }
}
