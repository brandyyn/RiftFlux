package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public abstract class MixinBlock_WoolShearsOnly {

    @Inject(method = {"harvestBlock"}, at = @At("HEAD"), cancellable = true)
    private void riftflux$woolDropsOnlyWithShears(World world, EntityPlayer player, int x, int y, int z, int meta, CallbackInfo ci) {
        if (world.isRemote) return;
        if (!ModConfig.woolRequireShears) return;
        if ((Object) this != Blocks.wool) return;
        if (player == null) { ci.cancel(); return; }
        if (player.capabilities.isCreativeMode) return;

        ItemStack held = player.getCurrentEquippedItem();
        if (held == null || held.getItem() != Items.shears) ci.cancel();
    }
}
