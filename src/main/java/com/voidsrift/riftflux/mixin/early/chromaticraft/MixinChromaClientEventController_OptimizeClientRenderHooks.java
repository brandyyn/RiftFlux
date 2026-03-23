package com.voidsrift.riftflux.mixin.early.chromaticraft;

import Reika.ChromatiCraft.Auxiliary.Ability.AbilityHelper;
import Reika.ChromatiCraft.ChromaClientEventController;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.ModInterface.VoidRitual.VoidMonsterDestructionRitual;
import Reika.ChromatiCraft.Magic.Potions.PotionVoidGaze;
import Reika.ChromatiCraft.Registry.Chromabilities;
import com.voidsrift.riftflux.ModConfig;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ChromaClientEventController.class, remap = false)
public abstract class MixinChromaClientEventController_OptimizeClientRenderHooks {

    @Inject(method = "onBlockTriedRender", at = @At("HEAD"), cancellable = true, remap = false)
    private void riftflux$skipBlockRenderMisses(Block block, int x, int y, int z, WorldRenderer worldRenderer, RenderBlocks renderBlocks, int renderPass, CallbackInfoReturnable<Boolean> cir) {
        if (!ModConfig.optimizeChromatiCraftRenderEventFastPaths) {
            return;
        }
        EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
        if (block == Blocks.snow_layer && player != null && PotionVoidGaze.VoidGazeLevels.FACEFLIP.isActiveOnPlayer((EntityPlayer)player)) {
            return;
        }
        if (AbilityHelper.instance.isLOSViewEnabled() || AbilityHelper.instance.isNoClipEnabled) {
            return;
        }
        if (block == Blocks.redstone_torch || block == Blocks.unlit_redstone_torch) {
            return;
        }
        if (renderPass != 1) {
            cir.setReturnValue(false);
            return;
        }
        IBlockAccess access = renderBlocks.blockAccess;
        if (access == null || !ChromatiCraft.isRainbowForest(access.getBiomeGenForCoords(x, z)) || !block.isWood(access, x, y, z)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "tryRenderEntity", at = @At("HEAD"), cancellable = true, remap = false)
    private void riftflux$skipEntityRenderMisses(Render render, Entity entity, double renderPosX, double renderPosY, double renderPosZ, float par8, float par9, CallbackInfoReturnable<Boolean> cir) {
        if (!ModConfig.optimizeChromatiCraftRenderEventFastPaths) {
            return;
        }
        if (entity == null || entity.worldObj == null) {
            cir.setReturnValue(false);
            return;
        }
        EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
        boolean hasVoidRitual = VoidMonsterDestructionRitual.ritualsActive();
        boolean hasChestClear = entity instanceof EntityPlayer && Chromabilities.CHESTCLEAR.enabledOn((EntityPlayer)entity);
        boolean hasMobSeek = player != null && Chromabilities.MOBSEEK.enabledOn((EntityPlayer)player);
        boolean hasSpawnerSee = player != null && Chromabilities.SPAWNERSEE.enabledOn((EntityPlayer)player);
        if (!hasVoidRitual && !hasChestClear && !hasMobSeek && !hasSpawnerSee) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "postTileRender", at = @At("HEAD"), cancellable = true, remap = false)
    private void riftflux$skipTileRenderMisses(TileEntitySpecialRenderer renderer, TileEntity tile, double x, double y, double z, float partialTick, CallbackInfo ci) {
        if (!ModConfig.optimizeChromatiCraftRenderEventFastPaths) {
            return;
        }
        EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
        if (tile == null || tile.getWorldObj() == null || player == null || !Chromabilities.SPAWNERSEE.enabledOn((EntityPlayer)player)) {
            ci.cancel();
        }
    }
}
