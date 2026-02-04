package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class MixinItemDoor_ReplaceTallGrass {

    @Inject(
            method = "tryPlaceItemIntoWorld(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/world/World;IIIIFFF)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private void riftflux$doorReplaceTallGrass(EntityPlayer player, World world, int x, int y, int z,
                                               int side, float hitX, float hitY, float hitZ,
                                               CallbackInfoReturnable<Boolean> cir) {
        if (!ModConfig.enableDoorAirPlacement) return;

        ItemStack stack = (ItemStack)(Object)this;
        Item item = stack.getItem();
        boolean itemDoor = item instanceof ItemDoor;
        boolean doorBlockItem = item instanceof ItemBlock
                && ((ItemBlock)item).field_150939_a instanceof BlockDoor;
        if (!itemDoor && !doorBlockItem) return;
        if (side != 1) return;

        Block target = world.getBlock(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);
        boolean replaceable = target == Blocks.snow_layer && (meta & 7) < 1;
        if (!replaceable) {
            replaceable = target == Blocks.vine
                    || target == Blocks.tallgrass
                    || target == Blocks.deadbush
                    || target.isReplaceable(world, x, y, z);
        }
        if (!replaceable) return;

        int primaryY = doorBlockItem ? y : (y > 0 ? y - 1 : y);
        int secondaryY = (primaryY == y) ? (y > 0 ? y - 1 : y) : y;
        int beforeSize = stack.stackSize;
        boolean result = attemptPlace(stack, player, world, item, x, primaryY, z, side, hitX, hitY, hitZ);

        if (!result && primaryY != secondaryY && stack.stackSize == beforeSize) {
            result = attemptPlace(stack, player, world, item, x, secondaryY, z, side, hitX, hitY, hitZ);
        }

        cir.setReturnValue(result);
    }

    private static boolean attemptPlace(ItemStack stack, EntityPlayer player, World world, Item item,
                                        int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            return ForgeHooks.onPlaceItemIntoWorld(stack, player, world, x, y, z, side, hitX, hitY, hitZ);
        }
        boolean result = item.onItemUse(stack, player, world, x, y, z, side, hitX, hitY, hitZ);
        if (result) {
            player.addStat(StatList.objectUseStats[Item.getIdFromItem(item)], 1);
        }
        return result;
    }
}
