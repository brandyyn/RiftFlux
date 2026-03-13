package com.voidsrift.riftflux.mixin.early.dualhotbar;

import com.voidsrift.riftflux.dualhotbar.DualHotbarPickBlockHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ForgeHooks.class)
public class MixinForgeHooks_PickBlock {

    @Inject(method = "onPickBlock", at = @At("HEAD"), cancellable = true, remap = false)
    private static void riftflux$extendPickBlock(MovingObjectPosition target, EntityPlayer player, World world,
                                                 CallbackInfoReturnable<Boolean> cir) {
        if (target == null || player == null || world == null) {
            cir.setReturnValue(false);
            return;
        }

        ItemStack result = null;
        boolean creative = player.capabilities.isCreativeMode;
        if (target.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            int x = target.blockX;
            int y = target.blockY;
            int z = target.blockZ;
            Block block = world.getBlock(x, y, z);
            if (block.isAir(world, x, y, z)) {
                cir.setReturnValue(false);
                return;
            }
            result = block.getPickBlock(target, world, x, y, z, player);
        } else if (target.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY && target.entityHit != null && creative) {
            result = target.entityHit.getPickedResult(target);
        }

        if (result == null) {
            cir.setReturnValue(false);
            return;
        }

        int slot = DualHotbarPickBlockHelper.findMatchingHotbarSlot(player, result);
        if (slot >= 0) {
            player.inventory.currentItem = slot;
            cir.setReturnValue(true);
            return;
        }

        if (!creative) {
            cir.setReturnValue(false);
            return;
        }

        slot = DualHotbarPickBlockHelper.findFirstEmptyHotbarSlot(player);
        if (slot < 0 || slot >= DualHotbarPickBlockHelper.getHotbarSize(player)) {
            slot = player.inventory.currentItem;
        }
        player.inventory.setInventorySlotContents(slot, result);
        player.inventory.currentItem = slot;
        cir.setReturnValue(true);
    }
}
