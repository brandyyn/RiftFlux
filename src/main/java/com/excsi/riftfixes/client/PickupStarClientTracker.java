package com.excsi.riftfixes.client;

import com.excsi.riftfixes.ModConfig;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayDeque;
import java.util.Deque;

/** Tags stacks ONLY when matching a recent pickup packet (no retrigger on sorting). */
public final class PickupStarClientTracker {
    private static final String TAG = "riftfixes_new";
    private static final int WINDOW_TICKS = 40; // ~2s to correlate with set-slot updates

    private static ItemStack[] baseline = null;

    private static final Deque<PickupKey> queue = new ArrayDeque<PickupKey>();
    private static final class PickupKey { final Item i; final int m; int ttl;
        PickupKey(Item i, int m, int ttl){this.i=i;this.m=m;this.ttl=ttl;}
        boolean matches(ItemStack s){return s!=null && s.getItem()==i && s.getItemDamage()==m;}
    }

    /** Called by net handler. */
    public static void enqueue(String itemName, int meta) {
        Item it = (Item) Item.itemRegistry.getObject(itemName);
        if (it != null) queue.addLast(new PickupKey(it, meta, WINDOW_TICKS));
    }

    public static void bootstrap() {
        cpw.mods.fml.common.FMLCommonHandler.instance().bus().register(new PickupStarClientTracker());
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent evt) {
        if (!ModConfig.enableItemPickupStar) return;
        if (evt.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer p = mc != null ? mc.thePlayer : null;
        if (p == null) return;

        // decay queue
        if (!queue.isEmpty()) {
            for (PickupKey pk : queue) pk.ttl--;
            while (!queue.isEmpty() && queue.peekFirst().ttl <= 0) queue.removeFirst();
        }

        ItemStack[] cur = p.inventory.mainInventory;
        if (cur == null) return;

        if (baseline == null || baseline.length != cur.length) {
            baseline = new ItemStack[cur.length];
            for (int i=0;i<cur.length;i++) baseline[i] = copy(cur[i]);
            return;
        }

        for (int i=0;i<cur.length;i++) {
            ItemStack now = cur[i];
            ItemStack was = baseline[i];

            boolean changed = (now != null && was == null)
                    || (now != null && was != null && !sameItem(was, now))
                    || (ModConfig.itemPickupStarOnStackIncrease && now!=null && was!=null
                    && sameItem(was, now) && now.stackSize > was.stackSize);

            if (changed && now != null && matchesAnyPickup(now)) {
                NBTTagCompound tag = now.getTagCompound();
                if (tag == null) tag = new NBTTagCompound();
                tag.setBoolean(TAG, true);
                now.setTagCompound(tag);
            }

            baseline[i] = copy(now);
        }
    }

    private static boolean matchesAnyPickup(ItemStack s){for(PickupKey pk:queue)if(pk.matches(s))return true;return false;}
    private static ItemStack copy(ItemStack in){return in!=null?in.copy():null;}
    private static boolean sameItem(ItemStack a, ItemStack b){return a.getItem()==b.getItem() && a.getItemDamage()==b.getItemDamage();}
}
