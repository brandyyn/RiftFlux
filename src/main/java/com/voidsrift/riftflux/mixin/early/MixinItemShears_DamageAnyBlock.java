package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.ModConfig;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public abstract class MixinItemShears_DamageAnyBlock {

    @Inject(method = {"harvestBlock"}, at = @At("TAIL"))
    private void riftflux$shearsDurabilityAnyBlock(World world, EntityPlayer player, int x, int y, int z, int meta, CallbackInfo ci) {
        if (!ModConfig.shearsDamageOnAnyBlock) return;
        if (world == null || world.isRemote) return;
        if (player == null || player.capabilities.isCreativeMode) return;

        ItemStack held = player.getCurrentEquippedItem();
        if (held == null || !(held.getItem() instanceof ItemShears)) return;

        Block block = (Block)(Object)this;
        if (block == null || block == Blocks.air) return;
        if (isVanillaShearsTarget(block)) return;

        float hardness = block.getBlockHardness(world, x, y, z);
        if (hardness <= 0F) return;

        held.damageItem(1, player);
    }

    private static boolean isVanillaShearsTarget(Block b) {
        return b == Blocks.web
                || b == Blocks.leaves
                || b == Blocks.leaves2
                || b == Blocks.vine
                || b == Blocks.tallgrass
                || b == Blocks.tripwire;
    }
}
