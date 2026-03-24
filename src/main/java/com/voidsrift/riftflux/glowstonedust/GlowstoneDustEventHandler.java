package com.voidsrift.riftflux.glowstonedust;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class GlowstoneDustEventHandler {

    @SubscribeEvent
    public void placeGlowstoneDust(PlayerInteractEvent event) {
        if (event.action != PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK
                || event.entityPlayer.getHeldItem() == null
                || event.entityPlayer.getHeldItem().getItem() != Items.glowstone_dust
                || BlockGlowstoneDust.instance == null) {
            return;
        }

        int posX = event.x;
        int posY = event.y;
        int posZ = event.z;
        Block block = event.world.getBlock(event.x, event.y, event.z);
        if (!block.isReplaceable(event.world, event.x, event.y, event.z)) {
            if (event.face == 0) {
                --posY;
            }
            if (event.face == 1) {
                ++posY;
            }
            if (event.face == 2) {
                --posZ;
            }
            if (event.face == 3) {
                ++posZ;
            }
            if (event.face == 4) {
                --posX;
            }
            if (event.face == 5) {
                ++posX;
            }
            if (!event.world.isAirBlock(posX, posY, posZ)
                    && !event.world.getBlock(posX, posY, posZ).isReplaceable(event.world, posX, posY, posZ)) {
                return;
            }
        }

        if (!event.entityPlayer.canPlayerEdit(posX, posY, posZ, event.face, event.entityPlayer.getHeldItem())) {
            return;
        }

        if (BlockGlowstoneDust.instance.canPlaceBlockAt(event.world, posX, posY, posZ)) {
            if (!event.entityPlayer.capabilities.isCreativeMode) {
                --event.entityPlayer.getHeldItem().stackSize;
            }
            event.world.setBlock(posX, posY, posZ, BlockGlowstoneDust.instance);
        }
    }
}
