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

public final class PickupStarClientTracker {

    private static final String TAG = "riftfixes_new";
    private static final int WINDOW_TICKS = 40; // ~2s

    // 36-slot main inv baseline
    private static ItemStack[] baselineMain = null;

    // recent pickups queue (item+meta)
    private static final Deque<PickupKey> queue = new ArrayDeque<PickupKey>();

    private static final class PickupKey {
        final Item item; final int meta; int ttl;
        PickupKey(Item i, int m, int ttl){ item=i; meta=m; this.ttl=ttl; }
        boolean matches(ItemStack s){ return s!=null && s.getItem()==item && s.getItemDamage()==meta; }
    }

    /** Preferred enqueue (from packet): full stack */
    public static void enqueue(ItemStack st){
        if (st == null || st.getItem() == null) return;
        queue.addLast(new PickupKey(st.getItem(), st.getItemDamage(), WINDOW_TICKS));
    }

    /** Legacy helpers kept for old call sites */
    public static void enqueue(String registryName, int meta){
        Item it = (Item) Item.itemRegistry.getObject(registryName);
        if (it != null) queue.addLast(new PickupKey(it, meta, WINDOW_TICKS));
    }
    public static void enqueue(String registryName, int meta, int ignoredLegacyId){
        enqueue(registryName, meta);
    }

    public static void bootstrap(){
        cpw.mods.fml.common.FMLCommonHandler.instance().bus().register(new PickupStarClientTracker());
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent evt){
        if (!ModConfig.enableItemPickupStar) return;
        if (evt.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer p = (mc != null) ? mc.thePlayer : null;
        if (p == null) return;

        // decay queue
        if (!queue.isEmpty()){
            for (PickupKey pk : queue) pk.ttl--;
            while (!queue.isEmpty() && queue.peekFirst().ttl <= 0) queue.removeFirst();
        }

        // main inv only (36)
        ItemStack[] cur = p.inventory.mainInventory;
        if (cur == null) return;

        if (baselineMain == null || baselineMain.length != cur.length){
            baselineMain = new ItemStack[cur.length];
            for (int i=0;i<cur.length;i++) baselineMain[i] = copy(cur[i]);
            return;
        }

        for (int i=0;i<cur.length;i++){
            ItemStack now = cur[i], was = baselineMain[i];

            boolean changed =
                    (now != null && was == null) ||
                            (now != null && was != null && !sameItem(was, now)) ||
                            (ModConfig.itemPickupStarOnStackIncrease && now!=null && was!=null
                                    && sameItem(was, now) && now.stackSize > was.stackSize);

            if (changed && now != null && matchesAnyPickup(now)) {
                NBTTagCompound tag = now.getTagCompound();
                if (tag == null) tag = new NBTTagCompound();
                tag.setBoolean(TAG, true);
                now.setTagCompound(tag);
            }
            baselineMain[i] = copy(now);
        }
    }

    private static boolean matchesAnyPickup(ItemStack s){
        if (queue.isEmpty()) return false;
        for (PickupKey pk : queue) if (pk.matches(s)) return true;
        return false;
    }

    private static ItemStack copy(ItemStack in){ return in!=null ? in.copy() : null; }
    private static boolean sameItem(ItemStack a, ItemStack b){
        return a.getItem()==b.getItem() && a.getItemDamage()==b.getItemDamage();
    }
}
