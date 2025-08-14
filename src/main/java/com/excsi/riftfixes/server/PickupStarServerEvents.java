package com.excsi.riftfixes.server;

import com.excsi.riftfixes.net.MsgPickup;
import com.excsi.riftfixes.net.RFNetwork;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;

public final class PickupStarServerEvents {

    @SubscribeEvent
    public void onPickup(EntityItemPickupEvent e) {
        if (e.entityPlayer == null || e.entityPlayer.worldObj.isRemote) return;

        EntityItem ei = e.item;
        if (ei == null) return;

        ItemStack st = ei.getEntityItem();
        if (st == null) return;

        // Send the *full stack* once so the client shows correct name/rarity.
        RFNetwork.CH.sendTo(new MsgPickup(st, st.stackSize), (EntityPlayerMP) e.entityPlayer);
    }
}
