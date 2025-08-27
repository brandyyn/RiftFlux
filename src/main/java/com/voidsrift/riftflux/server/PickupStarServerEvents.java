package com.voidsrift.riftflux.server;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.net.MsgPickup;
import com.voidsrift.riftflux.net.RFNetwork;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;

import java.util.*;

public final class PickupStarServerEvents {

    private static final String TAG_NEW = "riftfixes_new";

    private static final class Key {
        final Item item; final int meta;
        Key(Item i, int m){ item=i; meta=m; }
        @Override public boolean equals(Object o){ if(this==o) return true; if(!(o instanceof Key)) return false; Key k=(Key)o; return item==k.item && meta==k.meta; }
        @Override public int hashCode(){ return (System.identityHashCode(item)*31) ^ meta; }
        ItemStack toStack(int count){ return new ItemStack(item, Math.max(1,count), meta); }
        static Key of(ItemStack s){ return new Key(s.getItem(), s.getItemDamage()); }
    }

    private static final class State {
        Map<Key,Integer> baseTotals;           // aggregate snapshot at pickup time
        int retryTicks;                        // how many ticks left to watch for merges
        final Map<Key,Integer> expect = new HashMap<Key,Integer>(); // expected adds from the event
        boolean pending = false;
    }

    private final Map<UUID, State> states = new HashMap<UUID, State>();

    @SubscribeEvent
    public void onPickup(EntityItemPickupEvent e) {
        if (e.entityPlayer == null || e.entityPlayer.worldObj.isRemote) return;

        final UUID id = e.entityPlayer.getUniqueID();
        State st = states.get(id);
        if (st == null) { st = new State(); states.put(id, st); }

        // Snapshot aggregate totals BEFORE the merge happens
        st.baseTotals = totalsNow(e.entityPlayer.openContainer, e.entityPlayer);

        // Record what we expect to be added by this pickup (cap for noisy inventories)
        final ItemStack is = (e.item != null) ? e.item.getEntityItem() : null;
        if (is != null && is.getItem() != null && is.stackSize > 0) {
            final Key k = Key.of(is);
            final int add = is.stackSize;
            st.expect.put(k, st.expect.containsKey(k) ? st.expect.get(k) + add : add);
        }

        // Allow a small window for inventories that merge on a later tick
        st.retryTicks = 4;
        st.pending = true;
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent e) {
        if (e.phase != TickEvent.Phase.END) return;
        if (e.player.worldObj.isRemote) return;

        final UUID id = e.player.getUniqueID();
        final State st = states.get(id);
        if (st == null || !st.pending) return;

        boolean sentAny = false;

        // Current aggregate totals (main + any open container, ignoring NBT)
        final Map<Key,Integer> cur = totalsNow(e.player.openContainer, e.player);

        if (ModConfig.enablePickupNotifier && st.baseTotals != null) {
            for (Map.Entry<Key,Integer> en : cur.entrySet()) {
                final Key k = en.getKey();
                final int now = en.getValue();
                final int was = st.baseTotals.containsKey(k) ? st.baseTotals.get(k) : 0;
                int delta = now - was;
                if (delta <= 0) continue;

                // Cap by what we expect, if this pickup just announced it
                if (st.expect.containsKey(k)) {
                    int cap = st.expect.get(k);
                    if (cap <= 0) continue;
                    if (delta > cap) delta = cap;
                }

                if (delta > 0) {
                    RFNetwork.CH.sendTo(new MsgPickup(k.toStack(delta), delta), (EntityPlayerMP) e.player);
                    sentAny = true;
                }
            }
        }

        // If we sent anything or we ran out of retries, finalize and clean up tags once
        st.retryTicks--;
        if (sentAny || st.retryTicks <= 0) {
            stripOurTags(e.player.openContainer, e.player); // remove server-side poison tag so stacks keep merging
            st.pending = false;
            st.baseTotals = null;
            st.expect.clear();
        }
    }

    /* ---------------- helpers ---------------- */

    private static Map<Key,Integer> totalsNow(Container cur, net.minecraft.entity.player.EntityPlayer p){
        final Map<Key,Integer> m = new HashMap<Key,Integer>();
        addTotals(m, p.inventory.mainInventory);
        if (cur != null && cur.inventorySlots != null) {
            @SuppressWarnings("rawtypes")
            final List slots = cur.inventorySlots;
            for (int i=0;i<slots.size();i++){
                final Slot s = (Slot) slots.get(i);
                if (s.inventory == p.inventory) continue;
                final ItemStack st = s.getStack();
                if (st != null && st.getItem() != null) accum(m, Key.of(st), st.stackSize);
            }
        }
        return m;
    }

    private static void addTotals(Map<Key,Integer> m, ItemStack[] arr){
        if (arr == null) return;
        for (int i=0;i<arr.length;i++){
            final ItemStack st = arr[i];
            if (st != null && st.getItem() != null) accum(m, Key.of(st), st.stackSize);
        }
    }

    private static void accum(Map<Key,Integer> m, Key k, int add){
        m.put(k, m.containsKey(k) ? m.get(k) + add : add);
    }

    // Never write tags; only remove our tag so stacks remain combinable
    private static void stripOurTags(Container cur, net.minecraft.entity.player.EntityPlayer p){
        boolean any = false;
        final ItemStack[] main = p.inventory.mainInventory;
        if (main != null) {
            for (int i=0;i<main.length;i++){
                final ItemStack st = main[i];
                if (st != null && removeOurTag(st)) any = true;
            }
        }
        if (cur != null && cur.inventorySlots != null) {
            @SuppressWarnings("rawtypes")
            final List slots = cur.inventorySlots;
            for (int i=0;i<slots.size();i++){
                final Slot s = (Slot) slots.get(i);
                final ItemStack st = s.getStack();
                if (st != null && removeOurTag(st)) { s.onSlotChanged(); any = true; }
            }
        }
        if (any && cur != null) cur.detectAndSendChanges();
    }

    private static boolean removeOurTag(ItemStack st){
        NBTTagCompound t = st.getTagCompound();
        if (t != null && t.hasKey(TAG_NEW)) {
            t.removeTag(TAG_NEW);
            if (t.hasNoTags()) st.setTagCompound(null);
            return true;
        }
        return false;
    }
}
