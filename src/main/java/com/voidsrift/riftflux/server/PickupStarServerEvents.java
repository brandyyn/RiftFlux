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

import java.util.*;

/**
 * Server-authoritative stars + notifier.
 * - Snapshot at pickup, diff next tick (stars + exact notifier when possible)
 * - Fallback only “tops up” counts for keys that ALREADY had a strict diff delta
 *   (prevents notifications when inventory is full or nothing was actually added).
 */
public final class PickupStarServerEvents {

    private static final String TAG_NEW  = "riftflux_new";
    private static final String TAG_SEEN = "riftflux_seen";

    /** “Item key” used for notifier aggregation (item+meta only). */
    private static final class Key {
        final Item item; final int meta;
        Key(Item i, int m){ item=i; meta=m; }
        @Override public boolean equals(Object o){
            if (this == o) return true;
            if (!(o instanceof Key)) return false;
            Key k = (Key)o; return item == k.item && meta == k.meta;
        }
        @Override public int hashCode(){ return (System.identityHashCode(item)*31) ^ meta; }
        ItemStack toStack(int count){ return new ItemStack(item, Math.max(1,count), meta); }
    }
    private static Key keyOf(ItemStack s){ return new Key(s.getItem(), s.getItemDamage()); }

    /** Per-player state captured at pickup time, consumed next tick. */
    private static final class State {
        ItemStack[] baseMain;            // snapshot of main inventory
        int         baseWinId = Integer.MIN_VALUE;
        ItemStack[] baseCont;            // snapshot of open container slots
        boolean     pending;             // true until processed on next tick

        // What pickup events reported this frame (fallback for notifier only)
        final Map<Key, Integer> expect = new HashMap<Key, Integer>();
    }

    private final Map<UUID, State> states = new HashMap<UUID, State>();

    @SubscribeEvent
    public void onPickup(EntityItemPickupEvent e) {
        final EntityPlayer p = e.entityPlayer;
        if (p == null || p.worldObj.isRemote) return;

        final UUID id = p.getUniqueID();
        State st = states.get(id);
        if (st == null) {
            st = new State();
            states.put(id, st);
        }

        // Snapshot main inventory
        st.baseMain = copyArray(p.inventory.mainInventory);

        // Snapshot open container (satchels/backpacks/chests), if any
        final Container c = p.openContainer;
        if (c != null && c.inventorySlots != null) {
            st.baseWinId = c.windowId;
            st.baseCont  = copyContainer(c);
        } else {
            st.baseWinId = Integer.MIN_VALUE;
            st.baseCont  = null;
        }

        // Record what the event says we’re picking up (used only as a *cap* later)
        final ItemStack is = e.item.getEntityItem();
        if (is != null && is.getItem() != null) {
            final Key k = keyOf(is);
            final int add = Math.max(1, is.stackSize);
            st.expect.put(k, st.expect.containsKey(k) ? st.expect.get(k) + add : add);
        }

        st.pending = true; // process next server tick (END)
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent e) {
        if (e.phase != TickEvent.Phase.END) return;
        if (e.player.worldObj.isRemote) return;

        final UUID id = e.player.getUniqueID();
        final State st = states.get(id);
        if (st == null || !st.pending) return;

        boolean anyContainerChange = false;

        // Actual counted deltas by key (from strict diff), for notifier merge
        final Map<Key, Integer> counted = new HashMap<Key, Integer>();

        // ---- Diff main inventory (36 slots) ----
        final ItemStack[] curMain = e.player.inventory.mainInventory;
        if (curMain != null && st.baseMain != null && st.baseMain.length == curMain.length) {
            for (int i = 0; i < curMain.length; i++) {
                final ItemStack now = curMain[i];
                final ItemStack was = st.baseMain[i];
                final int added = tagIfNewOrGrew(now, was);
                if (added > 0) {
                    accum(counted, keyOf(now), added);
                    sendNotify((EntityPlayerMP) e.player, now, added);
                }
            }
        }

        // ---- Diff open container if it is still the same ----
        final Container cur = e.player.openContainer;
        if (cur != null && cur.inventorySlots != null && st.baseCont != null && cur.windowId == st.baseWinId) {
            @SuppressWarnings("rawtypes")
            final List slots = cur.inventorySlots;
            if (st.baseCont.length == slots.size()) {
                for (int i = 0; i < slots.size(); i++) {
                    final Slot s = (Slot) slots.get(i);

                    // Avoid double-accounting player inventory; main diff already handled it.
                    if (s.inventory == e.player.inventory) continue;

                    final ItemStack now = s.getStack();
                    final ItemStack was = st.baseCont[i];

                    final int added = tagIfNewOrGrew(now, was);
                    if (added > 0) {
                        anyContainerChange = true;
                        s.onSlotChanged();
                        accum(counted, keyOf(now), added);
                        sendNotify((EntityPlayerMP) e.player, now, added);
                    }
                }
            }
            if (anyContainerChange) cur.detectAndSendChanges();
        }

        // ---- Fallback: ONLY top-up keys that already had some counted delta ----
        // This prevents notifications when nothing actually entered the inventory (e.g., full bags).
        if (ModConfig.enablePickupNotifier && !st.expect.isEmpty() && !counted.isEmpty()) {
            for (Map.Entry<Key,Integer> ex : st.expect.entrySet()) {
                final Key k   = ex.getKey();
                if (!counted.containsKey(k)) continue; // <-- crucial: only for keys we truly saw change
                final int want = ex.getValue();
                final int got  = counted.get(k);
                final int missing = want - got;
                if (missing > 0) {
                    RFNetwork.CH.sendTo(new MsgPickup(k.toStack(missing), missing), (EntityPlayerMP) e.player);
                }
            }
        }

        // Consume snapshot
        st.pending   = false;
        st.baseMain  = null;
        st.baseCont  = null;
        st.baseWinId = Integer.MIN_VALUE;
        st.expect.clear();
    }

