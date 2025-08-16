package com.excsi.riftfixes.server;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.*;

/**
 * Server-authoritative, one-shot tagging:
 *  - Snapshot inventories exactly when pickup is fired
 *  - On next server tick, diff snapshot vs current
 *  - Star ONLY new stacks or stacks that grew
 * Clearing remains via MsgClearPickupTag (server).
 */
public final class PickupStarServerEvents {

    private static final String TAG_NEW  = "riftfixes_new";
    private static final String TAG_SEEN = "riftfixes_seen";

    /** Per-player state captured at pickup time, consumed next tick. */
    private static final class State {
        ItemStack[] baseMain;            // snapshot of main inventory
        int         baseWinId = Integer.MIN_VALUE;
        ItemStack[] baseCont;            // snapshot of open container slots
        boolean     pending;             // true until processed on next tick
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

        st.pending = true; // process next server tick (END)
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent e) {
        if (e.phase != TickEvent.Phase.END) return;
        if (e.player.worldObj.isRemote) return;

        final UUID id = e.player.getUniqueID();
        final State st = states.get(id);
        if (st == null || !st.pending) return;

        // ---- Diff main inventory (36 slots) ----
        final ItemStack[] curMain = e.player.inventory.mainInventory;
        if (curMain != null && st.baseMain != null && st.baseMain.length == curMain.length) {
            for (int i = 0; i < curMain.length; i++) {
                tagIfNewOrGrew(curMain[i], st.baseMain[i]);
            }
        }

        // ---- Diff open container if it is still the same ----
        final Container cur = e.player.openContainer;
        if (cur != null && cur.inventorySlots != null && st.baseCont != null && cur.windowId == st.baseWinId) {
            boolean changed = false;
            @SuppressWarnings("rawtypes")
            final List slots = cur.inventorySlots;
            if (st.baseCont.length == slots.size()) {
                for (int i = 0; i < slots.size(); i++) {
                    final Slot s = (Slot) slots.get(i);
                    final ItemStack now = s.getStack();
                    final ItemStack was = st.baseCont[i];

                    if (tagIfNewOrGrew(now, was)) {
                        s.onSlotChanged();
                        changed = true;
                    }
                }
            }
            if (changed) cur.detectAndSendChanges();
        }

        // Consume snapshot
        st.pending   = false;
        st.baseMain  = null;
        st.baseCont  = null;
        st.baseWinId = Integer.MIN_VALUE;
    }

    // ---------------- helpers ----------------

    /** Tag as NEW (and clear SEEN) if now is a brand-new stack or a strict-growth from was. */
    private static boolean tagIfNewOrGrew(ItemStack now, ItemStack was) {
        if (now == null) return false;

        final boolean isNew  = (was == null);
        final boolean grew   = (!isNew && sameStrict(was, now) && now.stackSize > was.stackSize);

        if (isNew || grew) {
            NBTTagCompound tag = now.getTagCompound();
            if (tag == null) tag = new NBTTagCompound();
            tag.removeTag(TAG_SEEN);
            tag.setBoolean(TAG_NEW, true);
            now.setTagCompound(tag);
            return true;
        }
        return false;
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
}
