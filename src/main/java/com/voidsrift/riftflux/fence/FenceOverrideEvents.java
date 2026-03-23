package com.voidsrift.riftflux.fence;

import com.voidsrift.riftflux.net.MsgSyncFenceOverrides;
import com.voidsrift.riftflux.net.RFNetwork;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;

public class FenceOverrideEvents {

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event == null || event.entityPlayer == null || event.entityPlayer.worldObj == null) {
            return;
        }
        if (event.action != PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        World world = event.entityPlayer.worldObj;
        if (world.isRemote) {
            return;
        }

        ItemStack held = event.entityPlayer.getCurrentEquippedItem();
        if (held == null || !(held.getItem() instanceof ItemAxe)) {
            return;
        }

        Block block = world.getBlock(event.x, event.y, event.z);
        if (!FenceRenderSupport.isSupportedFence(block)) {
            return;
        }

        int dim = world.provider == null ? 0 : world.provider.dimensionId;
        if (!FenceOverrideData.markOriginal(world, event.x, event.y, event.z, dim)) {
            return;
        }

        if (!event.entityPlayer.capabilities.isCreativeMode) {
            held.damageItem(1, event.entityPlayer);
        }

        playStripSound(world, block, event.x, event.y, event.z);
        world.markBlockForUpdate(event.x, event.y, event.z);
        sendDelta(dim, event.x, event.y, event.z, true);
        event.setCanceled(true);
    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event == null || event.world == null || event.world.isRemote) {
            return;
        }

        int dim = event.world.provider == null ? 0 : event.world.provider.dimensionId;
        if (FenceOverrideData.clearOriginal(event.world, event.x, event.y, event.z, dim)) {
            sendDelta(dim, event.x, event.y, event.z, false);
        }
    }

    @SubscribeEvent
    public void onBlockPlace(BlockEvent.PlaceEvent event) {
        if (event == null || event.world == null || event.world.isRemote) {
            return;
        }

        int dim = event.world.provider == null ? 0 : event.world.provider.dimensionId;
        if (FenceOverrideData.clearOriginal(event.world, event.x, event.y, event.z, dim)) {
            sendDelta(dim, event.x, event.y, event.z, false);
        }
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        syncPlayer(event == null ? null : event.player);
    }

    @SubscribeEvent
    public void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        syncPlayer(event == null ? null : event.player);
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        syncPlayer(event == null ? null : event.player);
    }

    private static void syncPlayer(EntityPlayer player) {
        if (!(player instanceof EntityPlayerMP) || player.worldObj == null || player.worldObj.isRemote || RFNetwork.CH == null) {
            return;
        }

        int dim = player.worldObj.provider == null ? 0 : player.worldObj.provider.dimensionId;
        int[] coordinates = FenceOverrideData.getDimensionCoordinates(player.worldObj, dim);
        RFNetwork.CH.sendTo(MsgSyncFenceOverrides.full(dim, coordinates), (EntityPlayerMP) player);
    }

    private static void sendDelta(int dim, int x, int y, int z, boolean enabled) {
        if (RFNetwork.CH == null) {
            return;
        }
        RFNetwork.CH.sendToDimension(MsgSyncFenceOverrides.delta(dim, x, y, z, enabled), dim);
    }

    private static void playStripSound(World world, Block block, int x, int y, int z) {
        if (world == null || block == null || block.stepSound == null) {
            return;
        }
        world.playSoundEffect(
                x + 0.5D,
                y + 0.5D,
                z + 0.5D,
                block.stepSound.func_150496_b(),
                (block.stepSound.getVolume() + 1.0F) / 2.0F,
                block.stepSound.getPitch() * 0.8F
        );
    }
}
