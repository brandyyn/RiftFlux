package com.excsi.riftfixes.server;

import com.excsi.riftfixes.ModConfig;
import com.excsi.riftfixes.net.MsgPickup;
import com.excsi.riftfixes.net.RFNetwork;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;

public final class PickupStarServerEvents {

    @SubscribeEvent
    public void onPickup(EntityItemPickupEvent e) {
        if (!ModConfig.enableItemPickupStar && !ModConfig.enablePickupNotifier) return;
        if (!(e.entityPlayer instanceof EntityPlayerMP)) return;

        ItemStack is = e.item.getEntityItem();
        if (is == null || is.getItem() == null) return;

        String name = (String) Item.itemRegistry.getNameForObject(is.getItem());
        if (name == null) return;

        int meta = is.getItemDamage();
        int count = Math.max(1, is.stackSize); // good enough visual count
        RFNetwork.CH.sendTo(new MsgPickup(name, meta, count), (EntityPlayerMP) e.entityPlayer);
    }
}
