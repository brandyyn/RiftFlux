package com.voidsrift.riftflux.server;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.net.MsgPickup;
import com.voidsrift.riftflux.net.RFNetwork;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Server-side item pickup notifier aggregator.
 *
 * Fixes:
 * 1) Use the previous-tick inventory snapshot as the baseline so pickups that merge
 *    immediately (common in heavy modpacks) are still detected.
 * 2) Aggregate across main inventory + open container (ignoring NBT) and send one
 *    MsgPickup per key. No false-positives: only active during a pickup window.
 */
public final class PickupStarServerEvents {

    private static final String TAG_NEW  = "riftflux_new";
    private static final int    MAX_PER_TICK_KEYS = 8;

    private static final class Key {
        final Item item;
        final int  meta;
        Key(Item i, int m){ item = i; meta = m; }
        @Override public boolean equals(Object o){
            if (this == o) return true;
            if (!(o instanceof Key)) return false;
            Key k = (Key)o;
            return item == k.item && meta == k.meta;
        }
        @Override public int hashCode(){
            return (System.identityHashCode(item) * 31) ^ meta;
        }
        ItemStack toStack(int count){ return new ItemStack(item, Math.max(1, count), meta); }

        // >>> CHANGE: treat damageable items as damage-insensitive so durability changes
        // don't appear as a new "added" stack during the pickup window.
        static Key of(ItemStack s){ return new Key(s.getItem(), s.isItemStackDamageable() ? 0 : s.getItemDamage()); }
    }

    private static final class State {
        Map<Key,Integer> lastTotals;         // rolling snapshot (prev tick)
        Map<Key,Integer> baseTotals;         // snapshot captured at pickup start
        Map<Key,Integer> expect = new HashMap<Key,Integer>();  // expected adds (from the entity's stackSize)
        boolean pending;
        int retryTicks;
    }

    private final Map<UUID, State> states = new HashMap<UUID, State>();

    @SubscribeEvent
    public void onPickup(EntityItemPickupEvent e) {
        final EntityPlayer player = e.entityPlayer;
        if (player == null || player.worldObj.isRemote) return;

        final UUID id = player.getUniqueID();
        State st = states.get(id);
        if (st == null) { st = new State(); states.put(id, st); }

        // Use the previous-tick totals as a guaranteed "before" snapshot.
        // If we don't have one yet, fall back to a fresh snapshot now.
        st.baseTotals = (st.lastTotals != null) ? new HashMap<Key,Integer>(st.lastTotals)
                : totalsNow(player.openContainer, player);

        // Record what we expect to be added by this pickup (cap for noisy inventories)
        final ItemStack is = (e.item != null) ? e.item.getEntityItem() : null;
        if (is != null && is.getItem() != null && is.stackSize > 0) {
            final Key k = Key.of(is);
            final int add = is.stackSize;
            st.expect.put(k, st.expect.containsKey(k) ? (st.expect.get(k) + add) : add);
        }

        // Allow a small window for inventories that merge on a later tick
        st.retryTicks = 6;   // slightly longer to catch slow merges
        st.pending = true;
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent e) {
        if (e.phase != TickEvent.Phase.END) return;
        if (e.player.worldObj.isRemote) return;

        final EntityPlayer player = e.player;
        final UUID id = player.getUniqueID();
        State st = states.get(id);
        if (st == null) { st = new State(); states.put(id, st); }

        boolean sentAny = false;

        if (st.pending && ModConfig.enablePickupNotifier) {
            // Current aggregate totals (main + any open container, ignoring NBT)
            final Map<Key,Integer> cur = totalsNow(player.openContainer, player);

            if (st.baseTotals != null) {
                int sent = 0;
                for (Map.Entry<Key,Integer> ent : cur.entrySet()) {
                    if (sent >= MAX_PER_TICK_KEYS) break;
                    final Key k = ent.getKey();
                    final int now = ent.getValue();
                    final int before = st.baseTotals.containsKey(k) ? st.baseTotals.get(k) : 0;
                    int delta = now - before;
                    if (delta <= 0) continue;

                    // Cap by what we expect from the pickup event (if present)
                    if (st.expect.containsKey(k)) {
                        final int cap = Math.max(0, st.expect.get(k));
                        if (cap == 0) continue;
                        if (delta > cap) delta = cap;
                    }

                    if (delta > 0) {
                        RFNetwork.CH.sendTo(new MsgPickup(k.toStack(delta), delta), (EntityPlayerMP) player);
                        sentAny = true;
                        sent++;
                    }
                }
            }

            // If we sent nothing and still have retries left, wait another tick
            if (!sentAny && st.retryTicks > 0) {
                st.retryTicks--;
            } else {
                // End the pickup window and clear expectations
                st.pending = false;
                st.expect.clear();
            }

            // Keep lastTotals in sync with what we just saw this tick
            st.lastTotals = cur;
        } else {
            // No pending pickup window; just keep a rolling snapshot for the next pickup
            st.lastTotals = totalsNow(player.openContainer, player);
        }
    }

    // ----- helpers -----

    /** Aggregate counts across main inventory and any open container (ignores NBT) */
    private static Map<Key,Integer> totalsNow(Container cur, EntityPlayer p){
        final Map<Key,Integer> m = new HashMap<Key,Integer>();
        addTotals(m, p.inventory.mainInventory);
        if (cur != null && cur.inventorySlots != null) {
            @SuppressWarnings("rawtypes")
            final List slots = cur.inventorySlots;
            for (int i = 0; i < slots.size(); i++) {
                final Slot s = (Slot) slots.get(i);
                if (s.inventory == p.inventory) continue; // already counted
                final ItemStack st = s.getStack();
                if (st == null || st.getItem() == null || st.stackSize <= 0) continue;
                addOne(m, st);
            }
        }
        return m;
    }

    private static void addTotals(Map<Key,Integer> m, ItemStack[] arr){
        if (arr == null) return;
        for (int i = 0; i < arr.length; i++) {
            final ItemStack st = arr[i];
            if (st == null || st.getItem() == null || st.stackSize <= 0) continue;
            addOne(m, st);
        }
    }

    private static void addOne(Map<Key,Integer> m, ItemStack st){
        final Key k = Key.of(st);
        final int c = m.containsKey(k) ? m.get(k) : 0;
        m.put(k, c + st.stackSize);
    }

    /** Never write tags; only remove our tag so stacks remain combinable */
    private static void stripOurTags(Container cur, EntityPlayer p){
        boolean any = false;
        final ItemStack[] main = p.inventory.mainInventory;
        if (main != null) {
            for (int i = 0; i < main.length; i++) {
                final ItemStack st = main[i];
                if (st != null && removeOurTag(st)) any = true;
            }
        }
        if (cur != null && cur.inventorySlots != null) {
            @SuppressWarnings("rawtypes")
            final List slots = cur.inventorySlots;
            for (int i = 0; i < slots.size(); i++) {
                final Slot s = (Slot) slots.get(i);
                final ItemStack st = s.getStack();
                if (st != null && removeOurTag(st)) any = true;
            }
        }
        if (any && p.openContainer != null) p.openContainer.detectAndSendChanges();
    }

    private static boolean removeOurTag(ItemStack st){
        final NBTTagCompound tag = st.getTagCompound();
        if (tag == null) return false;
        if (!tag.hasKey(TAG_NEW)) return false;
        tag.removeTag(TAG_NEW);
        return true;
    }
}