    // ---------------- helpers ----------------

    /** Tag as NEW (and clear SEEN) if now is a brand-new stack or a strict-growth from was.
     *  Returns the number of items newly added to this slot (0 if none). */
    private static int tagIfNewOrGrew(ItemStack now, ItemStack was) {
        if (now == null) return 0;

        final boolean isNew = (was == null);
        final boolean grew  = (!isNew && sameStrict(was, now) && now.stackSize > was.stackSize);

        if (isNew || grew) {
            NBTTagCompound tag = now.getTagCompound();
            if (tag == null) tag = new NBTTagCompound();
            tag.removeTag(TAG_SEEN);
            tag.setBoolean(TAG_NEW, true);
            now.setTagCompound(tag);
            return isNew ? now.stackSize : (now.stackSize - was.stackSize);
        }
        return 0;
    }

    /** Strict equality for “same stack” (item, meta, NBT), used to detect growth only. */
    private static boolean sameStrict(ItemStack a, ItemStack b) {
        if (a == null || b == null) return false;
        if (a.getItem() != b.getItem()) return false;
        if (a.getItemDamage() != b.getItemDamage()) return false;

        final NBTTagCompound ta = a.getTagCompound();
        final NBTTagCompound tb = b.getTagCompound();
        if (ta == null && tb == null) return true;
        if (ta == null || tb == null) return false;
        return ta.equals(tb);
    }

    private static ItemStack[] copyArray(ItemStack[] src) {
        if (src == null) return null;
        ItemStack[] out = new ItemStack[src.length];
        for (int i = 0; i < src.length; i++) out[i] = src[i] != null ? src[i].copy() : null;
        return out;
    }

    @SuppressWarnings("rawtypes")
    private static ItemStack[] copyContainer(Container c) {
        final List slots = c.inventorySlots;
        ItemStack[] out = new ItemStack[slots.size()];
        for (int i = 0; i < slots.size(); i++) {
            final Slot s = (Slot) slots.get(i);
            final ItemStack st = s.getStack();
            out[i] = (st != null) ? st.copy() : null;
        }
        return out;
    }

    private static void accum(Map<Key,Integer> m, Key k, int add){
        m.put(k, m.containsKey(k) ? m.get(k) + add : add);
    }

    /** Send the HUD message with the exact variant and amount picked. */
    private static void sendNotify(EntityPlayerMP p, ItemStack now, int added) {
        if (!ModConfig.enablePickupNotifier) return;
        if (now == null || added <= 0) return;

        ItemStack display = now.copy();
        display.stackSize = added; // HUD shows "name xN" using this size
        RFNetwork.CH.sendTo(new MsgPickup(display, added), p);
    }
}
